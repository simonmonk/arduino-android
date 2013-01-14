package org.simonmonk.templogger;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends DroidTempLoggerActivity {

	private InputController mInputController;

	public BaseActivity() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mAccessory != null) {
			showControls();
		} else {
			hideControls();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Open");
		menu.add("Settings");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle() == "Open") {
			showControls();
			mInputController.clearLog();
		} 
		if (item.getTitle() == "Settings") {
			// open the settings activity
			Intent i = new Intent(BaseActivity.this, SettingsActivity.class);
        	startActivity(i);
		} 
		return true;
	}

	protected void enableControls(boolean enable) {
		if (enable) {
			showControls();
		} else {
			hideControls();
		}
	}

	protected void hideControls() {
		setContentView(R.layout.no_device);
		mInputController = null;
	}

	protected void showControls() {
		setContentView(R.layout.main);

		mInputController = new InputController(this);
		mInputController.accessoryAttached();
	}

	protected void handleGeigerMessage(ValueMsg t) {
		if (mInputController != null) {
			mInputController.handleGeigerMessage(t.getFlag(), t.getReading());
		}
	}


}