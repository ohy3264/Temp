package com.hw.oh.temp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hw.oh.billing.IabHelper;
import com.hw.oh.billing.IabResult;
import com.hw.oh.billing.Inventory;
import com.hw.oh.billing.Purchase;
import com.hw.oh.sqlite.DBManager;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYNetworkInfo;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.InfoExtra;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by oh on 2015-02-01.
 */
public class IntroActivity extends Activity {
  public static final String TAG = "IntroActivity";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;

  private IabHelper mHelper;

  private InfoExtra mInfoExtra;
  private RequestQueue mRequestQueue;
  private HYFont mFont;
  private HYPreference mPref;
  private HYNetworkInfo mNet;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_intro);
    //util
    mRequestQueue = Volley.newRequestQueue(this);
    mInfoExtra = new InfoExtra(this);
    mFont = new HYFont(this);
    mPref = new HYPreference(this);
    mNet = new HYNetworkInfo(this);

    /*//InApp
    String base64EncodedPublicKey = getResources().getString(R.string.base64EncodedPublicKey);
    mHelper = new IabHelper(this, base64EncodedPublicKey);
    mHelper.enableDebugLogging(true, "IAB");
    mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
      public void onIabSetupFinished(IabResult result) {
        if (INFO)
          Log.i(TAG, "인앱구매 준비 : " + result.isSuccess());
        if (!result.isSuccess()) {
          Toast.makeText(getApplicationContext(), "구글 결재 초기화 오류", Toast.LENGTH_SHORT).show();
        } else {
          if (INFO)
            Log.i(TAG, "구매정보 조회 : " + result.isSuccess());
          mHelper.queryInventoryAsync(mQueryFinishedListener);
        }
      }
    });*/

    //View
    ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
    mFont.setGlobalFont(root);
    dbinit();

    if (!mNet.networkgetInfo()) {
      requestCallRest_Guide();
      requestUniqueID();
    } else {
      new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
          if (mPref.getValue(mPref.KEY_PASS_STATE, false)) {
            Log.i(TAG, "PassWord");
            Intent intent_pass = new Intent(IntroActivity.this, PassActivity.class);
            intent_pass.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent_pass);
            overridePendingTransition(0, 0);
            finish();
          } else {
            Log.i(TAG, "IntentMain");
            Intent intent_main = new Intent(IntroActivity.this, MainActivity.class);
            intent_main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent_main);
            overridePendingTransition(0, 0);
            finish();
          }
        }
      }, 1000);
    }
  }

  public void dbinit() {
    if (INFO)
      Log.i(TAG, "dbinit()");
    DBManager dbManager = new DBManager(this);
    dbManager.getWritableDatabase();
  }

  private IabHelper.QueryInventoryFinishedListener
      mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
      if (result.isFailure()) {
        // handle error
        if (DBUG)
          Log.d(TAG, "구매 이력조회 실패: " + result);
        return;
      }
      Purchase premiumPurchase = inventory.getPurchase(Constant.ADDMOB_REMOVE);
      if (premiumPurchase != null) {
        if (INFO)
          Log.i(TAG, "구매 이력이 있음 ");
        if (premiumPurchase.getPurchaseState() == 0) {
          if (DBUG)
            Log.i(TAG, "구매");
          Constant.ADMOB = false;
        } else if (premiumPurchase.getPurchaseState() == 1) {
          if (INFO)
            Log.i(TAG, "취소");
        } else if (premiumPurchase.getPurchaseState() == 2) {
          if (INFO)
            Log.i(TAG, "환불");
        }
      }
      if (DBUG)
        Log.d("myLog", "Query inventory was successful.");
    }
  };

  public void requestUniqueID() {
    if (INFO)
      Log.i(TAG, "requestUserInfoSend()");
    String url = "http://ohy3264.cafe24.com/Anony/api/memberCheck.php";
    StringRequest request = new StringRequest(Request.Method.POST, url,
        new Response.Listener<String>() {
          @Override
          public void onResponse(final String response) {
            Log.d(TAG, "onResponse :: " + response.toString());

            new Handler().postDelayed(new Runnable() {
              @Override
              public void run() {
                try {
                  JSONObject json = new JSONObject(response);
                  Message msg = IntentHandler.obtainMessage();
                  Bundle bundle = new Bundle();
                  bundle.putString("IntentFlag", json.getString("IntentFlag"));
                  bundle.putString("Gender", json.getString("Gender"));
                  msg.setData(bundle);
                  IntentHandler.sendMessage(msg);
                } catch (JSONException e) {
                  e.printStackTrace();

                } catch (Exception e) {
                  e.printStackTrace();
                }
              }
            }, 500);
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
        params.put("MODE", "LoginCheck");
        params.put("ANDROID_ID", mInfoExtra.getAndroidID());
        return params;
      }

    };
    request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 1, 1.0f));
    mRequestQueue.add(request);
  }

  final Handler IntentHandler = new Handler() {
    public void handleMessage(Message msg) {
      switch (msg.getData().getString("IntentFlag")) {
        case "IntentGender":
          Log.i(TAG, "IntentGender");
          Intent intent_gender = new Intent(IntroActivity.this, GenderActivity.class);
          intent_gender.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent_gender);
          overridePendingTransition(0, 0);
          finish();

          break;
        case "IntentMain":
          if (mPref.getValue(mPref.KEY_PASS_STATE, false)) {
            Log.i(TAG, "PassWord");
            mPref.put(mPref.KEY_GENDER, msg.getData().getString("Gender"));
            Intent intent_pass = new Intent(IntroActivity.this, PassActivity.class);
            intent_pass.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent_pass);
            overridePendingTransition(0, 0);
            finish();
          } else {
            Log.i(TAG, "IntentMain");
            mPref.put(mPref.KEY_GENDER, msg.getData().getString("Gender"));
            Intent intent_main = new Intent(IntroActivity.this, MainActivity.class);
            intent_main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent_main);
            overridePendingTransition(0, 0);
            finish();
          }
          break;
        default:
          if (mPref.getValue(mPref.KEY_PASS_STATE, false)) {
            Log.i(TAG, "PassWord");
            mPref.put(mPref.KEY_GENDER, msg.getData().getString("Gender"));
            Intent intent_pass = new Intent(IntroActivity.this, PassActivity.class);
            intent_pass.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent_pass);
            overridePendingTransition(0, 0);
            finish();
          } else {
            Log.i(TAG, "IntentMain");
            mPref.put(mPref.KEY_GENDER, msg.getData().getString("Gender"));
            Intent intent_main = new Intent(IntroActivity.this, MainActivity.class);
            intent_main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent_main);
            overridePendingTransition(0, 0);
            finish();
          }
          break;
      }
    }
  };

  public void requestCallRest_Guide() {
    if (INFO)
      Log.i(TAG, "requestCallRest_Guide()");
    String url = "http://ohy3264.cafe24.com/Anony/api/GuideMsgs.php";
    StringRequest request = new StringRequest(Request.Method.POST, url,
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {
            Log.d(TAG, "onResponse :: " + response.toString());
            try {
              JSONObject jsonObj = new JSONObject(response);
              Constant.GUIDE_MSG1 = jsonObj.get("talkMsg").toString();
              Constant.GUIDE_MSG2 = jsonObj.get("calendarMsg").toString();
              Constant.GUIDE_MSG3 = jsonObj.get("newworkMsg").toString();
            } catch (Exception e) {
              Log.i(TAG, e.toString());
            }
          }
        }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError e) {
        Log.d(TAG,
            "onErrorResponse :: " + e.getLocalizedMessage());
        e.printStackTrace();
      }
    });
    request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 1, 1.0f));
    mRequestQueue.add(request);
  }


}
