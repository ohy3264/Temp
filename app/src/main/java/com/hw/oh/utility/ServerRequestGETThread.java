package com.hw.oh.utility;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;

/**
 * GE Team - NMP AP
 *
 * @author GIL. K
 * @since 1.0
 */
public class ServerRequestGETThread  extends Thread {

	private String TAG = "ServerRequestGETThread";

	private Context mContext = null;
	private String serverUrl = null;
	private Map<String, String> params = null;
    private Handler mHandler = null;
    private int maxAttempts;

    private static final int BACKOFF_MILLI_SECONDS = 1500;

	public ServerRequestGETThread(final Context context, int attempts, String serverUrl, Map<String, String> params, Handler mHandler){
		this.mContext = context;
		this.serverUrl = serverUrl;
		this.params = params;
		this.mHandler = mHandler;
		maxAttempts = attempts;
	}
	
	/**
	 * POST & GET request to the server.
	 */
	public void run(){
        long backoff = BACKOFF_MILLI_SECONDS;

        // params 정리
        StringBuilder bodyBuilder = new StringBuilder();
        if( params != null ){
	        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();

	        while (iterator.hasNext()) {
	            Map.Entry<String, String> param = iterator.next();
	            bodyBuilder.append(param.getKey()).append('=').append(param.getValue());

	            if (iterator.hasNext()) {
	                bodyBuilder.append('&');
	            }
	        }
        }
        
		URL url = null;
        try {
            url = new URL(serverUrl+"?"+bodyBuilder.toString());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + url);
        }

        // 반복 시도
        System.setProperty("http.keepAlive", "false");

        for (int retry = 1; retry <= maxAttempts; retry++) {
            HttpURLConnection conn = null;
            BufferedReader br = null;
            
	        try {
	            Log.d(TAG, "(Trying attempt " + retry + "/" + maxAttempts + ") request to : " + serverUrl + "?" + bodyBuilder.toString());
	            conn = (HttpURLConnection) url.openConnection();
	            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	            
	            // handle the response
	            int status = conn.getResponseCode();
	            
	            if (status == HttpURLConnection.HTTP_OK) {
	            	if(mHandler != null){
	    	            
	            		StringBuilder sb = new StringBuilder();
	    	            char[] chars = new char[4*1024];
	    	            int len;
	    	            while((len = br.read(chars))>=0) {
	    	                sb.append(chars, 0, len);
	    	            }
	    	            
	            	    Message msg = mHandler.obtainMessage();
	            	    msg.obj = sb.toString();
	            	    mHandler.sendMessage(msg);
	            	    break;
	                }else{
//	                	Log.d(TAG, "[HTTP_OK] No Handler ");
	            	    break;
	                }
	            }else{
	            	switch(status){
	            		case HttpURLConnection.HTTP_NOT_FOUND :
	                    	Log.e(TAG, "Server reponse error : HTTP_NOT_FOUND " + status);
	                    	break;
	            		case HttpURLConnection.HTTP_GATEWAY_TIMEOUT :
	                    	Log.e(TAG, "Server reponse error : HTTP_GATEWAY_TIMEOUT " + status);
	                    	break;
	                    default:
	                    	Log.e(TAG, "Server reponse error : HTTP_SERVER_ERROR " + status);
	                    	break;
	            	}
		        	if (retry == maxAttempts) {
		        		returnError(mHandler, Constant.ERROR_SERVER);
		            	break;
		        	}
	            }
	        }catch(ConnectException e){
	        	returnError(mHandler, Constant.ERROR_NETWORK);
	        }catch(UnknownHostException e){
	        	returnError(mHandler, Constant.ERROR_NETWORK);
	        }catch(EOFException e){
	        	/*maxAttempts = 5;
	        	try {
                    Thread.sleep(backoff*5);
                } catch (InterruptedException e1) {}*/
	        }catch (Exception e) {
	        	Log.w(TAG, "ServerRequestThread exception : " + e.toString());

	        	/*if (retry == maxAttempts) {
	        		String error = e.toString();
	        		if(error.indexOf(":") > -1 ){
	        			error = error.substring(0, error.indexOf(":"));
	        		}
		        	returnError(mHandler, IConstants.ERROR_UNKNOWN);
                    break;
                }

	        	try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    Log.e(TAG, "Thread interrupted: abort remaining retries!");
                }
                backoff *= 2;*/
	        }finally {
		            try {
			        	if (br != null)        		br.close();
		        		if (conn != null)  			conn.disconnect();
					} catch (IOException e) {}
	        } // try {
        } // for (int retry = 1; retry <= MAX_ATTEMPTS; retry++)
	}

	public void returnError(Handler mHandler, final int error){
		try {
        	if( mHandler != null ){
        		mHandler.post(new Runnable(){
					@Override
					public void run() {
						ServerRequestUtils.connectionError(mContext, error);
					}
        		});
        		
//				ServerRequestUtils.connectionError(mContext, error);

//        		Message msg = mHandler.obtainMessage();
//        		msg.what = -2;
//        	    msg.obj = error;
//        	    mHandler.sendMessage(msg);
        	}
		} catch (Exception e) {
			Log.e(TAG, "Exception returnError  : " + e.getMessage());
		}
    }
}