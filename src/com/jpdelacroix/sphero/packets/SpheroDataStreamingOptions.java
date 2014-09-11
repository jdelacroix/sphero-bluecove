package com.jpdelacroix.sphero.packets;

import com.jpdelacroix.sphero.util.DataByteArray;

//Copyright (C) 2014, Jean-Pierre de la Croix
//see the LICENSE file included with this software

public class SpheroDataStreamingOptions {
	
	public static enum MASK {
		DISABLE (0x0000),
		ACCELEROMETER_AXIS_X_RAW		(0x80000000),
		ACCELEROMETER_AXIS_Y_RAW		(0x40000000),
		ACCELEROMETER_AXIS_Z_RAW		(0x20000000),
		GYRO_AXIS_X_RAW 				(0x10000000),
		GYRO_AXIS_Y_RAW 				(0x08000000),
		GYRO_AXIS_Z_RAW 				(0x04000000),
		IMU_PITCH_ANGLE_FILTERED 		(0x00008000),
		IMU_ROLL_ANGLE_FILTERED 		(0x00004000),
		IMU_YAW_ANGLE_FILTERED			(0x00002000),
		ACCELEROMETER_AXIS_X_FILTERED 	(0x00001000),
		ACCELEROMETER_AXIS_Y_FILTERED	(0x00000800),
		ACCELEROMETER_AXIS_Z_FILTERED	(0x00000400);
		
		private int optionBitMask = 0;
		
		MASK(int anOptionBitMask) {
			optionBitMask = anOptionBitMask;
		}
		
		public int getOptionBitMask() {
			return optionBitMask;
		}	
	}
	
	public static enum MASK2 {
		QUARTERNION_Q0	(0x80000000),
		QUARTERNION_Q1	(0x40000000),
		QUARTERNION_Q2	(0x20000000),
		QUARTERNION_Q3	(0x10000000);
		
		private int optionBitMask = 0;

		MASK2(int anOptionBitMask) {
			this.optionBitMask = anOptionBitMask;
		}
		
		public int getOptionBitMask() {
			return optionBitMask;
		}	
	}
	
	private int bitfieldMASK = 0;
	private int bitfieldMASK2 = 0;
	private int samplingRateFactor = 0;
	private int frameCountPerPacket = 0;
	private int packetCountPerStream = 0;
	
	public void setSamplingRateFactor(int factor) {
		// The sampling rate factor will divide the maximum sampling rate of 400Hz
		// to smaller sampling rate, for example, factor = 6 => sampling rate of 50Hz
		if (samplingRateFactor > 0 && samplingRateFactor <= Short.MAX_VALUE) {
			samplingRateFactor = factor;
		} else {
			System.err.println("Sampling rate factor has to be greater than zero.");
		}
	}
	
	public void setFrameCountPerPacket(int nFrames) {
		if (nFrames > 0 && nFrames <= Short.MAX_VALUE) {
			frameCountPerPacket = nFrames;
		} else {
			System.err.println("Frames per packet has to be greater than zero.");
		}
	}
	
	public void setPacketCountPerStream(int nPackets) {
		if (nPackets == 0) {
			packetCountPerStream = nPackets;
			System.err.println("Data stream from Sphero will continue until it is disabled.");
		} else if (nPackets > 0 && nPackets <= 255) {
			packetCountPerStream = nPackets;
			System.out.println("Data stream from Sphero will end after " + nPackets + " packet(s).");
		} else {
			System.err.println("Packets per stream has to be zero for unlimited or greater than zero.");
		}
	}
	
	public void addOptions(MASK... optionBitMasks) {
		for (MASK option : optionBitMasks) {
			bitfieldMASK |= option.getOptionBitMask();
		}
	}
	
	public void addOptions(MASK2... optionBitMasks) {
		for (MASK2 option : optionBitMasks) {
			bitfieldMASK2 |= option.getOptionBitMask();
		}
	}
	
	public byte[] toByteArray() {
		DataByteArray array = new DataByteArray();
		
		array.write(samplingRateFactor, 2);
		array.write(frameCountPerPacket, 2);
		array.write(bitfieldMASK, 4);
		array.write(packetCountPerStream);
		if (bitfieldMASK2 != 0) {
			array.write(bitfieldMASK2, 4);
		}
		
		return array.toByteArray();
	}
	
}
