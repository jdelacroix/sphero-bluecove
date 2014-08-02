package com.jpdelacroix.sphero;

import java.io.ByteArrayOutputStream;

public class SpheroCommand {
	
	public static final byte SOP1 = (byte) 0xFF;
	public static final byte SOP2 = (byte) 0xFF; // 0xFE if asynchronous
	public static final byte DID = (byte) 0x02;
	
	public static final byte CID_SET_HEADING = (byte) 0x01;
	public static final byte CID_SET_RGB_LED = (byte) 0x20;
	public static final byte CID_SET_BACK_LED = (byte) 0x21;
	
	public static byte sequenceNumber = 0;
	
	private byte cid;
	private ByteArrayOutputStream data = null;
	private byte checksum = 0;
	private int length;
	
	public SpheroCommand(byte cid, byte[] data, int length) {
		this.length = 6 + length + 1;						// header + data + checksum
		this.cid = cid;		
		
		this.data = new ByteArrayOutputStream(this.length);
		
		this.data.write(SpheroCommand.SOP1);				// SOP1
		this.data.write(SpheroCommand.SOP2);				// SOP2
		this.data.write(SpheroCommand.DID);					// DID
		this.data.write(this.cid);							// CID
		this.data.write(SpheroCommand.sequenceNumber++);	// sequence number
		this.data.write(length+1); 							// length of data and checksum in bytes
		this.data.write(data, 0, length);					// data
		
		byte[] packet = this.data.toByteArray();
		for(int i=2; i<this.length-1; i++) {
			this.checksum += packet[i];
		}
		this.data.write((checksum ^ 0xFF));							// checksum
	}
	
	public byte[] toByteArray() {
		return this.data.toByteArray();
	}
	

}
