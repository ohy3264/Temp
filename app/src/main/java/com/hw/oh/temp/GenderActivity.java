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

import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.InfoExtra;

import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by oh on 2015-02-01.
 */
public class GenderActivity extends BaseActivity implements View.OnClickListener {
  private static final String TAG = "GenderActivity";
  private static final boolean DBUG = true;
  private static final boolean INFO = true;
  private Button mBtnMan, mBtnWoman;

  //Utill
  private HYFont mFont;
  private InfoExtra mInfoExtra;
  private HYPreference mPref;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gender);
    //Utill
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
    try {
      OkHttpClient client = new OkHttpClient();
      RequestBody formBody = new FormBody.Builder()
              .add("MODE", "InsertUser")
              .add("ANDROID_ID", mInfoExtra.getAndroidID())
              .add("GENDER", Integer.toString(gender))
              .build();

      Request request = new Request.Builder()
              .url(url)
              .post(formBody)
              .build();

      Response response = client.newCall(request).execute();
      Log.d(TAG, "requestCall_HateState - onResponse :: " + response.toString());
      Message msg = IntentHandler.obtainMessage();
      IntentHandler.sendMessage(msg);
      mPref.put(mPref.KEY_GENDER, Integer.toString(gender));

    }catch (Exception e){
      Log.d(TAG, "requestCall_HateState - exception :: " + e.toString());
    }
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
