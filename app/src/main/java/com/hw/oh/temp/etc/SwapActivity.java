package com.hw.oh.temp.etc;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hw.oh.fragment.Fragment_Swap;
import com.hw.oh.model.PartTimeInfo;
import com.hw.oh.temp.ApplicationClass;
import com.hw.oh.temp.BaseActivity;
import com.hw.oh.temp.R;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYFont;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class SwapActivity extends BaseActivity {
    public static final String TAG = "SwapActivity";
    private ArrayList<PartTimeInfo> mAlbaInfoList = new ArrayList<PartTimeInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//ActionBar
        //ButterKnife
        ButterKnife.bind(this);
        // 구글 통계
        Tracker mTracker = ((ApplicationClass) getApplication()).getDefaultTracker();
        mTracker.setScreenName("알바목록순서 변경화면_(SwapActivity)");
        mTracker.send(new HitBuilders.AppViewBuilder().build());

        setToolbar("순서변경");

        Bundle bundle = getIntent().getBundleExtra("bundle");
        mAlbaInfoList = (ArrayList<PartTimeInfo>) bundle.getSerializable("ObjectDataList");
        for (int i = 0; i < mAlbaInfoList.size(); i++) {
            Log.i(TAG, mAlbaInfoList.get(i).getAlbaname());
        }
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
        //Fragment Container
        Fragment_Swap frag_dnd = new Fragment_Swap(mAlbaInfoList);
        android.support.v4.app.FragmentManager mFragManager = getSupportFragmentManager();
        mFragManager.beginTransaction().replace(R.id.container, frag_dnd).commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        // 구글 통계
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    ;

    @Override
    public void onStop() {
        super.onStop();
        // 구글 통계
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    ;

}
