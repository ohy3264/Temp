package com.hw.oh.temp;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.InfoExtra;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by oh on 2015-02-02.
 */
public class NewCommentActivity extends BaseActivity implements View.OnClickListener {
  public static final String TAG = "NewCommentActivity";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;
  //View
  private Toolbar mToolbar;
  private EditText mEdtNewPost;
  private ImageButton mBtnFloating;
  //Crouton
  private View mCroutonView;
  private TextView mTxtCrouton;
  private CroutonHelper mCroutonHelper;
  //Flag
  private Result mIntentFlag;
  //Utill
  private InfoExtra mInfoExtra;
  private HYPreference mPref;
  private HYFont mFont;
  private CountDownLatch mCDL_CommentInsert;

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_newcomment);
    // 구글 통계
    Tracker mTracker = ((ApplicationClass) getApplication()).getDefaultTracker();
    mTracker.setScreenName(" 새로운 댓글 쓰기화면_(NewCommentActivity)");
    mTracker.send(new HitBuilders.AppViewBuilder().build());


    //Util
    mInfoExtra = new InfoExtra(this);
    mPref = new HYPreference(this);
    mFont = new HYFont(this);
    ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
    mFont.setGlobalFont(root);
    mBtnFloating = (ImageButton) findViewById(R.id.btnFloating);
    mBtnFloating.setOnClickListener(this);
    //Crouton
    mCroutonHelper = new CroutonHelper(this);
    mCroutonView = getLayoutInflater().inflate(
        R.layout.crouton_custom_view, null);
    mTxtCrouton = (TextView) mCroutonView.findViewById(R.id.txt_crouton);

    //ActionBar
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    if (mToolbar != null) {
      mToolbar.setTitle("Send Comment");
      setSupportActionBar(mToolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      mFont.setGlobalFont((ViewGroup) mToolbar);
    }

    mEdtNewPost = (EditText) findViewById(R.id.edtNewPost);

    // ADmob
    if (Constant.ADMOB) {
      LinearLayout layout = (LinearLayout) findViewById(R.id.mainLayout);
      layout.setVisibility(View.VISIBLE);
      AdView ad = new AdView(this);
      ad.setAdUnitId("ca-app-pub-8578540426651700/9047026070");
      ad.setAdSize(AdSize.BANNER);
      layout.addView(ad);
      AdRequest adRequest = new AdRequest.Builder().build();
      ad.loadAd(adRequest);
    }

  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.btnFloating) {
      if (!mEdtNewPost.getText().toString().isEmpty()) {
        requestNewCommentSend();
      } else {
        mTxtCrouton.setText("아무글이나 써주세요");
        mCroutonHelper.setCustomView(mCroutonView);
        mCroutonHelper.setDuration(1000);
        mCroutonHelper.show();
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_newpost, menu);

    return true;

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();

    }
    return super.onOptionsItemSelected(item);
  }

  public void requestNewCommentSend() {
    if (INFO)
      Log.i(TAG, "requestNewPostSend()");
    mCDL_CommentInsert = new CountDownLatch(1);
    String url = "http://ohy3264.cafe24.com/Anony/api/newCommentSave.php";
    try {
      OkHttpClient client = new OkHttpClient();
      RequestBody formBody = new FormBody.Builder()
              .add("MODE", "NewPostSend")
              .add("BSEQ", getIntent().getStringExtra("Bseq"))
              .add("ANDROID_ID", mInfoExtra.getAndroidID())
              .add("GENDER", mPref.getValue(mPref.KEY_GENDER, "0"))
              .add("NEW_COMMENT", mEdtNewPost.getText().toString())
              .build();
      Request request = new Request.Builder()
              .url(url)
              .post(formBody)
              .build();

      Response response = client.newCall(request).execute();

      Log.d(TAG, "onResponse :: " + response.toString());
      mIntentFlag = new Gson().fromJson(response.body().toString(), Result.class);
      Message msg = IntentHandler.obtainMessage();
      IntentHandler.sendMessage(msg);
      mCDL_CommentInsert.countDown();
    }catch (Exception e){
      Log.d(TAG, "requestNewPostSend - exception :: " + e.toString());
      mCDL_CommentInsert.countDown();
    }

  }

  final Handler IntentHandler = new Handler() {
    public void handleMessage(Message msg) {
      Constant.REFRESH_DETAIL_FLAG = Constant.DETAIL_REQUEST_NEW_COMMENT;
      try {
        mCDL_CommentInsert.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      finish();
    }
  };

  public class Result {
    String BoardInsertResult;

    public String getBoardInsertResult() {
      return BoardInsertResult;
    }

    public void setBoardInsertResult(String boardInsertResult) {
      BoardInsertResult = boardInsertResult;
    }
  }
  @Override
  public void onStart() {
    super.onStart();
    // 구글 통계
    GoogleAnalytics.getInstance(this).reportActivityStart(this);
  };

  @Override
  public void onStop() {
    super.onStop();
    // 구글 통계
    GoogleAnalytics.getInstance(this).reportActivityStop(this);
  };
}
