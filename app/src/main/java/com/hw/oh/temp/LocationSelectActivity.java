package com.hw.oh.temp;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hw.oh.adapter.LocationAdapter;
import com.hw.oh.sqlite.KmDBManager;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


/**
 * Created by oh on 2015-02-26. 지역선택 액티비티
 */
public class LocationSelectActivity extends BaseActivity implements AdapterView.OnItemClickListener {

  // Log
  private static final String TAG = "LocationSelectActivity";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;

  private ListView mLocationListView;
  private LocationAdapter mLocationAdapter;
  private ArrayList<String> mKmaItemList = new ArrayList<String>();

  // ActionBar
  private Toolbar mToolbar;

  //Utill
  private HYFont mFont;
  private CountDownLatch CDLKmaDistanceLatch;
  private KmDBManager kmDBManager;
  private HYPreference mPref;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_location);
    //Utill
    mFont = new HYFont(this);
    ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
    mFont.setGlobalFont(root);
    kmDBManager = new KmDBManager(this);
    mPref = new HYPreference(this);

    // 구글 통계
    Tracker mTracker = ((ApplicationClass) getApplication()).getDefaultTracker();
    mTracker.setScreenName("위치선택화면_(LocationSelectActivity)");
    mTracker.send(new HitBuilders.AppViewBuilder().build());

    //ActionBar
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    if (mToolbar != null) {
      mToolbar.setTitle("위치찾기");
      setSupportActionBar(mToolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      mFont.setGlobalFont((ViewGroup) mToolbar);
    }

    mLocationListView = (ListView) root.findViewById(R.id.listview);
    mLocationListView.setOnItemClickListener(this);
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
    asyncTask_Location_Call();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (Constant.SERACH_MAIN_FLAG == Constant.SERACH_RESULT_SELECT) {
      Constant.SERACH_MAIN_FLAG = Constant.SERACH_RESULT_NONE;
      finish();
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    mPref.put(mPref.KEY_WEATHER_LOCATION, mKmaItemList.get(position).toString());
    finish();
  }

  public void setAdapter() {
    mLocationAdapter = new LocationAdapter(this, mKmaItemList);
    if (mLocationListView != null) {
      mLocationListView.setAdapter(mLocationAdapter);
    }
  }

  //기상청 가까운 지역코드 찾기
  public void requestCallDB_KmaDistance() {

    CDLKmaDistanceLatch = new CountDownLatch(1);
    try {
      Cursor cursor = kmDBManager.selectKMAllData();

      Cursor results = cursor;
      results.moveToFirst();
      mKmaItemList.clear();
      while (!results.isAfterLast()) {

        String addr = results.getString(1) + " " + results.getString(2) + " " + results.getString(3);
        mKmaItemList.add(addr);
        results.moveToNext();
      }
      results.close();
      CDLKmaDistanceLatch.countDown();
    } catch (Exception e) {
      Log.e(TAG, e.getMessage());
      CDLKmaDistanceLatch.countDown();
    }
  }

  public void asyncTask_Location_Call() {
    if (INFO)
      Log.i(TAG, "asyncTask_BoardList_Call");
    new asyncTask_Location().execute();
  }

  private class asyncTask_Location extends AsyncTask<Void, Integer, Void> {

    @Override
    protected void onPreExecute() {
      // TODO Auto-generated method stub
      super.onPreExecute();

    }

    @Override
    protected Void doInBackground(Void... params) {
      // TODO Auto-generated method stub

      requestCallDB_KmaDistance();

      try {
        CDLKmaDistanceLatch.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      // TODO Auto-generated method stub
      new Handler().post(new Runnable() {
        @Override
        public void run() {
          setAdapter();
        }
      });


    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.items, menu);

    SearchManager searchManager =
        (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    SearchView searchView =
        (SearchView) menu.findItem(R.id.menu_search).getActionView();

    searchView.setSearchableInfo(
        searchManager.getSearchableInfo(getComponentName()));

    return super.onCreateOptionsMenu(menu);

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

  private void hideKeypad(EditText editText) {
    InputMethodManager manager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
    manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
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

