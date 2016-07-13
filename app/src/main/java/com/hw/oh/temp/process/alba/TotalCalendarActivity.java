package com.hw.oh.temp.process.alba;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hw.oh.adapter.TotalCalendarAdapter;
import com.hw.oh.model.PartTimeItem;
import com.hw.oh.sqlite.DBConstant;
import com.hw.oh.sqlite.DBManager;
import com.hw.oh.temp.ApplicationClass;
import com.hw.oh.temp.BaseActivity;
import com.hw.oh.temp.R;
import com.hw.oh.utility.CommonUtil;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYPreference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by oh on 2015-02-01.
 */
public class TotalCalendarActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    public static final String TAG = "TotalCalendarActivity";
    public static final boolean DBUG = true;
    public static final boolean INFO = true;

    private boolean shouldShow = true;
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private CompactCalendarView compactCalendarView;

    @BindView(R.id.bookings_listview)
    ListView listView;


    // List Header
    private View mlistViewHeader;
    private TextView mTxtDayTotal;
    private TextView mTxtToday;

    private TotalCalendarAdapter mTotalCalendarAdapter;
    public DBManager mDB;

    private ArrayList<PartTimeItem> mPartTimeAllDataList = new ArrayList<PartTimeItem>(); //전체 데이터
    private ArrayList<PartTimeItem> mPartTimeMonthDataList = new ArrayList<PartTimeItem>(); //전체 데이터
    private ArrayList<PartTimeItem> mPartTimeDayDataList = new ArrayList<PartTimeItem>(); //전체 데이터

    private Double mDayTotalMoney = 0.0;
    private int mSelMonth;
    private int mSelDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_calendar);
        //ButterKnife
        ButterKnife.bind(this);

        // 구글 통계
        Tracker mTracker = ((ApplicationClass) getApplication()).getDefaultTracker();
        mTracker.setScreenName("전체알바 보기_(TotalCalendarActivity)");
        mTracker.send(new HitBuilders.AppViewBuilder().build());

        //Util
        mPref = new HYPreference(this);
        final ActionBar actionBar = setToolbar("");
        mDB = new DBManager(this);


        mTotalCalendarAdapter = new TotalCalendarAdapter(this, mPartTimeDayDataList);
        listView.setAdapter(mTotalCalendarAdapter);
        listView.setOnItemClickListener(this);

        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendarView.setCalendarBackgroundColor(getThemeColor(0));
        compactCalendarView.setCurrentDayBackgroundColor(getResources().getColor(R.color.dark_gray));
        compactCalendarView.setCurrentSelectedDayBackgroundColor(getThemeColor(2));
        compactCalendarView.invalidate();

        // Alba listview header
        mlistViewHeader = getLayoutInflater().inflate(
                R.layout.header_day_calendar, listView, false);
        mTxtDayTotal = (TextView) mlistViewHeader.findViewById(R.id.txtTotalMoney);
        mTxtToday = (TextView) mlistViewHeader.findViewById(R.id.txtToday);
        listView.addHeaderView(mlistViewHeader);

        //set initial title
        actionBar.setTitle(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        //Selected day
        mSelMonth = compactCalendarView.getFirstDayOfCurrentMonth().getMonth();
        mSelDay = Calendar.getInstance().get(Calendar.DATE);

        //set title on calendar scroll
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                mSelDay = dateClicked.getDate();
                setTodayData();

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Log.d(TAG, " onMonthScroll " + firstDayOfNewMonth);
                actionBar.setTitle(dateFormatForMonth.format(firstDayOfNewMonth));
                asyncTask_Calendar_Call(firstDayOfNewMonth.getMonth());
                mSelMonth = compactCalendarView.getFirstDayOfCurrentMonth().getMonth();
                mSelDay = compactCalendarView.getFirstDayOfCurrentMonth().getDate();
            }
        });


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

    public void setTodayData() {
        mPartTimeDayDataList.clear();
        mDayTotalMoney = 0.0;
        for (PartTimeItem item : mPartTimeMonthDataList) {
            String[] dayToken = todaySplit(item.getDate());
            if (mSelDay == Integer.parseInt(dayToken[2])) {
                mPartTimeDayDataList.add(item);
                Log.d(TAG, " item " + item.getDate());

                int resultMoney = (int) Double.parseDouble(item.getWorkPayTotal());
                Log.d(TAG, " item " + item.getWorkPayTotal());
                mDayTotalMoney += resultMoney;
            }
        }
        mTotalCalendarAdapter.notifyDataSetChanged();
        mTxtDayTotal.setText(numberFomat.format(mDayTotalMoney));

        mSelMonth = compactCalendarView.getFirstDayOfCurrentMonth().getMonth();
        StringBuilder sb = new StringBuilder();
        sb.append(mSelMonth + 1);
        sb.append("월 ");
        sb.append(mSelDay);
        sb.append("일의 총 급여");
        mTxtToday.setText(sb.toString());
    }


    private void addEvents(CompactCalendarView compactCalendarView, int month, int day) {
        day = day - 1;
        currentCalender.setTime(new Date());
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = currentCalender.getTime();
        currentCalender.setTime(firstDayOfMonth);
        if (month > -1) {
            currentCalender.set(Calendar.MONTH, month);
        }
        currentCalender.add(Calendar.DATE, day);
        setToMidnight(currentCalender);
        long timeInMillis = currentCalender.getTimeInMillis();

        List<Event> events = getEvents(timeInMillis);
        compactCalendarView.addEvents(events);

    }

    private List<Event> getEvents(long timeInMillis) {
        return Arrays.asList(new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)));
    }

    private void setToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
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
                compactCalendarView.showCalendarWithAnimation();
                shouldShow = !shouldShow;
                break;

            case R.id.action_header_off:
                compactCalendarView.hideCalendarWithAnimation();

                shouldShow = !shouldShow;
                break;

        }
        invalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (shouldShow) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
        } else {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    protected void onResume() {
        super.onResume();
        asyncTask_Calendar_Call(compactCalendarView.getFirstDayOfCurrentMonth().getMonth());
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


    public void asyncTask_Calendar_Call(int month) {
        if (INFO)
            Log.i(TAG, "asyncTask_Calendar_Call");
        new asyncTask_Calendar(month).execute();
    }

    private class asyncTask_Calendar extends AsyncTask<Void, Integer, Void> {
        private int mMonth = 0;

        public asyncTask_Calendar(int month) {
            mMonth = month;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            compactCalendarView.removeAllEvents();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            Cursor result = mDB.selectAllData(DBConstant.TABLE_PARTTIMEDATA);
            result.moveToFirst();
            mPartTimeAllDataList.clear();
            mPartTimeMonthDataList.clear();
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
                item.setWorkAddType(result.getString(result.getColumnIndex("workAddType")));
                item.setWorkAddHour(result.getString(result.getColumnIndex("workPayAddHour")));
                item.setWorkAddMin(result.getString(result.getColumnIndex("workPayAddMin")));
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
                mPartTimeAllDataList.add(item);
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
                if (!mPartTimeAllDataList.isEmpty()) {
                    for (int i = 0; i < mPartTimeAllDataList.size(); i++) {
                        Log.i(TAG, "getDate" + mPartTimeAllDataList.get(i).getDate());
                        String[] dayToken = todaySplit(mPartTimeAllDataList.get(i).getDate());
                        if (!CommonUtil.isNull(dayToken[1])) {
                            if (mMonth == Integer.parseInt(dayToken[1]) - 1) {
                                Log.i(TAG, "mMonth" + mMonth);
                                Log.i(TAG, "dayToken" + Integer.parseInt(dayToken[2]));
                                addEvents(compactCalendarView, mMonth, Integer.parseInt(dayToken[2]));
                                mPartTimeMonthDataList.add(mPartTimeAllDataList.get(i));
                            }
                        } else {
                            Log.i(TAG, "null");
                        }
                    }
                }

                //현재날짜 확인
                setTodayData();
            }
        }

    }

    public String[] todaySplit(String toDay) {
        String delims = "-";
        return toDay.split(delims);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Constant.NEWWORK_INTENT_FLAG = Constant.NEWWORK_INTENT_UPDATE;
        Intent intent = new Intent(this, NewWorkActivity.class);
        PartTimeItem temp = mPartTimeDayDataList.get(position - 1);
        String[] dayToken = todaySplit(temp.getDate());
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
        intent.putExtra("ALBA_NAME", temp.getAlbaname());
        intent.putExtra("YEAR", dayToken[0]);
        intent.putExtra("MONTH", dayToken[1]);
        intent.putExtra("DAY", dayToken[2]);
        intent.putExtra("TODAY", "NONE");

        Log.i("hwoh", "getHourMoney" + temp.getHourMoney());
        Log.i("hwoh", "getAlbaname" + temp.getAlbaname());
        Log.i("hwoh", "ADD_PAY_TYPE" + temp.getWorkAddType());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}


