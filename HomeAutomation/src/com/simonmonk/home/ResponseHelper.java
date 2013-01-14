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

import java.util.HashMap;

import android.content.SharedPreferences;

public class ResponseHelper 
{

	HomeActivity mActivity;
	
	public ResponseHelper(HomeActivity activity)
	{
		mActivity = activity;
	}
	
	  public String generateResponse(String page, String server, HashMap<String, String> params)
	  {
		String response = "";
		Template t;
		Template head = new Template("head", mActivity);
		response = response + head.toString();
		if (page.startsWith("heating"))
		{
			// redirect for deg C or F
			page += getSetting("UNITS", "F");
		}
		t = new Template(page, mActivity);
		handleActions(page, params, t);
		response = response + t.toString();
		Template foot =  new Template("foot", mActivity);
		response = response + foot.toString();
		return response;
	  }

	private void handleActions(String page, HashMap<String, String> params, Template t) 
	{
		if ("power".equalsIgnoreCase(page))
		{
			handlePower(params, t);
		}
		else if ("timers".equalsIgnoreCase(page))
		{
			handleTimers(params, t);
		}
		else if ("door".equalsIgnoreCase(page))
		{
			handleDoor(params, t);
		}
		else if ("bedtime".equalsIgnoreCase(page))
		{
			handleBedtime(params, t);
		}
		else if (page.startsWith("heating")) // two templates (C and F) one handler
		{
			handleHeating(params, t);
		}
	}
	
	private void handlePower(HashMap<String, String> params, Template t) 
	{
		// power sockets are outputs 10-13
		t.set("POWERNAME1", getSetting("POWERNAME1", "Outlet 1"));
		t.set("POWERNAME2", getSetting("POWERNAME2", "Outlet 2"));
		t.set("POWERNAME3", getSetting("POWERNAME3", "Lights 1"));
		t.set("POWERNAME4", getSetting("POWERNAME4", "Lights 2"));
		String light = params.get("l");
		String state = params.get("s");
		DigitalOutputs outputs = mActivity.outputs;
		if (light != null && state != null)
		{
			int stateInt = Integer.parseInt(state);
			if (light.equalsIgnoreCase("A"))
			{
				outputs.setOutput(0x01, stateInt);
				outputs.setOutput(0x02, stateInt);
				outputs.setOutput(0x03, stateInt);
				outputs.setOutput(0x04, stateInt);				
			}
			else
			{
				outputs.setOutput(Integer.parseInt(light), stateInt);
			}
		}
	}
	
	
	private void handleTimers(HashMap<String, String> params, Template t) 
	{
		t.set("POWERNAME1", getSetting("POWERNAME1", "Socket 1"));
		t.set("POWERNAME2", getSetting("POWERNAME2", "Socket 2"));
		t.set("POWERNAME3", getSetting("POWERNAME3", "Socket 3"));
		t.set("POWERNAME4", getSetting("POWERNAME4", "Socket 4"));
		for (int i = 1; i <= 5; i++)
		{
			t.set("TIMER_ITEM"+i, getParameterOrStored("item"+i, "TIMER_OP"+i, "null", params));
			t.set("TIMER_ON"+i, getParameterOrStored("on"+i, "TIMER_ON"+i, "null", params));
			t.set("TIMER_OFF"+i, getParameterOrStored("off"+i, "TIMER_OFF"+i, "null", params));
		}
	}
	
	private void handleHeating(HashMap<String, String> params, Template t) 
	{
		String[] keys = {"0004", "0406", "0607", "0708", "0809", "0912",
						"1215", "1516", "1617", "1722", "2223", "2324"
		};
		for (int i = 0; i < 12; i++)
		{
			t.set("TEMP_d" + keys[i], getParameterOrStored("d"+ keys[i], "TEMP_d" + keys[i], "0", params));
			t.set("TEMP_w" + keys[i], getParameterOrStored("w"+ keys[i], "TEMP_w" + keys[i], "0", params));
		}
	}
	
	
	private void handleDoor(HashMap<String, String> params, Template t) 
	{
		String storedPassword = getSetting("PASSWORD", "");
		String doorKeyS = getSetting("DOORKEY", "");
		int doorKey = Integer.parseInt(doorKeyS, 16);
		String enteredPassword = params.get("password");
		String action = params.get("action");
		if ("1".equals(action) || "0".equals(action))
		{
			if (storedPassword.equals(enteredPassword))
			{
				t.set("STATUS", "<p>DOOR UNLOCKED</p>");
				t.set("PASSWORD_WRONG", "");
				new Beeper().beep(doorKey); // do it 3 times, EF link unreliable
				new Beeper().beep(doorKey);
				new Beeper().beep(doorKey);				
			}
			else
			{
				t.set("PASSWORD_WRONG", "<p>Incorrect Password</p>");
				t.set("STATUS", "");
			}
		}
		else
		{
			t.set("PASSWORD_WRONG", "");
			t.set("STATUS", "");
		}
	}
	
	private void handleBedtime(HashMap<String, String> params, Template t) 
	{
		String action = params.get("action");
		if ("off".equals(action) )
		{
			DigitalOutputs outputs = mActivity.outputs;
			// turn off the power
			outputs.setOutput(0x01, 0);
			outputs.setOutput(0x02, 0);
			outputs.setOutput(0x03, 0);	
			outputs.setOutput(0x03, 0);	
			
			t.set("CLOCK_MESSAGE", "GOODNIGHT!");
			t.set("FIRST_TIME", "false");
		}
		else
		{
			t.set("CLOCK_MESSAGE", "READY!");
			t.set("FIRST_TIME", "true");
		}
	}
	
	private String getParameterOrStored(String paramName, String storedName, String initial, HashMap<String, String> params)
	{
		// Parameter takes precidence over stored, and finally initial value
		// if you get the value as a papam also store it
		String value = params.get(paramName);
		if (value == null || "".equals(value))
		{
			value = getSetting(storedName, initial);
		}
		else
		{
			setSetting(storedName, value);
		}
		return value;
	}
	
	
	String getSetting(String name, String initial)
	{
		SharedPreferences settings = mActivity.getSharedPreferences(PrefsActivity.PREFS_NAME, 0);
        return settings.getString(name, initial);
	}
	
	void setSetting(String name, String value)
	{
		SharedPreferences settings = mActivity.getSharedPreferences(PrefsActivity.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, value);
        editor.commit();
	}	
}
