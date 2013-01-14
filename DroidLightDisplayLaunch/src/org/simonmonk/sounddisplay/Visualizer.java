/**
 * code from the book Arduino + Android Projects for the Evil Genius
 * <br>Copyright 2011 Simon Monk
 *
 * <p>This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation (see COPYING).
 * 
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.simonmonk.sounddisplay;

import android.util.Log;

public class Visualizer {
	
	short peak = 0;
	short dPeak = 0;
	
	long lastUpdateTime = 0;
	
	public void updateDisplay(short[] sampleData, DroidSoundDisplayActivity hostActivity, int mode, int gain) {
		// 80 samples at 8 KHz
		int[] colors;
		long timeNow = System.currentTimeMillis();
		if (timeNow < lastUpdateTime + 100) return; // send updates at 10Hz
		lastUpdateTime = timeNow;
		switch (mode) {
		case 1: // test
			break;
		case 2: // bargraph
//			Log.d("SRM", "" + sampleData[0]);
			colors = calculateColorsBargraph(sampleData, gain);
			hostActivity.sendCommand((byte)1, (byte)0, colors[0]);
			hostActivity.sendCommand((byte)2, (byte)0, colors[1]);
			hostActivity.sendCommand((byte)3, (byte)0, colors[2]);
			break;
		case 3: // beat
//			Log.d("SRM", "" + sampleData[0]);
			colors = calculateColorsBeat(sampleData, gain);
			hostActivity.sendCommand((byte)1, (byte)0, colors[0]);
			hostActivity.sendCommand((byte)2, (byte)0, colors[1]);
			hostActivity.sendCommand((byte)3, (byte)0, colors[2]);
			break;

		default:
			break;
		}
	}

	private int[] calculateColorsBargraph(short[] sampleData, int gain) {
		int colors[];
		colors = new int[3];
		int value = Math.abs((findMax(sampleData))) * gain / 100;
		
		colors[0] =  Math.min(value, 255);
		value = (short) Math.max(value - 255, 0);
		colors[1] =  Math.min(value, 255);
		value = (short) Math.max(value - 255, 0);
		colors[2] =  Math.min(value, 255);
		Log.d("SRM", "bargraph red=" + colors[0] + " green=" + colors[1] + " blue=" + colors[2]);
		return colors;
	}
	
	private int[] calculateColorsBeat(short[] sampleData, int gain) {
		int colors[];
		colors = new int[3];
		int value = Math.abs((findMax(sampleData))) * gain / 100;
		colors[0] =  0;
		colors[1] =  0;		
		colors[2] =  0;
		if (value > peak / 2) {
			colors[0] = 255;
			colors[1] = 255;
			colors[2] = 255;
		}
		Log.d("SRM", "beat red=" + colors[0] + " green=" + colors[1] + " blue=" + colors[2]);
		if (value > peak)
		{
			peak = (short)value;
		}
		return colors;
	}

	private short findMax(short[] sampleData) {
		short max = 0;
		for (int i = 0; i < sampleData.length; i++)
		{
			if (sampleData[i] > max) max = sampleData[i];
		}
		return max;
	}

}
