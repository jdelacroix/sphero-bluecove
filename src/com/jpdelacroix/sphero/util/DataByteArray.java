package com.jpdelacroix.sphero.util;

import java.io.ByteArrayOutputStream;

//Copyright (C) 2014, Jean-Pierre de la Croix
//see the LICENSE file included with this software

public class DataByteArray extends ByteArrayOutputStream {
	
	public void writeFourBytes(int anInt) {
		write(anInt >> 24);
		write(anInt >> 16);
		write(anInt >> 8);
		write(anInt);
	}
	
	public void writeTwoBytes(int aShort) {
		write(aShort >> 8);
		write(aShort);
	}
	
	public void writeOneByte(int aByte) {
		write(aByte);
	}
	
	public void writeBytes(int bytes, int nBytes) {
		if (nBytes > 0 && nBytes <= 4) {
			while(nBytes > 0) {
				write(bytes >> 8*(nBytes-1));
				nBytes--;
			}
		}
	}
}
