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

package org.simonmonk.rangefinder;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;

public class InputController extends AccessoryController implements OnCheckedChangeListener, OnClickListener {
	private TextView mDistance;
	private TextView mLogView;
	private CheckBox mLaserCheckBox;
	private RadioButton mCmRadioButton;
	private RadioButton mInRadioButton;
	private boolean m_isCm = false;

	InputController(DroidGeigerActivity hostActivity) {
		super(hostActivity);
		mHostActivity = hostActivity;
		mDistance = (TextView) findViewById(R.id.distValue);
		mDistance.setOnClickListener(this);
		mLogView = (TextView) findViewById(R.id.logField);
		mLaserCheckBox = (CheckBox) findViewById(R.id.laser);
		mCmRadioButton = (RadioButton) findViewById(R.id.radio_cm);
		mCmRadioButton.setOnCheckedChangeListener(this);
		mInRadioButton = (RadioButton) findViewById(R.id.radio_inches);
		mInRadioButton.setOnCheckedChangeListener(this);
		mLaserCheckBox.setOnCheckedChangeListener(this);
	}

	
	protected void onAccesssoryAttached() {
	}
	
	public void clearLog()
	{
		mLogView.setText("");
	}

	public void handleGeigerMessage(char flag, int reading) {
		Log.d("SRM", "setDist " + reading);
		if (! m_isCm){
			reading = reading * 4 / 10;
		}
			
		if (flag == 'R')
		{
			mDistance.setText("" + reading);
		}
	}

	void setLaserState(int laserState)
	{
		Log.d("SRM", "set Laser to:" + laserState);
		mHostActivity.sendCommand((byte)1,(byte)0,laserState);
	}


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView == mLaserCheckBox) {
			if (isChecked)
			{
				setLaserState(1);
			}
			else
			{
				setLaserState(0);
			}
		}
		else if (buttonView == mCmRadioButton) {
			m_isCm = isChecked;
		}
		else if (buttonView == mInRadioButton) {
			m_isCm = ! isChecked;
		}
	}


	@Override
	public void onClick(View v) {
		// Save the reading
		String log = mLogView.getText().toString();
		String units = " in";
		if (m_isCm) {
			units = " cm";
		}
		mLogView.setText("" + mDistance.getText() + units + "\n" + log);
	}

}
