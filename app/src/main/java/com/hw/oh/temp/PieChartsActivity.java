package com.hw.oh.temp;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.ProgressDialog;
import android.database.Cursor;
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
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hw.oh.model.PartTimeItem;
import com.hw.oh.sqlite.DBConstant;
import com.hw.oh.sqlite.DBManager;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.HYStringUtill;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


/**
 * Created by oh on 2015-02-01.
 */
public class PieChartsActivity extends BaseActivity implements View.OnClickListener {
  public static final String TAG = "PieChartsActivity";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;

  // PieChart
  private PieChart mChart;

  // PieChart Data
  // private float[] yData = { 5, 10, 15, 30, 40 };
  //  private String[] xData = { "Sony", "Huawei", "LG", "Apple", "Samsung" };
  private int mTotalMoney = 0;
  private ArrayList<Map> mWorkDataList = new ArrayList<Map>();

  // View
  private LinearLayout mBtnCalendarPeriodStart, mBtnCalendarPeriodEnd;
  private TextView mTxtCalendarPeriodStart, mTxtCalendarPeriodEnd;
  private TextView mTxtTotalView;

  private LinearLayout mHeader_piechart;
  private Boolean mHeaderFlag = false;

  // Calendar
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
  private String[] mPeriod_start;
  private String[] mPeriod_end;


  // ActionBar
  private Toolbar mToolbar;

  //Util
  private HYFont mFont;
  private HYPreference mPref;
  private CountDownLatch mCdlPeroidLatch;


