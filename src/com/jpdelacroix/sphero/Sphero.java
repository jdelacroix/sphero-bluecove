package com.jpdelacroix.sphero;

// Copyright (C) 2014, Jean-Pierre de la Croix
// see the LICENSE file included with this software

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.jpdelacroix.sphero.heartbeat.SpheroHeartbeat;
import com.jpdelacroix.sphero.heartbeat.SpheroHeartbeatListener;
import com.jpdelacroix.sphero.packets.SpheroCommandPacket;
import com.jpdelacroix.sphero.packets.SpheroDataStreamingOptions;
import com.jpdelacroix.sphero.packets.SpheroPacket;
import com.jpdelacroix.sphero.packets.SpheroResponsePacket;

public class Sphero extends RemoteDevice {
	
	public static final int SPP_DEFAULT_CHANNEL = 1;
	
	private String spheroURL = null;
	private StreamConnection spheroConnection = null;
	private SpheroDataChannel spheroDataChannel = null;
	private String spheroFriendlyName = null;
    private boolean isConnected = false;
    
    // Heartbeat
    private ExecutorService heartbeatService;
    private SpheroHeartbeat spheroHeartbeat = new SpheroHeartbeat();
		
	public Sphero(String address, String name, int sppChannel) {
		super(address);
		this.spheroURL = "btspp://" + address + ":" + sppChannel + ";authenticate=false;encrypt=false;master=false";
		this.spheroFriendlyName = name;
	}
	
	public void setSpheroUrl(String url) {
		if(!isConnected) {
			if (url.matches("btspp://" + this.getBluetoothAddress() + ":([1-9]|[1-3]?[0-9]);authenticate=(true|false);encrypt=(true|false);master=(true|false)")) {
				spheroURL = new String(url);
			} else {
				System.err.println("New Sphero service URL is not properly formatted.");
			}
		} else {
			System.err.println("Unable to change Sphero (" + spheroFriendlyName +") URL while connected.");
		}
	}
	
	public void connect() {
        if(!isConnected) {
        	try {
        		startHeartbeat();
				spheroConnection = (StreamConnection) Connector.open(spheroURL);
				spheroDataChannel = new SpheroDataChannel(spheroConnection, spheroFriendlyName, spheroHeartbeat);
				spheroDataChannel.open();
				isConnected = true;
			} catch (IOException e) {
				System.err.println("Unable to connect to Sphero (" + spheroFriendlyName + ") at " + spheroURL);
			}
        } else {
        	System.err.println("Sphero (" + spheroFriendlyName + ") is already connected.");
        }
	}
	
	public void disconnect() {
		if(isConnected) {
			try {
				spheroDataChannel.close();
				spheroConnection.close();
				isConnected = false;
				stopHeartbeat();
			} catch (IOException e) {
				System.err.println("Unable to disconnect from the Sphero (" + spheroFriendlyName + ").");
			}
		} else {
			System.err.println("Sphero (" + spheroFriendlyName + ") is already disconnected.");
		}
	}
	
	public boolean isConnected() {
		if (!isConnected) {
			System.err.println("Sperho (" + spheroFriendlyName + ") is not connected!");
		}
		return isConnected;
	}
	
	public String getFriendlyName() {
		return this.spheroFriendlyName;
	}
	
	// Heartbeat 
	
	private void startHeartbeat() {
		heartbeatService = Executors.newSingleThreadExecutor();
		this.addHeartbeatListener(new SpheroHeartbeatListener() {
			@Override
			public void onChange() {
				disconnect();
			}
		});
	}
	
	private void stopHeartbeat() {
		heartbeatService.shutdown();
	}
	
	private void addHeartbeatListener(SpheroHeartbeatListener aListener) {
		aListener.setHeartbeat(spheroHeartbeat);
		heartbeatService.execute(aListener);
	}
	
	// Sphero Command Set
	
