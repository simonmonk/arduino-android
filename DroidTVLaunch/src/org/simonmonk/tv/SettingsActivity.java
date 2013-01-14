/**
 * code from the book Arduino + Android Projects for the Evil Genius
 * <br>Copyright 2011 Simon Monk
 *
 * <p>This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation (see COPYING).
 * 
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.simonmonk.tv;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class SettingsActivity extends Activity implements OnClickListener {

	public static final String PREFS_NAME = "TVPrefsFile";
	
	protected DroidTVActivity mHostActivity;


	Button saveButton;
	EditText labelField;

	
	
	   public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        setContentView(R.layout.settings);
	        
	        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	        
	        
	        Bundle bundle = this.getIntent().getExtras();
	        Long index = bundle.getLong("index");

	        labelField = (EditText)findViewById(R.id.labelfield);
	        String label = settings.getString("label" + index, "-");
	        labelField.setText(label);
	        	       
	        saveButton = (Button)findViewById(R.id.saveButton);
	        saveButton.setOnClickListener(this);
	    }
	   


	public void onClick(View v) {
	    Bundle bundle = this.getIntent().getExtras();
        int index = (int)bundle.getLong("index");
		if (v == saveButton) {
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		    SharedPreferences.Editor editor = settings.edit();
		    
	        editor.putString("label"+index, labelField.getText().toString());
		    editor.commit();
		}
	}
}
