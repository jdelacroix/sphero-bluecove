package com.jpdelacroix.sphero.packets;

//Copyright (C) 2014, Jean-Pierre de la Croix
//see the LICENSE file included with this software

import java.io.ByteArrayOutputStream;

public abstract class SpheroPacket {
	
	public static enum SOP {
		// SOP (Start Of Packet)
		SOP1 (0xFF),
		SOP2 (0xFF),
		SOP2_ASYNC (0xFE);
		
		private byte byteCode = 0x00;
		
		SOP(int aByteCode) {
			this.byteCode = (byte) aByteCode;
		}
		
		public byte getByteCode() {
			return byteCode;
		}
	};
	
	public static enum DID {
		// DID (Device ID)
		CORE (0x00),
		BOOTLOADER (0x01),
		SPHERO (0x02);
		
		private byte byteCode = 0x00;
		
		DID(int aByteCode) {
			this.byteCode = (byte) aByteCode;
		}
		
		public byte getByteCode() {
			return this.byteCode;
		}
	};
	
	public static enum CID {
		// CID (Command ID)
		SET_CAL (0x01),
		SET_RGB_LED (0x20),
		SET_BACK_LED (0x21);
		
		private byte byteCode = 0x00;
		
		CID(int aByteCode) {
			this.byteCode = (byte) aByteCode;
		}
		
		public byte getByteCode() {
			return this.byteCode;
		}
	};
	
	public static enum RSP_CODE {
		// MRSP (Message Response Code)
		OK (0x00);
		
		private byte byteCode = 0x00;
		
		RSP_CODE(int aByteCode) {
			this.byteCode = (byte) aByteCode;
		}
		
		public byte getByteCode() {
			return this.byteCode;
		}
	};
	
	protected ByteArrayOutputStream byteDataBuffer = new ByteArrayOutputStream();
	
	public String toString() {
		if(byteDataBuffer.size() > 0) {
			StringBuffer outputBuffer = new StringBuffer();
			outputBuffer.append("[ ");
			byte[] byteArray = toByteArray();
			for(byte b : byteArray) {
				outputBuffer.append(String.format("%02X", b));
			}
			outputBuffer.append("]");
			return outputBuffer.toString();
		}
		return null;
	}
	
	public byte[] toByteArray() {
		if(byteDataBuffer.size() > 0) {
			return byteDataBuffer.toByteArray();
		}
		return null;
	}

}
