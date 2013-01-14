package org.simonmonk.templogger;

import java.util.Date;

import Pachube.Feed;
import Pachube.Pachube;
import Pachube.PachubeException;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;

public class InputController extends AccessoryController {
	private TextView mTemperature;
	private TextView mLogView;
	private CheckBox mSendCheckBox;
	private RadioButton mDegFRadioButton;
	private RadioButton mDegCRadioButton;
	
	long lastReadingTime = 0;
	double lastReading = 0.0d;


	InputController(DroidTempLoggerActivity hostActivity) {
		super(hostActivity);
		mHostActivity = hostActivity;
		mTemperature = (TextView) findViewById(R.id.tempValue);
		mLogView = (TextView) findViewById(R.id.logField);
		mSendCheckBox = (CheckBox) findViewById(R.id.pachube);
		mDegFRadioButton = (RadioButton) findViewById(R.id.radio_f);
		mDegCRadioButton = (RadioButton) findViewById(R.id.radio_c);
	}

	
	protected void onAccesssoryAttached() {
	}
	
	public void clearLog()
	{
		mLogView.setText("");
	}

	public void handleGeigerMessage(char flag, int reading) {
			lastReading = ((double)reading) / 10.0f;
			double readingToDisplay = lastReading;
			if (mDegFRadioButton.isChecked()){
				readingToDisplay = readingToDisplay * 9.0d / 5.0d + 32.0d;
			}
			mTemperature.setText("" + readingToDisplay);
			long timeNow = System.currentTimeMillis();
			if (timeNow > lastReadingTime + 60000l) {	
				lastReadingTime = timeNow;
				addLogEntry(readingToDisplay);
				if (mSendCheckBox.isChecked()) {
					sendPachubeReading(lastReading);
				}
			}
	}


	private void sendPachubeReading(double reading) {
		SharedPreferences settings = mHostActivity.getSharedPreferences(SettingsActivity.PREFS_NAME, 0);
        String pachubeFeed = settings.getString("PACHUBEFEEDID", null);
        String pachubeKey = settings.getString("PACHUBEKEY", null);	
        if (pachubeFeed == null ||  pachubeKey == null) {
        	alert("Go to Settings and add entries for Parachube Feed ID and Key.");
        }
        else {
        	try {
                Pachube p = new Pachube(pachubeKey);
                Feed f = p.getFeed(Integer.parseInt(pachubeFeed));
                f.updateDatastream(1, reading);
        	} 
        	catch (PachubeException e) {
             //   alert(e.errorMessage);
                mSendCheckBox.setChecked(false);
        	}
        }
	}

	public void alert(String message)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(mHostActivity).create();
	    alertDialog.setTitle("Alert");
	    alertDialog.setMessage(message);
	    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int which) {
	    		return;
	    	} 
	    }); 
	    alertDialog.show();
	}
	

	
	private void addLogEntry(double reading) {
		String log = mLogView.getText().toString();
		String timeFormatted = (String) DateFormat.format("hh:mm:ss", new Date());
		mLogView.setText(timeFormatted + "\t\t\t" + reading + "\n" + log);
	}

}
