package com.hw.oh.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.hw.oh.temp.R;

import java.util.Map;

import static com.hw.oh.utility.Constant.LOG;

/**
 * GE Team - NMP AP 
 *
 * @author GIL. K
 * @since 1.0
 */
public class ServerRequestUtils {

    private static ServerRequestThread mServerRequestThread;
    private static ServerRequestGETThread mServerRequestGETThread;
    
    // GM2 버전에서 파일업로드 사용안함. 필요시 web server의 controller 구현후 사용할것.
    //private static ServerUploadFileThread mServerUploadFileThread;
    //private static ServerUploadFileByHttpClientThread mServerUploadFileByHttpClientThread;
    
    private static Toast toast;
    // TODO 테스트(반복시도 1회)
    private static int RETRY = 1;

    /**
     * retry 5 attempt (default) (POST)
     *
     * @param uri
     * @param params
     * @param mHandler
     * @return
     */
    public static ServerRequestThread requestRetry(final Context context, final String uri, Map<String, String> params, Handler mHandler) {
        
    	String serverUrl = uri;
        return callThred(context, RETRY, serverUrl, params, mHandler);
    }

    public static ServerRequestThread requestRetry(final Context context, final String uri, Map<String, String> params) {
    	
        return requestRetry(context, uri, params, null);
    }

    public static ServerRequestThread requestRetry(final Context context, String serverDomain, final String uri, Map<String, String> params, Handler mHandler) {

        String serverUrl = serverDomain + uri;
        return callThred(context, 5, serverUrl, params, mHandler);
    }
    
    /**
     * one rquest (POST)
     *
     * @param uri
     * @param params
     * @param mHandler
     * @return
     */
    public static ServerRequestThread request(final Context context, final String uri, Map<String, String> params, Handler mHandler) {

        String serverUrl = uri;
        return callThred(context, RETRY, serverUrl, params, mHandler);
    }
    
    // 시도 횟수 조절 - retryCount 변수(LYS)
    public static ServerRequestThread request(final Context context, final String uri, Map<String, String> params, Handler mHandler, int retryCount) {

        String serverUrl = uri;
        return callThred(context, retryCount, serverUrl, params, mHandler);
    }

    public static ServerRequestThread request(final Context context, final String uri, Map<String, String> params) {
    	
        return request(context, uri, params, null);
    }

    public static ServerRequestThread request(final Context context, String serverDomain, final String uri, Map<String, String> params, Handler mHandler) {

        String serverUrl = serverDomain + uri;
        return callThred(context, RETRY, serverUrl, params, mHandler);
    }
    
    // 시도 횟수 조절 - retryCount 변수(LYS)
    public static ServerRequestThread request(final Context context, String serverDomain, final String uri, Map<String, String> params, Handler mHandler, int retryCount) {
    	
    	String serverUrl = serverDomain + uri;
    	return callThred(context, retryCount, serverUrl, params, mHandler);
    }
    
    /**
     * one rquest (GET)
     *
     * @param uri
     * @param params
     * @param mHandler
     * @return
     */
    public static ServerRequestGETThread requestGET(final Context context, final String uri, Map<String, String> params, Handler mHandler) {
    	
        String serverUrl =  uri;
        return callGETThred(context, RETRY, serverUrl, params, mHandler);
    }
    
    // 시도 횟수 조절 - retryCount 변수(LYS)
    public static ServerRequestGETThread requestGET(final Context context, final String uri, Map<String, String> params, Handler mHandler, int retryCount) {
    	
        String serverUrl =  uri;
        return callGETThred(context, retryCount, serverUrl, params, mHandler);
    }
    
    public static ServerRequestGETThread requestGET(final Context context, String serverDomain, final String uri, Map<String, String> params, Handler mHandler) {
    	
    	String serverUrl = serverDomain + uri;
        return callGETThred(context, RETRY, serverUrl, params, mHandler);
    }
    
    // 시도 횟수 조절 - retryCount 변수(LYS)
    public static ServerRequestGETThread requestGET(final Context context, String serverDomain, final String uri, Map<String, String> params, Handler mHandler, int retryCount) {
    	
    	String serverUrl = serverDomain + uri;
    	return callGETThred(context, retryCount, serverUrl, params, mHandler);
    }
    
    /**
     * POST 용 Thread 공통 호출
     * @param context
     * @param attempts
     * @param serverUrl
     * @param params
     * @param mHandler
     * @return
     */
    public static ServerRequestThread callThred(final Context context, int attempts, final String serverUrl, Map<String, String> params, Handler mHandler) {

        if(!isConnect(context))
            return null;

        try {
            mServerRequestThread = new ServerRequestThread(context, attempts, serverUrl, params, mHandler);
            mServerRequestThread.start();

        } catch (Exception e) {
            Log.e(LOG, "server request error : " + e.getMessage());
            mServerRequestThread.interrupt();
        }
        return mServerRequestThread;
    }

