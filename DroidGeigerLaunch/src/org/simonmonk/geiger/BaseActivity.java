/**
 * code from the book Arduino + ANdroid Projects for the Evil Genius
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

package org.simonmonk.geiger;

import org.simonmonk.geiger.R;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends DroidGeigerActivity {

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
		menu.add("Clear");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle() == "Clear") {
			mInputController.clearLog();
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

	protected void handleGeigerMessage(GeigerMsg t) {
		if (mInputController != null) {
			mInputController.handleGeigerMessage(t.getFlag(), t.getReading());
		}
	}


}