  private NumberFormat mNumFomat = new DecimalFormat("###,###,###");
  private DBManager mDBManager = new DBManager(this);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_piechart);
    // 구글 통계
    Tracker mTracker = ((ApplicationClass) getApplication()).getDefaultTracker();
    mTracker.setScreenName("알바별 원형 차트");
    mTracker.send(new HitBuilders.AppViewBuilder().build());

    //Util
    mFont = new HYFont(this);
    ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
    mFont.setGlobalFont(root);
    mPref = new HYPreference(this);

    //ActionBar
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    if (mToolbar != null) {
      mToolbar.setTitle("알바별 그래프");
      setSupportActionBar(mToolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      mFont.setGlobalFont((ViewGroup) mToolbar);
    }


    //View Set
    mTxtTotalView = (TextView) findViewById(R.id.txtTotalMoney);
    mTxtCalendarPeriodStart= (TextView) findViewById(R.id.txt_calendar_period_start);
    mTxtCalendarPeriodEnd= (TextView) findViewById(R.id.txt_calendar_period_end);
    mBtnCalendarPeriodStart = (LinearLayout) findViewById(R.id.btn_calendar_period_start);
    mBtnCalendarPeriodStart.setOnClickListener(this);
    mBtnCalendarPeriodEnd = (LinearLayout) findViewById(R.id.btn_calendar_period_end);
    mBtnCalendarPeriodEnd.setOnClickListener(this);

    mHeader_piechart = (LinearLayout) findViewById(R.id.header_piechart);
    mHeader_piechart.setVisibility(View.GONE);

    // add pie chart
    mChart = (PieChart) findViewById(R.id.chart1);

    // configure pie chart
    mChart.setCenterTextColor(getResources().getColor(R.color.gray));
    mChart.setBackgroundColor(getResources().getColor(R.color.material_white_2));
    mChart.setUsePercentValues(true);
    mChart.setDescription("알바 별 금액 비교");
    mChart.setDescriptionTextSize(10);

    // enable hole and configure
    mChart.setDrawHoleEnabled(true);
    mChart.setHoleRadius(7);
    mChart.setTransparentCircleRadius(10);

    // enable rotation of the chart by touch
    mChart.setRotationAngle(0);
    mChart.setRotationEnabled(true);
    mChart.animateXY(500, 3000);//x, y축 애니메이션

    // set a chart value selected listener
    mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {


      @Override
      public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        if (e == null)
          return;
        Log.i(TAG, Integer.toString(dataSetIndex));
        Toast.makeText(getApplicationContext(),
            mWorkDataList.get(
                e.getXIndex()).get("AlbaName") + " = " + mNumFomat.format(e.getVal()) + "원", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onNothingSelected() {

      }
    });

    // date picker set
    datePickerSet();
    // data Set
    asyncTask_AlbaList_Call();
    // customize legends
    // ADmob
    if (Constant.ADMOB) {
      //if (true) {
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

  private void chartAddData() {
    ArrayList<Entry> yVals1 = new ArrayList<Entry>();
    mTotalMoney = 0;
    for (int i = 0; i < mWorkDataList.size(); i++) {
      mTotalMoney += Integer.parseInt(mWorkDataList.get(i).get("TotalMoney").toString());
      yVals1.add(new Entry(Integer.parseInt(mWorkDataList.get(i).get("TotalMoney").toString()), i));
    }
    mTxtTotalView.setText(mNumFomat.format(mTotalMoney) + "원");

    ArrayList<String> xVals = new ArrayList<String>();

    for (int i = 0; i < mWorkDataList.size(); i++)
      xVals.add(mWorkDataList.get(i).get("AlbaName").toString());

    // create pie data set
    PieDataSet dataSet = new PieDataSet(yVals1, "");
    dataSet.setSliceSpace(3);
    dataSet.setSelectionShift(5);

    // add many colors
    ArrayList<Integer> colors = new ArrayList<Integer>();

    for (int c : ColorTemplate.VORDIPLOM_COLORS)
      colors.add(c);

    for (int c : ColorTemplate.JOYFUL_COLORS)
      colors.add(c);

    for (int c : ColorTemplate.COLORFUL_COLORS)
      colors.add(c);

    for (int c : ColorTemplate.LIBERTY_COLORS)
      colors.add(c);

    for (int c : ColorTemplate.PASTEL_COLORS)
      colors.add(c);

    colors.add(ColorTemplate.getHoloBlue());
    dataSet.setColors(colors);

    // instantiate pie data object now

    PieData data = new PieData(xVals, dataSet);
    data.setValueTextSize(9f);
    data.setValueTextColor(getResources().getColor(R.color.light_red));

    mChart.setData(data);

    // undo all highlights
    mChart.highlightValues(null);

    // update pie chart
    mChart.invalidate();

    Legend l = mChart.getLegend();
    l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
    l.setXEntrySpace(7);
    l.setYEntrySpace(5);


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
            mTxtCalendarPeriodStart.setText(startDialog.getYear() + "년 " + (startDialog.getMonth() + 1) + "월 " + startDialog.getDay() + "일 ");

            mStartYear = startDialog.getYear();
            mStartMonth = startDialog.getMonth();
            mStartDay = startDialog.getDay();
            mPref.put(mPref.KEY_PIE_PERIOD_START, mStartYear + "-" + mStartMonth + "-" + mStartDay);
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
            mPref.put(mPref.KEY_PIE_PERIOD_END, mEndYear + "-" + mEndMonth + "-" + mEndDay);

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

  /*  private DatePickerDialog.OnDateSetListener startListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mBtnCalendarPeriodStart.setText(year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일 ");

            mStartYear = year;
            mStartMonth = monthOfYear;
            mStartDay = dayOfMonth;
            mPref.put(mPref.KEY_PIE_PERIOD_START, mStartYear + "-" + mStartMonth + "-" + mStartDay);
            asyncTask_AlbaList_Call();

        }
    };
    private DatePickerDialog.OnDateSetListener endListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mBtnCalendarPeriodEnd.setText(year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일 ");

            mEndYear = year;
            mEndMonth = monthOfYear;
            mEndDay = dayOfMonth;
            mPref.put(mPref.KEY_PIE_PERIOD_END, mEndYear + "-" + mEndMonth + "-" + mEndDay);
           asyncTask_AlbaList_Call();

        }
    };*/

  // 초기 데이트 피커 셋팅
  public void datePickerSet() {
    //DatePicker Data Set
    mPeriod_start = HYStringUtill.splitFunction(mPref.getValue(mPref.KEY_PIE_PERIOD_START, mYear + "-" + 0 + "-" + 1));
    mPeriod_end = HYStringUtill.splitFunction(mPref.getValue(mPref.KEY_PIE_PERIOD_END, mYear + "-" + 11 + "-" + 31));

    mStartYear = Integer.parseInt(mPeriod_start[0]);
    mStartMonth = Integer.parseInt(mPeriod_start[1]);
    mStartDay = Integer.parseInt(mPeriod_start[2]);

    mEndYear = Integer.parseInt(mPeriod_end[0]);
    mEndMonth = Integer.parseInt(mPeriod_end[1]);
    mEndDay = Integer.parseInt(mPeriod_end[2]);
    mTxtCalendarPeriodStart.setText(mStartYear + "년 " + (mStartMonth + 1) + "월 " + mStartDay + "일 ");
    mTxtCalendarPeriodEnd.setText(mEndYear + "년 " + (mEndMonth + 1) + "월 " + mEndDay + "일 ");

  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    if (mHeaderFlag) {
      Log.d(TAG, "1");
      menu.getItem(0).setVisible(false);
      menu.getItem(1).setVisible(true);
    } else {
      Log.d(TAG, "2");
      menu.getItem(0).setVisible(true);
      menu.getItem(1).setVisible(false);
    }
    return super.onPrepareOptionsMenu(menu);

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
        mHeader_piechart.setVisibility(View.VISIBLE);
        mHeaderFlag = true;
        break;

      case R.id.action_header_off:
        mHeader_piechart.setVisibility(View.GONE);
        mHeaderFlag = false;

        break;

    }
    invalidateOptionsMenu();
    return super.onOptionsItemSelected(item);
  }

  public void workDataSet() {
    mCdlPeroidLatch = new CountDownLatch(1);
    mWorkDataList.clear();
    Cursor results = mDBManager.selectAll(DBConstant.TABLE_PARTTIMEINFO);
    results.moveToFirst();
    while (!results.isAfterLast()) {
      mWorkDataList.add(periodDate(results.getString(results.getColumnIndex("albaname")), mStartYear, mStartMonth, mStartDay, mEndYear, mEndMonth, mEndDay));
      results.moveToNext();
    }
    results.close();
    mCdlPeroidLatch.countDown();
  }

  // 선택된 기간에 데이터 추출
  public Map periodDate(String albaName, int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay) {

    //Date타입으로 변경

    int mTotalMoney = 0;
    Calendar c1 = Calendar.getInstance();
    Calendar c2 = Calendar.getInstance();

    //Calendar 타입으로 변경 add()메소드로 1일씩 추가해 주기위해 변경
    c1.set(startYear, startMonth, startDay);
    c2.set(endYear, endMonth, endDay);
    Log.d(TAG, "time1 :: " + c1.getTime().toString());
    Log.d(TAG, "time2 :: " + c2.getTime().toString());

    //시작날짜와 끝 날짜를 비교해, 시작날짜가 작거나 같은 경우 출력
    Map map = new HashMap();
    while (c1.compareTo(c2) != 1) {
      //출력
      Log.d(TAG, c1.getTime().toString());
      Log.d(TAG, Integer.toString(c1.get(Calendar.YEAR)) + "-" + Integer.toString(c1.get(Calendar.MONTH) + 1) + "-" + Integer.toString(c1.get(Calendar.DATE)));
      PartTimeItem results = mDBManager.selectOneData(DBConstant.COLUMN_ALBANAME, albaName, DBConstant.COLUMN_DATE, Integer.toString(c1.get(Calendar.YEAR)) + "-" + Integer.toString(c1.get(Calendar.MONTH) + 1) + "-" + Integer.toString(c1.get(Calendar.DATE)));
      try {
        if ((results instanceof PartTimeItem)) {
          Double resultMoney = Double.parseDouble(results.getWorkPayTotal());
          Log.i(TAG, results.getWorkPayTotal());
          mTotalMoney += resultMoney;
        }
        //시작날짜 + 1 일
        c1.add(Calendar.DATE, 1);
        Log.d(TAG, "time :: " + c1.getTime().toString());
      } catch (Exception e) {
        Log.e(TAG, "Exception :: " + e.toString());
        break;
      }
    }
    map.put("AlbaName", albaName);
    map.put("TotalMoney", mTotalMoney);

    return map;
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
      mLoadingDialog = showLoadingDialog();

    }

    @Override
    protected Void doInBackground(Void... params) {
      // TODO Auto-generated method stub
      workDataSet();
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      // TODO Auto-generated method stub
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          try {
            mCdlPeroidLatch.await();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          chartAddData();
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


