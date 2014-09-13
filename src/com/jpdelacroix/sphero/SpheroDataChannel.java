package com.jpdelacroix.sphero;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.microedition.io.StreamConnection;

import com.jpdelacroix.sphero.heartbeat.SpheroHeartbeat;
import com.jpdelacroix.sphero.packets.SpheroAsynchronousPacket;
import com.jpdelacroix.sphero.packets.SpheroCommandPacket;
import com.jpdelacroix.sphero.packets.SpheroPacket;
import com.jpdelacroix.sphero.packets.SpheroResponsePacket;

public class SpheroDataChannel {

	private StreamConnection spheroConnection = null;
	private String spheroFriendlyName = null;
	
	private DataOutputStream spheroCommandLine = null;
	private DataInputStream spheroResponseLine = null;
	
	private BlockingQueue<SpheroResponsePacket> incomingQueue = new LinkedBlockingQueue<>();
	private BlockingQueue<SpheroCommandPacket> outgoingQueue = new LinkedBlockingQueue<>();
	
	private final InboundProcessor inboundProcessor = new InboundProcessor();
	private final OutboundProcessor outboundProcessor = new OutboundProcessor();
	
	private final ExecutorService processingService = Executors.newFixedThreadPool(2);
	
	private SpheroHeartbeat spheroHeartbeat = null;
	
	public SpheroDataChannel(StreamConnection connection, String friendlyName, SpheroHeartbeat aHeartbeat) {
		spheroConnection = connection;
		spheroFriendlyName = friendlyName;
		spheroHeartbeat = aHeartbeat;
	}
	
	public void send(SpheroCommandPacket packet) {
		try {
			outgoingQueue.put(packet);
		} catch (InterruptedException e) {
			System.err.println("Sending command to the queue was interrupted.");
		}
	}
	
	public SpheroResponsePacket receive() {
		SpheroResponsePacket response = null;
		try {
			while (response == null && inboundProcessor.isRunning()) {
				response = incomingQueue.poll(1, TimeUnit.SECONDS);
			}
		} catch (InterruptedException e) {
			System.err.println("Receiving response from the queue was interrupted.");
		}
		return response;
	}
	
	public ArrayList<SpheroResponsePacket> receiveAll() {
		ArrayList<SpheroResponsePacket> allQueuedResponses = new ArrayList<>();
		incomingQueue.drainTo(allQueuedResponses);
		return allQueuedResponses;
	}
	
	public int numberOfQueuedResponses() {
		return incomingQueue.size();
	}
	
	public void open() throws IOException {
		spheroCommandLine = spheroConnection.openDataOutputStream();
		spheroResponseLine = spheroConnection.openDataInputStream();
		
		if(!processingService.isShutdown()) {
			processingService.execute(inboundProcessor);
			processingService.execute(outboundProcessor);
		}
	}
	
	public void close() throws IOException {
		processingService.shutdownNow();
		spheroCommandLine.close();
		spheroResponseLine.close();
	}
	
	private class OutboundProcessor implements Runnable {

		private boolean isRunning = false;
		
		@Override
		public void run() {
			isRunning = true;
			try {
				while (!processingService.isShutdown()) {
					SpheroCommandPacket packet = outgoingQueue.take();
					spheroCommandLine.write(packet.toByteArray());
				}
			} catch (InterruptedException e) {
				System.err.println("Writing command to Sphero (" + spheroFriendlyName + ") was interrupted.");
			} catch (IOException e) {
				System.err.println("Unable to write command to Sphero (" + spheroFriendlyName + ").");
			} finally {
				spheroHeartbeat.kill();
				isRunning = false;
			}
		
		}
		
		public boolean isRunning() {
			return isRunning();
		}
		
	}
	
	private class InboundProcessor implements Runnable {
		
		private boolean isRunning = false;
		
		@Override
		public void run() {
			isRunning = true;
			try {
				while (!processingService.isShutdown()) {
					ByteArrayOutputStream incomingByteStream = new ByteArrayOutputStream();
					
					incomingByteStream.write(spheroResponseLine.readByte()); 			// SOP1
					byte SOP2 = spheroResponseLine.readByte();
					incomingByteStream.write(SOP2);										// SOP2
					
					int lengthOfData = 0;
					boolean isAsynchronous = false;
					if (SOP2 == SpheroPacket.SOP.DEFAULT.getByteCode()) {
						// ACK						
						incomingByteStream.write(spheroResponseLine.readByte());		// MSRP
						incomingByteStream.write(spheroResponseLine.readByte());		// SEQ
						byte DLEN = spheroResponseLine.readByte();
						incomingByteStream.write(DLEN);									// DLEN
						lengthOfData = DLEN;
					} else if (SOP2 == SpheroPacket.SOP.ASYNC.getByteCode()) {
						// ASYNC
						isAsynchronous = true;					
						incomingByteStream.write(spheroResponseLine.readByte());		// ID CODE
						byte DLEN_MSB = spheroResponseLine.readByte();				
						incomingByteStream.write(DLEN_MSB);								// DLEN MSB
						byte DLEN_LSB = spheroResponseLine.readByte();
						incomingByteStream.write(DLEN_LSB);								// DLEN LSB
						lengthOfData = (DLEN_MSB << 8) | (DLEN_LSB); 
					}

					for (int i=0; i<(lengthOfData-1); i++) {
						incomingByteStream.write(spheroResponseLine.readByte());		// DATA
					}
					incomingByteStream.write(spheroResponseLine.readByte());			// CHK
					
					SpheroResponsePacket packet = null;
					if (isAsynchronous) {
						packet = new SpheroAsynchronousPacket(incomingByteStream.toByteArray());
					} else {
						packet = new SpheroResponsePacket(incomingByteStream.toByteArray());
					}
					incomingQueue.put(packet);
				}
			} catch (IOException e) {
				System.err.println("Unable to read response from Sphero (" + spheroFriendlyName + "), because channel closed.");
			} catch (InterruptedException e) {
				System.err.println("Reading response from Sphero (" + spheroFriendlyName + ") was interrupted.");
			} finally {
				spheroHeartbeat.kill();
				isRunning = false;
			}
		}
		
		public boolean isRunning() {
			return isRunning;
		}
		
	}
}
