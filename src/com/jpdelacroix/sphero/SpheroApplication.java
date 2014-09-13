package com.jpdelacroix.sphero;

import java.util.ArrayList;

import com.jpdelacroix.sphero.packets.SpheroDataStreamingOptions;
import com.jpdelacroix.sphero.packets.SpheroResponsePacket;

//Copyright (C) 2014, Jean-Pierre de la Croix
//see the LICENSE file included with this software

public class SpheroApplication implements Runnable {

	private Sphero roboticBall = null;
	
	public SpheroApplication(Sphero aSphero) {
		this.roboticBall = aSphero;
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
		
		setStreamingOptions();
		
		while (roboticBall.isConnected()) {
			System.out.println(roboticBall.waitForNextResponse());
		}
	}
	
	public void setStreamingOptions() {
		SpheroDataStreamingOptions options = new SpheroDataStreamingOptions(200, 1, 4);
		options.addOptions(SpheroDataStreamingOptions.MASK.IMU_PITCH_ANGLE_FILTERED, SpheroDataStreamingOptions.MASK.IMU_ROLL_ANGLE_FILTERED, SpheroDataStreamingOptions.MASK.IMU_YAW_ANGLE_FILTERED);
		options.addOptions(SpheroDataStreamingOptions.MASK2.QUARTERNION_Q0, SpheroDataStreamingOptions.MASK2.QUARTERNION_Q1, SpheroDataStreamingOptions.MASK2.QUARTERNION_Q2, SpheroDataStreamingOptions.MASK2.QUARTERNION_Q3);
		roboticBall.enableDataStreaming(options);
	}

	
}
