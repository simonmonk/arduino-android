//    HomeAutomation software to accompany the book 
//    'Practical Arduino + Android Projects for the Evil Genius'
//    Copyright (C) 2011. Simon Monk
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.simonmonk.home;

import java.util.Date;
import android.content.SharedPreferences;
import android.util.Log;


class HeatingMonitor extends Thread
{
	static final String[] keys = {"0004", "0406", "0607", "0708", "0809", "0912",
		"1215", "1516", "1617", "1722", "2223", "2324"};
	
	private HomeActivity mActivity;
	
	public HeatingMonitor(HomeActivity activity)
	{
		super();
		mActivity = activity;
	}
	
	public void run()
	{
		while (true)
		{
			for (int i = 1; i < 12; i++)
			{
				handleTimeSlot(keys[i]);
			}
			try 
			{
				Thread.sleep(10000); // check every 10 seconds
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	
	private void handleTimeSlot(String key) 
	{
		Date d = new Date();
		String temp;
		int day = d.getDay();
		Log.d("SRM", "d="+day);
		if (day > 0 && day < 6)
		{
		    Log.d("SRM", "weekday");
			temp = getSetting("TEMP_d" + key);
		}
		else
		{
		    Log.d("SRM", "weekend");
			temp = getSetting("TEMP_w" + key);
		}
		String hourFromS = key.substring(0,2);
		String hourToS = key.substring(2,4);
		int hourFrom = Integer.parseInt(hourFromS);
		int hourTo = Integer.parseInt(hourToS);
		int hour = d.getHours();
		if (hour >= hourFrom && hour < hourTo)
		{
			DigitalOutputs outputs = mActivity.outputs;
			int t = Integer.parseInt(temp);
			if ("C".equals(getSetting("UNITS")))
			{
				t = (t * 9) / 5 + 32;
			}
			t = t + 20; // fiddle factor  deg F - sensor gets warm in box
			outputs.setOutput(0x41, t);
		}
	}

	String getSetting(String name)
	{
		SharedPreferences settings = mActivity.getSharedPreferences(PrefsActivity.PREFS_NAME, 0);
        return settings.getString(name, "0");
	}
	  
}
