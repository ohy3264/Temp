package com.hw.oh.utility;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class InfoExtra {
  static final String TAG = "InfoExtra";
  private Context mContext;

  public InfoExtra(Context context) {
    // TODO Auto-generated constructor stub
    mContext = context;
  }


  public String getAndroidID() {

    return android.provider.Settings.Secure.getString(
        mContext.getContentResolver(),
        android.provider.Settings.Secure.ANDROID_ID);
  }

  public String getAppName() {
    String appName = null;
    try {
      appName = (String) mContext.getPackageManager()
          .getApplicationLabel(
              mContext.getPackageManager().getApplicationInfo(
                  mContext.getPackageName(),
                  PackageManager.GET_UNINSTALLED_PACKAGES));
      return appName;
    } catch (NameNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      appName = "null";
    }
    return appName;

  }

}
