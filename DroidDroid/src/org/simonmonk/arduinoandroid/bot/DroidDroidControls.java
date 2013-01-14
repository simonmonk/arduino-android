package org.simonmonk.arduinoandroid.bot;

import org.simonmonk.arduinodroid.bot.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import at.abraxas.amarino.Amarino;

public class DroidDroidControls extends Activity implements OnSeekBarChangeListener {
	
	private static final String TAG = "SM";
	
	
	final int DELAY = 150;
	SeekBar leftSB;
	SeekBar rightSB;
	
	int left = 255;
	int right = 255;
	long lastChange;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controls);
        
        // get references to views defined in our main.xml layout file
        leftSB = (SeekBar) findViewById(R.id.SeekBarLeft);
        rightSB = (SeekBar) findViewById(R.id.SeekBarRight);

        // register listeners
        leftSB.setOnSeekBarChangeListener(this);
        rightSB.setOnSeekBarChangeListener(this);
    }
    
	@Override
	protected void onStart() {
		super.onStart();
        leftSB.setProgress(left);
        rightSB.setProgress(right);      
	}

	@Override
	protected void onStop() {
		super.onStop();
	
		Amarino.disconnect(this, DroidDroid.DEVICE_ADDRESS);
	}



	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// do not send to many updates, Arduino can't handle so much
		if (System.currentTimeMillis() - lastChange > DELAY ){
			updateState(seekBar);
			lastChange = System.currentTimeMillis();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		lastChange = System.currentTimeMillis();
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		updateState(seekBar);
	}

	private void updateState(final SeekBar seekBar) {
		
		switch (seekBar.getId()){
			case R.id.SeekBarLeft:
				left = seekBar.getProgress();
				updateLeft();
				break;
			case R.id.SeekBarRight:
				right = seekBar.getProgress();
				updateRight();
				break;
		}
		// provide user feedback
	}
	
	private void updateLeft(){
		Log.d(TAG, "update left=" + (512 - left));
		Log.d(TAG, "device address=" + DroidDroid.DEVICE_ADDRESS);
		Amarino.sendDataToArduino(this, DroidDroid.DEVICE_ADDRESS, 'l', (511 - left));
	}
	
	private void updateRight(){
		Log.d(TAG, "update right=" + (512 - right));
		Log.d(TAG, "device address=" + DroidDroid.DEVICE_ADDRESS);
		Amarino.sendDataToArduino(this, DroidDroid.DEVICE_ADDRESS, 'r', (511 - right));
	}
	
}