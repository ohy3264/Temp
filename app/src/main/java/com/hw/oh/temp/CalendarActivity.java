package com.hw.oh.temp;

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

import com.hw.oh.fragment.Fragment_Calendar;
import com.hw.oh.model.PartTimeInfo;
import com.hw.oh.model.WeekWeatherItem;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import java.util.ArrayList;

import zh.wang.android.apis.yweathergetter4a.YahooWeather;


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
  private ArrayList<WeekWeatherItem> mWeekWeatherDataList = new ArrayList<WeekWeatherItem>();

  //Crouton
  private View mCroutonView;
  private TextView mTxtCrouton;
  private CroutonHelper mCroutonHelper;

  private PartTimeInfo mPartInfoData_Intent;

  // ActionBar
  private Toolbar mToolbar;

  // Utill
  private ProgressDialog mProgressDialog;
  private HYFont mFont;
  private HYPreference mPref;


  private YahooWeather mYahooWeather = YahooWeather.getInstance(5000, 5000, true);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_calendar);
    //Utill
    mFont = new HYFont(this);
    ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
    mFont.setGlobalFont(root);
    mPref = new HYPreference(this);

    mPartInfoData_Intent = (PartTimeInfo) getIntent().getSerializableExtra("ObjectData");
    Log.i(TAG, mPartInfoData_Intent.getAlbaname());
    //ActionBar
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    if (mToolbar != null) {
      mToolbar.setTitle(mPartInfoData_Intent.getAlbaname());
      setSupportActionBar(mToolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      mFont.setGlobalFont((ViewGroup) mToolbar);
    }

    //Crouton
    mCroutonHelper = new CroutonHelper(this);
    mCroutonView = getLayoutInflater().inflate(
        R.layout.crouton_custom_view, null);
    mTxtCrouton = (TextView) mCroutonView.findViewById(R.id.txt_crouton);

    mFragment_Calendar = new Fragment_Calendar(mPartInfoData_Intent);
    getFragmentManager().beginTransaction().replace(R.id.container, mFragment_Calendar).commit();

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
}

