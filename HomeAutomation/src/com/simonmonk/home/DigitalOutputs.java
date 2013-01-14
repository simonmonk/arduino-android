package com.simonmonk.home;

import android.content.SharedPreferences;
import android.util.Log;

public class DigitalOutputs 
{

	private HomeActivity mActivity;
	
	public DigitalOutputs(HomeActivity activity)
	{
		super();
		mActivity = activity;
	}
	
	public void turnOn(int op)
	{
		Log.i("SRM", "Turning on " + op);
		setOutput(op, 1);
		saveState(op, 1);
	}

	public void turnOff(int op)
	{
		Log.i("SRM", "Turning off " + op);
		setOutput(op, 0);
		saveState(op, 0);		
	}
	
	public boolean isOn(int op)
	{
		return (getSetting("op_state" + op).equals("ON"));
	}
		
	public void setOutput(int op, int value)
	{
		Log.i("SRM", "Setting Output" + op + " to " + value);
		int x = op * 0x100 + value;
		Beeper b = new Beeper();
		b.beep(x);
	}
	
	private void saveState(int op, int value)
	{
		if (value == 1)
		{
			setSetting("op_state" + op, "ON");
		}
		else
		{
			setSetting("op_state" + op, "OFF");			
		}
	}
	
	String getSetting(String name)
	{
		SharedPreferences settings = mActivity.getSharedPreferences(PrefsActivity.PREFS_NAME, 0);
        return settings.getString(name, "null");
	}
	
	void setSetting(String name, String value)
	{
		SharedPreferences settings = mActivity.getSharedPreferences(PrefsActivity.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, value);
        editor.commit();
	}	
	
}
