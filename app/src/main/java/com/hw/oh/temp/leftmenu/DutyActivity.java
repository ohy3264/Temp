package com.hw.oh.temp.leftmenu;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.hw.oh.fragment.Fragment_Duty;
import com.hw.oh.fragment.Fragment_insurance1;
import com.hw.oh.fragment.Fragment_insurance2;
import com.hw.oh.fragment.Fragment_insurance3;
import com.hw.oh.temp.BaseActivity;
import com.hw.oh.temp.R;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYPreference;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

/**
 * Created by oh on 2015-09-09.
 */
public class DutyActivity extends BaseActivity implements MaterialTabListener {
    // Log
    private static final String TAG = "AlbaInfoActivity";
    private static final boolean DEBUG = true;
    private static final boolean INFO = true;


    //ViewPager
    @BindView(R.id.materialTabHost)
    MaterialTabHost mTabHost;
    @BindView(R.id.pager)
    ViewPager mViewPager;
    private ViewPagerAdapter mPagerAdapter;

    //Fragment
    private Fragment_Duty mDutyFragment1;
    private Fragment_insurance1 mInsuranceFragment1;
    private Fragment_insurance2 mInsuranceFragment2;
    private Fragment_insurance3 mInsuranceFragment3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duty);
        //ButterKnife
        ButterKnife.bind(this);
        //Utill
        mPref = new HYPreference(this);
        setToolbar("모의계산");

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
                            .setTabListener(DutyActivity.this)
            );
        }
        //Fragment
        mDutyFragment1 = new Fragment_Duty();
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
                    return mDutyFragment1;
                case 1:
                    return mInsuranceFragment1;
                case 2:
                    return mInsuranceFragment2;
                case 3:
                    return mInsuranceFragment3;

            }
            return new Fragment();

        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "근로소득자";
                case 1:
                    return "연금보험";
                case 2:
                    return "건강보험";
                case 3:
                    return "고용보험";
            }
            return "";
        }
    }

}
