package org.simonmonk.arduinoandroid.bot;

import org.simonmonk.arduinodroid.bot.R;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import at.abraxas.amarino.Amarino;

public class DroidDroid extends Activity implements OnClickListener {
	
	private static final String TAG = "SM";
	
	public static String DEVICE_ADDRESS;
	
	EditText idField;
	Button button;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Log.d(TAG, "Main onStart");
        
        // get references to views defined in our main.xml layout file
        idField = (EditText) findViewById(R.id.deviceIDField);
        button = (Button) findViewById(R.id.okButton);
        // register listeners
        button.setOnClickListener(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        DEVICE_ADDRESS = prefs.getString("device", "00:11:04:08:04:64");
        idField.setText(DEVICE_ADDRESS);
    }
    

	public void onClick(View v) 
	{
		DEVICE_ADDRESS = idField.getText().toString();
		PreferenceManager.getDefaultSharedPreferences(this)
			.edit()
				.putString("device", DEVICE_ADDRESS)
			.commit();
		Amarino.connect(this, DEVICE_ADDRESS);
		Intent i = new Intent(this, DroidDroidControls.class);
    	startActivity(i);
	}
	
}