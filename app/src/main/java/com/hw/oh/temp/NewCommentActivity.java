package com.hw.oh.temp;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.InfoExtra;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

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
  private RequestQueue mRequestQueue;
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
    //Util
    mInfoExtra = new InfoExtra(this);
    mPref = new HYPreference(this);
    mRequestQueue = Volley.newRequestQueue(this);
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
    StringRequest request = new StringRequest(Request.Method.POST, url,
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {
            Log.d(TAG, "onResponse :: " + response.toString());
            mIntentFlag = new Gson().fromJson(response, Result.class);
            Message msg = IntentHandler.obtainMessage();
            IntentHandler.sendMessage(msg);
            mCDL_CommentInsert.countDown();
          }
        }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError e) {
        Log.d(TAG,
            "onErrorResponse :: " + e.getLocalizedMessage());
        e.printStackTrace();
        mCDL_CommentInsert.countDown();
      }
    }) {
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        // TODO Auto-generated method stub
        if (DBUG) {

        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("MODE", "NewPostSend");
        params.put("BSEQ", getIntent().getStringExtra("Bseq"));
        params.put("ANDROID_ID", mInfoExtra.getAndroidID());
        params.put("GENDER", mPref.getValue(mPref.KEY_GENDER, "0"));
        params.put("NEW_COMMENT", mEdtNewPost.getText().toString());
        return params;
      }

    };
    mRequestQueue.add(request);
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
}
