package com.hw.oh.temp;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.hw.oh.adapter.LocationAdapter;
import com.hw.oh.sqlite.KmDBManager;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class SearchResultsActivity extends BaseActivity implements AdapterView.OnItemClickListener {

  // Log
  private static final String TAG = "SearchResultsActivity";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;

  private ListView mLocationListView;
  private LocationAdapter mLocationAdapter;
  private ArrayList<String> mKmaItemList = new ArrayList<String>();

  // ActionBar
  private Toolbar mToolbar;
  private HYFont mFont;
  private CountDownLatch CDLKmaDistanceLatch;
  private KmDBManager kmDBManager;
  private HYPreference mPref;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_location);
    //Util
    mFont = new HYFont(this);
    ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
    mFont.setGlobalFont(root);
    kmDBManager = new KmDBManager(this);
    mPref = new HYPreference(this);

    //ActionBar
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    if (mToolbar != null) {
      mToolbar.setTitle("검색결과");
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
    handleIntent(getIntent());


  }

  public void setAdapter() {
    mLocationAdapter = new LocationAdapter(this, mKmaItemList);
    if (mLocationListView != null) {
      mLocationListView.setAdapter(mLocationAdapter);
    }
  }


  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    mPref.put(mPref.KEY_WEATHER_LOCATION, mKmaItemList.get(position).toString());
    Constant.SERACH_MAIN_FLAG = Constant.SERACH_RESULT_SELECT;
    finish();
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
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_main, menu);


    return true;
  }

  @Override
  protected void onNewIntent(Intent intent) {
    handleIntent(intent);
  }

  private void handleIntent(Intent intent) {

    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
      String query = intent.getStringExtra(SearchManager.QUERY);
      //use the query to search
      Toast.makeText(getApplicationContext(), query, Toast.LENGTH_LONG).show();

      asyncTask_Location_Call(query);
    }
  }

  public void asyncTask_Location_Call(String query) {
    if (INFO)
      Log.i(TAG, "asyncTask_BoardList_Call");
    new asyncTask_Location().execute(query);
  }

  private class asyncTask_Location extends AsyncTask<String, Integer, Void> {

    @Override
    protected void onPreExecute() {
      // TODO Auto-generated method stub
      super.onPreExecute();

    }

    @Override
    protected Void doInBackground(String... params) {
      // TODO Auto-generated method stub

      requestCallDB_KmaDistance(params[0]);

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

  //기상청 가까운 지역코드 찾기
  public void requestCallDB_KmaDistance(String p) {

    CDLKmaDistanceLatch = new CountDownLatch(1);
    try {
      Cursor cursor = kmDBManager.selectKMAllData();

      Cursor results = cursor;
      results.moveToFirst();
      mKmaItemList.clear();
      while (!results.isAfterLast()) {

        String addr = results.getString(1) + " " + results.getString(2) + " " + results.getString(3);
        if (addr.matches(".*" + p + ".*"))
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
}
