package com.hw.oh.temp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.InfoExtra;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by oh on 2015-02-01.
 */
public class GenderActivity extends Activity implements View.OnClickListener {
  private static final String TAG = "GenderActivity";
  private static final boolean DBUG = true;
  private static final boolean INFO = true;
  private Button mBtnMan, mBtnWoman;

  //Utill
  private HYFont mFont;
  private RequestQueue mRequestQueue;
  private InfoExtra mInfoExtra;
  private HYPreference mPref;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gender);
    //Utill
    mRequestQueue = Volley.newRequestQueue(this);
    mInfoExtra = new InfoExtra(this);
    mPref = new HYPreference(this);
    mFont = new HYFont(this);

    ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
    mFont.setGlobalFont(root);

    //View
    mBtnMan = (Button) findViewById(R.id.btnMan);
    mBtnMan.setOnClickListener(this);
    mBtnWoman = (Button) findViewById(R.id.btnWoman);
    mBtnWoman.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btnMan:
        requestUniqueID(0);

        break;
      case R.id.btnWoman:
        requestUniqueID(1);
        break;
    }
  }


  public void requestUniqueID(final int gender) {
    if (INFO)
      Log.i(TAG, "requestUserInfoSend()");
    String url = "http://ohy3264.cafe24.com/Anony/api/insertMember.php";
    StringRequest request = new StringRequest(Request.Method.POST, url,
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {
            Log.d(TAG, "onResponse :: " + response);
            Message msg = IntentHandler.obtainMessage();
            IntentHandler.sendMessage(msg);

            mPref.put(mPref.KEY_GENDER, Integer.toString(gender));

          }
        }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError e) {
        Log.d(TAG,
            "onErrorResponse :: " + e.getLocalizedMessage());
        e.printStackTrace();
      }
    }) {
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        // TODO Auto-generated method stub
        if (DBUG) {

        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("MODE", "InsertUser");
        params.put("ANDROID_ID", mInfoExtra.getAndroidID());
        params.put("GENDER", Integer.toString(gender));
        return params;
      }
    };
    mRequestQueue.add(request);
  }


  final Handler IntentHandler = new Handler() {
    public void handleMessage(Message msg) {
      Intent intent_main = new Intent(GenderActivity.this, MainActivity.class);
      intent_main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent_main);
      overridePendingTransition(0, 0);
      finish();
    }

  };
}
