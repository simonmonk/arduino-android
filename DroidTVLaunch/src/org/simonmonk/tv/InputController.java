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

package org.simonmonk.tv;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.Toast;


public class InputController extends AccessoryController implements OnClickListener, OnLongClickListener {

	private int index = 0;
	private final int n = 9;
    private SharedPreferences settings = mHostActivity.getSharedPreferences(SettingsActivity.PREFS_NAME, 0);


	InputController(DroidTVActivity hostActivity) {
		super(hostActivity);
		Log.d("SRM", "constructor");
		mHostActivity = hostActivity;

		for (int i = 0; i < n; i++) {
			int buttonID = getResources().getIdentifier("button" + i, "id", hostActivity.getPackageName());
			Button button = (Button) findViewById(buttonID);
			button.setOnClickListener(this);
			button.setOnLongClickListener(this);
			
			String label = settings.getString("label"+i, "-");
			
			button.setText(label);
			button.setTag(new Integer(i));
		}
	}

	
	protected void onAccesssoryAttached() {
	}
	
	@Override
	public void onClick(View v) {
		// send the number pressed to the Arduino
		index = (Integer)v.getTag();
	//	toast("i="+ index);
		mHostActivity.sendCommand((byte)1,(byte)0, index);
	}


	@Override
	public boolean onLongClick(View v) {
		// Long hold to get context menu
//		index = (Integer)v.getTag();
//		openSettings(index);
		
		return false;
	}
	
	public void openSettings()
	{
		Bundle bundle = new Bundle();
		bundle.putLong("index", new Long(index));
		Intent newIntent = new Intent(mHostActivity, SettingsActivity.class);
		newIntent.putExtras(bundle);
		mHostActivity.startActivityForResult(newIntent, 0);
	}
	
	public void sendProgramCommand()
	{
		mHostActivity.sendCommand((byte)1,(byte)1, index);
	}

	public void handleValueMessage(char flag, int reading) {
		Log.d("SRM", "got message from Arduino " + reading);
		if (flag == 'R') {
			toast("Programmed Button " + reading);
		}
	}

	void toast(CharSequence text) {
		Context context = mHostActivity.getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	
	
}
