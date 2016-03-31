package com.hw.oh.temp;

import com.google.android.gms.analytics.GoogleAnalytics;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.hw.oh.model.BarCalendarItem;
import com.hw.oh.model.BarItem;
import com.hw.oh.model.PartTimeInfo;
import com.hw.oh.model.PartTimeItem;
import com.hw.oh.model.WorkItem;
import com.hw.oh.sqlite.DBConstant;
import com.hw.oh.sqlite.DBManager;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.HYStringUtill;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;


/**
 * Created by oh on 2015-02-01. Bar 그래프
 */
public class BarChartsActivity extends BaseActivity implements View.OnClickListener {
  public static final String TAG = "BarChartsActivity";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;
  private LinearLayout mHeader_barchart;
  private Boolean mHeaderFlag = false;

  protected BarChart mChart;
  private LinearLayout mBtnCalendarPeriodStart, mBtnCalendarPeriodEnd;
  private TextView mTxtCalendarPeriodStart, mTxtCalendarPeriodEnd;
  private Calendar mCal = Calendar.getInstance(); // 현재시점의 객체를 가져옴
  private int mYear = this.mCal.get(Calendar.YEAR); // 년
  private int mMonth = this.mCal.get(Calendar.MONTH); // 월
  private int mDay = this.mCal.get(Calendar.DATE); // 일
  private int mStartYear;
  private int mStartMonth;
  private int mStartDay;
  private int mEndYear;
  private int mEndMonth;
  private int mEndDay;
  private TextView mTxtTotal;
  private String[] mPeriod_start;
  private String[] mPeriod_end;

  // ActionBar
  private Toolbar mToolbar;

  private ArrayList<BarCalendarItem> mWorkCalList = new ArrayList<BarCalendarItem>();
  private ArrayList<WorkItem> mWorkDataList = new ArrayList<WorkItem>();
  private PartTimeInfo mPartInfoData_Intent;

  //Utill
  private HYFont mFont;
  private HYPreference mPref;
  private DBManager mDBManager = new DBManager(this);
  private CountDownLatch mCdlPeroidLatch;
  private NumberFormat mNumFomat = new DecimalFormat("###,###,###");

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mFont = new HYFont(this);
    ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
    mFont.setGlobalFont(root);
    mPref = new HYPreference(this);
    setContentView(R.layout.activity_barchart);

