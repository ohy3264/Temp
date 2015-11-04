package com.hw.oh.temp;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hw.oh.fragment.Fragment_insurance1;
import com.hw.oh.fragment.Fragment_insurance2;
import com.hw.oh.fragment.Fragment_insurance3;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

/**
 * Created by oh on 2015-09-09.
 */
public class InsuranceActivity extends AppCompatActivity implements MaterialTabListener {
  // Log
  private static final String TAG = "AlbaInfoActivity";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;

  // ActionBar
  private Toolbar mToolbar;

  //TabHost
  private MaterialTabHost mTabHost;

  //ViewPager
  private ViewPager mViewPager;
  private ViewPagerAdapter mPagerAdapter;

  //Fragment
  private Fragment_insurance1 mInsuranceFragment1;
  private Fragment_insurance2 mInsuranceFragment2;
  private Fragment_insurance3 mInsuranceFragment3;


  // utill
  private HYFont mFont;
  private HYPreference mPref;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_insurance);

    //Utill
    mPref = new HYPreference(this);
    mFont = new HYFont(this);
    ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
    mFont.setGlobalFont(root);

    //ActionBar, TabHost
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    mTabHost = (MaterialTabHost) this.findViewById(R.id.materialTabHost);
    if (mToolbar != null) {
      mToolbar.setTitle("4대보험 모의계산");
      setSupportActionBar(mToolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      mFont.setGlobalFont((ViewGroup) mToolbar);
    }

    // init view pager

    mViewPager = (ViewPager) this.findViewById(R.id.pager);
    mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
    mViewPager.setAdapter(mPagerAdapter);
    mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
        // when user do a swipe the selected tab change
        mTabHost.setSelectedNavigationItem(position);

      }
    });

    // insert all tabs from pagerAdapter data
    for (int i = 0; i < mPagerAdapter.getCount(); i++) {

      mTabHost.addTab(
          mTabHost.newTab()
              .setText(mPagerAdapter.getPageTitle(i))
              .setTabListener(InsuranceActivity.this)
      );
    }
    //Fragment
    mInsuranceFragment1 = new Fragment_insurance1();
    mInsuranceFragment2 = new Fragment_insurance2();
    mInsuranceFragment3 = new Fragment_insurance3();

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
    // Inflate the menu; this adds items to the action bar if it is present.
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
  public void onTabSelected(MaterialTab tab) {
    mViewPager.setCurrentItem(tab.getPosition());
  }

  @Override
  public void onTabReselected(MaterialTab tab) {

  }

  @Override
  public void onTabUnselected(MaterialTab tab) {

  }

  private class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(FragmentManager fm) {
      super(fm);

    }

    public Fragment getItem(int num) {
      switch (num) {

        case 0:
          return mInsuranceFragment1;
        case 1:
          return mInsuranceFragment2;
        case 2:
          return mInsuranceFragment3;

      }
      return new Fragment();

    }

    @Override
    public int getCount() {
      return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      switch (position) {
        case 0:
          return "연금보험";
        case 1:
          return "건강보험";
        case 2:
          return "고용보험";
      }
      return "";
    }
  }

}
