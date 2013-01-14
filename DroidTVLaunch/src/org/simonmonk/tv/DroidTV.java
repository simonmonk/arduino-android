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


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DroidTV extends BaseActivity implements OnClickListener {
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