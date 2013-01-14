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


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class PrefsActivity extends Activity
{
	
	public static final String PREFS_NAME = "HomeAutomationPrefsFile";

	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.prefs);
        
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                
        String powerName1 = settings.getString("POWERNAME1", "Outlet 1");
        final EditText powerNameField1 = (EditText)findViewById(R.id.powername1);
        powerNameField1.setText(powerName1);

        String powerName2 = settings.getString("POWERNAME2", "Outlet 2");
        final EditText powerNameField2 = (EditText)findViewById(R.id.powername2);
        powerNameField2.setText(powerName2);

        String powerName3 = settings.getString("POWERNAME3", "Lights 1");
        final EditText powerNameField3 = (EditText)findViewById(R.id.powername3);
        powerNameField3.setText(powerName3);
        
        String powerName4 = settings.getString("POWERNAME4", "Lights 2");
        final EditText powerNameField4 = (EditText)findViewById(R.id.powername4);
        powerNameField4.setText(powerName4);
        
        String password = settings.getString("PASSWORD", "password");
        final EditText passwordField = (EditText)findViewById(R.id.password);
        passwordField.setText(password);

        String doorkey = settings.getString("DOORKEY", "A045");
        final EditText doorkeyField = (EditText)findViewById(R.id.doorkey);
        doorkeyField.setText(doorkey);
        
        
        String units = settings.getString("UNITS", "F");
        final CheckBox cb = (CheckBox)findViewById(R.id.degC);
        if (units.equals("C"))
        {
        	cb.setChecked(true);
        }
        else
        {
        	cb.setChecked(false);
        }

        Button button = (Button)findViewById(R.id.saveButton);
        button.setOnClickListener(
        		new OnClickListener()
        		{
					public void onClick(View v) 
					{
						SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
					    SharedPreferences.Editor editor = settings.edit();
					    editor.putString("POWERNAME1", powerNameField1.getText().toString());
					    editor.putString("POWERNAME2", powerNameField2.getText().toString());
					    editor.putString("POWERNAME3", powerNameField3.getText().toString());
					    editor.putString("POWERNAME4", powerNameField4.getText().toString());
					    editor.putString("PASSWORD", passwordField.getText().toString());
					    String key = doorkeyField.getText().toString();
					    if (validateDoorKey(key))
					    {
					    	editor.putString("DOORKEY", key);		
					    }
					    if (cb.isChecked())
					    {
					    	editor.putString("UNITS", "C");
					    }
					    else
					    {
					    	editor.putString("UNITS", "F");
					    }
					    editor.commit();
					}

					private boolean validateDoorKey(String key) 
					{
						if (key.length() != 4)
						{
							alert("Door key must be 4 digit hex");
							return false;
						}
						try 
						{
							Integer.parseInt(key, 16);
						} 
						catch (NumberFormatException e) 
						{
							alert("Door key must be 4 digit hex");
							return false;
						}
						return true;
					}

					public void alert(String message)
					{
						AlertDialog alertDialog = new AlertDialog.Builder(PrefsActivity.this).create();
					    alertDialog.setTitle("Alert");
					    alertDialog.setMessage(message);
					    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					    	public void onClick(DialogInterface dialog, int which) {
					    		return;
					    	} 
					    }); 
					    alertDialog.show();
					}
        		});
    }
}
