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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;

public class Template 
{
	private Activity mActivity;
	private String mValue;
	
	public Template(String resourceName, Activity activity)
	{
		mActivity = activity;
		mValue = readTemplate(resourceName);
	}
	
	public void set(String key, String value)
	{
		mValue = mValue.replaceAll("::" + key + "::", value);
	}
	
	public String toString()
	{
		return mValue;
	}
	
	private String readTemplate(String resourceName)
	{
		InputStream inputStream = null;
		try 
		{
			inputStream = mActivity.getAssets().open("templates/"+ resourceName + ".html");
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Template not found: " + resourceName;
		}
		return readTextFile(inputStream);
	}
	
	
    private String readTextFile(InputStream inputStream) 
    {
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    	byte buf[] = new byte[1024];
    	int len;
    	try 
    	{
    		while ((len = inputStream.read(buf)) != -1) 
    		{
    	       outputStream.write(buf, 0, len);
    	    }
    	    outputStream.close();
    	    inputStream.close();
    	} 
    	catch (IOException e) 
    	{
    	}
    	return outputStream.toString();
   }
	
}
