package com.jpdelacroix.sphero;

// Copyright (C) 2014, Jean-Pierre de la Croix
// see the LICENSE file included with this software

import java.util.ArrayList;

public class SpheroNexus {
	
	public static void main(String[] args) throws InterruptedException {
		
//		SpheroDiscovery discoverer = new SpheroDiscovery();
//		ArrayList<Sphero> nearbySpheros = discoverer.findNearbySpheros();
		
		Sphero s = new Sphero("00066644239C", "Sphero-OYW", Sphero.SPP_DEFAULT_CHANNEL);
		
		String[] colorSequence = { "00B7EB", "FF0090", "FFEF00", "000000" };
		
		s.connect();
		for(String color : colorSequence) {
			s.setRgbLedColor(color, true);
			Thread.sleep(200);
		}
		s.disconnect();
		
		System.out.println("Done.");
	}
}
