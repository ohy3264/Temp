package com.hw.oh.temp;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hw.oh.fragment.Fragment_Calculation_Calendar;
import com.hw.oh.model.PartTimeInfo;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYFont;


/**
 * Created by oh on 2015-02-26. 계산화면
 */
public class CalculationCalendarActivity extends BaseActivity {

  // Log
  private static final String TAG = "CalculationCalendarActivity";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;

  //Fragment
  private Fragment_Calculation_Calendar mFragment_CalculationCalendar;
  private PartTimeInfo mPartInfoData_Intent;

  // ActionBar
  private Toolbar mToolbar;
  private HYFont mFont;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_calculation_calendar);
    //Utill
    mFont = new HYFont(this);
    ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
    mFont.setGlobalFont(root);

    mPartInfoData_Intent = (PartTimeInfo) getIntent().getSerializableExtra("ObjectData");

    //ActionBar
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    if (mToolbar != null) {
      mToolbar.setTitle(mPartInfoData_Intent.getAlbaname());
      setSupportActionBar(mToolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      mFont.setGlobalFont((ViewGroup) mToolbar);
    }

    mFragment_CalculationCalendar = new Fragment_Calculation_Calendar(mPartInfoData_Intent);

    Bundle bundle = new Bundle();
    bundle.putInt("year", getIntent().getIntExtra("year", 2015));
    bundle.putInt("month", getIntent().getIntExtra("month", 1));
    mFragment_CalculationCalendar.setArguments(bundle);
    getFragmentManager().beginTransaction().replace(R.id.container, mFragment_CalculationCalendar).commit();

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