    /**
     * POST 용 Thread 공통 호출
     * @param context
     * @param attempts
     * @param serverUrl
     * @param params
     * @param mHandler
     * @return
     */
    public static ServerRequestGETThread callGETThred(final Context context, int attempts, final String serverUrl, Map<String, String> params, Handler mHandler) {

        if(!isConnect(context))
            return null;

        try {
        	mServerRequestGETThread = new ServerRequestGETThread(context, attempts, serverUrl, params, mHandler);
        	mServerRequestGETThread.start();
            
        } catch (Exception e) {
            Log.e(LOG, "server request error : " + e.getMessage());
            mServerRequestGETThread.interrupt();
        }
        return mServerRequestGETThread;
    }

    /**
     *
     *  UPLOAD FILE
     *  - GM2 버전에서 파일업로드 사용안함. 필요시 web server의 controller 구현후 사용할것.
     *  
     * @param context
     * @param fileName
     * @param params
     * @param mHandler
     * @return
     */
	/*public static boolean uploadFile(final Context context, final String fileName, Map<String, String> params, Handler mHandler) {
	
	    if(!isConnect(context))
			return false;
	
	    try {
	    	mServerUploadFileThread = new ServerUploadFileThread(context, 2, fileName, params, mHandler);
	    	mServerUploadFileThread.start();
	
	        return true;
	    } catch (Exception e) {
	        Log.e(LOG, "uploadFile error : " + e.getMessage());
	
	        return false;
	    }
	}
	public static boolean uploadFile(final Context context, final String fileName, Map<String, String> params) {
	
		return uploadFile(context, fileName, params, null);
	}*/

   /* *//**
     *
     * UPLOAD FILE ByHttpClient
     * - GM2 버전에서 파일업로드 사용안함. 필요시 web server의 controller 구현후 사용할것.
     *
     * @param context
     * @param fileName
     * @param params
     * @param mHandler
     * @return
     *//*
    public static boolean uploadFileByHttpClient(final Context context, final String fileName, Map<String, String> params, Handler mHandler) {

        if(!isConnect(context))
            return false;

        try {
            mServerUploadFileByHttpClientThread = new ServerUploadFileByHttpClientThread(context, 2, fileName, params, mHandler);
            mServerUploadFileByHttpClientThread.start();

            return true;
        } catch (Exception e) {
            Log.e(LOG, "uploadFile error : " + e.getMessage());

            return false;
        }
    }
    public static boolean uploadFileByHttpClient(final Context context, final String fileName, Map<String, String> params) {

        return uploadFileByHttpClient(context, fileName, params, null);
    }*/

    /**
     * check connect
     *
     * @param context
     */
    private static boolean isConnect(final Context context){
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
        
//        Log.d("ServerRequestUtils", "연결상태 확인2 : " + isOnline());
        
//        if(!isOnline()) {
//        	return connectionError(context, IConstants.ERROR_NETWORK);
        
//        }
        
        if(!isMobile && !isWifi && !isLte) {
            return connectionError(context, Constant.ERROR_NETWORK);
        }
        return true;
    }
    
    /**
     * 
     * LYS 테스트
     * 
     * @return
     */
//    public static boolean isOnline() {
//        Runtime runtime = Runtime.getRuntime();
//        try {
//        	// 구글 DNS(e.g. 8.8.8.8)
//            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
//            int     exitValue = ipProcess.waitFor();
//            return (exitValue == 0);
//        } catch (IOException e)          { e.printStackTrace(); } 
//          catch (InterruptedException e) { e.printStackTrace(); }
//        
//        return false;
//    }

    public static boolean connectionError(final Context context, int error){
    	
    	// 토스트 중복 방지(LYS)
    	if(toast == null){
    		toast = Toast.makeText(context, context.getResources().getString(R.string.error_network), Toast.LENGTH_LONG);
    	} else {
    		toast.cancel();
    		toast = Toast.makeText(context, context.getResources().getString(R.string.error_network), Toast.LENGTH_LONG);
    	}
    	toast.show();
    	
//        try {
//
//            Intent intent = new Intent(context, NoConnectActivity.class);
//            intent.putExtra(IConstants.DISPLAY_CONNECT_ERROR, error);
//            PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//            pi.send();
//
//        } catch (PendingIntent.CanceledException e) {
//            Log.e(TAG, "Exception connectionError  : "+e.getMessage());
//        }
        return false;
    }
}