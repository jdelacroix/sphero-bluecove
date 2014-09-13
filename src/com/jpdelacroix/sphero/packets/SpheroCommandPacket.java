package com.jpdelacroix.sphero.packets;

import java.io.ByteArrayOutputStream;

//Copyright (C) 2014, Jean-Pierre de la Croix
//see the LICENSE file included with this software

public class SpheroCommandPacket extends SpheroPacket {
	
	private static byte sequenceNumber = 0;
	
	public SpheroCommandPacket(SpheroPacket.DID deviceID, SpheroPacket.CID commandID, byte[] data, int length) {
		this(SpheroPacket.SOP.DEFAULT, deviceID, commandID, data, length);
	}
	
	public SpheroCommandPacket(SpheroPacket.SOP deliveryOption, SpheroPacket.DID deviceID, SpheroPacket.CID commandID, byte[] data, int length) {
		
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		
		byteStream.write(SpheroPacket.SOP.DEFAULT.getByteCode());							// SOP1
		byteStream.write(deliveryOption.getByteCode());										// SOP2
		if(deliveryOption.equals(SpheroPacket.SOP.ASYNC) || deliveryOption.equals(SpheroPacket.SOP.NORESET_ASYNC)) {
			isAsynchronous = true;
		}
		byteStream.write(deviceID.getByteCode());											// DID
		byteStream.write(commandID.getByteCode());											// CID
		byteStream.write(SpheroCommandPacket.sequenceNumber++);								// sequence number
		byteStream.write(length+1); 														// length of data and checksum in bytes
		byteStream.write(data, 0, length);													// data
		byteStream.write(computeChecksum(byteStream.toByteArray(), byteStream.size()+1));	// checksum
		
		internalByteArray = byteStream.toByteArray();
	}

}
