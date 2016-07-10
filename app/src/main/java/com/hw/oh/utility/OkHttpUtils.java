package com.hw.oh.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.hw.oh.temp.R;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * OkHttpUtill
 *
 * @author hwoh
 * @since 1.0
 */
public class OkHttpUtils {
    // 네트워크 에러코드
    public static final int ERROR_NETWORK = 1;
    public static final int ERROR_SERVER = 2;
    public static final int ERROR_UNKNOWN = 3;

    private static Toast toast;
    // HttpClient
    public static OkHttpClient okHttpClient = new OkHttpClient().
            newBuilder().addNetworkInterceptor(new StethoInterceptor()).
            connectTimeout(5, TimeUnit.SECONDS).build();

    /**
     * 동기 요청 (GET, POST)
     *
     * @param context
     * @param serverDomain
     * @param uri
     * @return String
     */
    public static String get(final Context context, String serverDomain, final String uri) throws IOException {
        if(!isConnect(context))
            return null;

        String serverUrl = serverDomain + uri;
        Request request = new Request.Builder()
                .url(serverUrl)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();

        return response.body().string();
    }
    public static String post(final Context context, String serverDomain, final String uri, RequestBody formBody) throws IOException {
        if(!isConnect(context))
            return null;

        String serverUrl = serverDomain + uri;
        Request request = new Request.Builder()
                .url(serverUrl)
                .post(formBody)
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    /**
     * 비동기 요청 (GET, POST)
     *
     * @param context
     * @param serverDomain
     * @param uri
     * @param callback
     * @return Call
     */
    public static Call get(final Context context, String serverDomain, final String uri, Callback callback) throws IOException {
        if(!isConnect(context))
            return null;

        String serverUrl = serverDomain + uri;
        Request request = new Request.Builder()
                .url(serverUrl)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call post(final Context context, String serverDomain, final String uri, RequestBody formBody, Callback callback) throws IOException {
        if(!isConnect(context))
            return null;

        String serverUrl = serverDomain + uri;
        Request request = new Request.Builder()
                .url(serverUrl)
                .post(formBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
        return call;
    }

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
            return connectionError(context, ERROR_NETWORK);
        }
        return true;
    }
    public static boolean connectionError(final Context context, int error){

    	// 토스트 중복 방지(LYS)
    	if(toast == null){
    		toast = Toast.makeText(context, context.getResources().getString(R.string.error_network), Toast.LENGTH_LONG);
    	} else {
    		toast.cancel();
    		toast = Toast.makeText(context, context.getResources().getString(R.string.error_network), Toast.LENGTH_LONG);
    	}
    	toast.show();
        return false;
    }
}