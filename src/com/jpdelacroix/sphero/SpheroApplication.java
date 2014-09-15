package com.jpdelacroix.sphero;

import java.util.ArrayList;
import java.util.HashMap;

import com.jpdelacroix.sphero.packets.SpheroAsynchronousPacket;
import com.jpdelacroix.sphero.packets.SpheroDataStreamingOptions;
import com.jpdelacroix.sphero.packets.SpheroResponsePacket;

//Copyright (C) 2014, Jean-Pierre de la Croix
//see the LICENSE file included with this software

public class SpheroApplication implements Runnable {

	private Sphero roboticBall = null;
	private SpheroDataStreamingOptions options = new SpheroDataStreamingOptions(200, 1, 0);
	
	public SpheroApplication(Sphero aSphero) {
		this.roboticBall = aSphero;
		options.addOptions(SpheroDataStreamingOptions.MASK.IMU_PITCH_ANGLE_FILTERED, SpheroDataStreamingOptions.MASK.IMU_ROLL_ANGLE_FILTERED, SpheroDataStreamingOptions.MASK.IMU_YAW_ANGLE_FILTERED);
		options.addOptions(SpheroDataStreamingOptions.MASK2.QUARTERNION_Q0, SpheroDataStreamingOptions.MASK2.QUARTERNION_Q1, SpheroDataStreamingOptions.MASK2.QUARTERNION_Q2, SpheroDataStreamingOptions.MASK2.QUARTERNION_Q3);
		this.roboticBall.enableDataStreaming(options);
	}
	
	@Override
	public void run() {
		//String[] colorSequence = { "00B7EB", "FF0090", "FFEF00", "000000" };		// CMYK
		String[] colorSequence = { "FF5800", "FEDF00", "FFFFFF" };					// OYW
		
		for(String color : colorSequence) {
			roboticBall.setRgbLedColor(color, true);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		roboticBall.setStabilizationMode(false);
		
//		ArrayList<SpheroResponsePacket> allResponses = roboticBall.getAllPackets();
//		for (SpheroResponsePacket r : allResponses) {
//			System.out.println(r);
//		}
				
		while (roboticBall.isConnected()) {
			SpheroResponsePacket packet = roboticBall.waitForNextPacket();
			if (packet.isAsynchronous()) {
				System.out.println("***");
				System.out.println(packet);
				HashMap<String, Double> data = ((SpheroAsynchronousPacket) packet).parseDataWithOptions(options);
				for (String name : data.keySet()) {
					System.out.println(name + " : " + data.get(name));
				}
			}
		}
	}

	
}
