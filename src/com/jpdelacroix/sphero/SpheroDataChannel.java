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

import javax.microedition.io.StreamConnection;

import com.jpdelacroix.sphero.packets.SpheroCommandPacket;
import com.jpdelacroix.sphero.packets.SpheroResponsePacket;

public class SpheroDataChannel {

	private StreamConnection spheroConnection = null;
	private String spheroFriendlyName = null;
	
	private DataOutputStream spheroCommandLine = null;
	private DataInputStream spheroResponseLine = null;
	
	private BlockingQueue<SpheroResponsePacket> responseQueue = new LinkedBlockingQueue<>();
	private BlockingQueue<SpheroCommandPacket> commandQueue = new LinkedBlockingQueue<>();
	
	private final ExecutorService processingService = Executors.newFixedThreadPool(2);
	
	public SpheroDataChannel(StreamConnection connection, String friendlyName) {
		spheroConnection = connection;
		spheroFriendlyName = friendlyName;
	}
	
	public void send(SpheroCommandPacket packet) {
		try {
			commandQueue.put(packet);
		} catch (InterruptedException e) {
			System.err.println("Sending command to the queue was interrupted.");
		}
	}
	
	public SpheroResponsePacket receive() {
		try {
			return responseQueue.take();
		} catch (InterruptedException e) {
			System.err.println("Receiving response from the queue was interrupted.");
		}
		return null;
	}
	
	public ArrayList<SpheroResponsePacket> receiveAll() {
		ArrayList<SpheroResponsePacket> allQueuedResponses = new ArrayList<>();
		responseQueue.drainTo(allQueuedResponses);
		return allQueuedResponses;
	}
	
	public int numberOfQueuedResponses() {
		return responseQueue.size();
	}
	
	public void open() throws IOException {
		spheroCommandLine = spheroConnection.openDataOutputStream();
		spheroResponseLine = spheroConnection.openDataInputStream();
		
		if(!processingService.isShutdown()) {
			processingService.execute(new ResponseProcessor());
			processingService.execute(new CommandProcessor());
		}
	}
	
	public void close() throws IOException {
		processingService.shutdownNow();
		spheroCommandLine.close();
		spheroResponseLine.close();
	}
	
	private class CommandProcessor implements Runnable {

		@Override
		public void run() {
			while (!processingService.isShutdown()) {
				try {
					SpheroCommandPacket packet = commandQueue.take();
					spheroCommandLine.write(packet.toByteArray());
				} catch (InterruptedException e) {
					System.err.println("Writing command to Sphero (" + spheroFriendlyName + ") was interrupted.");
				} catch (IOException e) {
					System.err.println("Unable to write command to Sphero (" + spheroFriendlyName + ").");
				}
			}
		}
		
	}
	
	
	private class ResponseProcessor implements Runnable {
		
		@Override
		public void run() {
			try {
				while (!processingService.isShutdown()) {
					ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
					
					responseBuffer.write(spheroResponseLine.readByte()); 			// SOP1
					byte SOP2 = spheroResponseLine.readByte();
					responseBuffer.write(SOP2);										// SOP2
					
					int dataLength = 0;
					boolean isAsynchronous = false;
					if (SOP2 == (byte) (0xFF)) {
						// ACK						
						responseBuffer.write(spheroResponseLine.readByte());		// MSRP
						responseBuffer.write(spheroResponseLine.readByte());		// SEQ
						byte DLEN = spheroResponseLine.readByte();
						responseBuffer.write(DLEN);									// DLEN
						dataLength = DLEN;
					} else if (SOP2 == (byte) (0xFE)) {
						// ASYNC
						isAsynchronous = true;					
						responseBuffer.write(spheroResponseLine.readByte());		// ID CODE
						byte DLEN_MSB = spheroResponseLine.readByte();				
						responseBuffer.write(DLEN_MSB);								// DLEN MSB
						byte DLEN_LSB = spheroResponseLine.readByte();
						responseBuffer.write(DLEN_LSB);								// DLEN LSB
						dataLength = (DLEN_MSB << 8) | (DLEN_LSB); 
					}

					for (int i=0; i<(dataLength-1); i++) {
						responseBuffer.write(spheroResponseLine.readByte());		// DATA
					}
					responseBuffer.write(spheroResponseLine.readByte());			// CHK
					
					SpheroResponsePacket response = new SpheroResponsePacket(responseBuffer.toByteArray(), isAsynchronous);
//					System.out.println(response);
					responseQueue.put(response);
				}
			} catch (IOException e) {
				System.err.println("Unable to read response from Sphero (" + spheroFriendlyName + "), because channel closed.");
			} catch (InterruptedException e) {
				System.err.println("Reading response from Sphero (" + spheroFriendlyName + ") was interrupted.");
			}
		}
		
	}
}
