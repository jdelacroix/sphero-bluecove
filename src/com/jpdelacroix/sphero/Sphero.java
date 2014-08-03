package com.jpdelacroix.sphero;

// Copyright (C) 2014, Jean-Pierre de la Croix
// see the LICENSE file included with this software

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.jpdelacroix.sphero.packets.SpheroCommandPacket;
import com.jpdelacroix.sphero.packets.SpheroPacket;

public class Sphero extends RemoteDevice {
	
	private String spheroURL = null;
	private StreamConnection spheroConnection = null;
	private OutputStream spheroCommandLine = null;
	private InputStream spheroResponseLine = null;
	private String spheroFriendlyName = null;
    private boolean isConnected = false;
	
	public static final int SPP_DEFAULT_CHANNEL = 1;
	
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
				spheroConnection = (StreamConnection) Connector.open(spheroURL);
				spheroCommandLine = spheroConnection.openOutputStream();
				spheroResponseLine = spheroConnection.openInputStream();
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
				spheroCommandLine.close();
				spheroConnection.close();
				isConnected = false;
			} catch (IOException e) {
				System.err.println("Unable to disconnect from the Sphero: " + this.getBluetoothAddress() + "  " + spheroFriendlyName);
			}
		} else {
			System.err.println("Sphero (" + spheroFriendlyName + ") is already disconnected.");
		}
	}
	
	public String getFriendlyName() {
		return this.spheroFriendlyName;
	}
	
	// Sphero Command Set
	
	public void setRgbLedColor(String hexColor, boolean isPersistant) {
		if(hexColor != null && hexColor.matches("[0-9A-Fa-f]{6}")) {
			byte r = (byte) Integer.parseInt(hexColor.substring(0,2), 16);
			byte g = (byte) Integer.parseInt(hexColor.substring(2,4), 16);
			byte b = (byte) Integer.parseInt(hexColor.substring(4,6), 16);
			setRgbLedColor(r, g, b, isPersistant);
		} else {
			System.out.println("Unable to parse and set RGB LED color. Expected, for example, setRgbLedColor(\"FF1493\").");
		}
	}
	
	public void setRgbLedColor(String hexColor) {
		setRgbLedColor(hexColor, false);
	}
	
	public void setRgbLedColor(byte r, byte g, byte b, boolean isPersistant) {
		byte[] data = { r, g, b, (byte) (isPersistant ? 1 : 0) };
		send(new SpheroCommandPacket(SpheroPacket.DID.SPHERO, SpheroPacket.CID.SET_RGB_LED, data, data.length));
	}
	
	public void setRgbLedColor(byte r, byte g, byte b) {
		setRgbLedColor(r, g, b, false);
	}
	
	public void setBackLedBrightness(int brightness) {
		if (brightness >=0 && brightness <= 255) {
			byte[] data = { (byte) brightness };
			send(new SpheroCommandPacket(SpheroPacket.DID.SPHERO, SpheroPacket.CID.SET_BACK_LED, data, data.length));
		} else {
			System.err.println("Expected integer brightness in range [0,255].");
		}
	}
	
	public void setRelativeHeading(int heading) {
		if (heading >=0 && heading <= 359) {
			byte[] data = { (byte) (heading >> 8), (byte) heading };
			send(new SpheroCommandPacket(SpheroPacket.DID.SPHERO, SpheroPacket.CID.SET_CAL, data, data.length));
		} else {
			System.err.println("Expected integer heading in range [0,359].");
		}
	}
	
	private void send(SpheroCommandPacket packet) {
		if(isConnected) {
			try {
				spheroCommandLine.write(packet.toByteArray());
//				while(spheroResponseLine.available() > 0) {
//					byte[] responseBuffer = new byte[spheroResponseLine.available()];
//					spheroResponseLine.read(responseBuffer, 0, responseBuffer.length);
//					System.out.println(Arrays.toString(responseBuffer));
//				}
			} catch (IOException e) {
				System.err.println("Unable to write command to Sphero (" + spheroFriendlyName + ").");
			}
		} else {
			System.err.println("Sphero (" + spheroFriendlyName + ") is not connected.");
		}
	}
	
	

}
