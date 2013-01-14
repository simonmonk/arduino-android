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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


class WebServer extends Thread
{

	
	final int PORT = 8080;
	private HomeActivity mActivity;
	
	public WebServer(HomeActivity activity)
	{
		super();
		mActivity = activity;
	}
	
	public void run()
	{
		ResponseHelper helper = new ResponseHelper(mActivity);
		try 
		{
			ServerSocket serversocket = new ServerSocket(PORT);
			setStatus("Started, Connect to: " + mActivity.URL + "home?");
			while (true)
			{		
				Socket connectionsocket = serversocket.accept();
				BufferedReader input = new BufferedReader(new InputStreamReader(connectionsocket.getInputStream()), 8192);
				DataOutputStream output = new DataOutputStream(connectionsocket.getOutputStream());
				handleRequest(input, output, helper);
			}
		} 
		catch (Throwable e) 
		{
			
			setStatus(e.getMessage());
		}
	}

	private void setStatus(final String message) 
	{
		mActivity.runOnUiThread(new Runnable()
			{
				public void run() 
				{
					mActivity.setStatus(message);
				}
			});
	}
	


	  private void handleRequest(BufferedReader input, DataOutputStream output, ResponseHelper helper) throws Exception 
	  {
		  // todo - make bullet proof !!!!!!
	    String request = input.readLine(); 
	    // GET /page?arg1=fred&arg2=smith HTTP/1.1
	    int indexOfSlash = request.indexOf('/');
	    int indexOfQ = request.indexOf('?');
	    String page = null;
	    String paramsString = null;
	    String response = "";
	    if (indexOfSlash < 0 || indexOfQ <= indexOfSlash)
	    {
	    	response = generateError("No page specified");
	    }
	    else 
	    {
	    	int indexOfSpace = request.indexOf("HTTP")-1;
	    	if (indexOfSpace >= indexOfQ)
	    	{
	    		page = request.substring(indexOfSlash+1, indexOfQ);	   
	    		paramsString = request.substring(indexOfQ+1, indexOfSpace); 		
	    		response = helper.generateResponse(page, mActivity.URL, parseParams(paramsString));
	    	}
	    }
	    output.writeBytes(constructHttpHeader());
	    output.writeBytes(response);
	   	 
	    output.close();
	  }
	
	  private String constructHttpHeader()
	  {
		    String s = "HTTP/1.0 ";
		    s = s + "200 OK";
		    s = s + "\r\n"; //other header fields,
		    s = s + "Connection: close\r\n"; //we can't handle persistent connections
		    s = s + "Server: Home Automation v0\r\n"; //server name
		        s = s + "Content-Type: text/html\r\n";
		    s = s + "\r\n"; 
		    return s;
		  }
	  
	  
	  private HashMap<String, String> parseParams(String paramsString)
	  {
		  HashMap<String, String> map = new HashMap<String, String>();
		  //arg1=fred&arg2=smith
		  if (paramsString == null)
		  {
			  return map;
		  }
		  final int READING_NAME = 0;
		  final int READING_VALUE = 1;
		  String name = "";
		  String value = "";
		  int state = READING_NAME;
		  int i = 0;
		  char ch;
		  int n = paramsString.length();
		  while (i < n)
		  {
			  ch = paramsString.charAt(i);
			  i++;
			  if (state == READING_NAME)
			  {
				  if (ch == '=')
				  {
					  state = READING_VALUE;
				  }
				  else
				  {
					  name = name + ch;
				  }
				  continue;
			  }
			  if (state == READING_VALUE)
			  {
				  if (ch == '&' || i == n)
				  {
					  if (i == n)
					  {
						  value = value + ch;
					  }
					  map.put(name, value);
					  name = "";
					  value = "";
					  state = READING_NAME;
				  }
				  else
				  {
					  value = value + ch;
				  }
			  }
		  }
		  return map;
	  }
	  

	  
	  private String generateError(String message)
	  {
		String s = "<html><body>";
	    s = s + "<H1>Error</H1>";
	    s = s + "<p>" + message + "</p>";
	    s = s +"</body></html>";
	    return s;
	  }
	  


	  
}
