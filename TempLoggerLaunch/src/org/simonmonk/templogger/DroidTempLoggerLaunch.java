package org.simonmonk.templogger;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class DroidTempLoggerLaunch extends Activity {
	static final String TAG = "SRM";

	static Intent createIntent(Activity activity) {
		return new Intent(activity, DroidTempLogger.class);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = createIntent(this);

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Log.e(TAG, "unable to start activity", e);
		}
		finish();
	}
}
