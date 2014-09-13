package com.jpdelacroix.sphero.packets;

//Copyright (C) 2014, Jean-Pierre de la Croix
//see the LICENSE file included with this software

public class SpheroAsynchronousPacket extends SpheroResponsePacket {

	public SpheroAsynchronousPacket(byte[] byteArray) {
		super(byteArray);
		isAsynchronous = true;
	}
	
	

}
