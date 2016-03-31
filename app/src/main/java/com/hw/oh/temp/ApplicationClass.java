package com.hw.oh.temp;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import android.app.Application;
import android.support.multidex.MultiDexApplication;


/**
 * Platform Development Center - NMP Corp.
 *
 * @author David KIM
 * @since 1.0
 */
public class ApplicationClass extends MultiDexApplication {

  private Tracker mTracker;

    public ApplicationClass() {
    	super();
	}

  synchronized public Tracker getDefaultTracker() {
    if (mTracker == null) {
      GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
      // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
      mTracker = analytics.newTracker(R.xml.global_tracker);
    }
    return mTracker;
  }
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
