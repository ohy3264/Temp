package com.hw.oh.network;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by oh on 2015-02-11.
 */
public class RestClient {
  public static final String BASE_URL = "http://ohy3264.cafe24.com/Anony/api/";

  private static AsyncHttpClient client = new AsyncHttpClient();

  RestClient() {
    client.setURLEncodingEnabled(false);
  }

  public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
    client.get(getAbsoluteUrl(url), params, responseHandler);
  }

  public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
    client.post(getAbsoluteUrl(url), params, responseHandler);
  }

  private static String getAbsoluteUrl(String relativeUrl) {
    return BASE_URL + relativeUrl;
  }
}