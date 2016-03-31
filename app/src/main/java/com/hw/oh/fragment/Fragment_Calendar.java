package com.hw.oh.fragment;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hw.oh.dialog.WeekWeatherDialog;
import com.hw.oh.model.PartTimeInfo;
import com.hw.oh.model.PartTimeItem;
import com.hw.oh.model.WeekWeatherItem;
import com.hw.oh.sqlite.DBConstant;
import com.hw.oh.sqlite.DBManager;
import com.hw.oh.temp.ApplicationClass;
import com.hw.oh.temp.CalculationCalendarActivity;
import com.hw.oh.temp.NewWorkActivity;
import com.hw.oh.temp.R;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import zh.wang.android.apis.yweathergetter4a.WeatherInfo;
import zh.wang.android.apis.yweathergetter4a.YahooWeather;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherExceptionListener;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherInfoListener;

public class Fragment_Calendar extends Fragment implements OnClickListener, YahooWeatherInfoListener,
    YahooWeatherExceptionListener {
  private static final String TAG = "Fragment_Calendar";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;
  // View
  private Button[] mBtn = new Button[42];
  private TextView[] mTxtBottom = new TextView[42];
  private TextView[] mTxtTop = new TextView[42];
  private LinearLayout mLin_month_prev;
  private LinearLayout mLin_month_next;
  private TextView mTxtYear;
  private TextView mTxtMonth;

  // private ImageView mImgRandom;
  // DataSet
  private String mToday;
  private String[] yoil = {"일", "월", "화", "수", "목", "금", "토"};
  private Calendar mCal;
  private int mYear;
  private int mMonth;
  private int mDay;
  private int mToday_Week;
  private ArrayList<String> mDay_checked = new ArrayList<String>();
  private ArrayList<String> mDel_list = new ArrayList<String>();
  private boolean requestFlag = true;
  private ArrayList<PartTimeItem> mPartTimeDataList = new ArrayList<PartTimeItem>();
  private PartTimeInfo mPartInfoData;

  //Yahoo Weather
  private YahooWeather mYahooWeather = YahooWeather.getInstance(5000, 5000, true);

  //Crouton
  private View mCroutonView;
  private TextView mTxtCrouton;
  private CroutonHelper mCroutonHelper;
  private ArrayList<WeekWeatherItem> mWeekWeatherDataList = new ArrayList<WeekWeatherItem>();

  // utill
  private HYFont mFont;
  private HYPreference mPref;
  private DBManager mDBManager;
  private Snackbar snackbar;
  private com.rey.material.widget.ProgressView mProgress;
  private SimpleDateFormat mDateFormat = new SimpleDateFormat("HH:mm");
  private NumberFormat mNumFomat = new DecimalFormat("###,###,###");

  public Fragment_Calendar(PartTimeInfo info) {
    mPartInfoData = info;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    View fragView = inflater.inflate(R.layout.fragment_calendar, container,
        false);
    /*// 구글 통계
    Tracker mTracker = ((ApplicationClass) getActivity().getApplication()).getDefaultTracker();
    mTracker.setScreenName("알바 캘린더");
    mTracker.send(new HitBuilders.AppViewBuilder().build());*/

    // Utill Set
    mFont = new HYFont(getActivity());
    mFont.setGlobalFont((ViewGroup) fragView);
    mDBManager = new DBManager(getActivity());
    mPref = new HYPreference(getActivity());
    mProgress = (com.rey.material.widget.ProgressView) fragView.findViewById(R.id.progressBar);

    // 오늘 날짜 세팅
    mCal = Calendar.getInstance();
    mYear = mCal.get(Calendar.YEAR);
    mMonth = mCal.get(Calendar.MONTH);
    mDay = mCal.get(Calendar.DATE);
    mToday_Week = mCal.get(Calendar.DAY_OF_WEEK);
    mToday = todaySet(this.mYear, this.mMonth + 1, this.mDay);


    mTxtYear = (TextView) fragView.findViewById(R.id.Year);
    mTxtMonth = (TextView) fragView.findViewById(R.id.Month);

    mLin_month_prev = (LinearLayout) fragView.findViewById(R.id.prev);
    mLin_month_next = (LinearLayout) fragView.findViewById(R.id.next);
    //mImgRandom = (ImageView) fragView.findViewById(R.id.imgRandom);
    mLin_month_next.setOnClickListener(this);
    mLin_month_prev.setOnClickListener(this);

    // View Data Set
    mTxtYear.setText(Integer.toString(this.mYear).toString());
    mTxtMonth.setTag(1 + this.mMonth);
    monthDataSet((int) mTxtMonth.getTag());


    for (int i = 0; ; i++) {
      if (i >= 42) {
        // resetCal();
        updateCal();
        break;
      }
      int tb = getResources().getIdentifier("TextView" + (i + 1), "id",
          "com.hw.oh.temp");
      mTxtBottom[i] = ((TextView) fragView.findViewById(tb));
      int tt = getResources().getIdentifier("TextTop" + (i + 1), "id",
          "com.hw.oh.temp");
      mTxtTop[i] = ((TextView) fragView.findViewById(tt));
      int j = getResources().getIdentifier("Button" + (i + 1), "id",
          "com.hw.oh.temp");
      mBtn[i] = ((Button) fragView.findViewById(j));
      mBtn[i].setOnClickListener(new btnDayclick(i + 1));
      mBtn[i].setOnLongClickListener(new btnDayLongclick(i + 1));
    }

    //Crouton
    mCroutonHelper = new CroutonHelper(getActivity());
    mCroutonView = getActivity().getLayoutInflater().inflate(
        R.layout.crouton_custom_view, null);
    mTxtCrouton = (TextView) mCroutonView.findViewById(R.id.txt_crouton);
    mTxtCrouton.setText(mYear + "년 " + (mMonth + 1) + "월 " + mDay + "일 "
        + yoil[mToday_Week - 1] + "요일");
    mCroutonHelper.setCustomView(mCroutonView);
    mCroutonHelper.setDuration(500);


    setHasOptionsMenu(true);
    return fragView;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_calendar, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        getActivity().finish();
        break;
      case R.id.action_weather:
        String addr = mPref.getValue(mPref.KEY_WEATHER_LOCATION, "");
        if (!addr.equals("")) {
          if (mProgress.getVisibility() == View.INVISIBLE) {
            if (!mWeekWeatherDataList.isEmpty()) {
              new Handler().post(new Runnable() {
                @Override
                public void run() {
                  WeekWeatherDialog weekWeatherDialog = new WeekWeatherDialog();
                  Bundle weekBundle = new Bundle();
                  weekBundle.putSerializable("weekWeather", mWeekWeatherDataList);
                  weekBundle.putSerializable("weekWeatherLocation", mPref.getValue(mPref.KEY_WEATHER_LOCATION, ""));
                  weekWeatherDialog.setArguments(weekBundle);
                  weekWeatherDialog.show(getFragmentManager(), null);
                }
              });
            } else {
              mProgress.setVisibility(View.VISIBLE);
              searchByPlaceName(addr);
            }
          }
        } else {
          mTxtCrouton.setText("설정메뉴에서 날씨위치 선택");
          mCroutonHelper.setCustomView(mCroutonView);
          mCroutonHelper.setDuration(1000);
          mCroutonHelper.show();
        }

        break;
      case R.id.action_delete:
        if (mDel_list.isEmpty()) {
          Toast.makeText(getActivity(), "달력의 일한날짜를 길게눌러보세요.", Toast.LENGTH_SHORT).show();
        } else {
          AlertDialog alert = new AlertDialog.Builder(getActivity())
              .setTitle(mDel_list.size() + "개의 항목을 삭제하시겠습니까?")
              .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  for (int i = 0; i < mDel_list.size(); i++) {
                    mDBManager.removeData(mDel_list.get(i).toString(), mPartInfoData.getAlbaname());
                  }
                  asyncTask_Calendar_Call();
                  totalSnackBar();
                  dialog.dismiss();
                }
              })
              .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                  dialog.cancel();
                }
              }).show();


        }
        break;
      case R.id.action_calculation:
        Intent intent = new Intent(getActivity(), CalculationCalendarActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("ObjectData", mPartInfoData);
        intent.putExtra("year", mYear);
        intent.putExtra("month", mMonth);
        startActivity(intent);
        getActivity().overridePendingTransition(0, 0);

        break;

    }
    return super.onOptionsItemSelected(item);

  }

  @Override
  public void gotWeatherInfo(WeatherInfo weatherInfo) {
    // TODO Auto-generated method stub

    if (weatherInfo != null) {
      if (mYahooWeather.getSearchMode() == YahooWeather.SEARCH_MODE.GPS) {
        //  mTxtAreaOfCity.setText("YOUR CURRENT LOCATION");
      }

      //WeekWeatherSet
      mWeekWeatherDataList.clear();
      for (int i = 0; i < YahooWeather.FORECAST_INFO_MAX_SIZE; i++) {
        final WeatherInfo.ForecastInfo forecastInfo = weatherInfo.getForecastInfoList().get(i);
        WeekWeatherItem item = new WeekWeatherItem();
        item.setDay(forecastInfo.getForecastDay());
        item.setDate(forecastInfo.getForecastDate());
        item.setHighTemp(Integer.toString(forecastInfo.getForecastTempHigh()));
        item.setLowTemp(Integer.toString(forecastInfo.getForecastTempLow()));
        item.setTempStr(forecastInfo.getForecastText());
        item.setTempImg(forecastInfo.getForecastConditionIcon());
        item.setTempCode(forecastInfo.getForecastCode());
        mWeekWeatherDataList.add(item);
      }
      new Handler().post(new Runnable() {
        @Override
        public void run() {
          if (isVisible() && mProgress.getVisibility() == View.VISIBLE) {
            WeekWeatherDialog weekWeatherDialog = new WeekWeatherDialog();
            Bundle weekBundle = new Bundle();
            weekBundle.putSerializable("weekWeather", mWeekWeatherDataList);
            weekBundle.putSerializable("weekWeatherLocation", mPref.getValue(mPref.KEY_WEATHER_LOCATION, ""));
            weekWeatherDialog.setArguments(weekBundle);
            weekWeatherDialog.show(getFragmentManager(), null);
            mProgress.setVisibility(View.INVISIBLE);
          }
        }
      });

    } else {

    }
  }

  @Override
  public void onFailConnection(final Exception e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onFailParsing(final Exception e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onFailFindLocation(final Exception e) {
    // TODO Auto-generated method stub

  }

  private void searchByGPS() {
    mYahooWeather.setNeedDownloadIcons(true);
    mYahooWeather.setUnit(YahooWeather.UNIT.CELSIUS);
    mYahooWeather.setSearchMode(YahooWeather.SEARCH_MODE.GPS);
    mYahooWeather.queryYahooWeatherByGPS(getActivity(), this);
  }

  private void searchByPlaceName(String location) {
    mYahooWeather.setNeedDownloadIcons(true);
    mYahooWeather.setUnit(YahooWeather.UNIT.CELSIUS);
    mYahooWeather.setSearchMode(YahooWeather.SEARCH_MODE.PLACE_NAME);
    mYahooWeather.queryYahooWeatherByPlaceName(getActivity(), location, this);
  }


  @Override
  public void onResume() {
    super.onResume();
    Log.i(TAG + "_", "onResume");
    Log.i(TAG + "_", "isAdded " + Boolean.toString(isAdded()));
    Log.i(TAG + "_", "isHidden " + Boolean.toString(isHidden()));
    Log.i(TAG + "_", "isDetached " + Boolean.toString(isDetached()));
    Log.i(TAG + "_", "isInLayout " + Boolean.toString(isInLayout()));
    Log.i(TAG + "_", "isRemoving " + Boolean.toString(isRemoving()));
    Log.i(TAG + "_", "isResumed " + Boolean.toString(isResumed()));
    Log.i(TAG + "_", "isVisible " + Boolean.toString(isVisible()));
    asyncTask_Calendar_Call();
    totalSnackBar();
  }

  @Override
  public void onPause() {
    super.onPause();
    mProgress.setVisibility(View.INVISIBLE);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onDetach() {
    super.onDetach();

  }

  @Override
  public void onClick(View v) {
    // TODO Auto-generated method stub
    if (snackbar != null) {
      snackbar.dismiss();
    }
    // 이전달 이동
    if (v.getId() == R.id.prev) {
      this.mCal.add(Calendar.MONTH, -1);
      this.mYear = this.mCal.get(Calendar.YEAR);
      this.mMonth = this.mCal.get(Calendar.MONTH);
      this.mTxtYear.setText(Integer.toString(this.mYear).toString());
      mTxtMonth.setTag(1 + this.mMonth);
      monthDataSet((int) mTxtMonth.getTag());
      asyncTask_Calendar_Call();
      totalSnackBar();
      // 다음달 이동
    } else if (v.getId() == R.id.next) {
      this.mCal.add(Calendar.MONTH, 1);
      this.mYear = this.mCal.get(Calendar.YEAR);
      this.mMonth = this.mCal.get(Calendar.MONTH);
      this.mTxtYear.setText(Integer.toString(this.mYear).toString());
      mTxtMonth.setTag(1 + this.mMonth);
      monthDataSet((int) mTxtMonth.getTag());
      asyncTask_Calendar_Call();
      totalSnackBar();
    }
  }

  public void totalSnackBar() {
    Calendar today = Calendar.getInstance();
    Calendar c1 = Calendar.getInstance();
    Calendar c2 = Calendar.getInstance();
    if(mPartInfoData.getWorkMonthWeekFlag() == 0){
      if(c1.get(Calendar.DAY_OF_WEEK) < mPartInfoData.getWorkWeekDay()){
        c1.set(Calendar.DAY_OF_WEEK, mPartInfoData.getWorkWeekDay());
        c2.set(Calendar.DAY_OF_WEEK, mPartInfoData.getWorkWeekDay());
        c1.add(Calendar.DATE, -7);
        c2.add(Calendar.DATE, -1);
      }else{
        c1.set(Calendar.DAY_OF_WEEK, mPartInfoData.getWorkWeekDay());
        c2.set(Calendar.DAY_OF_WEEK, mPartInfoData.getWorkWeekDay());
        c2.add(Calendar.DATE, 6);
      }
    }else{
      //Calendar 타입으로 변경 add()메소드로 1일씩 연장해 주기위해 변경
      c1.set(Integer.parseInt(mTxtYear.getText().toString()), Integer.parseInt(mTxtMonth.getTag().toString()) - 1, mPartInfoData.getWorkMonthDay());
      c2.set(Integer.parseInt(mTxtYear.getText().toString()), Integer.parseInt(mTxtMonth.getTag().toString()) - 1, mPartInfoData.getWorkMonthDay());
      c2.add(Calendar.DATE, -1);

      //월급 종료날이 오늘 보다 뒤일때
      if (today.after(c2)) {
        if (mPartInfoData.getWorkMonthDay() == 1) {
          Log.i(TAG, "오늘날짜보다 월급 종료날이 더 앞 (1)");
          c2.set(Integer.parseInt(mTxtYear.getText().toString()), Integer.parseInt(mTxtMonth.getTag().toString()) - 1, mPartInfoData.getWorkMonthDay());
          c2.add(Calendar.MONTH, 1);
          c2.add(Calendar.DATE, -1);
        } else {
          Log.i(TAG, "오늘날짜보다 월급 종료날이 더 앞");
          c2.add(Calendar.MONTH, 1);
        }
      } else {
        Log.i(TAG, "오늘날짜보다 월급 종료날이 더 뒤");
        c1.add(Calendar.MONTH, -1);
      }
    }
    // c2.set(Integer.parseInt(mTxtYear.getText().toString()), Integer.parseInt(mTxtMonth.getTag().toString()) - 1, mCal.getActualMaximum(Calendar.DATE));
    Log.d(TAG, "time1 :: " + c1.getTime().toString());
    Log.d(TAG, "time2 :: " + c2.getTime().toString());

    int totalMoney = 0;
    StringBuffer sb = new StringBuffer();
    sb.append((c1.get(Calendar.MONTH) + 1) + "월 " + c1.get(Calendar.DATE) + "일 부터 " + (c2.get(Calendar.MONTH) + 1) + "월 " + c2.get(Calendar.DATE) + "일 :: ");

    totalMoney = totalMoneyReturn(c1, c2);

    sb.append(Integer.toString(totalMoney) + " 원");

    //Snackbar 생성
    snackbar = Snackbar.make(getView(), sb.toString(), Snackbar.LENGTH_INDEFINITE);
    //Snackbar 버튼 선택시 호출 되는 이벤트 리스너
    snackbar.setAction("닫기", new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        snackbar.dismiss();
      }
    });
    if (totalMoney != 0)
      snackbar.show();
  }

  public void asyncTask_Calendar_Call() {
    if (INFO)
      Log.i(TAG, "asyncTask_BoardList_Call");
    new asyncTask_Calendar().execute();
  }

  private class asyncTask_Calendar extends AsyncTask<Void, Integer, Void> {

    @Override
    protected void onPreExecute() {
      // TODO Auto-generated method stub
      super.onPreExecute();
      mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... params) {
      // TODO Auto-generated method stub
      Cursor result = mDBManager.selectAllData(DBConstant.TABLE_PARTTIMEDATA, DBConstant.COLUMN_ALBANAME, mPartInfoData.getAlbaname());
      result.moveToFirst();
      mPartTimeDataList.clear();
      while (!result.isAfterLast()) {
        PartTimeItem item = new PartTimeItem();
        item.setAlbaname(result.getString(result.getColumnIndex("albaname")));
        item.setDate(result.getString(result.getColumnIndex("date")));
        item.setHourMoney(result.getString(result.getColumnIndex("hourMoney")));
        item.setStartTimeHour(result.getString(result.getColumnIndex("startTimeHour")));
        item.setStartTimeMin(result.getString(result.getColumnIndex("startTimeMin")));
        item.setEndTimeHour(result.getString(result.getColumnIndex("endTimeHour")));
        item.setEndTimeMin(result.getString(result.getColumnIndex("endTimeMin")));
        item.setSimpleMemo(result.getString(result.getColumnIndex("simpleMemo")));
        item.setWorkTimeHour(result.getString(result.getColumnIndex("workTimeHour")));
        item.setWorkTimeMin(result.getString(result.getColumnIndex("workTimeMin")));
        item.setWorkPayGabul(result.getString(result.getColumnIndex("workPaygabul")));
        item.setWorkPayGabulVal(result.getString(result.getColumnIndex("workPaygabulVal")));
        item.setWorkRefreshTime(result.getString(result.getColumnIndex("workRefresh")));
        item.setWorkPayNight(result.getString(result.getColumnIndex("workPayNight")));
        item.setWorkPayTotal(result.getString(result.getColumnIndex("workPayTotal")));
        item.setWorkPayAdd(result.getString(result.getColumnIndex("workPayAdd")));
        item.setWorkRefreshType(result.getString(result.getColumnIndex("workRefreshType")));
        item.setWorkRefreshHour(result.getString(result.getColumnIndex("workRefreshHour")));
        item.setWorkRefreshMin(result.getString(result.getColumnIndex("workRefreshMin")));
        item.setWorkPayTotalTime(result.getInt(result.getColumnIndex("workPayTotalTime")));
        item.setWorkPayNightTotalTime(result.getInt(result.getColumnIndex("workPayNightTotalTime")));
        item.setWorkPayAddTotalTime(result.getInt(result.getColumnIndex("workPayAddTotalTime")));
        item.setWorkPayRefreshTotalTime(result.getInt(result.getColumnIndex("workPayRefreshTotalTime")));
        item.setWorkEtcMoeny(result.getString(result.getColumnIndex("workPayEtcMoney")));
        item.setWorkEtcNum(result.getString(result.getColumnIndex("workPayEtcNum")));
        item.setWorkEtc(result.getString(result.getColumnIndex("workPayEtc")));
        item.setWorkPayWeekTime(result.getDouble(result.getColumnIndex("workPayWeekTime")));
        item.setWorkPayWeekMoney(result.getInt(result.getColumnIndex("workPayWeekMoney")));
        item.setWorkPayWeek(result.getString(result.getColumnIndex("workPayWeek")));
              /*  PartTimeItem info = new PartTimeItem(results.getString(1),
                        results.getString(2), results.getString(3), results.getString(4), results.getString(5), results.getString(6), results.getString(7), results.getString(8), results.getString(9), results.getString(10), results.getString(11),  results.getString(12), results.getString(13), results.getString(14),results.getString(15));
               */
        mPartTimeDataList.add(item);
        result.moveToNext();
      }
      result.close();
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      // TODO Auto-generated method stub
      super.onPostExecute(result);
      if (!isCancelled()) {
        resetCal();
        updateCal();
      }
      //totalDataSet();
      mProgress.setVisibility(View.INVISIBLE);

    }
  }

  public ProgressDialog showLoadingDialog(Context context, boolean cancelable) {
    ProgressDialog dialog = new ProgressDialog(context);
    dialog.setMessage("Loding..");
    dialog.setIndeterminate(true);
    dialog.setCancelable(cancelable);
    dialog.show();
    return dialog;
  }

  // 달력 지우고 다시그리기
  public void resetCal() {
    mDel_list.clear();
    mDay_checked.removeAll(mDay_checked);
    for (int i = 0; ; i++) {
      if (i >= 42)
        return;
      this.mBtn[i].setText("");
      this.mTxtBottom[i].setText("");
      this.mTxtTop[i].setText("");
      this.mBtn[i].setTextColor(getResources().getColor(R.color.material_white_2));
      this.mBtn[i].setPaintFlags(this.mBtn[i].getPaintFlags()
          & ~Paint.FAKE_BOLD_TEXT_FLAG);
      this.mBtn[i].setPaintFlags(this.mBtn[i].getPaintFlags()
          & ~Paint.UNDERLINE_TEXT_FLAG);
           /* this.mBtn[i]
                    .setBackgroundResource(R.drawable.calendar_day_btn_selector);*/
      this.mTxtBottom[i]
          .setBackgroundColor(Color.parseColor("#00ff0000"));
      // this.btn[i].setBackgroundResource(2130837508);
    }
  }

  // 달력 날짜 채우기
  public void updateCal() {
    int i = 0;
    int max;
    int f_week_day = 0;
    mCal.set(Calendar.DATE, 1);
    max = mCal.getActualMaximum(Calendar.DATE);
    f_week_day = mCal.get(Calendar.DAY_OF_WEEK) - 1;
    Log.d(TAG, "getActualMaximum : " + Integer.toString(max));
    ArrayList<String> dayList = new ArrayList<String>();
    // dayList = underLineText();
    for (i = 0; i <= max + f_week_day; i++) {
      this.mBtn[i].setTextColor(getResources().getColor(R.color.material_white_2));
      mBtn[i].setTag("NONE");
      if (i % 7 == 0) {
        if (i != 0) {
          this.mBtn[i - 1].setTextColor(getResources().getColor(R.color.material_blue_2));
        }
        this.mBtn[i].setTextColor(getResources().getColor(R.color.light_red));
      }
      if (i >= f_week_day && i < max + f_week_day) {
        this.mBtn[i].setText("" + (i - f_week_day + 1));
        if (mToday.equals(todaySet(this.mYear, 1 + this.mMonth, i
            - f_week_day + 1))) {
          // this.mBtn[i].setTextColor(getResources().getColor(R.color.white));
          this.mBtn[i].setPaintFlags(mBtn[i].getPaintFlags()
              | Paint.FAKE_BOLD_TEXT_FLAG);
          this.mTxtBottom[i].setBackgroundResource(R.drawable.calendar_today_bg);

          mBtn[i].setTag("TODAY");
        }
      }

      String tempDay = mYear + "-" + (mMonth + 1) + "-" + mBtn[i].getText().toString();
      for (int j = 0; j < mPartTimeDataList.size(); j++) {
        if (mPartTimeDataList.get(j).getDate().equals(tempDay)) {
          this.mBtn[i].setTextColor(getResources().getColor(R.color.gray));
          if (Boolean.parseBoolean(mPartTimeDataList.get(j).getWorkPayGabul())) {
            mTxtBottom[i].setBackgroundResource(R.drawable.calendar_gabul_bg);
          } else {
            this.mTxtBottom[i].setBackgroundResource(R.drawable.calendar_normal_bg);
          }
          if (Boolean.parseBoolean(mPartTimeDataList.get(j).getWorkPayWeek())) {
            if (Boolean.parseBoolean(mPartTimeDataList.get(j).getWorkPayNight())) {
              if (Boolean.parseBoolean(mPartTimeDataList.get(j).getWorkPayAdd())) {
                mTxtTop[i].setText("주휴/야간/연장");
              } else {
                mTxtTop[i].setText("주휴/야간");
                //    this.mTxtBottom[i].setBackgroundResource(R.drawable.calendar_night_gabul_work_bg);
              }
            } else {
              if (Boolean.parseBoolean(mPartTimeDataList.get(j).getWorkPayAdd())) {
                mTxtTop[i].setText("주휴/연장");
                //   this.mTxtBottom[i].setBackgroundResource(R.drawable.calendar_gabul_add_bg);
              } else {
                mTxtTop[i].setText("주휴");
                //     this.mTxtBottom[i].setBackgroundResource(R.drawable.calendar_gabul_bg);
              }
            }
          } else {
            if (Boolean.parseBoolean(mPartTimeDataList.get(j).getWorkPayNight())) {
              if (Boolean.parseBoolean(mPartTimeDataList.get(j).getWorkPayAdd())) {
                mTxtTop[i].setText("야간/연장");
                //     this.mTxtBottom[i].setBackgroundResource(R.drawable.calendar_night_work_add_bg);
              } else {
                mTxtTop[i].setText("야간");
                //      this.mTxtBottom[i].setBackgroundResource(R.drawable.calendar_night_work_bg);
              }
            } else {
              if (Boolean.parseBoolean(mPartTimeDataList.get(j).getWorkPayAdd())) {
                mTxtTop[i].setText("연장");
                //    this.mTxtBottom[i].setBackgroundResource(R.drawable.calendar_work_add_bg);
              } else {
                //      this.mTxtBottom[i].setBackgroundResource(R.drawable.calendar_work_bg);
              }
            }
          }
          Calendar cal = Calendar.getInstance();
          cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(mPartTimeDataList.get(j).getWorkTimeHour()));
          cal.set(Calendar.MINUTE, Integer.parseInt(mPartTimeDataList.get(j).getWorkTimeMin()));
          mBtn[i].setTag("WORK");
          mTxtBottom[i].setText(mDateFormat.format(cal.getTime()));
          // mTxtBottom[i].setText(mNumFomat.format(Double.parseDouble(mPartTimeDataList.get(j).getWorkPayTotal())));
          Log.i(TAG, mPartTimeDataList.get(j).getWorkTimeHour() + ":" + mPartTimeDataList.get(j).getWorkTimeMin());
          Log.i(TAG, cal.getTime().toString());
          // mTxtBottom[i].setText(mPartTimeDataList.get(j).getWorkTimeHour() + ":" + mPartTimeDataList.get(j).getWorkTimeMin() );
        }
      }
    }
  }

  public String todaySet(int year, int month, int day) {

    Object[] arrayOfObject = new Object[3];
    arrayOfObject[0] = Integer.valueOf(year);
    arrayOfObject[1] = Integer.valueOf(month);
    arrayOfObject[2] = Integer.valueOf(day);
    return String.format("%d-%d-%d", arrayOfObject);
  }

  class btnDayclick implements OnClickListener {
    int position;
    String selectedDay;

    public btnDayclick(int position) {
      // TODO Auto-generated constructor stub
      this.position = position;

    }

    @Override
    public void onClick(View v) {
      // TODO Auto-generated method stub
      int j = getResources().getIdentifier("Button" + (position), "id",
          "com.hw.oh.temp");
      if (v.getId() == j) {
        Button btn_temp = (Button) v;
        selectedDay = mTxtYear.getText().toString() + "-"
            + mTxtMonth.getTag().toString() + "-"
            + btn_temp.getText().toString();
        // 버튼에 날짜가 있을때...
        if (!btn_temp.getText().toString().equals("")) {
          Intent intent = new Intent(getActivity(), NewWorkActivity.class);
          if (v.getTag().equals("WORK")) {
            if (INFO)
              Log.i(TAG, "NEWWORK_INTENT_UPDATE");
            Constant.NEWWORK_INTENT_FLAG = Constant.NEWWORK_INTENT_UPDATE;
            PartTimeItem temp = mDBManager.selectOneData(DBConstant.COLUMN_DATE, mYear + "-" + (mMonth + 1) + "-" + btn_temp.getText(), DBConstant.COLUMN_ALBANAME, mPartInfoData.getAlbaname());
            intent.putExtra("HOURMONEY", temp.getHourMoney());
            intent.putExtra("START_HOUR", temp.getStartTimeHour());
            intent.putExtra("START_MIN", temp.getStartTimeMin());
            intent.putExtra("END_HOUR", temp.getEndTimeHour());
            intent.putExtra("END_MIN", temp.getEndTimeMin());
            intent.putExtra("MEMO", temp.getSimpleMemo());
            intent.putExtra("GABUL", temp.getWorkPayGabul());
            intent.putExtra("GABUL_PAY", temp.getWorkPayGabulVal());
            intent.putExtra("NIGHT_PAY", temp.getWorkPayNight());
            intent.putExtra("REFRESH_TIME", temp.getWorkRefreshTime());
            intent.putExtra("ADD_PAY", temp.getWorkPayAdd());
            intent.putExtra("ADD_PAY_TYPE", temp.getWorkAddType());
            intent.putExtra("ADD_PAY_HOUR", temp.getWorkAddHour());
            intent.putExtra("ADD_PAY_MIN", temp.getWorkAddMin());
            intent.putExtra("REFRESH_TYPE", temp.getWorkRefreshType());
            intent.putExtra("REFRESH_HOUR", temp.getWorkRefreshHour());
            intent.putExtra("REFRESH_MIN", temp.getWorkRefreshMin());
            intent.putExtra("ETC_MONEY", temp.getWorkEtcMoeny());
            intent.putExtra("ETC_NUM", temp.getWorkEtcNum());
            intent.putExtra("ETC_PAY", temp.getWorkEtc());
            intent.putExtra("WEEK_HOURKMONEY", temp.getWorkPayWeekMoney());
            intent.putExtra("WEEK_TIME", temp.getWorkPayWeekTime());
            intent.putExtra("WEEK_PAY", temp.getWorkPayWeek());
            intent.putExtra("REFRESH_STATE", temp.getWorkRefreshState());
          } else {
            if (INFO)
              Log.i(TAG, "NEWWORK_INTENT_NEW");
            intent.putExtra("HOURMONEY", mPartInfoData.getHourMoney());
            intent.putExtra("START_HOUR", mPartInfoData.getStartTimeHour());
            intent.putExtra("START_MIN", mPartInfoData.getStartTimeMin());
            intent.putExtra("END_HOUR", mPartInfoData.getEndTimeHour());
            intent.putExtra("END_MIN", mPartInfoData.getEndTimeMin());
            intent.putExtra("MEMO", "");
            intent.putExtra("GABUL", "false");
            intent.putExtra("GABUL_PAY", "0");
            intent.putExtra("NIGHT_PAY", mPartInfoData.getWorkPayNight());
            intent.putExtra("REFRESH_TIME", mPartInfoData.getWorkRefresh());
            intent.putExtra("ADD_PAY", mPartInfoData.getWorkPayAdd());
            intent.putExtra("ADD_PAY_TYPE", mPartInfoData.getWorkAddType());
            intent.putExtra("ADD_PAY_HOUR", mPartInfoData.getWorkPayAddHour());
            intent.putExtra("ADD_PAY_MIN", mPartInfoData.getWorkPayAddMin());
            intent.putExtra("REFRESH_TYPE", mPartInfoData.getWorkRefreshType());
            intent.putExtra("REFRESH_HOUR", mPartInfoData.getWorkRefreshHour());
            intent.putExtra("REFRESH_MIN", mPartInfoData.getWorkRefreshMin());
            intent.putExtra("ETC_MONEY", mPartInfoData.getWorkPayEtcMoney());
            intent.putExtra("ETC_NUM", mPartInfoData.getWorkPayEtcNum());
            intent.putExtra("ETC_PAY", mPartInfoData.getWorkPayEtc());
            intent.putExtra("WEEK_HOURKMONEY", mPartInfoData.getWorkPayWeekMoney());
            intent.putExtra("WEEK_TIME", mPartInfoData.getWorkPayWeekTime());
            intent.putExtra("WEEK_PAY", mPartInfoData.getWorkPayWeek());
            intent.putExtra("REFRESH_STATE", 0);
            Constant.NEWWORK_INTENT_FLAG = Constant.NEWWORK_INTENT_NEW;
          }
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          intent.putExtra("ALBA_NAME", mPartInfoData.getAlbaname());
          intent.putExtra("YEAR", Integer.toString(mYear));
          intent.putExtra("MONTH", Integer.toString(mMonth + 1));
          intent.putExtra("DAY", btn_temp.getText());
          startActivity(intent);
          getActivity().overridePendingTransition(0, 0);
        }
      }
    }
  }

  class btnDayLongclick implements View.OnLongClickListener {
    int position;
    String selectedDay;

    public btnDayLongclick(int position) {
      // TODO Auto-generated constructor stub
      this.position = position;

    }

    @Override
    public boolean onLongClick(View v) {
      int j = getResources().getIdentifier("Button" + (position), "id",
          "com.hw.oh.temp");
      if (v.getId() == j) {
        Button btn_temp = (Button) v;
        selectedDay = mTxtYear.getText().toString() + "-"
            + mTxtMonth.getTag().toString() + "-"
            + btn_temp.getText().toString();
        // 버튼에 날짜가 있을때...
        if (!btn_temp.getText().toString().equals("")) {
          if (mBtn[position - 1].getTag().equals("WORK")) {
            vibrator();
            mTxtBottom[position - 1]
                .setBackgroundResource(R.drawable.bg_view_bar);
            mTxtBottom[position - 1].setText("삭제 대기");
            mTxtTop[position - 1].setTextColor(getResources().getColor(R.color.white));
            mBtn[position - 1].setTextColor(getResources().getColor(R.color.white));
            mDel_list.add(mYear + "-" + (mMonth + 1) + "-" + btn_temp.getText());
          }
        }

      }

      return true;
    }
  }

  public int totalMoneyReturn(Calendar c1, Calendar c2) {
    int totalMoney = 0;
    while (c1.compareTo(c2) != 1) {
      //출력
      Log.d(TAG, c1.getTime().toString());
      Log.d(TAG, Integer.toString(c1.get(Calendar.YEAR)) + "-" + Integer.toString(c1.get(Calendar.MONTH) + 1) + "-" + Integer.toString(c1.get(Calendar.DATE)));

      PartTimeItem results = mDBManager.selectOneData(DBConstant.COLUMN_ALBANAME, mPartInfoData.getAlbaname(), DBConstant.COLUMN_DATE, Integer.toString(c1.get(Calendar.YEAR)) + "-" + Integer.toString(c1.get(Calendar.MONTH) + 1) + "-" + Integer.toString(c1.get(Calendar.DATE)));
      try {
        if ((results instanceof PartTimeItem)) {
          try {
            int resultMoney = (int) Double.parseDouble(results.getWorkPayTotal());
            Log.i(TAG, results.getWorkPayTotal());
            totalMoney += resultMoney;
          } catch (NumberFormatException e) {
            Log.e(TAG, "NumberFormatException :: " + e.toString());
          }
        }
        //시작날짜 + 1 일
        c1.add(Calendar.DATE, 1);
        Log.d(TAG, "time :: " + c1.getTime().toString());
      } catch (Exception e) {
        Log.e(TAG, "Exception :: " + e.toString());
        break;
      }
    }
    return totalMoney;
  }

  public void monthDataSet(int month) {
    switch (month) {
      case 1:
        //  mImgRandom.setImageResource(R.drawable.icon_season1);
        mTxtMonth.setText(getResources().getString(R.string.January));
        break;
      case 2:
        mTxtMonth.setText(getResources().getString(R.string.Febuary));
        break;
      case 3:
        //    mImgRandom.setImageResource(R.drawable.icon_season3);
        mTxtMonth.setText(getResources().getString(R.string.March));
        break;
      case 4:
        mTxtMonth.setText(getResources().getString(R.string.April));
        break;
      case 5:
        //  mImgRandom.setImageResource(R.drawable.icon_season5);
        mTxtMonth.setText(getResources().getString(R.string.May));
        break;
      case 6:
        mTxtMonth.setText(getResources().getString(R.string.June));
        break;
      case 7:
        //   mImgRandom.setImageResource(R.drawable.icon_season7);
        mTxtMonth.setText(getResources().getString(R.string.July));
        break;
      case 8:
        mTxtMonth.setText(getResources().getString(R.string.August));
        break;
      case 9:
        //   mImgRandom.setImageResource(R.drawable.icon_season9);
        mTxtMonth.setText(getResources().getString(R.string.September));
        break;
      case 10:
        mTxtMonth.setText(getResources().getString(R.string.October));
        break;
      case 11:
        //  mImgRandom.setImageResource(R.drawable.icon_season11);
        mTxtMonth.setText(getResources().getString(R.string.November));
        break;
      case 12:
        mTxtMonth.setText(getResources().getString(R.string.December));
        break;
    }
  }

  private void vibrator() {
    Vibrator vibe = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
                 /*long[] pattern = {1000, 200, 1000, 2000, 1200};
                 vibe.vibrate(pattern, 0);*/
    vibe.vibrate(50);
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
}
