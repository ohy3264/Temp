package com.hw.oh.fragment;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hw.oh.adapter.WorkDataAdapter;
import com.hw.oh.model.CalendarInfo;
import com.hw.oh.model.PartTimeInfo;
import com.hw.oh.model.PartTimeItem;
import com.hw.oh.model.WorkItem;
import com.hw.oh.sqlite.DBConstant;
import com.hw.oh.sqlite.DBManager;
import com.hw.oh.temp.ApplicationClass;
import com.hw.oh.temp.BarChartsActivity;
import com.hw.oh.temp.NewWorkActivity;
import com.hw.oh.temp.R;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYExcelWrite;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.HYStringUtill;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import jxl.write.WriteException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by oh on 2015-02-01.
 */
public class Fragment_Calculation_Calendar extends Fragment implements View.OnClickListener {
  public static final String TAG = "Fragment_Calculation_Calendar";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;
  private static final int INITIAL_DELAY_MILLIS = 300;

  private TextView mTxtGuide;
  private TextView mTxtTotalDay;
  private TextView mTxtTotalMoney;
  //ListView
  private DynamicListView mListView;
  private WorkDataAdapter mWorkDataAdapter;
  private ArrayList<WorkItem> mWorkDataList = new ArrayList<WorkItem>();

  // List Header
  private View mlistViewHeader;
  private LinearLayout mBtnCalendarPeriodStart, mBtnCalendarPeriodEnd;
  private TextView mTxtCalendarPeriodStart,mTxtCalendarPeriodEnd;
  private TextView mTxtTotalTime, mTxtAlbaName;
  private LinearLayout mLinDuty, mLinInsurance, mLinResult;
  private TextView mTxtDutyResult, mTxtInsurance1, mTxtInsurance2, mTxtInsurance3, mTxtInsurance4, mTxtInsuranceResult, mTxtTotalresult, mTxtAddTotalTime, mTxtRefreshTotalTime;
  // mTxtRefreshTotalTime, mTxtNightTotalTime, mTxtAddTotalTime,

  //Crouton
  private View mCroutonView;
  private TextView mTxtCrouton;
  private CroutonHelper mCroutonHelper;

  //Date Picker
  private int mTotalMoney;
  private String[] mPeriod_start;
  private String[] mPeriod_end;
  private int mStartYear;
  private int mStartMonth;
  private int mStartDay;
  private int mEndYear;
  private int mEndMonth;
  private int mEndDay;


  private int mTotalTime;
  private int mTotalDay;
  private int mNightTotalTime;
  private int mAddTotalTime;
  private int mRefreshTotalTime;
  private PartTimeInfo mPartInfoData;

  //Utill
  private HYFont mFont;
  private HYPreference mPref;
  private DBManager mDB;

  // Calendar
  private Calendar mCal = Calendar.getInstance(); // 현재시점의 객체를 가져옴
  private int mYear = this.mCal.get(Calendar.YEAR); // 년
  private int mMonth = this.mCal.get(Calendar.MONTH); // 월
  private int mDay = this.mCal.get(Calendar.DATE); // 일
  private int mToday_yoil = mCal.get(Calendar.DAY_OF_WEEK); // 주의 몇번 째
  private CountDownLatch mCdlPeroidLatch;
  private NumberFormat mNumFomat = new DecimalFormat("###,###,###");


  private List<Bitmap>
      bmps = new ArrayList<Bitmap>();

