package com.jpdelacroix.sphero.packets;

import com.jpdelacroix.sphero.util.DataByteArray;

//Copyright (C) 2014, Jean-Pierre de la Croix
//see the LICENSE file included with this software

public class SpheroDataStreamingOptions {
	
	public static enum MASK {
		DISABLE 						(0x00000000),
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
	
	private int optionsMask = 0;
	private int optionsMask2 = 0;
	
	// DEFAULTS
	private int samplingRateFactor = 1;		// 400Hz
	private int samplesPerPacket = 1;		// 1 sample/packet
	private int packetsPerStream = 1;		// 1 packet/stream
	
	public SpheroDataStreamingOptions(int factor, int nSamples, int nPackets) {
		this.setSamplingRateFactor(factor);
		this.setSamplesPerPacket(nSamples);
		this.setPacketsPerStream(nPackets);
	}
	
	public SpheroDataStreamingOptions() {
		// accept defaults
	}
	
	public void setSamplingRateFactor(int factor) {
		// The sampling rate factor will divide the maximum sampling rate of 400Hz
		// to smaller sampling rate, for example, factor = 6 => sampling rate of 50Hz
		if (samplingRateFactor > 0 && samplingRateFactor <= Short.MAX_VALUE) {
			samplingRateFactor = factor;
		} else {
			System.err.println("Sampling rate factor has to be greater than zero.");
		}
	}
	
	public void setSamplesPerPacket(int nSamples) {
		if (nSamples > 0 && nSamples <= Short.MAX_VALUE) {
			samplesPerPacket = nSamples;
		} else {
			System.err.println("Frames per packet has to be greater than zero.");
		}
	}
	
	public void setPacketsPerStream(int nPackets) {
		if (nPackets == 0) {
			packetsPerStream = nPackets;
			System.err.println("Data stream from Sphero will continue until it is disabled.");
		} else if (nPackets > 0 && nPackets <= 255) {
			packetsPerStream = nPackets;
			System.out.println("Data stream from Sphero will end after " + nPackets + " packet(s).");
		} else {
			System.err.println("Packets per stream has to be zero for unlimited or greater than zero.");
		}
	}
	
	public void addOptions(MASK... optionBitMasks) {
		for (MASK option : optionBitMasks) {
			optionsMask |= option.getOptionBitMask();
		}
	}
	
	public void addOptions(MASK2... optionBitMasks) {
		for (MASK2 option : optionBitMasks) {
			optionsMask2 |= option.getOptionBitMask();
		}
	}
	
	public byte[] toByteArray() {
		DataByteArray array = new DataByteArray();
		
		array.writeTwoBytes(samplingRateFactor);
		array.writeTwoBytes(samplesPerPacket);
		array.writeFourBytes(optionsMask);
		array.writeOneByte(packetsPerStream);
		if (optionsMask2 != 0) {
			array.writeFourBytes(optionsMask2);
		}
		
		return array.toByteArray();
	}
	
}
