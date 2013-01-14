package org.simonmonk.templogger;

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

public class SettingsActivity extends Activity {

	public static final String PREFS_NAME = "TempLoggerPrefsFile";

	
	   public void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);

	        setContentView(R.layout.settings);
	        
	        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	                
	        String pachubeFeed = settings.getString("PACHUBEFEEDID", "11111");
	        final EditText pachubeFeedField = (EditText)findViewById(R.id.pachubefeed);
	        pachubeFeedField.setText(pachubeFeed);
//
//	        int period = settings.getInt("PERIOD", 10);
//	        final EditText periodField = (EditText)findViewById(R.id.period);
//	        periodField.setText("" + period);

	        String pachubeKey = settings.getString("PACHUBEKEY", "");
	        final EditText pachubeKeyField = (EditText)findViewById(R.id.pachubekey);
	        pachubeKeyField.setText(pachubeKey);


	       
	        Button button = (Button)findViewById(R.id.saveButton);
	        button.setOnClickListener(
	        		new OnClickListener()
	        		{
						public void onClick(View v) 
						{
							SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
						    SharedPreferences.Editor editor = settings.edit();
						    editor.putString("PACHUBEFEEDID", pachubeFeedField.getText().toString());
						    editor.putString("PACHUBEKEY", pachubeKeyField.getText().toString());
//						    String periodStr = periodField.getText().toString();
//						    if (validatePeriod(periodStr))
//						    {
//						    	editor.putInt("PERIOD", Integer.parseInt(periodStr));		
//						    }
						    editor.commit();
						}
						
						private boolean validatePeriod(String key) 
						{
							int period = 0;
							try 
							{
								period = Integer.parseInt(key);
							} 
							catch (NumberFormatException e) 
							{
								alert("Period must be a whole number of seconds.");
								return false;
							}
							if (period < 1 || period > 1000)
							{
								alert("Period must be between 1 and 1000 seconds.");
								return false;
							}
							return true;
						}
						
						public void alert(String message)
						{
							AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
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