  public Fragment_Calculation_Calendar(PartTimeInfo info) {
    mPartInfoData = info;
  }
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_calculation_calendar, container, false);
    //Utill
    mFont = new HYFont(getActivity());
    mFont.setGlobalFont((ViewGroup) rootView);
    mDB = new DBManager(getActivity());
    mPref = new HYPreference(getActivity());

   // 구글 통계
    Tracker mTracker = ((ApplicationClass) getActivity().getApplication()).getDefaultTracker();
    mTracker.setScreenName("알바 토탈 계산");
    mTracker.send(new HitBuilders.AppViewBuilder().build());

    //Crouton
    mCroutonHelper = new CroutonHelper(getActivity());
    mCroutonView = getActivity().getLayoutInflater().inflate(
        R.layout.crouton_custom_view, null);
    mTxtCrouton = (TextView) mCroutonView.findViewById(R.id.txt_crouton);


    //View Set
    mListView = (DynamicListView) rootView.findViewById(R.id.mainlistView);
    //mListView.setFocusable(false);

    // Alba listview header
    mlistViewHeader = getActivity().getLayoutInflater().inflate(
        R.layout.header_calcul_calendar, mListView, false);
    mBtnCalendarPeriodStart = (LinearLayout) mlistViewHeader.findViewById(R.id.btn_calendar_period_start);
    mBtnCalendarPeriodStart.setOnClickListener(this);
    mBtnCalendarPeriodEnd = (LinearLayout) mlistViewHeader.findViewById(R.id.btn_calendar_period_end);
    mBtnCalendarPeriodEnd.setOnClickListener(this);
    mTxtCalendarPeriodStart= (TextView) mlistViewHeader.findViewById(R.id.txt_calendar_period_start);
    mTxtCalendarPeriodEnd= (TextView) mlistViewHeader.findViewById(R.id.txt_calendar_period_end);
    mFont.setGlobalFont((ViewGroup) mlistViewHeader);
    mListView.addHeaderView(mlistViewHeader);

    mTxtTotalMoney = (TextView) mlistViewHeader.findViewById(R.id.txtTotalMoney);
    mTxtAlbaName = (TextView) mlistViewHeader.findViewById(R.id.txt_albaname);
    mTxtTotalTime = (TextView) mlistViewHeader.findViewById(R.id.txtTotalTime);
    mTxtTotalDay = (TextView) mlistViewHeader.findViewById(R.id.txtTotalDay);
    mTxtRefreshTotalTime = (TextView) mlistViewHeader.findViewById(R.id.txtTotalRefreshTime);
    //mTxtNightTotalTime = (TextView) mlistViewHeader.findViewById(R.id.txtTotalNightTime);
    mTxtAddTotalTime = (TextView) mlistViewHeader.findViewById(R.id.txtTotalADDTime);
    mTxtDutyResult = (TextView) mlistViewHeader.findViewById(R.id.txtDutyResult);
    mLinDuty = (LinearLayout) mlistViewHeader.findViewById(R.id.linDuty);
    mLinInsurance = (LinearLayout) mlistViewHeader.findViewById(R.id.linInsurance);
    mLinInsurance.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(getActivity(), "test", Toast.LENGTH_LONG).show();
      }
    });
    mLinResult = (LinearLayout) mlistViewHeader.findViewById(R.id.lin_Result);
    mTxtInsurance1 = (TextView) mlistViewHeader.findViewById(R.id.txtInsurance1);
    mTxtInsurance2 = (TextView) mlistViewHeader.findViewById(R.id.txtInsurance2);
    mTxtInsurance3 = (TextView) mlistViewHeader.findViewById(R.id.txtInsurance3);
    mTxtInsurance4 = (TextView) mlistViewHeader.findViewById(R.id.txtInsurance4);
    mTxtInsuranceResult = (TextView) mlistViewHeader.findViewById(R.id.txtInsuranceResult);
    mTxtTotalresult = (TextView) mlistViewHeader.findViewById(R.id.txtTotalresult);
    header_inits();

    mTxtAlbaName.setText(mPartInfoData.getAlbaname());
    mTxtGuide = (TextView) rootView.findViewById(R.id.txtGuide);
    mTxtGuide.setText(Constant.GUIDE_MSG2);
    mTxtGuide.setSelected(true);
    datePickerSet();
        /* Setup the adapter */
    setAdapter();

        /* Add new items on item click */
    mListView.setOnItemClickListener(new MyOnItemClickListener(mListView));

        /* Enable swipe to dismiss */
    //mListView.enableSimpleSwipeUndo();
    setHasOptionsMenu(true);
    return rootView;
  }

  public void header_inits() {
    if (mPref.getValue(mPref.KEY_INSURANCE_STATE, false)) {
      mLinInsurance.setVisibility(View.VISIBLE);
    } else {
      mLinInsurance.setVisibility(View.GONE);
    }
  }

  // 초기 데이트 피커 셋팅
  public void datePickerSet() {
    //DatePicker Data Set
    if (!mPref.getValue(mPref.KEY_BAR_PERIOD_CHECK, false)) {
       /* mPeriod_start = HYStringUtill.splitFunction(mPref.getValue(mPref.KEY_BAR_PERIOD_START, mYear + "-" + mMonth + "-" + mDay));
        mPeriod_end = HYStringUtill.splitFunction(mPref.getValue(mPref.KEY_BAR_PERIOD_END, mYear + "-" + mMonth + "-" + mDay));*/

      CalendarInfo Info = mDB.selectCalendar(mPartInfoData.getAlbaname());
      if ((Info instanceof CalendarInfo)) {
        mStartYear = Integer.parseInt(Info.getStartYear());
        mStartMonth = Integer.parseInt(Info.getStartMonth());
        mStartDay = Integer.parseInt(Info.getStartDay());

        mEndYear = Integer.parseInt(Info.getEndYear());
        mEndMonth = Integer.parseInt(Info.getEndMonth());
        mEndDay = Integer.parseInt(Info.getEndDay());
        mTxtCalendarPeriodStart.setText(mStartYear + "년 " + (mStartMonth + 1) + "월 " + mStartDay + "일 ");
        mTxtCalendarPeriodEnd.setText(mEndYear + "년 " + (mEndMonth + 1) + "월 " + mEndDay + "일 ");
      } else {
        mStartYear = mYear;
        mStartMonth = mMonth;
        mStartDay = mDay;

        mEndYear = mYear;
        mEndMonth = mMonth;
        mEndDay = mDay;
        mTxtCalendarPeriodStart.setText("기간선택");
        mTxtCalendarPeriodEnd.setText("기간선택");
      }
    } else {
      Calendar today = Calendar.getInstance();
      Calendar c1 = Calendar.getInstance();
      Calendar c2 = Calendar.getInstance();

      if (mPartInfoData.getWorkMonthWeekFlag() == 0) {
        Log.i(TAG, mPartInfoData.getWorkMonthWeekFlag()  + " :: " + c1.get(Calendar.DAY_OF_WEEK));
        if (c1.get(Calendar.DAY_OF_WEEK) < mPartInfoData.getWorkWeekDay()) {
          c1.set(Calendar.DAY_OF_WEEK, mPartInfoData.getWorkWeekDay());
          c2.set(Calendar.DAY_OF_WEEK, mPartInfoData.getWorkWeekDay());
          c1.add(Calendar.DATE, -7);
          c2.add(Calendar.DATE, -1);
        } else {
          c1.set(Calendar.DAY_OF_WEEK, mPartInfoData.getWorkWeekDay());
          c2.set(Calendar.DAY_OF_WEEK, mPartInfoData.getWorkWeekDay());
          c2.add(Calendar.DATE, 6);
        }
      } else {

        //Calendar 타입으로 변경 add()메소드로 1일씩 추가해 주기위해 변경
        //          c1.set(getArguments().getInt("year"), getArguments().getInt("month"), mPref.getValue(mPref.KEY_MONTH_PAY, 1));
//            c2.set(getArguments().getInt("year"), getArguments().getInt("month"), mPref.getValue(mPref.KEY_MONTH_PAY, 1));

        c1.set(getArguments().getInt("year"), getArguments().getInt("month"), mPartInfoData.getWorkMonthDay());
        c2.set(getArguments().getInt("year"), getArguments().getInt("month"), mPartInfoData.getWorkMonthDay());
        c2.add(Calendar.DATE, -1);

        //월급 종료날이 오늘 보다 뒤일때
        if (today.after(c2)) {
          Log.i(TAG, "오늘날짜보다 월급 종료날이 더 앞");
          c2.add(Calendar.MONTH, 1);
        } else {
          Log.i(TAG, "오늘날짜보다 월급 종료날이 더 뒤");
          c1.add(Calendar.MONTH, -1);
        }
        // c2.set(Integer.parseInt(mTxtYear.getText().toString()), Integer.parseInt(mTxtMonth.getTag().toString()) - 1, mCal.getActualMaximum(Calendar.DATE));

      }
      Log.d(TAG, "time1 :: " + c1.getTime().toString());
      Log.d(TAG, "time2 :: " + c2.getTime().toString());

      mStartYear = c1.get(Calendar.YEAR);
      mStartMonth = c1.get(Calendar.MONTH);
      mStartDay = c1.get(Calendar.DATE);

      mEndYear = c2.get(Calendar.YEAR);
      mEndMonth = c2.get(Calendar.MONTH);
      mEndDay = c2.get(Calendar.DATE);

      mTxtCalendarPeriodStart.setText(mStartYear + "년 " + (mStartMonth + 1) + "월 " + mStartDay + "일 ");
      mTxtCalendarPeriodEnd.setText(mEndYear + "년 " + (mEndMonth + 1) + "월 " + mEndDay + "일 ");
    }

  }

  // 선택된 기간에 데이터 추출
  public void periodDate(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay) {
    mTotalDay = 0;
    mTotalTime = 0;
    mNightTotalTime = 0;
    mRefreshTotalTime = 0;
    mAddTotalTime = 0;
    mCdlPeroidLatch = new CountDownLatch(1);
    //Date타입으로 변경

    mWorkDataList.clear();
    mTotalMoney = 0;
    Calendar c1 = Calendar.getInstance();
    Calendar c2 = Calendar.getInstance();

    //Calendar 타입으로 변경 add()메소드로 1일씩 추가해 주기위해 변경
    c1.set(startYear, startMonth, startDay);
    c2.set(endYear, endMonth, endDay);
    Log.d(TAG, "time1 :: " + c1.getTime().toString());
    Log.d(TAG, "time2 :: " + c2.getTime().toString());

    //시작날짜와 끝 날짜를 비교해, 시작날짜가 작거나 같은 경우 출력

    while (c1.compareTo(c2) != 1) {
      //출력
      Log.d(TAG, c1.getTime().toString());
      Log.d(TAG, Integer.toString(c1.get(Calendar.YEAR)) + "-" + Integer.toString(c1.get(Calendar.MONTH) + 1) + "-" + Integer.toString(c1.get(Calendar.DATE)));
      PartTimeItem results = mDB.selectOneData(DBConstant.COLUMN_ALBANAME, mPartInfoData.getAlbaname(), DBConstant.COLUMN_DATE, Integer.toString(c1.get(Calendar.YEAR)) + "-" + Integer.toString(c1.get(Calendar.MONTH) + 1) + "-" + Integer.toString(c1.get(Calendar.DATE)));
      try {
        if ((results instanceof PartTimeItem)) {
          WorkItem info = new WorkItem();
          info.setHourMoney(Integer.parseInt(results.getHourMoney()));
          info.setYear(Integer.toString(c1.get(Calendar.YEAR)));
          info.setMonth(Integer.toString(c1.get(Calendar.MONTH) + 1));
          info.setDay(Integer.toString(c1.get(Calendar.DATE)));

          info.setStartTimeHour(results.getStartTimeHour());
          info.setStartTimeMin(results.getStartTimeMin());
          info.setEndTimeHour(results.getEndTimeHour());
          info.setEndTimeMin(results.getEndTimeMin());

          if (results.getWorkPayTotalTime() >= 480) {
            if (Boolean.parseBoolean(results.getWorkPayAdd())) {
              info.setDayTimeHour(Integer.toString(480 / 60));
              info.setDayTimeMin(Integer.toString(480 % 60));
              info.setWorkPayDay(Double.toString(dayTimeMoney(Double.parseDouble(results.getHourMoney()), 480 / 60, 480 % 60)));
            } else {
              info.setDayTimeHour(Integer.toString(results.getWorkPayTotalTime() / 60));
              info.setDayTimeMin(Integer.toString(results.getWorkPayTotalTime() % 60));
              info.setWorkPayDay(Double.toString(dayTimeMoney(Double.parseDouble(results.getHourMoney()), results.getWorkPayTotalTime() / 60, results.getWorkPayTotalTime() % 60)));

            }
          } else {
            info.setDayTimeHour(Integer.toString(results.getWorkPayTotalTime() / 60));
            info.setDayTimeMin(Integer.toString(results.getWorkPayTotalTime() % 60));
            info.setWorkPayDay(Double.toString(dayTimeMoney(Double.parseDouble(results.getHourMoney()), results.getWorkPayTotalTime() / 60, results.getWorkPayTotalTime() % 60)));

          }
          info.setNightTimeHour(Integer.toString(results.getWorkPayNightTotalTime() / 60));
          info.setNightTimeMin(Integer.toString(results.getWorkPayNightTotalTime() % 60));
          info.setAddTimeHour(Integer.toString(results.getWorkPayAddTotalTime() / 60));
          info.setAddTimeMin(Integer.toString(results.getWorkPayAddTotalTime() % 60));
          info.setRefreshTimeHour(results.getWorkRefreshHour());
          info.setRefreshTimeMin(results.getWorkRefreshMin());
          info.setWorkTimeHour(Integer.toString(results.getWorkPayTotalTime() / 60));
          info.setWorkTimeMin(Integer.toString(results.getWorkPayTotalTime() % 60));
          info.setWorkEtc(results.getWorkEtc());
          info.setWorkEtcNum(results.getWorkEtcNum());
          info.setWorkEtcMoney(results.getWorkEtcMoeny());
          info.setWorkNight(results.getWorkPayNight());
          info.setWorkPayNight(Double.toString(dayTimeMoney_night(Double.parseDouble(results.getHourMoney()), (results.getWorkPayNightTotalTime() / 60), (results.getWorkPayNightTotalTime() % 60))));
          info.setWorkAdd(results.getWorkPayAdd());
          info.setWorkPayAdd(Double.toString(dayTimeMoney_add(Double.parseDouble(results.getHourMoney()), (results.getWorkPayAddTotalTime() / 60), (results.getWorkPayAddTotalTime() % 60))));
          info.setWorkWeek(results.getWorkPayWeek());
          info.setWorkWeekMoney(Integer.toString(results.getWorkPayWeekMoney()));
          info.setWorkPayWeek(Double.toString(weekPaySet(results.getWorkPayWeekTime(), results.getWorkPayWeekMoney())));
          info.setWorkRefresh(results.getWorkRefreshTime());
          info.setWorkPayRefresh(Double.toString(dayTimeMoney(Double.parseDouble(results.getHourMoney()), Integer.parseInt(results.getWorkRefreshHour()), Integer.parseInt(results.getWorkRefreshMin()))));
          info.setWorkPayGabul(results.getWorkPayGabul());
          info.setWorkPayGabulValue(results.getWorkPayGabulVal());
          try {
            int resultMoney = (int) Double.parseDouble(results.getWorkPayTotal());
            Log.i(TAG, results.getWorkPayTotal());
            mTotalMoney += resultMoney;
            mTotalTime += results.getWorkPayTotalTime();
            mNightTotalTime += results.getWorkPayNightTotalTime();
            mRefreshTotalTime += results.getWorkPayRefreshTotalTime();
            mAddTotalTime += results.getWorkPayAddTotalTime();
            info.setTotalMoney(resultMoney);
          } catch (NumberFormatException e) {
            Log.e(TAG, "NumberFormatException :: " + e.toString());

          }
          info.setSimpleMemo(results.getSimpleMemo());
          mWorkDataList.add(info);
          mTotalDay++;
        }
        //시작날짜 + 1 일
        c1.add(Calendar.DATE, 1);
        Log.d(TAG, "time :: " + c1.getTime().toString());
      } catch (Exception e) {
        Log.e(TAG, "Exception :: " + e.toString());
        break;
      }
    }
    mCdlPeroidLatch.countDown();
  }

  public double weekPaySet(Double weektime, int mWeekHourMoney) {
    Log.i("weekPaySet", Double.toString((weektime / 40) * mWeekHourMoney * 8));
    return (weektime / 40.0) * mWeekHourMoney * 8;
  }

  public Double dayTimeMoney_add(Double hourmoney, int hour, int min) {
    hourmoney = hourmoney * 1.5;
    Double minmoney = (hourmoney) / 60;
    return (hourmoney * hour + minmoney * min);
  }

  // 구간 달력정보 입력
  public void InsertPeroid() {
    CalendarInfo info = new CalendarInfo(mPartInfoData.getAlbaname(), Integer.toString(mStartYear), Integer.toString(mStartMonth), Integer.toString(mStartDay), Integer.toString(mEndYear), Integer.toString(mEndMonth), Integer.toString(mEndDay));
    if (mDB.insertCalendarInfo(info)) {
      Log.i(TAG, "달력 구간 정보 Insert 성공");
    } else {
      if (mDB.updateCalendarInfo(info)) {
        Log.i(TAG, "달력 구간 정보 Update 성공");
      }
    }
    ;

  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_calendar_period_start:
              /*  DatePickerDialog startDialog = new DatePickerDialog(getActivity(), startListener,
                        mStartYear, mStartMonth, mStartDay);
                startDialog.show();*/

        final com.rey.material.app.DatePickerDialog
            startDialog = new com.rey.material.app.DatePickerDialog(getActivity());
        startDialog.dateRange(1, 1, 1987, 31, 12, 2100);
        startDialog.date(mStartDay, mStartMonth, mStartYear);
        startDialog.positiveAction("확인");
        startDialog.positiveActionClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mTxtCalendarPeriodStart.setText(startDialog.getYear() + "년 " + (startDialog.getMonth() + 1) + "월 " + startDialog.getDay() + "일 ");

            mStartYear = startDialog.getYear();
            mStartMonth = startDialog.getMonth();
            mStartDay = startDialog.getDay();
            asyncTask_AlbaList_Call();
            startDialog.dismiss();
          }
        });
        startDialog.negativeAction("취소");
        startDialog.negativeActionClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            startDialog.dismiss();
          }
        });
        startDialog.show();


        break;
      case R.id.btn_calendar_period_end:

            /*    DatePickerDialog endDialog = new DatePickerDialog(getActivity(), endListener,
                        mEndYear, mEndMonth, mEndDay);
                endDialog.show();*/
        final com.rey.material.app.DatePickerDialog
            endDialog = new com.rey.material.app.DatePickerDialog(getActivity());
        endDialog.dateRange(1, 1, 1987, 31, 12, 2100);
        endDialog.date(mEndDay, mEndMonth, mEndYear);
        endDialog.positiveAction("확인");
        endDialog.positiveActionClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mTxtCalendarPeriodEnd.setText(endDialog.getYear() + "년 " + (endDialog.getMonth() + 1) + "월 " + endDialog.getDay() + "일 ");

            mEndYear = endDialog.getYear();
            mEndMonth = endDialog.getMonth();
            mEndDay = endDialog.getDay();

            asyncTask_AlbaList_Call();
            endDialog.dismiss();
          }
        });
        endDialog.negativeAction("취소");
        endDialog.negativeActionClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            endDialog.dismiss();
          }
        });
        endDialog.show();

        break;
    }
  }

  private DatePickerDialog.OnDateSetListener startListener = new DatePickerDialog.OnDateSetListener() {
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
      mTxtCalendarPeriodStart.setText(year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일 ");

      mStartYear = year;
      mStartMonth = monthOfYear;
      mStartDay = dayOfMonth;
      asyncTask_AlbaList_Call();

    }
  };
  private DatePickerDialog.OnDateSetListener endListener = new DatePickerDialog.OnDateSetListener() {
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
      mTxtCalendarPeriodEnd.setText(year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일 ");

      mEndYear = year;
      mEndMonth = monthOfYear;
      mEndDay = dayOfMonth;
      asyncTask_AlbaList_Call();

    }
  };

  @Override
  public void onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);

  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_calendar_calculation, menu);

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        getActivity().finish();
        break;

      case R.id.action_month:
        Intent intent = new Intent(getActivity(), BarChartsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("ObjectData", mPartInfoData);
        intent.putExtra("WorkData", mWorkDataList);
        startActivity(intent);
        getActivity().overridePendingTransition(0, 0);

        break;
      case R.id.action_excel:
        HYExcelWrite excel = new HYExcelWrite();
        if (isExternalStorageWritable()) {
          Log.i(TAG, "쓰기 가능");
          File file = new File(getActivity().getExternalFilesDir(null), mPartInfoData.getAlbaname() + "_" + mTxtCalendarPeriodStart.getText() + "_" + mTxtCalendarPeriodEnd.getText() + ".xls");
          Log.i(TAG, file.getAbsolutePath().toString());
          try {
            excel.excelWrite(mTxtAlbaName.getText().toString(), file, getHeader(), mWorkDataList);
          } catch (IOException e) {
            e.printStackTrace();
          } catch (WriteException e) {
            e.printStackTrace();
          }

          Intent emailSend = new Intent("android.intent.action.SEND");
          emailSend.setType("plain/text");
          emailSend.putExtra(Intent.EXTRA_SUBJECT, mPartInfoData.getAlbaname() + "_" + mTxtCalendarPeriodStart.getText() + "_" + mTxtCalendarPeriodEnd.getText());
          emailSend.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
          startActivity(emailSend);


        }
        break;

      case R.id.action_kakao_share:
        //AlertDialog_AlbaDel();


        Bitmap bm = getWholeListViewItemsToBitmap();
        Message msg = kakaoHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("Path", showScreen(bm));
        msg.setData(bundle);
        kakaoHandler.sendMessage(msg);
        bm.recycle();
        bm = null;


        break;
    }
    getActivity().invalidateOptionsMenu();
    return super.onOptionsItemSelected(item);
  }

  public Bitmap getWholeListViewItemsToBitmap() {
    mListView.invalidate();
    mListView.setDrawingCacheEnabled(true);
    ListAdapter adapter = mListView.getAdapter();
    int itemscount = adapter.getCount();
    int allitemsheight = 0;

    Log.i(TAG, "listview.getAdapter() :: " + itemscount);
    for (int i = 0;
         i <
             itemscount;
         i++) {
      Log.i(TAG, "count :: " + i);
      View childView = adapter.getView(i, null, mListView);
      childView.measure(View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
      childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
      childView.setDrawingCacheEnabled(true); // 뷰에 표시되는 내용을 Bitmap 형태로 캐싱 허용
      //childView.buildDrawingCache(); // 비트맵형태로 캐싱
      bmps.add(childView.getDrawingCache()); // 캐싱된 뷰 비트맵 객체가져와서 담음
      allitemsheight += childView.getMeasuredHeight();
    }
    Bitmap bigbitmap = Bitmap.createBitmap(mListView.getMeasuredWidth(), allitemsheight, Bitmap.Config.ARGB_8888);
    Canvas bigcanvas = new Canvas(bigbitmap);
    Paint paint = new Paint();
    int iHeight = 0;
    for (int i = 0;
         i <
             bmps.size();
         i++) {
      Bitmap bmp = bmps.get(i);
      bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
      iHeight += bmp.getHeight();

    }
    return bigbitmap;
  }


  public boolean isExternalStorageWritable() {
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)) {
      return true;
    }
    return false;
  }

  public Map<String, Object> getHeader() {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("title", mTxtCalendarPeriodStart.getText() + "~" + mTxtCalendarPeriodEnd.getText());
    map.put("date", "날짜");
    map.put("startTime", "시작시간");
    map.put("endTime", "종료시간");
    map.put("allTime", "총 근무시간");
    map.put("dayTime", "기본근무");
    map.put("nightTime", "야간근무");
    map.put("addTime", "연장근무");
    map.put("refreshTime", "휴식시간");
    map.put("hourPay", "시급");
    map.put("etcNum", "총 건수");
    map.put("etcNumPay", "건당 수당");
    map.put("dayPay", "기본수당");
    map.put("nightPay", "야간수당");
    map.put("addPay", "연장수당");
    map.put("etcAllPay", "총 건수당");
    map.put("weekPay", "주휴수당");
    map.put("refreshPay", "휴식수당");
    map.put("gabulPay", "가불금액");
    map.put("allPay", "총 일급");
    map.put("simpleMemo", "메모");
    return map;
  }

  private class MyOnItemClickListener implements AdapterView.OnItemClickListener {

    private final DynamicListView mListView;

    MyOnItemClickListener(final DynamicListView listView) {
      mListView = listView;
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
      try {
        String year = mWorkDataList.get(position - 1).getYear();
        String month = mWorkDataList.get(position - 1).getMonth();
        String day = mWorkDataList.get(position - 1).getDay();

        Intent intent = new Intent(getActivity(), NewWorkActivity.class);
        if (INFO)
          Log.i(TAG, "NEWWORK_INTENT_UPDATE");
        Constant.NEWWORK_INTENT_FLAG = Constant.NEWWORK_INTENT_UPDATE;
        PartTimeItem temp = mDB.selectOneData(DBConstant.COLUMN_DATE, year + "-" + month + "-" + day, DBConstant.COLUMN_ALBANAME, mPartInfoData.getAlbaname());

        intent.putExtra("ALBA_NAME", temp.getAlbaname());
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
        intent.putExtra("REFRESH_TYPE", temp.getWorkRefreshType());
        intent.putExtra("REFRESH_HOUR", temp.getWorkRefreshHour());
        intent.putExtra("REFRESH_MIN", temp.getWorkRefreshMin());
        intent.putExtra("ETC_MONEY", temp.getWorkEtcMoeny());
        intent.putExtra("ETC_NUM", temp.getWorkEtcNum());
        intent.putExtra("ETC_PAY", temp.getWorkEtc());
        intent.putExtra("WEEK_HOURKMONEY", temp.getWorkPayWeekMoney());
        intent.putExtra("WEEK_TIME", temp.getWorkPayWeekTime());
        intent.putExtra("WEEK_PAY", temp.getWorkPayWeek());
        intent.putExtra("ADD_PAY_TYPE", temp.getWorkAddType());
        intent.putExtra("ADD_PAY_HOUR", temp.getWorkAddHour());
        intent.putExtra("ADD_PAY_MIN", temp.getWorkAddMin());


        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("YEAR", year);
        intent.putExtra("MONTH", month);
        intent.putExtra("DAY", day);
        startActivity(intent);
        getActivity().overridePendingTransition(0, 0);
      }catch (Exception e){
        Log.e(TAG, e.toString());
      }

    }
  }


  @Override
  public void onResume() {
    super.onResume();
    getActivity().invalidateOptionsMenu();
    asyncTask_AlbaList_Call();

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    for (int i = 0;
         i <
             bmps.size();
         i++) {
      bmps.get(i).recycle();

    }
  }

  /* Setup the adapter */
  public void setAdapter() {
    mWorkDataAdapter = new WorkDataAdapter(getActivity(), mWorkDataList);
    if (mListView != null) {
      SimpleSwipeUndoAdapter simpleSwipeUndoAdapter = new SimpleSwipeUndoAdapter(mWorkDataAdapter, getActivity(), new MyOnDismissCallback(mWorkDataAdapter));
      AlphaInAnimationAdapter animAdapter = new AlphaInAnimationAdapter(simpleSwipeUndoAdapter);
      animAdapter.setAbsListView(mListView);
      assert animAdapter.getViewAnimator() != null;
      animAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
      mListView.setAdapter(mWorkDataAdapter);
    }
  }
  private static class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener {

    private final DynamicListView mListView;

    MyOnItemLongClickListener(final DynamicListView listView) {
      mListView = listView;
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
      if (mListView != null) {
        mListView.startDragging(position - mListView.getHeaderViewsCount());
      }
      return true;
    }
  }

  private class MyOnDismissCallback implements OnDismissCallback {

    private final WorkDataAdapter mAdapter;

    @Nullable
    private Toast mToast;

    MyOnDismissCallback(final WorkDataAdapter adapter) {
      mAdapter = adapter;
    }

    @Override
    public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
      for (int position : reverseSortedPositions) {
        mWorkDataList.remove(position);
        mAdapter.notifyDataSetChanged();
      }

      if (mToast != null) {
        mToast.cancel();
      }
      mToast = Toast.makeText(getActivity(),
          getString(R.string.removed_positions, Arrays.toString(reverseSortedPositions)),
          Toast.LENGTH_LONG
      );
      mToast.show();
    }
  }


  public Double dayTimeMoney(Double hourmoney, int hour, int min) {
    Double minmoney = hourmoney / 60;
    return (hourmoney * hour + minmoney * min);
  }

  public Double dayTimeMoney_night(Double hourmoney, int hour, int min) {
    hourmoney = hourmoney * 0.5;
    Double minmoney = (hourmoney) / 60;
    return (hourmoney * hour + minmoney * min);
  }

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
      mLoadingDialog = showLoadingDialog(getActivity(), false);

    }

    @Override
    protected Void doInBackground(Void... params) {
      // TODO Auto-generated method stub
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          periodDate(mStartYear, mStartMonth, mStartDay, mEndYear, mEndMonth, mEndDay);
        }
      });
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      // TODO Auto-generated method stub
      try {
        getActivity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            try {
              mCdlPeroidLatch.await();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            mTxtTotalDay.setText(mTotalDay + "일");
            mTxtTotalMoney.setText("");
            mTxtTotalMoney.setText(mNumFomat.format(mTotalMoney) + "원");
            mTxtTotalTime.setText("합계 : "+mTotalTime / 60 + "시간 " + mTotalTime % 60 + "분");

            //소득세 계산
            Double duty = 0.0;
            if (mTotalMoney < 1060000) {
              // Toast.makeText(getActivity(), "월급여 1,060,000원 미만은 원천징수할 세금이 없습니다.", Toast.LENGTH_SHORT).show();
            } else {
              switch (mPref.getValue(mPref.KEY_DUTY_SELECTOR, 0)) {
                case 0:
                  duty = dutyCalculation(mTotalMoney, mTotalMoney * 12, mPref.getValue(mPref.KEY_DUTY_PERSON_1, 1), mPref.getValue(mPref.KEY_DUTY_PERSON_2, 0));
                  break;

                case 1:
                  duty = mTotalMoney * 0.033;
                  break;
                case 2:
                  duty = mTotalMoney * (Double.parseDouble(mPref.getValue(mPref.KEY_DUTY_INPUT, "0")) / 100);
                  break;
              }
            }
            mTxtDutyResult.setText(mNumFomat.format(duty) + "원");

            //4대보험 계산
            Double insurance1 = insurance1Calculation(mTotalMoney);
            Double insurance2 = insurance2Calulation(mTotalMoney);
            Double insurance3 = insurance3Calulation(mTotalMoney);
            Double insurance4 = insurance4Calulation(mTotalMoney);
            mTxtInsurance1.setText(mNumFomat.format(insurance1));
            mTxtInsurance2.setText(mNumFomat.format(insurance2));
            mTxtInsurance3.setText(mNumFomat.format(insurance3));
            mTxtInsurance4.setText(mNumFomat.format(insurance4));
            mTxtInsuranceResult.setText(mNumFomat.format(insurance1 + insurance2 + insurance3 + insurance4) + "원");

            //공제 후 급여
            if (mPref.getValue(mPref.KEY_INSURANCE_STATE, false) && mPref.getValue(mPref.KEY_DUTY_STATE, false)) {
              mTxtTotalresult.setText(mNumFomat.format(mTotalMoney - (insurance1 + insurance2 + insurance3 + insurance4) - duty) + "원");
              mLinResult.setVisibility(View.VISIBLE);
              mLinDuty.setVisibility(View.VISIBLE);
              mLinInsurance.setVisibility(View.VISIBLE);
            } else if (mPref.getValue(mPref.KEY_DUTY_STATE, false)) {
              mTxtTotalresult.setText(mNumFomat.format(mTotalMoney - duty) + "원");
              mLinResult.setVisibility(View.VISIBLE);
              mLinDuty.setVisibility(View.VISIBLE);
              mLinInsurance.setVisibility(View.GONE);
            } else if (mPref.getValue(mPref.KEY_INSURANCE_STATE, false)) {
              mTxtTotalresult.setText(mNumFomat.format(mTotalMoney - (insurance1 + insurance2 + insurance3 + insurance4)) + "원");
              mLinResult.setVisibility(View.VISIBLE);
              mLinDuty.setVisibility(View.GONE);
              mLinInsurance.setVisibility(View.VISIBLE);
            } else {
              mLinResult.setVisibility(View.GONE);
              mLinDuty.setVisibility(View.GONE);
              mLinInsurance.setVisibility(View.GONE);
            }
                        /*mTxtTotalTime.setText("총 일한시간 " + mTotalTime / 60 + "시간 " + mTotalTime % 60 + "분");
                        mTxtRefreshTotalTime.setText("총 휴식시간 " + mRefreshTotalTime / 60 + "시간 " + mRefreshTotalTime % 60 + "분");
                        mTxtNightTotalTime.setText("총 야간시간 " + mNightTotalTime / 60 + "시간 " + mNightTotalTime % 60 + "분");
                        mTxtAddTotalTime.setText("총 추가시간 " + mAddTotalTime / 60 + "시간 " + mAddTotalTime % 60 + "분");*/
            mTxtRefreshTotalTime.setText("휴식 : " + mRefreshTotalTime / 60 + "시간 " + mRefreshTotalTime % 60 + "분");
            mTxtAddTotalTime.setText("연장 : " + mAddTotalTime / 60 + "시간 " + mAddTotalTime % 60 + "분");
            mLoadingDialog.dismiss();
          }
        });
        mListView.invalidate();
        mWorkDataAdapter.notifyDataSetChanged();
        //캘린더 업데이트
        if (!mPref.getValue(mPref.KEY_BAR_PERIOD_CHECK, false)) {
          InsertPeroid();
        }
      } catch (Exception e) {
        Log.i(TAG, e.toString());
        mLoadingDialog.dismiss();
      }
    }
  }

  //연금보험
  public double insurance1Calculation(int monthPay) {
    if (mTotalMoney > 4210000) {
      //Toast.makeText(getActivity(), "기준 월 소득세액의 최고금액은 421만원 까지의 범위로 합니다. ", Toast.LENGTH_SHORT).show();
      monthPay = 4210000;
    } else if (mTotalMoney < 270000) {
      //Toast.makeText(getActivity(), "기준 월 소득세액의 최소금액은 27만원 까지의 범위로 합니다. ", Toast.LENGTH_SHORT).show();
      monthPay = 270000;
    }
    return Math.floor(monthPay * 0.045 / 10) * 10;
  }

  //건강보험
  public double insurance2Calulation(int monthPay) {
    if (monthPay > 7810000) {
      // Toast.makeText(getActivity(), "기준 월 소득세액의 최고금액은 781만원 까지의 범위로 합니다. ", Toast.LENGTH_SHORT).show();
      monthPay = 7810000;
    } else if (monthPay < 280000) {
      // Toast.makeText(getActivity(), "기준 월 소득세액의 최소금액은 28만원 까지의 범위로 합니다. ", Toast.LENGTH_SHORT).show();
      monthPay = 280000;
    }
    return Math.floor(monthPay * 0.03035 / 10) * 10;
  }

  //장기요양보험
  public Double insurance3Calulation(int monthPay) {
    if (monthPay > 7810000) {
      // Toast.makeText(getActivity(), "기준 월 소득세액의 최고금액은 781만원 까지의 범위로 합니다. ", Toast.LENGTH_SHORT).show();
      monthPay = 7810000;
    } else if (monthPay < 280000) {
      // Toast.makeText(getActivity(), "기준 월 소득세액의 최소금액은 28만원 까지의 범위로 합니다. ", Toast.LENGTH_SHORT).show();
      monthPay = 280000;
    }
    return Math.floor((((monthPay * 0.0607) * 0.0655) / 2) / 10) * 10;
  }

  //고용보험
  public Double insurance4Calulation(int monthPay) {
    return Math.floor((monthPay * 0.0065) / 10) * 10;

  }

  private void calendarInfoSetDB(CalendarInfo calndarInfo) {
    mDB.updateCalendarInfo(calndarInfo);
  }


  private String showScreen(Bitmap bm) {
    try {
      File[] filepath = ContextCompat.getExternalFilesDirs(getActivity(), null);
      if (DBUG) {
        Log.d(TAG, "filepath[0] :: " + filepath[0].getPath());
        Log.d("Storage Max Count : ", String.valueOf(filepath.length));
      }
      File path = new File(filepath[0].getPath() + "/");
      String filename = HYStringUtill.getRandomString(6) + ".jpg";

      if (!path.isDirectory()) {
        path.mkdirs();
      }

      FileOutputStream out = new FileOutputStream(path.getPath() + filename);
      bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
      return path.getPath() + filename;
    } catch (FileNotFoundException e) {
      Log.d("FileNotFoundException:", e.getMessage());
      return "false";
    }
  }

  public double dutyCalculation(int monthPay, int yearPay, int personNumber1, int personNumber2) {
    double dutyMinus1 = 0.0; // 근로소득공제
    double dutyMinus2 = 0.0; // 근로소득금액
    double dutyMinus3 = 0.0; // 인적공제
    double dutyMinus4 = 0.0; // 연금보험료공제
    double dutyMinus5 = 0.0; // 특별 소득공제 등
    double dutyMinus6 = 0.0; // 과세표준
    double dutyMinus7 = 0.0; // 산출세액
    double dutyMinus8 = 0.0; // 근로소득세액
    double dutyMinus9 = 0.0; // 결정세액
    double dutyMinus10 = 0.0; // 간이세액
    double dutyMinus11 = 0.0; // 지방소득세


    //근로소득공제
    if (yearPay <= 5000000) {
      Log.i(TAG, "근로소득공제 : 500만원 이하");
      dutyMinus1 = monthPay * 0.7;
    } else if (yearPay <= 15000000) {
      Log.i(TAG, "근로소득공제 : 1,500만원 이하");
      dutyMinus1 = 3500000 + (yearPay - 5000000) * 0.4;
    } else if (yearPay <= 45000000) {
      Log.i(TAG, "근로소득공제 : 4,500만원 이하");
      dutyMinus1 = 7500000 + (yearPay - 15000000) * 0.15;
    } else if (yearPay <= 100000000) {
      Log.i(TAG, "근로소득공제 : 1억원 이하");
      dutyMinus1 = 12000000 + (yearPay - 45000000) * 0.05;
    } else {
      Log.i(TAG, "근로소득공제 : 1억원 초과");
      dutyMinus1 = 12000000 + (14750000 + ((yearPay) - 100000000) * 0.02);
    }
    Log.i(TAG, "근로소득 공제 : " + mNumFomat.format(dutyMinus1));

    //근로소득금액
    dutyMinus2 = yearPay - dutyMinus1;
    Log.i(TAG, "근로소득 금액 : " + mNumFomat.format(dutyMinus2));

    //인적공제
    dutyMinus3 = (personNumber1 + personNumber2) * 1500000;
    Log.i(TAG, "인적공제 : " + personNumber1 + "명(" + personNumber2 + ")" + mNumFomat.format(dutyMinus3));

    //연금보험료 공제
    if (monthPay > 3980000) {
      Log.i(TAG, "기준 월 소득세액의 최고공제금액은 3,980,000만원 까지의 범위로 합니다.");
      dutyMinus4 = (Math.floor((3980000 * 0.045) / 10) * 10) * 12;
    } else if (monthPay < 250000) {
      Log.i(TAG, "기준 월 소득세액의 최소금액은 25만원 까지의 범위로 합니다.");
      dutyMinus4 = (Math.floor((250000 * 0.045) / 10) * 10) * 12;
    } else {
      dutyMinus4 = (Math.floor((monthPay * 0.045) / 10) * 10) * 12;
    }
    Log.i(TAG, "연금보험료공제 : " + mNumFomat.format(dutyMinus4));

    //특별 소득공제 등
    if (personNumber1 == 1) {
      Log.i(TAG, "공제대상자가 1명인 경우");
      if (yearPay <= 30000000) {
        Log.i(TAG, "3,000만원 이하");
        dutyMinus5 = 3100000 + (yearPay * 0.04);
      } else if (yearPay <= 45000000) {
        Log.i(TAG, "3,000만원 초과 4,500만원 이하");
        dutyMinus5 = (3100000 + (yearPay * 0.04)) - ((yearPay - 30000000) * 0.05);
      } else if (yearPay <= 70000000) {
        Log.i(TAG, "4,500만원 초과 7,000만원 이하");
        dutyMinus5 = 3100000 + (yearPay * 0.015);

      } else if (yearPay <= 120000000) {
        Log.i(TAG, "7,000만원 초과 1억 2,000만원 이하");
        dutyMinus5 = 3100000 + (yearPay * 0.005);
      }
    } else if (personNumber1 == 2) {
      Log.i(TAG, "공제대상자가 2명인 경우");
      if (yearPay <= 30000000) {
        Log.i(TAG, "3,000만원 이하");
        dutyMinus5 = 3600000 + (yearPay * 0.04);

      } else if (yearPay <= 45000000) {
        Log.i(TAG, "3,000만원 초과 4,500만원 이하");
        dutyMinus5 = (3600000 + (yearPay * 0.04)) - ((yearPay - 30000000) * 0.05);

      } else if (yearPay <= 70000000) {
        Log.i(TAG, "4,500만원 초과 7,000만원 이하");
        dutyMinus5 = 3600000 + (yearPay * 0.02);

      } else if (yearPay <= 120000000) {
        Log.i(TAG, "7,000만원 초과 1억 2,000만원 이하");
        dutyMinus5 = 3600000 + (yearPay * 0.01);
      }

    } else if (personNumber1 >= 3) {
      Log.i(TAG, "공제대상자가 3명 이상인 경우");
      if (yearPay <= 30000000) {
        Log.i(TAG, "3,000만원 이하");
        dutyMinus5 = 5000000 + (yearPay * 0.07);

      } else if (yearPay <= 45000000) {
        Log.i(TAG, "3,000만원 초과 4,500만원 이하");
        if (yearPay > 40000000) {

          dutyMinus5 = (5000000 + (yearPay * 0.07)) - ((yearPay - 30000000) * 0.05) + (yearPay - 40000000) * 0.04;
        } else {
          dutyMinus5 = (5000000 + (yearPay * 0.07)) - ((yearPay - 30000000) * 0.05);
        }

      } else if (yearPay <= 70000000) {
        Log.i(TAG, "4,500만원 초과 7,000만원 이하");
        dutyMinus5 = 5000000 + (yearPay * 0.05) + (yearPay - 40000000) * 0.04;

      } else if (yearPay <= 120000000) {
        Log.i(TAG, "7,000만원 초과 1억 2,000만원 이하");
        dutyMinus5 = (5000000 + (yearPay * 0.03) + (yearPay - 40000000) * 0.04);
      }
    }
    Log.i(TAG, "특별 소득공제 등 : " + mNumFomat.format(dutyMinus5));

    //과세표준
    dutyMinus6 = dutyMinus2 - dutyMinus3 - dutyMinus4 - dutyMinus5;
    if (dutyMinus6 < 0) {
      dutyMinus6 = 0.0;
    }
    Log.i(TAG, "과세표준 : " + mNumFomat.format(dutyMinus6));
    //산출세액
    if (dutyMinus6 <= 12000000) {
      dutyMinus7 = dutyMinus6 * 0.06;
    } else if (dutyMinus6 <= 46000000) {
      dutyMinus7 = 720000 + (dutyMinus6 - 12000000) * 0.15;

    } else if (dutyMinus6 <= 88000000) {
      dutyMinus7 = 5820000 + (dutyMinus6 - 46000000) * 0.24;

    } else if (dutyMinus6 <= 150000000) {
      dutyMinus7 = 15900000 + (dutyMinus6 - 88000000) * 0.35;

    } else if (dutyMinus6 > 150000000) {
      dutyMinus7 = 37600000 + (dutyMinus6 - 150000000) * 0.38;
    }
    Log.i(TAG, "산출세액 : " + mNumFomat.format(dutyMinus7));

    // 근로소득세액
    if (dutyMinus7 <= 500000) {
      dutyMinus8 = dutyMinus7 * 0.55;
    } else {
      dutyMinus8 = 275000 + (dutyMinus7 - 500000) * 0.30;
    }

    // 근로소득세액 한도체크
    if (yearPay <= 55000000) {
      if (dutyMinus8 > 660000) {
        dutyMinus8 = 660000;
      }
    } else if (yearPay <= 70000000) {
      if (dutyMinus8 > 630000) {
        dutyMinus8 = 630000;
      }
    } else if (yearPay > 70000000) {
      if (dutyMinus8 > 500000) {
        dutyMinus8 = 500000;
      }
    }
    Log.i(TAG, "근로소득세액 : " + mNumFomat.format(dutyMinus8));

    //결정세액
    dutyMinus9 = dutyMinus7 - dutyMinus8;
    Log.i(TAG, "결정세액 : " + mNumFomat.format(dutyMinus9));

    //간이세액
    dutyMinus10 = Math.floor((dutyMinus9 / 12) / 10) * 10;
    Log.i(TAG, "간이세액 : " + mNumFomat.format(dutyMinus10));

    //지방소득세
    dutyMinus11 = Math.floor((dutyMinus10 * 0.1) / 10) * 10;
    Log.i(TAG, "지방소득세 : " + mNumFomat.format(dutyMinus11));
    //납부세액합계
    return dutyMinus10 + dutyMinus11;
  }


  final Handler kakaoHandler = new Handler() {
    public void handleMessage(Message msg) {
      File myFile = new File(msg.getData().getString("Path"));

      Intent intent = new Intent(Intent.ACTION_SEND);
      intent.putExtra(Intent.EXTRA_SUBJECT, "제목");
      Uri uri = Uri.fromFile(myFile);
      intent.putExtra(Intent.EXTRA_STREAM, uri);
      intent.setType("image/*");
      startActivity(Intent.createChooser(intent, "이 사진을 공유합니다."));
    }
  };

  public ProgressDialog showLoadingDialog(Context context, boolean cancelable) {
    ProgressDialog dialog = new ProgressDialog(context);
    dialog.setMessage("로딩중....");
    dialog.setIndeterminate(true);
    dialog.setCancelable(cancelable);
    dialog.show();
    return dialog;
  }

  /**
   * Get Bitmap's Width
   **/
  public static int getBitmapOfWidth(String fileName) {
    try {
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeFile(fileName, options);
      return options.outWidth;
    } catch (Exception e) {
      return 0;
    }
  }

  /**
   * Get Bitmap's height
   **/
  public static int getBitmapOfHeight(String fileName) {

    try {
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeFile(fileName, options);

      return options.outHeight;
    } catch (Exception e) {
      return 0;
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
}
