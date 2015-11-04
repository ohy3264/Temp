package com.hw.oh.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


/**
 * <pre>
 *
 * @author : oh
 * @Day : 2014. 11. 20.
 * @Time : 오전 10:42:30
 * @Explanation : 네트워크 연결 여부확인 </pre>
 */
public class HYNetworkInfo {
  private Context mContext;

  public HYNetworkInfo(Context paramContext) {
    this.mContext = paramContext;
  }

  public boolean networkgetInfo() {
    try {
      ConnectivityManager manager = (ConnectivityManager) mContext
          .getSystemService(Context.CONNECTIVITY_SERVICE);

      NetworkInfo wifi = manager
          .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
      if (wifi.isConnected())
        return false;
      NetworkInfo mobile = manager
          .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
      if (mobile.isConnected())
        return false;
      NetworkInfo wimax = manager
          .getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
      if (wimax.isConnected())
        return false;
                        /*if (mobile.isConnected() || wifi.isConnected()
					|| wimax.isConnected()) {
				Log.d("network", "isConnected");
				isConnected = true;
			} else {
				Log.d("network", "not isConnected");
				isConnected = false;
			}*/
    } catch (NullPointerException localNullPointerException) {
      Log.d("network", "not NullPointerException");
      return true;
    }
    return true;
  }
}