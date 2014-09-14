package com.jpdelacroix.sphero.packets;

import java.util.HashMap;

//Copyright (C) 2014, Jean-Pierre de la Croix
//see the LICENSE file included with this software

public class SpheroAsynchronousPacket extends SpheroResponsePacket {

	public SpheroAsynchronousPacket(byte[] byteArray) {
		super(byteArray);
		isAsynchronous = true;
	}
	
	public HashMap<String, Double> parseDataWithOptions(SpheroDataStreamingOptions options) {
		HashMap<String, Double> data = new HashMap<>();
		if (internalByteArray[2] == SpheroPacket.ID_CODE.SENSOR_DATA_STREAMING.getByteCode()) {
			int length = (internalByteArray[3] << 8) | internalByteArray[4];
			int index = 5;
			int value = 0;
			for (SpheroDataStreamingOptions.MASK option : SpheroDataStreamingOptions.MASK.values()) {
				if ((option.getOptionBitMask() & options.getOptionsMask()) != 0) {
					value = (internalByteArray[index]<<8) | internalByteArray[index+1];
					if (((value >> 15) & 1) == 1) {
						value = -value;
					}
					double scaled_value = value*option.getScaleFactor();
					data.put(option.name(), scaled_value);
					index+= 2;
				}
			}
			for (SpheroDataStreamingOptions.MASK2 option : SpheroDataStreamingOptions.MASK2.values()) {
				if ((option.getOptionBitMask() & options.getOptionsMask2()) != 0) {
					value = (internalByteArray[index]<<8) | internalByteArray[index+1];
					if (((value >> 15) & 1) == 1) {
						value = -value;
					}
					double scaled_value = value*option.getScaleFactor();
					data.put(option.name(), scaled_value);
					index+= 2;
				}
			}
		}
		return data;
	}

}
