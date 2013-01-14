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


class TimerMonitor extends Thread
{
	private HomeActivity mActivity;
	
	public TimerMonitor(HomeActivity activity)
	{
		super();
		mActivity = activity;
	}
	
	public void run()
	{
		while (true)
		{
			for (int i = 1; i <= 5; i++)
			{
				handleTimerNumber(i);
				try 
				{
					Thread.sleep(5000);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
			try 
			{
				Thread.sleep(10000);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	
	private void handleTimerNumber(int i) 
	{
		String itemStr = getSetting("TIMER_OP" + i);
		if ("null".equals(itemStr))
		{
			return; // timer not in use
		}
		String startTimeStr = getSetting("TIMER_ON"+ i);
		String endTimeStr = getSetting("TIMER_OFF" + i);
		int start = Integer.parseInt(startTimeStr);
		int end = Integer.parseInt(endTimeStr);
		int op = Integer.parseInt(itemStr);
		// compare with time now and if it should be on, but isn't, turn it on.
		// the times can just be compared as 4 digit numbers
		Date d = new Date();
		int t = d.getHours() * 100 + d.getMinutes();

		boolean shouldBeOn = false;
		// if the off time appears to be before the on time, then it refers to the next day
		if (end < start)
		{
			end = end + 2400;
		}
		shouldBeOn = (t >= start && t < end);
		DigitalOutputs outputs = mActivity.outputs;
		if (shouldBeOn && ! outputs.isOn(op))
		{
			outputs.turnOn(op);
		}
		if (! shouldBeOn && outputs.isOn(op))
		{
			outputs.turnOff(op);
		}
	}

	String getSetting(String name)
	{
		SharedPreferences settings = mActivity.getSharedPreferences(PrefsActivity.PREFS_NAME, 0);
        return settings.getString(name, "null");
	}
	  
}
