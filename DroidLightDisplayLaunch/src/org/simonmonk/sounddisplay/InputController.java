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


import org.cbase.blinkendroid.audio.AudioReader;

import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class InputController extends AccessoryController implements OnCheckedChangeListener, OnSeekBarChangeListener {

	private AudioReader mAR;
	private RadioButton mCheckboxTest;
	private RadioButton mCheckboxBargraph;
	private RadioButton mCheckboxBeat;
	private SeekBar mGainControl;
	private Visualizer mViz;
	private int mMode = 1; // 1=test, 2=bargraph, 3=beat
	private int mGain = 50;
	

	InputController(DroidSoundDisplayActivity hostActivity) {
		super(hostActivity);
		Log.d("SRM", "constructor");
		mHostActivity = hostActivity;
		mCheckboxTest = (RadioButton)findViewById(R.id.radio_test);
		mCheckboxTest.setOnCheckedChangeListener(this);
		mCheckboxBargraph = (RadioButton)findViewById(R.id.radio_bargraph);
		mCheckboxBargraph.setOnCheckedChangeListener(this);
		mCheckboxBeat = (RadioButton)findViewById(R.id.radio_beat);
		mCheckboxBeat.setOnCheckedChangeListener(this);
		mGainControl = (SeekBar)findViewById(R.id.seekbar_gain);
		mGainControl.setOnSeekBarChangeListener(this);

		mViz = new Visualizer();
		AudioReader.Listener listener = new AudioReader.Listener()
		{

			@Override
			public void onReadComplete(short[] buffer) {
				//Log.d("SRM", "" + buffer[0]);
				mViz.updateDisplay(buffer, mHostActivity, mMode, mGain);
			}

		};
		mAR = new AudioReader();
		mAR.startReader(8000, 80, listener);
		Log.d("SRM", "started sampler");
	}

	
	protected void onAccesssoryAttached() {
	}


	@Override
	public void onCheckedChanged(CompoundButton button, boolean value) {
		if (button == mCheckboxTest && value) {
			mHostActivity.sendCommand((byte)4, (byte)0, (byte)0); // test mode on
			mMode = 1;
		}
		else if (button == mCheckboxBargraph && value) {
			mHostActivity.sendCommand((byte)5, (byte)0, (byte)0); // test mode off
			mMode = 2;
		}
		else if (button == mCheckboxBeat && value) {
			mHostActivity.sendCommand((byte)5, (byte)0, (byte)0); // test mode off
			mMode = 3;
		}
	}


	@Override
	public void onProgressChanged(SeekBar ignore, int value, boolean ignore2) {
		mGain = value;
	}


	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}


}
