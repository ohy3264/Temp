package com.hw.oh.temp;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hw.oh.fragment.Fragment_Setting;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYFont;

/**
 * Created by oh on 2015-02-26.
 */
public class SettingActivity extends BaseActivity {

  // Log
  private static final String TAG = "SettingActivity";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;


  // ActionBar
  private Toolbar mToolbar;


  //Fragment
  private Fragment_Setting mFragment_Setting;

  // util
  private HYFont mFont;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);
    // 구글 통계
    Tracker mTracker = ((ApplicationClass) getApplication()).getDefaultTracker();
    mTracker.setScreenName("설정화면_(SettingActivity)");
    mTracker.send(new HitBuilders.AppViewBuilder().build());

    //Util
    mFont = new HYFont(this);
    ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
    mFont.setGlobalFont(root);

    //ActionBar
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    if (mToolbar != null) {
      mToolbar.setTitle("설정");
      setSupportActionBar(mToolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      mFont.setGlobalFont((ViewGroup) mToolbar);
    }

    mFragment_Setting = new Fragment_Setting();
    getFragmentManager().beginTransaction().replace(R.id.container, mFragment_Setting).commit();

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
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        break;

    }
    return super.onOptionsItemSelected(item);
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

