package com.jpdelacroix.sphero.packets;

//Copyright (C) 2014, Jean-Pierre de la Croix
//see the LICENSE file included with this software

public abstract class SpheroPacket {
	
	public static enum SOP {
		// SOP (Start Of Packet)
		DEFAULT (0xFF),
		ASYNC (0xFE),
		NORESET (0xFD),
		NORESET_ASYNC (0xFC);
		
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
		SET_STABILIZATION (0x02),
		SET_DATA_STREAMING (0x11),
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
	
	public static enum ID_CODE {
		// ID_CODE
		SENSOR_DATA_STREAMING (0x03);
		
		private byte byteCode = 0x00;
		
		ID_CODE(int aByteCode) {
			this.byteCode = (byte) aByteCode;
		}
		
		public byte getByteCode() {
			return this.byteCode;
		}
	};
	
	protected byte[] internalByteArray = null;
	protected boolean isAsynchronous = false;
	
	@Override
	public String toString() {
		if(internalByteArray.length > 0) {
			StringBuffer outputBuffer = new StringBuffer();
			outputBuffer.append("[ ");
			for(byte b : internalByteArray) {
				outputBuffer.append(String.format("%02X ", b));
			}
			outputBuffer.append("]");
			return outputBuffer.toString();
		}
		return null;
	}
	
	public byte[] toByteArray() {
		if(internalByteArray.length > 0) {
			return internalByteArray.clone();
		}
		return null;
	}
	
	public boolean isAsynchronous() {
		return isAsynchronous;
	}
	
	protected byte computeChecksum(byte[] byteArray, int length) {
		byte checksum = 0;							// + checksum is last byte in the packet
		for(int i=2; i<(length-1); i++) {			// + sum of all bytes starting after the
			checksum += byteArray[i];				//   first two SOP bytes until the end
		}											//   of the data payload.
		return (byte) (checksum ^ (byte) 0xFF);		// + bit-wise inverse
	}

}
