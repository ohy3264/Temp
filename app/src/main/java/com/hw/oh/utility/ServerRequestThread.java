package com.hw.oh.utility;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.io.OutputStream;
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
public class ServerRequestThread extends Thread {

	private String mResult;
	private String TAG = "ServerRequestThread";

	private Context mContext = null;
	private String serverUrl = null;
	private Map<String, String> params = null;
    private Handler mHandler = null;
    private int maxAttempts;

    private static final int BACKOFF_MILLI_SECONDS = 1500;

	public ServerRequestThread(final Context context, int attempts, String serverUrl, Map<String, String> params, Handler mHandler){
		this.mContext = context;
		this.serverUrl = serverUrl;
		this.params = params;
		this.mHandler = mHandler;
		maxAttempts = attempts;
	}

	/**
	 * POST & GET request to the server.
	 */
	@SuppressWarnings("deprecation")
	public void run(){
        long backoff = BACKOFF_MILLI_SECONDS;

		URL url = null;
        try {
            url = new URL(serverUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + url);
        }

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

        // 반복 시도
        System.setProperty("http.keepAlive", "false");
        
        byte[] bytes = bodyBuilder.toString().getBytes();

        // TODO 테스트(반복횟수 관련) 기본 : maxAttempts, 변경 : 1
        for (int retry = 1; retry <= 1; retry++) {
            HttpURLConnection conn = null;
	        try {
	            Log.d(TAG, "(Trying attempt " + retry + "/" + maxAttempts + ") request to : " + serverUrl + "?" + bodyBuilder.toString());
	            conn = (HttpURLConnection) url.openConnection();
	            conn.setDoOutput(true);
	            conn.setUseCaches(false);
	            conn.setConnectTimeout(5*BACKOFF_MILLI_SECONDS);
	            conn.setReadTimeout(5*BACKOFF_MILLI_SECONDS);
	            //conn.setFixedLengthStreamingMode(bytes.length);
	            conn.setRequestMethod("POST");
	            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
	            
	            if (Build.VERSION.SDK != null
	            		&& Build.VERSION.SDK_INT > 13) {
	            	conn.setRequestProperty("Connection", "close");
	            }
	            
	            // post the request
	            OutputStream out = conn.getOutputStream();
	            out.write(bytes);
	            out.close();

	            // handle the response
	            int status = conn.getResponseCode();
	            if (status == HttpURLConnection.HTTP_OK) {
	            	if(mHandler != null){

	            		InputStream is = null;
	            		ByteArrayOutputStream baos = null;
		            	try{
		            		is = conn.getInputStream();
		            		baos = new ByteArrayOutputStream();
		            	    byte[] byteBuffer = new byte[1024];
		            	    byte[] byteData = null;
		            	    int nLength = 0;
		            	    while((nLength = is.read(byteBuffer)) > 0) {
		            	    	baos.write(byteBuffer, 0, nLength);
		            	    }
		            	    byteData = baos.toByteArray();
		            	    mResult = new String(byteData);

		            	    Message msg = mHandler.obtainMessage();

		            	    msg.obj = mResult;
		            	    mHandler.sendMessage(msg);

		            	    break;
		            	}catch(Exception e){
//		            		returnError(mHandler, "[네트워크 오류]\n Server reponse OutputStream : "+ e.toString());
		            		returnError(mHandler, Constant.ERROR_SERVER);
		            	}finally {
		    	            if (is != null) {
		    	            	is.close();
		    	            }
		    	            if (baos != null) {
		    	            	baos.close();
		    	            }
		    	        }
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
	        	e.printStackTrace();
	        	Log.w(TAG, "EOFException : " + e.toString());
	        	maxAttempts = 5;
	        	try {
                    Thread.sleep(backoff * 2);
                } catch (InterruptedException e1) {}
	        }catch (Exception e) {
	        	Log.w(TAG, "ServerRequestThread exception : " + e.toString());

	        	if (retry == maxAttempts) {
	        		String error = e.toString();
	        		if(error.indexOf(":") > -1 ){
	        			error = error.substring(0, error.indexOf(":"));
	        		}
		        	returnError(mHandler, Constant.ERROR_UNKNOWN);
                    break;
                }

	        	try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    Log.e(TAG, "Thread interrupted: abort remaining retries!");
                }
                backoff *= 2;
	        }finally {
	            if (conn != null) {
	                conn.disconnect();
	            }
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
        		
//        		ServerRequestUtils.connectionError(mContext, error);
        		
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