    mPartInfoData_Intent = (PartTimeInfo) getIntent().getSerializableExtra("ObjectData");
    //ActionBar
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    if (mToolbar != null) {
      mToolbar.setTitle("월별 바 그래프");
      setSupportActionBar(mToolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      mFont.setGlobalFont((ViewGroup) mToolbar);
    }

    //View Set
    mTxtCalendarPeriodStart = (TextView) findViewById(R.id.txt_calendar_period_start);
    mTxtCalendarPeriodEnd = (TextView) findViewById(R.id.txt_calendar_period_end);
    mBtnCalendarPeriodStart = (LinearLayout) findViewById(R.id.btn_calendar_period_start);
    mBtnCalendarPeriodStart.setOnClickListener(this);
    mBtnCalendarPeriodEnd = (LinearLayout) findViewById(R.id.btn_calendar_period_end);
    mBtnCalendarPeriodEnd.setOnClickListener(this);
    mTxtTotal = (TextView) findViewById(R.id.txt_Total);
    mHeader_barchart = (LinearLayout) findViewById(R.id.header_barchart);
    mHeader_barchart.setVisibility(View.GONE);
    //Chart View Set
    mChart = (BarChart) findViewById(R.id.chart1);
    // mChart.setOnChartValueSelectedListener(this);
    mChart.setDrawBarShadow(false);
    mChart.setDrawValueAboveBar(true);
    mChart.setPinchZoom(false);
    mChart.setDescription("");
    // if more than 60 entries are displayed in the chart, no values will be
    // drawn
    mChart.setMaxVisibleValueCount(60);
    // scaling can now only be done on x- and y-axis separately
    // draw shadows for each bar that show the maximum value
    // mChart.setDrawBarShadow(true);
    // mChart.setDrawXLabels(false);
    mChart.setBackgroundColor(getResources().getColor(R.color.material_white_2));
    mChart.setDrawGridBackground(false);
    mChart.animateXY(500, 3000);//x, y축 애니메이션
    // mChart.setDrawYLabels(false);

    XAxis xAxis = mChart.getXAxis();
    xAxis.setTextSize(10f);
    xAxis.setDrawLimitLinesBehindData(true);
    xAxis.setTextColor(getResources().getColor(R.color.dark_gray));
    xAxis.setPosition(XAxis.XAxisPosition.TOP);
    xAxis.setGridColor(getResources().getColor(R.color.hint));
    xAxis.setSpaceBetweenLabels(2);


    YAxis leftAxis = mChart.getAxisLeft();
    leftAxis.setGridColor(getResources().getColor(R.color.hint));
    leftAxis.setLabelCount(8, false);
    leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
    leftAxis.setTextSize(10f);
    leftAxis.setTextColor(getResources().getColor(R.color.dark_gray));
    leftAxis.setSpaceTop(15f);

    YAxis rightAxis = mChart.getAxisRight();
    rightAxis.setDrawLabels(false);
    rightAxis.setGridColor(getResources().getColor(R.color.hint));
    rightAxis.setLabelCount(8, false);
    rightAxis.setSpaceTop(15f);

    Legend l = mChart.getLegend();
    l.setTextColor(getResources().getColor(R.color.light_red));
    l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
    l.setForm(Legend.LegendForm.SQUARE);
    l.setFormSize(9f);
    l.setTextSize(10f);
    l.setXEntrySpace(4f);

    //Date picker Set
    datePickerSet();
    asyncTask_AlbaList_Call();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_calendar_period_start:
               /* DatePickerDialog startDialog = new DatePickerDialog(this, startListener,
                        mStartYear, mStartMonth, mStartDay);
                startDialog.show();*/

        final com.rey.material.app.DatePickerDialog
            startDialog = new com.rey.material.app.DatePickerDialog(this);
        startDialog.dateRange(1, 1, 1987, 31, 12, 2100);
        startDialog.date(mStartDay, mStartMonth, mStartYear);
        startDialog.positiveAction("확인");
        startDialog.positiveActionClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mTxtCalendarPeriodStart.setText(startDialog.getYear() + "년 " + (startDialog.getMonth() + 1) + "월 ");

            mStartYear = startDialog.getYear();
            mStartMonth = startDialog.getMonth();
            mStartDay = startDialog.getDay();
            mPref.put(mPref.KEY_BAR_PERIOD_START, mStartYear + "-" + mStartMonth + "-" + mStartDay);
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

           /*     DatePickerDialog endDialog = new DatePickerDialog(this, endListener,
                        mEndYear, mEndMonth, mEndDay);
                endDialog.show();*/
        final com.rey.material.app.DatePickerDialog
            endDialog = new com.rey.material.app.DatePickerDialog(this);
        endDialog.dateRange(1, 1, 1987, 1, 12, 2100);
        endDialog.date(mEndDay, mEndMonth, mEndYear);
        endDialog.positiveAction("확인");
        endDialog.positiveActionClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mTxtCalendarPeriodEnd.setText(endDialog.getYear() + "년 " + (endDialog.getMonth() + 1) + "월 ");
            mEndYear = endDialog.getYear();
            mEndMonth = endDialog.getMonth();
            mEndDay = endDialog.getDay();

            mPref.put(mPref.KEY_BAR_PERIOD_END, mEndYear + "-" + mEndMonth + "-" + mEndDay);
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

  private void chartDataSet() {
    Log.i("hwoh", "chartDataSet");
    //DataSet
    // mWorkDataList = (ArrayList<WorkItem>) getIntent().getSerializableExtra("WorkData");
    if (!mWorkCalList.isEmpty()) {
      ArrayList<BarEntry> entries = new ArrayList<>();
      ArrayList<String> labels = new ArrayList<String>();
      mChart.clear();
      entries.clear();
      labels.clear();

      Calendar c1 = Calendar.getInstance();
      Calendar c2 = Calendar.getInstance();

      Log.i(TAG, "mWorkCalList");
      int monthTotal = 0;
      for (int i = 0; i < mWorkCalList.size(); i++) {
        Log.i(TAG, "c1 : " + c1.getTime());
        Log.i(TAG, "c2 : " + c2.getTime());
        BarItem barItem = new BarItem();
        c1.set(mWorkCalList.get(i).getYear(), mWorkCalList.get(i).getMonth(), mPartInfoData_Intent.getWorkMonthDay());
        c2.set(mWorkCalList.get(i).getYear(), mWorkCalList.get(i).getMonth(), mPartInfoData_Intent.getWorkMonthDay());
        c2.add(Calendar.MONTH, 1);
        c2.add(Calendar.DATE, -1);
        int totalMoney = totalMoneyReturn(c1, c2);
        barItem.setMonth(mWorkCalList.get(i).getYear() + "." + String.format("%02d", mWorkCalList.get(i).getMonth() + 1));
        barItem.setMonthTotal(totalMoney);
        monthTotal += totalMoney;
        entries.add(new BarEntry(barItem.getMonthTotal(), i));
        labels.add(barItem.getMonth());
        Log.i("hwoh", "MONTH : " + barItem.getMonth());
        Log.i("hwoh", "TOTAL : " + barItem.getMonthTotal());
      }

      Log.i("entries", Integer.toString(entries.size()));
      Log.i("labels", Integer.toString(labels.size()));

      //Chart Data Set
      BarDataSet dataset = new BarDataSet(entries, "월급");
      dataset.setBarSpacePercent(35f);
      dataset.setColor(getResources().getColor(R.color.newwork_background));
      BarData d = new BarData(labels, dataset);

      d.setValueTextColor(getResources().getColor(R.color.light_red));
      d.setValueTextSize(9f);
      mChart.setData(d);
      mChart.invalidate();
      mTxtTotal.setText(mTxtCalendarPeriodStart.getText() + "부터 " + mTxtCalendarPeriodEnd.getText() + "까지의 총 누적액은 " + mNumFomat.format(monthTotal) + "원 입니다.");
    }
  }

  // 초기 데이트 피커 셋팅
  public void datePickerSet() {
    //DatePicker Data Set
    mPeriod_start = HYStringUtill.splitFunction(mPref.getValue(mPref.KEY_BAR_PERIOD_START, mYear + "-" + 0 + "-" + 1));
    mPeriod_end = HYStringUtill.splitFunction(mPref.getValue(mPref.KEY_BAR_PERIOD_END, mYear + "-" + 11 + "-" + 31));

    mStartYear = Integer.parseInt(mPeriod_start[0]);
    mStartMonth = Integer.parseInt(mPeriod_start[1]);
    mStartDay = Integer.parseInt(mPeriod_start[2]);

    mEndYear = Integer.parseInt(mPeriod_end[0]);
    mEndMonth = Integer.parseInt(mPeriod_end[1]);
    mEndDay = Integer.parseInt(mPeriod_end[2]);
    mTxtCalendarPeriodStart.setText(mStartYear + "년 " + (mStartMonth + 1) + "월 ");
    mTxtCalendarPeriodEnd.setText(mEndYear + "년 " + (mEndMonth + 1) + "월 ");

  }

  // 선택된 기간에 데이터 추출
  public void periodDate(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay) {
    mCdlPeroidLatch = new CountDownLatch(1);
    mWorkCalList.clear();
    Calendar c1 = Calendar.getInstance();
    Calendar c2 = Calendar.getInstance();

    //Calendar 타입으로 변경 add()메소드로 1일씩 추가해 주기위해 변경
    c1.set(startYear, startMonth, startDay, 0, 0);
    c2.set(endYear, endMonth, endDay, 0, 0);
    Log.d(TAG, "time1 :: " + c1.getTime().toString());
    Log.d(TAG, "time2 :: " + c2.getTime().toString());

    //시작날짜와 끝 날짜를 비교해, 시작날짜가 작거나 같은 경우 출력
    while (c1.compareTo(c2) != 1) {
      Log.d(TAG, "time :: " + c1.getTime().toString());
      BarCalendarItem bcal = new BarCalendarItem();
      bcal.setYear(c1.get(Calendar.YEAR));
      bcal.setMonth(c1.get(Calendar.MONTH));
      mWorkCalList.add(bcal);
      c1.add(Calendar.MONTH, 1);

    }

    mCdlPeroidLatch.countDown();


  }

  public int totalMoneyReturn(Calendar c1, Calendar c2) {
    int totalMoney = 0;
    while (c1.compareTo(c2) != 1) {
      //출력
      Log.d(TAG, c1.getTime().toString());
      Log.d(TAG, Integer.toString(c1.get(Calendar.YEAR)) + "-" + Integer.toString(c1.get(Calendar.MONTH) + 1) + "-" + Integer.toString(c1.get(Calendar.DATE)));

      PartTimeItem results = mDBManager.selectOneData(DBConstant.COLUMN_ALBANAME, mPartInfoData_Intent.getAlbaname(), DBConstant.COLUMN_DATE, Integer.toString(c1.get(Calendar.YEAR)) + "-" + Integer.toString(c1.get(Calendar.MONTH) + 1) + "-" + Integer.toString(c1.get(Calendar.DATE)));
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


  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_chart, menu);
    return true;

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        break;
      case R.id.action_header_on:
        mHeader_barchart.setVisibility(View.VISIBLE);
        mHeaderFlag = true;
        break;

      case R.id.action_header_off:
        mHeader_barchart.setVisibility(View.GONE);
        mHeaderFlag = false;

        break;

    }
    invalidateOptionsMenu();
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    if (mHeaderFlag) {
      menu.getItem(0).setVisible(false);
      menu.getItem(1).setVisible(true);
    } else {
      menu.getItem(0).setVisible(true);
      menu.getItem(1).setVisible(false);
    }
    return super.onPrepareOptionsMenu(menu);

  }

  public void asyncTask_AlbaList_Call() {
    if (INFO)
      Log.i(TAG, "asyncTask_AlbaList_Call");
    new asyncTask_AlbaList().execute();
  }

  private class asyncTask_AlbaList extends AsyncTask<Void, Integer, Void> {
    private ProgressDialog mLoadingDialog;

    @Override
    protected void onPreExecute() {
      // TODO Auto-generated method stub
      super.onPreExecute();
      mLoadingDialog = showLoadingDialog();

    }

    @Override
    protected Void doInBackground(Void... params) {
      // TODO Auto-generated method stub
      periodDate(mStartYear, mStartMonth, mStartDay, mEndYear, mEndMonth, mEndDay);
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      // TODO Auto-generated method stub

      // TODO Auto-generated method stub
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          try {
            mCdlPeroidLatch.await();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          chartDataSet();
          mLoadingDialog.dismiss();
        }
      });


    }
  }

  public ProgressDialog showLoadingDialog() {
    ProgressDialog dialog = new ProgressDialog(this);
    dialog.setMessage("Loding..");
    dialog.setIndeterminate(true);
    dialog.show();
    return dialog;
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
