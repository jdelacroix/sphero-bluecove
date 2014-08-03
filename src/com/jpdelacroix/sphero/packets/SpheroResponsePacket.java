package com.jpdelacroix.sphero.packets;

//Copyright (C) 2014, Jean-Pierre de la Croix
//see the LICENSE file included with this software

public class SpheroResponsePacket extends SpheroPacket {
	
	private boolean isErrorFree = false;
	
	public SpheroResponsePacket(byte[] byteArray, int length) {
		byteDataBuffer.write(byteArray, 0, length);
		isErrorFree = (computeChecksum() == byteArray[length-1]);
		if(!isErrorFree) {
			System.err.println("Invalid checksum detected. " + String.format("%02X vs %02X", (computeChecksum() ^ 0xFF), byteArray[length-1]));
		}
	}
	
	public boolean isErrorFree() {
		return isErrorFree;
	}

}
