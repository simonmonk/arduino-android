/**
 * code from the book Arduino + ANdroid Projects for the Evil Genius
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

package org.simonmonk.geiger;

import java.util.Date;


import org.simonmonk.geiger.R;

import android.media.MediaPlayer;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class InputController extends AccessoryController {
	private TextView mTemperature;
	private ImageView mRadiationImage;
	private TextView mLogView;
	
	private DroidGeigerActivity mHostActivity;
	private MediaPlayer mp;
	

	InputController(DroidGeigerActivity hostActivity) {
		super(hostActivity);
		mHostActivity = hostActivity;
		mTemperature = (TextView) findViewById(R.id.tempValue);
		mRadiationImage = (ImageView) findViewById(R.id.radImage);
		mLogView = (TextView) findViewById(R.id.logField);
		mp = MediaPlayer.create(mHostActivity, R.raw.click);

		mRadiationImage.setVisibility(ImageView.INVISIBLE);

	}

	
	protected void onAccesssoryAttached() {
	}
	
	public void clearLog()
	{
		mLogView.setText("Time\t\t\tCPM");
	}

	public void handleGeigerMessage(char flag, int reading) {
		Log.d("SRM", "setTemp " + reading);
		if (flag == 'E')
		{
			mRadiationImage.setVisibility(ImageView.VISIBLE);
			mp.start();
		}
		else if (flag == 'R')
		{
			mRadiationImage.setVisibility(ImageView.INVISIBLE);
			mTemperature.setText("" + reading);
		}
		else if (flag == 'L')
		{
			String logText = mLogView.getText().toString();
			String timeFormatted = (String) DateFormat.format("hh:mm", new Date());
			mLogView.setText(logText + "\n" + timeFormatted + "\t\t\t" + reading);
		}
	}



}
