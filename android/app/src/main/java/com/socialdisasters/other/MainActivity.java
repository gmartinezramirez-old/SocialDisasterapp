/*
 * MainActivity.java
 * 
 * Copyright (C) 2011 IBR, TU Braunschweig
 *
 * Written-by: Johannes Morgenroth <morgenroth@ibr.cs.tu-bs.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.socialdisasters.other;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends Activity implements DefaultHardwareBackBtnHandler {
   private ReactRootView mReactRootView; 
   private ReactInstanceManager mReactInstanceManager;

	@SuppressWarnings("unused")
	private final String TAG = "MainActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mReactRootView = new ReactRootView(this);
    mReactInstanceManager = ReactInstanceManager.builder().setApplication(getApplication()).setBundleAssetName("index.android.bundle").setJSMainModuleName("index.android").addPackage(new MainReactPackage()) .setUseDeveloperSupport(BuildConfig.DEBUG).setInitialLifecycleState(LifecycleState.RESUMED).build();
    mReactRootView.startReactApplication(mReactInstanceManager, "HelloWorld", null);
    setContentView(mReactRootView);
	}

  @Override
  public void invokeDefaultOnBackPressed()
    { super.onBackPressed(); }
}
