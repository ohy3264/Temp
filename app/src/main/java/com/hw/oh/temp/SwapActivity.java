package com.hw.oh.temp;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hw.oh.fragment.Fragment_Swap;
import com.hw.oh.model.PartTimeInfo;
import com.hw.oh.utility.HYFont;

import java.util.ArrayList;

public class SwapActivity extends BaseActivity {
  public static final String TAG = "SwapActivity";
  private ArrayList<PartTimeInfo> mAlbaInfoList = new ArrayList<PartTimeInfo>();
  private Toolbar mToolbar;
  private HYFont mFont;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);//ActionBar
    // 구글 통계
    Tracker mTracker = ((ApplicationClass) getApplication()).getDefaultTracker();
    mTracker.setScreenName("알바목록순서 변경화면_(SwapActivity)");
    mTracker.send(new HitBuilders.AppViewBuilder().build());


    mFont = new HYFont(this);
    ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
    mFont.setGlobalFont(root);

    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    mToolbar.setTitle("순서변경");
    setSupportActionBar(mToolbar);
    mFont.setGlobalFont((ViewGroup) mToolbar);
    final ActionBar ab = getSupportActionBar();
    ab.setDisplayHomeAsUpEnabled(true);

    Bundle bundle = getIntent().getBundleExtra("bundle");
    mAlbaInfoList = (ArrayList<PartTimeInfo>) bundle.getSerializable("ObjectDataList");
    for(int i = 0; i < mAlbaInfoList.size(); i++){
      Log.i(TAG, mAlbaInfoList.get(i).getAlbaname());
    }

    //Fragment Container
    Fragment_Swap frag_dnd = new Fragment_Swap(mAlbaInfoList);
    FragmentManager mFragManager = getFragmentManager();
    mFragManager.beginTransaction().replace(R.id.container, frag_dnd).commit();
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