	public void setRgbLedColor(String hexColor, boolean isPersistant) {
		if (isConnected()) {
			if(hexColor != null && hexColor.matches("[0-9A-Fa-f]{6}")) {
				byte r = (byte) Integer.parseInt(hexColor.substring(0,2), 16);
				byte g = (byte) Integer.parseInt(hexColor.substring(2,4), 16);
				byte b = (byte) Integer.parseInt(hexColor.substring(4,6), 16);
				setRgbLedColor(r, g, b, isPersistant);
			} else {
				System.out.println("Unable to parse hexidecimal color for RGB LED. Expected, for example, ff1493.");
			}
		}
	}
	
	public void setRgbLedColor(String hexColor) {
		setRgbLedColor(hexColor, false);
	}
	
	public void setRgbLedColor(byte r, byte g, byte b, boolean isPersistant) {
		if (isConnected()) {
			byte[] data = { r, g, b, (byte) (isPersistant ? 1 : 0) };
			spheroDataChannel.send(new SpheroCommandPacket(SpheroPacket.SOP.DEFAULT, SpheroPacket.DID.SPHERO, SpheroPacket.CID.SET_RGB_LED, data, data.length));
		}
	}
	
	public void setRgbLedColor(byte r, byte g, byte b) {
		setRgbLedColor(r, g, b, false);
	}
	
	public void setBackLedBrightness(int brightness) {
		if (isConnected()) {
			if (brightness >=0 && brightness <= 255) {
				byte[] data = { (byte) brightness };
				spheroDataChannel.send(new SpheroCommandPacket(SpheroPacket.DID.SPHERO, SpheroPacket.CID.SET_BACK_LED, data, data.length));
			} else {
				System.err.println("Expected integer brightness in range [0,255].");
			}
		}
	}
	
	public void setRelativeHeading(int heading) {
		if (isConnected()) {
			if (heading >=0 && heading <= 359) {
				byte[] data = { (byte) (heading >> 8), (byte) heading };
				spheroDataChannel.send(new SpheroCommandPacket(SpheroPacket.DID.SPHERO, SpheroPacket.CID.SET_CAL, data, data.length));
			} else {
				System.err.println("Expected integer heading in range [0,359].");
			}
		}
	}
	
	public void enableDataStreaming(SpheroDataStreamingOptions options) {
		if (isConnected()) {
			if (options != null) {
				byte[] data = options.toByteArray();
				spheroDataChannel.send(new SpheroCommandPacket(SpheroPacket.DID.SPHERO, SpheroPacket.CID.SET_DATA_STREAMING, data, data.length));
			} else {
				System.err.println("Expected a valid set of options.");
			}
		}
	}
	
	public void disableDataStreaming() {
		if (isConnected()) {
			SpheroDataStreamingOptions options = new SpheroDataStreamingOptions();
			options.addOptions(SpheroDataStreamingOptions.MASK.DISABLE);
			
			byte[] data = options.toByteArray();
			spheroDataChannel.send(new SpheroCommandPacket(SpheroPacket.DID.SPHERO, SpheroPacket.CID.SET_DATA_STREAMING, data, data.length));
		}
	}
	
	public void setStabilizationMode(boolean isStabilizationOn) {
		if (isConnected()) {
			byte[] data = { 0x00 };
			if (isStabilizationOn) {
				data[0] = 0x01;
			}
			spheroDataChannel.send(new SpheroCommandPacket(SpheroPacket.DID.SPHERO, SpheroPacket.CID.SET_STABILIZATION, data, data.length));
		}
	}
	
	// Data Channel access
	
	public SpheroResponsePacket getNextPacket() {
		if (spheroDataChannel.numberOfQueuedResponses() > 0) {
			return spheroDataChannel.receive();
		}
		System.out.println("No packets from Sphero (" + spheroFriendlyName + ") are currently in the queue.");
		return null;
	}
	
	public SpheroResponsePacket waitForNextPacket() {
		return spheroDataChannel.receive();
	}
	
	public ArrayList<SpheroResponsePacket> getAllPackets() {
		return spheroDataChannel.receiveAll();
	}

}
