package org.simonmonk.templogger;

import android.content.res.Resources;
import android.view.View;

public abstract class AccessoryController {

	protected DroidTempLoggerActivity mHostActivity;

	public AccessoryController(DroidTempLoggerActivity activity) {
		mHostActivity = activity;
	}

	protected View findViewById(int id) {
		return mHostActivity.findViewById(id);
	}

	protected Resources getResources() {
		return mHostActivity.getResources();
	}

	void accessoryAttached() {
		onAccesssoryAttached();
	}

	abstract protected void onAccesssoryAttached();

}