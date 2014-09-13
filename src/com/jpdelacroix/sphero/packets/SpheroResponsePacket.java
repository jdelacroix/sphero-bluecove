package com.jpdelacroix.sphero.packets;

//Copyright (C) 2014, Jean-Pierre de la Croix
//see the LICENSE file included with this software

public class SpheroResponsePacket extends SpheroPacket {
	
	private boolean isErrorFree = false;
	
	public SpheroResponsePacket(byte[] byteArray) {
//		byteStream.write(byteArray, 0, byteArray.length);
		internalByteArray = byteArray.clone();
		byte checksum = computeChecksum(byteArray, byteArray.length);
		isErrorFree = (checksum == byteArray[byteArray.length-1]);
		if(!isErrorFree) {
			System.err.println("Invalid checksum detected. " + String.format("%02X vs %02X", checksum, byteArray[byteArray.length-1]));
		}
	}
	
	public boolean isErrorFree() {
		return isErrorFree;
	}

}
