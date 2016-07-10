package com.hw.oh.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.hw.oh.temp.R;


/**
 * <pre>
 *
 * @author : oh
 * @Day : 2014. 11. 20.
 * @Time : 오전 10:42:30
 * @Explanation : 네트워크 연결 여부확인 </pre>
 */
public class NetworkUtil {
  /**
   * winCheck connect
   *
   * @param context
   */
  public static boolean isConnect(final Context context){
    boolean isMobile = false;
    boolean isWifi = false;
    boolean isLte = false;
    NetworkInfo ni = null;

    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    if(ni != null)  isMobile = ni.isConnected();

    ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    if(ni != null)  isWifi = ni.isConnected();

    ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
    if(ni != null) 	isLte = ni.isConnected();

    Log.d("ServerRequestUtils", "연결상태 확인1 : " + (!isMobile && !isWifi && !isLte));

    if(!isMobile && !isWifi && !isLte) {
  //    return connectionError(context);
      return false;
    }
    return true;
  }
}