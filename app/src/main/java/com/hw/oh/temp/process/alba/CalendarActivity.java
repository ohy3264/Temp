package com.hw.oh.temp.process.alba;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hw.oh.fragment.Fragment_Calendar;
import com.hw.oh.model.PartTimeInfo;
import com.hw.oh.temp.ApplicationClass;
import com.hw.oh.temp.BaseActivity;
import com.hw.oh.temp.R;
import com.hw.oh.utility.Constant;


/**
 * Created by oh on 2015-02-26. 달력
 */
public class CalendarActivity extends BaseActivity {
  // Log
  private static final String TAG = "CalendarActivity";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;

  //Fragment
  private Fragment_Calendar mFragment_Calendar;

  //Crouton
  private PartTimeInfo mPartInfoData_Intent;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_calendar);
    //Utill
    mPartInfoData_Intent = (PartTimeInfo) getIntent().getSerializableExtra("ObjectData");
    setToolbar(mPartInfoData_Intent.getAlbaname());

    mFragment_Calendar = new Fragment_Calendar(mPartInfoData_Intent);
    getSupportFragmentManager().beginTransaction().replace(R.id.container, mFragment_Calendar).commit();

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

    // 구글 통계
    Tracker mTracker = ((ApplicationClass) getApplication()).getDefaultTracker();
    mTracker.setScreenName("달력 화면 (CalendarActivity)");
    mTracker.send(new HitBuilders.AppViewBuilder().build());
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

