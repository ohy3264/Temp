package com.hw.oh.temp.process.alba;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hw.oh.fragment.Fragment_Info1;
import com.hw.oh.fragment.Fragment_Info2;
import com.hw.oh.fragment.Fragment_Info3;
import com.hw.oh.temp.BaseActivity;
import com.hw.oh.temp.R;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

/**
 * Created by oh on 2015-05-23. 알바정보
 */
public class AlbaInfoActivity extends BaseActivity implements MaterialTabListener {
  // Log
  private static final String TAG = "AlbaInfoActivity";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;

  //ViewPager
  private ViewPagerAdapter mPagerAdapter;
  @BindView(R.id.pager)
  ViewPager mViewPager;
  @BindView(R.id.materialTabHost)
  MaterialTabHost mTabHost;

  //Fragment
  private Fragment_Info1 mInfoFragment1;
  private Fragment_Info2 mInfoFragment2;
  private Fragment_Info3 mInfoFragment3;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_albainfo);
    //ButterKnife
    ButterKnife.bind(this);


    setToolbar("알바매니저");
    // init view pager
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
              .setTabListener(AlbaInfoActivity.this)
      );
    }
    //Fragment
    mInfoFragment1 = new Fragment_Info1();
    mInfoFragment2 = new Fragment_Info2();
    mInfoFragment3 = new Fragment_Info3();

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
          return mInfoFragment1;
        case 1:
          return mInfoFragment2;
        case 2:
          return mInfoFragment3;

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
          return "급여관련";
        case 1:
          return "준비중";
        case 2:
          return "준비중";
      }
      return "";
    }
  }

}
