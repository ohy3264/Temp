package com.hw.oh.fragment;

import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hw.oh.adapter.WorkAlbaInfoAdapter_Array;
import com.hw.oh.model.PartTimeInfo;
import com.hw.oh.sqlite.DBConstant;
import com.hw.oh.sqlite.DBManager;
import com.hw.oh.temp.ApplicationClass;
import com.hw.oh.temp.process.alba.CalendarActivity;
import com.hw.oh.temp.process.alba.NewAlbaActivity;
import com.hw.oh.temp.charts.PieChartsActivity;
import com.hw.oh.temp.R;
import com.hw.oh.temp.etc.SwapActivity;
import com.hw.oh.temp.process.alba.TotalCalendarActivity;
import com.hw.oh.utility.HYFont;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by oh on 2015-02-01.
 */
public class Fragment_Alba extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
  public static final String TAG = "Fragment_Alba";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;
  private Activity mActivity;

  //Crouton

  //View
  private LinearLayout mLin_guide;
  //private ImageButton mBtnNewAlba;
  private android.support.design.widget.FloatingActionButton mFabNewAlba;
  //List
  private ListView mListView;
  private WorkAlbaInfoAdapter_Array mBoardAdapter;
  private ArrayList<PartTimeInfo> mAlbaInfoList = new ArrayList<PartTimeInfo>();

  //Utill
  private Unbinder unbinder;
  private ProgressBar mProgressBar;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_alba, container, false);
    unbinder = ButterKnife.bind(this, rootView);

    // 구글 통계
    Tracker mTracker = ((ApplicationClass) getActivity().getApplication()).getDefaultTracker();
    mTracker.setScreenName("알바리스트");
    mTracker.send(new HitBuilders.AppViewBuilder().build());


    mLin_guide = (LinearLayout) rootView.findViewById(R.id.lin_guide);
    mListView = (ListView) rootView.findViewById(R.id.mainlistView);
    mListView.setFocusable(false);
    mListView.setOnItemClickListener(this);

    mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress);
    ThreeBounce threeBounce = new ThreeBounce();
    threeBounce.setColor(getThemeColor(1));
    mProgressBar.setIndeterminateDrawable(threeBounce);


    setHasOptionsMenu(true);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      mFabNewAlba = (android.support.design.widget.FloatingActionButton) rootView.findViewById(R.id.fabNewAlba);
      mFabNewAlba.setOnClickListener(this);
      mFabNewAlba.setVisibility(View.VISIBLE);
    } else {
      //Float Button
      int redActionButtonSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_size);
      int redActionButtonMargin = getResources().getDimensionPixelOffset(R.dimen.action_button_margin);
      int redActionButtonContentSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_size);
      int redActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_margin);

      ImageView fabIconStar = new ImageView(getActivity()); // Create an icon
      fabIconStar.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_48dp));

      FloatingActionButton.LayoutParams starParams = new FloatingActionButton.LayoutParams(redActionButtonSize, redActionButtonSize);
      starParams.setMargins(redActionButtonMargin,
          redActionButtonMargin,
          redActionButtonMargin,
          redActionButtonMargin);
      fabIconStar.setLayoutParams(starParams);

      FloatingActionButton.LayoutParams fabIconStarParams = new FloatingActionButton.LayoutParams(redActionButtonContentSize, redActionButtonContentSize);
      fabIconStarParams.setMargins(redActionButtonContentMargin,
          redActionButtonContentMargin,
          redActionButtonContentMargin,
          redActionButtonContentMargin);


      FloatingActionButton actionButton = new FloatingActionButton.Builder(getActivity())
          .setContentView(fabIconStar, fabIconStarParams)
          .setContentView(fabIconStar)
          .setBackgroundDrawable(R.drawable.button_action_red_selector)
          .build();


      actionButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intnet_newAlba = new Intent(getActivity(), NewAlbaActivity.class);
          getActivity().overridePendingTransition(0, 0);
          intnet_newAlba.putExtra("Flag", "NEW");
          startActivity(intnet_newAlba);
        }
      });
    }


    return rootView;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mActivity = activity;
  }

  public void setAdapter() {
    mBoardAdapter = new WorkAlbaInfoAdapter_Array(getActivity(), mAlbaInfoList);
    if (mListView != null) {
      mListView.setAdapter(mBoardAdapter);
    }
  }

  public void setDBData() {
    mAlbaInfoList.clear();
    Cursor results = mDB.selectAll(DBConstant.TABLE_PARTTIMEINFO);
    results.moveToFirst();

    while (!results.isAfterLast()) {
      PartTimeInfo info = new PartTimeInfo();
      info.set_id(results.getInt(results.getColumnIndex("_id")));
      info.setAlbaname(results.getString(results.getColumnIndex("albaname")));
      info.setHourMoney(results.getString(results.getColumnIndex("hourMoney")));
      info.setStartTimeHour(results.getString(results.getColumnIndex("startTimeHour")));
      info.setStartTimeMin(results.getString(results.getColumnIndex("startTimeMin")));
      info.setEndTimeHour(results.getString(results.getColumnIndex("endTimeHour")));
      info.setEndTimeMin(results.getString(results.getColumnIndex("endTimeMin")));
      info.setSimpleMemo(results.getString(results.getColumnIndex("simpleMemo")));
      info.setWorkPayNight(results.getString(results.getColumnIndex("workPayNight")));
      info.setWorkRefresh(results.getString(results.getColumnIndex("workRefresh")));
      info.setWorkPayAdd(results.getString(results.getColumnIndex("workPayAdd")));
      info.setWorkRefreshType(results.getString(results.getColumnIndex("workRefreshType")));
      info.setWorkRefreshHour(results.getString(results.getColumnIndex("workRefreshHour")));
      info.setWorkRefreshMin(results.getString(results.getColumnIndex("workRefreshMin")));

      info.setWorkPayEtc(results.getString(results.getColumnIndex("workPayEtc")));
      info.setWorkPayEtcNum(results.getString(results.getColumnIndex("workPayEtcNum")));
      info.setWorkPayEtcMoney(results.getString(results.getColumnIndex("workPayEtcMoney")));

      info.setWorkPayWeekMoney(results.getInt(results.getColumnIndex("workPayWeekMoney")));
      info.setWorkPayWeek(results.getString(results.getColumnIndex("workPayWeek")));
      info.setWorkPayWeekTime(results.getDouble(results.getColumnIndex("workPayWeekTime")));
      info.setWorkAddType(results.getString(results.getColumnIndex("workAddType")));
      info.setWorkPayAddHour(results.getString(results.getColumnIndex("workPayAddHour")));
      info.setWorkPayAddMin(results.getString(results.getColumnIndex("workPayAddMin")));
      info.setWorkAlarm(Boolean.parseBoolean(results.getString(results.getColumnIndex("workAlarm"))));
      info.setWorkMonthDay(results.getInt(results.getColumnIndex("workMonthDay")));
      info.setWorkWeekDay(results.getInt(results.getColumnIndex("workWeekDay")));
      info.setWorkMonthWeekFlag(results.getInt(results.getColumnIndex("workMonthWeekFlag")));
      mAlbaInfoList.add(info);
      results.moveToNext();
    }
    results.close();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.fabNewAlba:
        Intent intnet_newAlba = new Intent(getActivity(), NewAlbaActivity.class);
        getActivity().overridePendingTransition(0, 0);
        intnet_newAlba.putExtra("Flag", "NEW");
        startActivity(intnet_newAlba);
        break;
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    //  Constant.SEL_INFO = mAlbaInfoList.get(position);
    Intent intent_main = new Intent(getActivity(), CalendarActivity.class);
    intent_main.putExtra("ObjectData", mAlbaInfoList.get(position));
    intent_main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent_main);
    getActivity().overridePendingTransition(0, 0);
  }


  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_albalist, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        getActivity().finish();

        break;
      case R.id.action_month:
        Intent intent = new Intent(getActivity(), PieChartsActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(0, 0);

        break;
      case R.id.action_month_calendar:
        Intent intent_total_calendar = new Intent(getActivity(), TotalCalendarActivity.class);
        startActivity(intent_total_calendar);
        getActivity().overridePendingTransition(0, 0);

        break;

      case R.id.action_swap:
        Intent intent_swap = new Intent(getActivity(), SwapActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("ObjectDataList", mAlbaInfoList);
        intent_swap.putExtra("bundle", bundle);
        startActivity(intent_swap);
        getActivity().overridePendingTransition(0, 0);
        break;
    }
    getActivity().invalidateOptionsMenu();
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.i(TAG, "onResume");
    asyncTask_AlbaList_Call();
  }
  public void guideVisibility(){
    if(mAlbaInfoList.isEmpty()){
      mLin_guide.setVisibility(View.VISIBLE);
    }else{
      mLin_guide.setVisibility(View.GONE);
    }
  }


  @Override
  public void onStart() {
    super.onStart();
    // 구글 통계
    GoogleAnalytics.getInstance(getActivity()).reportActivityStart(getActivity());
  };

  @Override
  public void onStop() {
    super.onStop();
    // 구글 통계
    GoogleAnalytics.getInstance(getActivity()).reportActivityStop(getActivity());
  };


  public void asyncTask_AlbaList_Call() {
    if (INFO)
      Log.i(TAG, "asyncTask_BoardList_Call");
    new asyncTask_AlbaList().execute();
  }

  private class asyncTask_AlbaList extends AsyncTask<Void, Integer, Void> {
    private ProgressDialog mLoadingDialog;

    @Override
    protected void onPreExecute() {
      // TODO Auto-generated method stub
      super.onPreExecute();
      mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... params) {
      // TODO Auto-generated method stub
      setDBData();
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      // TODO Auto-generated method stub
      super.onPostExecute(result);
      setAdapter();
      mProgressBar.setVisibility(View.GONE);
      guideVisibility();
    }
  }
}
