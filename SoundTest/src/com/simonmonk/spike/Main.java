package com.simonmonk.spike;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class Main extends Activity {


	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final EditText field = (EditText)findViewById(R.id.numberField);
        
        Button button = (Button)findViewById(R.id.playButton);
        button.setOnClickListener(
        		new OnClickListener()
        		{
					public void onClick(View v) 
					{
						try 
						{
							int i = Integer.parseInt(field.getText().toString(), 16);
							Log.i("SRM", "i=" + i);
							Beeper b = new Beeper();
							b.beep(i);
						} 
						catch (NumberFormatException e) 
						{
							field.setText("Err");
						}
					}
        		});
    }
} 