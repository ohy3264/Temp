package com.hw.oh.temp;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
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

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 로딩화면
 */
public class IntroActivity extends BaseActivity {
  public static final String TAG = "IntroActivity";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;

  private IabHelper mHelper;

  private InfoExtra mInfoExtra;
  private HYFont mFont;
  private HYPreference mPref;
  private HYNetworkInfo mNet;
  private ImageView mIntroDot;
  /**
   * ATTENTION: This was auto-generated to implement the App Indexing API.
   * See https://g.co/AppIndexing/AndroidStudio for more information.
   */
  private GoogleApiClient client;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_intro);
    //util
    mInfoExtra = new InfoExtra(this);
    mFont = new HYFont(this);
    mPref = new HYPreference(this);
    mNet = new HYNetworkInfo(this);

    //InApp
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
    });

    //View
    ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
    mFont.setGlobalFont(root);

    // 구글 통계
    Tracker mTracker = ((ApplicationClass) getApplication()).getDefaultTracker();
    mTracker.setScreenName("스플래쉬");
    mTracker.send(new HitBuilders.AppViewBuilder().build());

    dbinit();

// 인트로 애니메이션
    root.post(new Runnable() {
      @Override
      public void run() {
        introAnimation();
      }
    });


    if (mNet.networkgetInfo()) {
      //requestCallRest_Guide();
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
      }, 3000);
    }
    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
  }

  private void introAnimation() {
    mIntroDot = (ImageView) findViewById(R.id.intro_dot);

    AnimationDrawable animation = new AnimationDrawable();
    animation.addFrame(getResources().getDrawable(R.drawable.intro_img1), 700); //ms(밀리세컨드 1/1000초)
    animation.addFrame(getResources().getDrawable(R.drawable.intro_img2), 700);
    animation.addFrame(getResources().getDrawable(R.drawable.intro_img3), 700);
    animation.addFrame(getResources().getDrawable(R.drawable.intro_img1), 700);

    animation.setOneShot(false);
    mIntroDot.setBackgroundDrawable(animation);
    animation.start();
  }


  /**
   * 초기 db 세팅
   */

  public void dbinit() {
    if (INFO)
      Log.i(TAG, "dbinit()");
    DBManager dbManager = new DBManager(this);
    dbManager.getWritableDatabase();
  }

  /**
   * 인앱결제 상태 체크
   */

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

  /**
   * 로그인 체크
   */

  public void requestUniqueID() {
    if (INFO)
      Log.i(TAG, "requestUserInfoSend()");
    String url = "http://ohy3264.cafe24.com/Anony/api/memberCheck.php";
    try {
      OkHttpClient client = new OkHttpClient();
      RequestBody formBody = new FormBody.Builder()
              .add("MODE", "LoginCheck")
              .add("ANDROID_ID", mInfoExtra.getAndroidID())
              .build();

      Request request = new Request.Builder()
              .url(url)
              .post(formBody)
              .build();

      Response response = client.newCall(request).execute();

        JSONObject json = new JSONObject(response.body().toString());
        Message msg = IntentHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("IntentFlag", json.getString("IntentFlag"));
        bundle.putString("Gender", json.getString("Gender"));
        msg.setData(bundle);
        IntentHandler.sendMessage(msg);
      }catch (Exception e){
      Log.d(TAG, "requestCall_HateState - exception :: " + e.toString());
    }
  }

  /**
   * 로딩화면에서 이동분기 처리
   */

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


  /**
   * 가이드 메시지 호출
   */

  public void requestCallRest_Guide() {
    if (INFO)
      Log.i(TAG, "requestCallRest_Guide()");
    String url = "http://ohy3264.cafe24.com/Anony/api/GuideMsgs.php";
    try {
      OkHttpClient client = new OkHttpClient();
      RequestBody formBody = new FormBody.Builder()
              .add("MODE", "LoginCheck")
              .add("ANDROID_ID", mInfoExtra.getAndroidID())
              .build();

      Request request = new Request.Builder()
              .url(url)
              .post(formBody)
              .build();

      Response response = client.newCall(request).execute();

      JSONObject jsonObj = new JSONObject(response.body().toString());
      Constant.GUIDE_MSG1 = jsonObj.get("talkMsg").toString();
      Constant.GUIDE_MSG2 = jsonObj.get("calendarMsg").toString();
      Constant.GUIDE_MSG3 = jsonObj.get("newworkMsg").toString();
    }catch (Exception e){
      Log.d(TAG, "requestCall_HateState - exception :: " + e.toString());
    }
  }


  @Override
  public void onStart() {
    super.onStart();
    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client.connect();
    // 구글 통계
    GoogleAnalytics.getInstance(this).reportActivityStart(this);
    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    Action viewAction = Action.newAction(
            Action.TYPE_VIEW, // TODO: choose an action type.
            "Intro Page", // TODO: Define a title for the content shown.
            // TODO: If you have web page content that matches this app activity's content,
            // make sure this auto-generated web page URL is correct.
            // Otherwise, set the URL to null.
            Uri.parse("http://host/path"),
            // TODO: Make sure this auto-generated app deep link URI is correct.
            Uri.parse("android-app://com.hw.oh.temp/http/host/path")
    );
    AppIndex.AppIndexApi.start(client, viewAction);
  }

  @Override
  public void onStop() {
    super.onStop();
    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    Action viewAction = Action.newAction(
            Action.TYPE_VIEW, // TODO: choose an action type.
            "Intro Page", // TODO: Define a title for the content shown.
            // TODO: If you have web page content that matches this app activity's content,
            // make sure this auto-generated web page URL is correct.
            // Otherwise, set the URL to null.
            Uri.parse("http://host/path"),
            // TODO: Make sure this auto-generated app deep link URI is correct.
            Uri.parse("android-app://com.hw.oh.temp/http/host/path")
    );
    AppIndex.AppIndexApi.end(client, viewAction);
    // 구글 통계
    GoogleAnalytics.getInstance(this).reportActivityStop(this);
    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client.disconnect();
  }
}
