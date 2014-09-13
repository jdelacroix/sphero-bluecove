package com.jpdelacroix.sphero;

import java.util.ArrayList;

import com.jpdelacroix.sphero.packets.SpheroResponsePacket;

//Copyright (C) 2014, Jean-Pierre de la Croix
//see the LICENSE file included with this software

public class SpheroApplication implements Runnable {

	private Sphero roboticBall = null;
	
	public SpheroApplication(Sphero aSphero) {
		roboticBall = aSphero;
	}
	
	@Override
	public void run() {
		String[] colorSequence = { "00B7EB", "FF0090", "FFEF00", "000000" };
		
		for(String color : colorSequence) {
			roboticBall.setRgbLedColor(color, true);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ArrayList<SpheroResponsePacket> allResponses = roboticBall.getAllResponses();
		for (SpheroResponsePacket r : allResponses) {
			System.out.println(r);
		}
	}

}
