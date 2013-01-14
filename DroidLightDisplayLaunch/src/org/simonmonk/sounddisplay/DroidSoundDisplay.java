package org.simonmonk.sounddisplay;



import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DroidSoundDisplay extends BaseActivity implements OnClickListener {
	static final String TAG = "DemoKitPhone";
	/** Called when the activity is first created. */
	TextView mInputLabel;
	LinearLayout mInputContainer;
	Drawable mFocusedTabImage;
	Drawable mNormalTabImage;

	@Override
	protected void hideControls() {
		super.hideControls();
	}

	public void onCreate(Bundle savedInstanceState) {
		mFocusedTabImage = getResources().getDrawable(
				R.drawable.tab_focused_holo_dark);
		mNormalTabImage = getResources().getDrawable(
				R.drawable.tab_normal_holo_dark);
		super.onCreate(savedInstanceState);
	}

	protected void showControls() {
		super.showControls();

		mInputLabel = (TextView) findViewById(R.id.inputLabel);
		mInputContainer = (LinearLayout) findViewById(R.id.inputContainer);
		mInputLabel.setOnClickListener(this);

		showTabContents(true);
	}

	void showTabContents(Boolean showInput) {
		if (showInput) {
			mInputContainer.setVisibility(View.VISIBLE);
			mInputLabel.setBackgroundDrawable(mFocusedTabImage);
		} else {
			mInputContainer.setVisibility(View.GONE);
			mInputLabel.setBackgroundDrawable(mNormalTabImage);
		}
	}

	public void onClick(View v) {
		int vId = v.getId();
		switch (vId) {
		case R.id.inputLabel:
			showTabContents(true);
			break;
		}
	}

}