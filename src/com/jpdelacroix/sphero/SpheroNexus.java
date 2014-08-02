package com.jpdelacroix.sphero;

// Copyright (C) 2014, Jean-Pierre de la Croix
// see the LICENSE file included with this software

import java.util.ArrayList;

public class SpheroNexus {
	
	public static void main(String[] args) throws InterruptedException {
		
//		SpheroDiscovery discoverer = new SpheroDiscovery();
//		ArrayList<Sphero> nearbySpheros = discoverer.findNearbySpheros();
		
		Sphero s = new Sphero("00066644239C", "Sphero-OYW", Sphero.SPP_DEFAULT_CHANNEL);
		
//		for(Sphero s : nearbySpheros) {
			s.connect();
			s.setRgbLedColor("FF1493");
//			s.setBackLedBrightness(128);
//			s.setRelativeHeading(180);
//			s.setRelativeHeading(0);
//			s.setBackLedBrightness(0);
			Thread.sleep(2000);
			s.setRgbLedColor("008ccc");
			Thread.sleep(2000);
			s.disconnect();
////		}
		
		System.out.println("Done.");
	}
}
