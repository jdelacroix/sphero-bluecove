package com.jpdelacroix.sphero;

// Copyright (C) 2014, Jean-Pierre de la Croix
// see the LICENSE file included with this software

import java.util.ArrayList;

import com.jpdelacroix.sphero.util.RunnableLauncher;

public class SpheroNexus {
		
	public static void main(String[] args) throws InterruptedException {
		
//		SpheroDiscovery discoverer = new SpheroDiscovery();
//		ArrayList<Sphero> nearbySpheros = discoverer.findNearbySpheros();
		
		Sphero s = new Sphero("00066644239C", "Sphero-OYW", Sphero.SPP_DEFAULT_CHANNEL);
				
		s.connect();
		
		(new RunnableLauncher()).launch(new SpheroApplication(s));
		
		s.disconnect();
		
		System.out.println("Done.");
	}
}
