package com.jpdelacroix.sphero.packets;

//Copyright (C) 2014, Jean-Pierre de la Croix
//see the LICENSE file included with this software

import java.io.ByteArrayOutputStream;

public class SpheroCommandPacket extends SpheroPacket {
	
	private static byte sequenceNumber = 0;
	
	public SpheroCommandPacket(SpheroPacket.DID deviceID, SpheroPacket.CID commandID, byte[] data, int length) {
		int packetLength = 6 + length + 1;								// header + data + checksum
		
		byteDataBuffer = new ByteArrayOutputStream(packetLength);
		
		byteDataBuffer.write(SpheroPacket.SOP.SOP1.getByteCode());		// SOP1
		byteDataBuffer.write(SpheroPacket.SOP.SOP2.getByteCode());		// SOP2
		byteDataBuffer.write(deviceID.getByteCode());					// DID
		byteDataBuffer.write(commandID.getByteCode());					// CID
		byteDataBuffer.write(SpheroCommandPacket.sequenceNumber++);		// sequence number
		byteDataBuffer.write(length+1); 								// length of data and checksum in bytes
		byteDataBuffer.write(data, 0, length);							// data
		
		byte[] byteArray = byteDataBuffer.toByteArray();
		
		byte checksum = 0;
		for(int i=2; i<packetLength-1; i++) {
			checksum += byteArray[i];
		}
		byteDataBuffer.write((checksum ^ 0xFF));						// checksum
	}

}
