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

import android.content.res.Resources;
import android.view.View;

public abstract class AccessoryController {

	protected DroidGeigerActivity mHostActivity;

	public AccessoryController(DroidGeigerActivity activity) {
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