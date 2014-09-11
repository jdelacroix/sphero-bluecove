package com.jpdelacroix.sphero.util;

import java.io.ByteArrayOutputStream;

//Copyright (C) 2014, Jean-Pierre de la Croix
//see the LICENSE file included with this software

public class DataByteArray extends ByteArrayOutputStream {
	
	public void write(int aByte, int nBytes) {
		if (nBytes > 0 && nBytes <= 4) {
			while(nBytes > 0) {
				write(aByte >> 8*(nBytes-1));
				nBytes--;
			}
		}
	}
}
