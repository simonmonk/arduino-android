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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends Activity 
{
    /** Called when the activity is first created. */
	
	public DigitalOutputs outputs;
	public String URL = "not connected";

	
	private WebServer mWebserver = null;
	private TextView mStatusView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        URL = startWebServer();
		
		TimerMonitor tm = new TimerMonitor(HomeActivity.this);
		tm.start();
		
		HeatingMonitor hm = new HeatingMonitor(HomeActivity.this);
		hm.start();	
        
		Button button = (Button)findViewById(R.id.openButton);
        button.setOnClickListener(
        		new OnClickListener()
        		{
					public void onClick(View v) 
					{
						Intent myIntent = new Intent(Intent.ACTION_VIEW);
						myIntent.setData(Uri.parse(URL + "home?"));
						startActivity(myIntent);
					}
        		});
        button = (Button)findViewById(R.id.prefsButton);
        button.setOnClickListener(
        		new OnClickListener()
        		{
					public void onClick(View v) 
					{
						Intent i = new Intent(HomeActivity.this, PrefsActivity.class);
			        	startActivity(i);
					}
        		});
        button = (Button)findViewById(R.id.restartButton);
        button.setOnClickListener(
        		new OnClickListener()
        		{
					public void onClick(View v) 
					{
						mWebserver.destroy(); // BAD BAD BAD! but I cannot work out how to restart server gracefully
					}
        		});
    }

	private String startWebServer() 
	{
		mStatusView = (TextView)findViewById(R.id.statusview);
        URL = "http://" + getLocalIpAddress() + ":8080/";
        outputs = new DigitalOutputs(HomeActivity.this);
       
     	mWebserver = new WebServer(HomeActivity.this);
		mWebserver.start();
		return URL;
	}
    
    public void setStatus(String status)
    {
    	mStatusView.setText(status);
    }
    
    public String getLocalIpAddress() 
    {
    	try 
    	{
    		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
    		{
    			NetworkInterface intf = en.nextElement();
    			for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
    			{
    				InetAddress inetAddress = enumIpAddr.nextElement();
    				if (!inetAddress.isLoopbackAddress()) 
    				{
    					return inetAddress.getHostAddress().toString();
    				}
    			}
    		}
    	} 
    	catch (SocketException ex) 
    	{
    		Log.e("SRM", ex.toString());
    	}
    	return null;
    }

	  
	  
	  
}