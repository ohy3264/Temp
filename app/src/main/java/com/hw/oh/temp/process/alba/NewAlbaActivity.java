package com.hw.oh.temp.process.alba;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hw.oh.dialog.AddInfoDialog;
import com.hw.oh.dialog.EtcDialog;
import com.hw.oh.dialog.MonthPayDialog;
import com.hw.oh.dialog.RefreshInfoDialog;
import com.hw.oh.dialog.WeekDialog;
import com.hw.oh.model.CalendarNoti;
import com.hw.oh.model.PartTimeInfo;
import com.hw.oh.service.AlarmService;
import com.hw.oh.sqlite.DBConstant;
import com.hw.oh.sqlite.DBManager;
import com.hw.oh.temp.ApplicationClass;
import com.hw.oh.temp.BaseActivity;
import com.hw.oh.temp.R;
import com.hw.oh.utility.CommonUtil;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYStringUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by oh on 2015-02-02.
 */
public class NewAlbaActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, RefreshInfoDialog.RefreshDialogListener, WeekDialog.WeekDialogListener, EtcDialog.EtcDialogListener, AddInfoDialog.AddDialogListener, MonthPayDialog.OnHeadlineSelectedListener {
    public static final String TAG = "NewPostActivity";
    public static final boolean DBUG = true;
    public static final boolean INFO = true;
    //View


    //Data
    private PartTimeInfo mPartInfoData, mPartInfoData_Intent;
    private int mStartMin;
    private int mStartHour;
    private int mEndMin;
    private int mEndHour;
    private int mID;
    private int mTotalTimeAll = 600;
    private int mWeekHourMoney = 0;
    private Double mWeekTime = 0.0;
    private int mEtcMoney = 0;
    private int mEtcNum = 0;
    private int mMonthDay = 1;
    private int mWeekDay = 1;
    private int mMonthWeekFlag = 1;

    private int mAddType = 0;
    private int mAddHour = 0;
    private int mAddMin = 0;
    private int mRefreshType = 0;
    private int mRefreshHour = 0;
    private int mRefreshMin = 0;
    private CalendarNoti mWeekItem = new CalendarNoti();

    //TextView
    @BindView(R.id.txtMonthPay)
    TextView mTxtMonthPay;
    @BindView(R.id.txtStartTime_hour)
    TextView mTxtStartTime_hour;
    @BindView(R.id.txtStartTime_min)
    TextView mTxtStartTime_min;
    @BindView(R.id.txtEndTime_hour)
    TextView mTxtEndTime_hour;
    @BindView(R.id.txtEndTime_min)
    TextView mTxtEndTime_min;
    @BindView(R.id.txtRefreshView1)
    TextView mTxtRefreshView1;
    @BindView(R.id.txtRefreshView2)
    TextView mTxtRefreshView2;
    @BindView(R.id.txtAddMoney)
    TextView mTxtAdd;
    @BindView(R.id.txtEtcMoney)
    TextView mTxtEtcMoney;
    @BindView(R.id.txtWeekMoney)
    TextView mTxtWeekMoney;
    @BindView(R.id.txtSave)
    TextView mTxtSave;

    //EditText
    @BindView(R.id.edtAlbaName)
    EditText mEdtAlbaName;
    @BindView(R.id.edtHourMoney)
    EditText mEdtHourMoney;
    @BindView(R.id.edtMemo)
    EditText mEdtMemo;

    //Lebel
    @BindView(R.id.linAddTimelebel)
    LinearLayout mLinAddTimelebel;
    @BindView(R.id.linNightlebel)
    LinearLayout mLinNightTimelebel;
    @BindView(R.id.linEtclebel)
    LinearLayout mLinEtcTimelebel;
    @BindView(R.id.linWeeklebel)
    LinearLayout mLinWeekTimelebel;
    @BindView(R.id.linRefreshlebel)
    LinearLayout mLinRefreshTimelebel;
    @BindView(R.id.linAlarmlebel)
    LinearLayout mLinAlarmlebel;
    @BindView(R.id.linWeekSelector)
    LinearLayout mLinWeekSelector;

    //CheckBox Weekend
    @BindView(R.id.chk_mon)
    CheckBox mChk_mon;
    @BindView(R.id.chk_thu)
    CheckBox mChk_thu;
    @BindView(R.id.chk_wed)
    CheckBox mChk_wed;
    @BindView(R.id.chk_thur)
    CheckBox mChk_thur;
    @BindView(R.id.chk_fri)
    CheckBox mChk_fri;
    @BindView(R.id.chk_sat)
    CheckBox mChk_sat;
    @BindView(R.id.chk_sun)
    CheckBox mChk_sun;

    //CheckBox work
    @BindView(R.id.chk_nightMoney)
    CheckBox mChkNightMoney;
    @BindView(R.id.chk_refreshTime)
    CheckBox mChkRefreshTime;
    @BindView(R.id.chk_addMoney)
    CheckBox mChkAddMoney;
    @BindView(R.id.chk_etcMoney)
    CheckBox mChkEtcPay;
    @BindView(R.id.chk_WeekMoney)
    CheckBox mChkWeekPay;
    @BindView(R.id.chk_Alaram)
    CheckBox mChk_Alaram;


    //Flag
    private Boolean mFlag_NightAdvance = false;
    private Boolean mFlag_RefreshTime = false;
    private Boolean mFlag_AddPay = false;
    private Boolean mFlag_EtcPay = false;
    private Boolean mFlag_WeekPay = false;
    private Boolean mFlag_Alarm = false;


    //AlarmManager
    private PendingIntent mAlarmSender;
    private AlarmManager mAlarmMgr;


    //Utill
    private DBManager mDB;
    private NumberFormat mNumFomat = new DecimalFormat("###,###,###");


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newalba);
        //ButterKnife
        ButterKnife.bind(this);

        // 구글 통계
        Tracker mTracker = ((ApplicationClass) getApplication()).getDefaultTracker();
        mTracker.setScreenName("새로운 알바 등록");
        mTracker.send(new HitBuilders.AppViewBuilder().build());

        mDB = new DBManager(this);

        //ActionBar
        setToolbar("알바등록");

        mChk_Alaram.setOnCheckedChangeListener(this);       // 알람체크
        mChkNightMoney.setOnCheckedChangeListener(this);    // 야간 체크
        mChkRefreshTime.setOnCheckedChangeListener(this);   // 휴식 체크
        mChkAddMoney.setOnCheckedChangeListener(this);      // 추가 체크
        mChkEtcPay.setOnCheckedChangeListener(this);        // 기타체크
        mChkWeekPay.setOnCheckedChangeListener(this);       // 주휴체크
        mChk_mon.setOnCheckedChangeListener(this);          // 알람체크박스
        mChk_thu.setOnCheckedChangeListener(this);
        mChk_wed.setOnCheckedChangeListener(this);
        mChk_thur.setOnCheckedChangeListener(this);
        mChk_fri.setOnCheckedChangeListener(this);
        mChk_sat.setOnCheckedChangeListener(this);
        mChk_sun.setOnCheckedChangeListener(this);

        //전체적인 기초 셋팅
        initFlagSet(getIntent().getStringExtra("Flag"));
        //체크 박스 & 라벨 초기세팅
        initCheckBox();
        //주말상태 셋팅
        initWeekNoti(getIntent().getStringExtra("Flag"));

        //알림정보 셋팅
        mAlarmSender = PendingIntent.getService(getApplicationContext(), mWeekItem.getRequestCode()
                , new Intent(this, AlarmService.class).putExtra("AlbaName", mEdtAlbaName.getText().toString()), PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);


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
    }

    public void initFlagSet(String flag) {
        switch (flag) {
            case "NEW":
                mTxtSave.setText("저장하기");
                setToolbar("알바저장");
                mTxtAdd.setText("8시간 이상 근무한 시급 1.5배 적용");
                mTxtWeekMoney.setText("한주간의 근로시간이 15시간 이상 시 적용");
                mTxtRefreshView1.setText("4시간 마다");
                mTxtRefreshView2.setText("30분 휴식");
                mWeekItem.setRequestCode(HYStringUtil.getRandomKey(4));
                break;
            case "UPDATE":
                mTxtSave.setText("수정하기");
                setToolbar("알바수정");
                mPartInfoData_Intent = (PartTimeInfo) getIntent().getSerializableExtra("ObjectData");
                mID = mPartInfoData_Intent.get_id();
                mEdtAlbaName.setText(mPartInfoData_Intent.getAlbaname());
                mEdtHourMoney.setText(mPartInfoData_Intent.getHourMoney());
                mEdtMemo.setText(mPartInfoData_Intent.getSimpleMemo());
                mTxtStartTime_hour.setText(mPartInfoData_Intent.getStartTimeHour());
                mTxtStartTime_min.setText(mPartInfoData_Intent.getStartTimeMin());
                mTxtEndTime_hour.setText(mPartInfoData_Intent.getEndTimeHour());
                mTxtEndTime_min.setText(mPartInfoData_Intent.getEndTimeMin());
                mFlag_NightAdvance = Boolean.parseBoolean(mPartInfoData_Intent.getWorkPayNight());
                mFlag_RefreshTime = Boolean.parseBoolean(mPartInfoData_Intent.getWorkRefresh());
                mFlag_AddPay = Boolean.parseBoolean(mPartInfoData_Intent.getWorkPayAdd());
                mRefreshType = Integer.parseInt(mPartInfoData_Intent.getWorkRefreshType());
                mRefreshHour = Integer.parseInt(mPartInfoData_Intent.getWorkRefreshHour());
                mRefreshMin = Integer.parseInt(mPartInfoData_Intent.getWorkRefreshMin());
                mFlag_EtcPay = Boolean.parseBoolean(mPartInfoData_Intent.getWorkPayEtc());
                mEtcMoney = Integer.parseInt(mPartInfoData_Intent.getWorkPayEtcMoney());
                mEtcNum = Integer.parseInt(mPartInfoData_Intent.getWorkPayEtcNum());
                mWeekHourMoney = mPartInfoData_Intent.getWorkPayWeekMoney();
                mWeekTime = mPartInfoData_Intent.getWorkPayWeekTime();
                mAddType = Integer.parseInt(mPartInfoData_Intent.getWorkAddType());
                mAddHour = Integer.parseInt(mPartInfoData_Intent.getWorkPayAddHour());
                mAddMin = Integer.parseInt(mPartInfoData_Intent.getWorkPayAddMin());

                mMonthDay = mPartInfoData_Intent.getWorkMonthDay();
                mWeekDay = mPartInfoData_Intent.getWorkWeekDay();
                mMonthWeekFlag = mPartInfoData_Intent.getWorkMonthWeekFlag();

                mFlag_Alarm = mPartInfoData_Intent.getWorkAlarm();
                switch (mPartInfoData_Intent.getWorkAddType()) {
                    case "0":
                        mTxtAdd.setText("8시간 이상 근무한 시급 1.5배 적용");
                        break;
                    case "1":
                        mTxtAdd.setText("풀타임 근무한 시급 1.5배 적용");
                        break;
                    case "2":
                        mTxtAdd.setText("입력한 시간 만큼 시급 1.5배 적용");
                        break;
                }
                switch (mRefreshType) {
                    case 0:
                        mTxtRefreshView1.setText("4시간 마다");
                        mTxtRefreshView2.setText("30분 휴식");
                        break;

                    case 1:
                        mTxtRefreshView1.setText(mRefreshHour + " 시간");
                        mTxtRefreshView2.setText(mRefreshMin + " 분 휴식");
                        break;
                }
                //월급날
                onArticleSelected(mMonthWeekFlag, mWeekDay, mMonthDay);
                //월급상태
                workHourSet();
                break;
        }
    }

    public void initCheckBox() {
        //체크박스 상태 갱신
        mChkWeekPay.setChecked(mFlag_WeekPay);
        mChkEtcPay.setChecked(mFlag_EtcPay);
        mChkAddMoney.setChecked(mFlag_AddPay);
        mChkRefreshTime.setChecked(mFlag_RefreshTime);
        mChkNightMoney.setChecked(mFlag_NightAdvance);
        mChk_Alaram.setChecked(mFlag_Alarm);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //체크박스에 따른 view 상태 갱신
                if (mFlag_WeekPay) {
                    mLinWeekTimelebel.setVisibility(View.VISIBLE);
                } else {
                    mLinWeekTimelebel.setVisibility(View.INVISIBLE);
                }
                if (mFlag_EtcPay) {
                    mLinEtcTimelebel.setVisibility(View.VISIBLE);
                } else {
                    mLinEtcTimelebel.setVisibility(View.INVISIBLE);
                }
                if (mFlag_AddPay) {
                    mLinAddTimelebel.setVisibility(View.VISIBLE);
                } else {
                    mLinAddTimelebel.setVisibility(View.INVISIBLE);
                }
                if (mFlag_RefreshTime) {
                    mLinRefreshTimelebel.setVisibility(View.VISIBLE);
                } else {
                    mLinRefreshTimelebel.setVisibility(View.INVISIBLE);
                }
                if (mFlag_NightAdvance) {
                    mLinNightTimelebel.setVisibility(View.VISIBLE);
                } else {
                    mLinNightTimelebel.setVisibility(View.INVISIBLE);
                }
                if (mFlag_Alarm) {
                    mLinAlarmlebel.setVisibility(View.VISIBLE);
                    mLinWeekSelector.setVisibility(View.VISIBLE);
                } else {
                    mLinAlarmlebel.setVisibility(View.VISIBLE);
                    mLinWeekSelector.setVisibility(View.GONE);
                }
            }
        });

    }

    public void initWeekNoti(String flag) {
        Log.i(TAG, "initWeekNoti");
        switch (flag) {
            case "NEW":
                mWeekItem.setSun(false);
                mWeekItem.setMon(false);
                mWeekItem.setThu(false);
                mWeekItem.setWed(false);
                mWeekItem.setThur(false);
                mWeekItem.setFri(false);
                mWeekItem.setSat(false);
                break;
            case "UPDATE":
                try {
                    Cursor result = mDB.selectWEEKSeachData("albaname", mPartInfoData_Intent.getAlbaname());
                    if (result.moveToFirst()) {
                        result.getInt(result.getColumnIndex("_id"));
                        Log.i(TAG, Integer.toString(result.getColumnIndex("_id")));
                        mWeekItem.setAlbaName(result.getString(result.getColumnIndex("albaname")));
                        Log.i(TAG, mWeekItem.getAlbaName());
                        mWeekItem.setSun(Boolean.parseBoolean(result.getString(result.getColumnIndex("sun"))));
                        Log.i(TAG, result.getString(result.getColumnIndex("sun")));
                        mWeekItem.setMon(Boolean.parseBoolean(result.getString(result.getColumnIndex("mon"))));
                        Log.i(TAG, result.getString(result.getColumnIndex("mon")));
                        mWeekItem.setThu(Boolean.parseBoolean(result.getString(result.getColumnIndex("thu"))));
                        Log.i(TAG, result.getString(result.getColumnIndex("thu")));
                        mWeekItem.setWed(Boolean.parseBoolean(result.getString(result.getColumnIndex("wed"))));
                        Log.i(TAG, result.getString(result.getColumnIndex("wed")));
                        mWeekItem.setThur(Boolean.parseBoolean(result.getString(result.getColumnIndex("thur"))));
                        Log.i(TAG, result.getString(result.getColumnIndex("thur")));
                        mWeekItem.setFri(Boolean.parseBoolean(result.getString(result.getColumnIndex("fri"))));
                        Log.i(TAG, result.getString(result.getColumnIndex("fri")));
                        mWeekItem.setSat(Boolean.parseBoolean(result.getString(result.getColumnIndex("sat"))));
                        Log.i(TAG, result.getString(result.getColumnIndex("sat")));
                        mWeekItem.setRequestCode(result.getInt(result.getColumnIndex("requestCode")));
                        Log.i(TAG, Integer.toString(result.getColumnIndex("requestCode")));
                    } else {
                        mWeekItem.setAlbaName(mPartInfoData_Intent.getAlbaname());
                        mWeekItem.setRequestCode(HYStringUtil.getRandomKey(4));
                        mWeekItem.setSun(false);
                        mWeekItem.setMon(false);
                        mWeekItem.setThu(false);
                        mWeekItem.setWed(false);
                        mWeekItem.setThur(false);
                        mWeekItem.setFri(false);
                        mWeekItem.setSat(false);
                        if (mDB.insertWeekInfo(mWeekItem)) {
                            Log.i(TAG, "TABLE 없음 생성");
                        } else {
                            Log.i(TAG, "TABLE 없음 생성 실패");
                        }
                    }
                    result.close();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                } finally {
                    Log.i(TAG, "Cursor close");
                }
                break;
        }
        mChk_mon.setChecked(mWeekItem.isMon());
        mChk_thu.setChecked(mWeekItem.isThu());
        mChk_wed.setChecked(mWeekItem.isWed());
        mChk_thur.setChecked(mWeekItem.isThur());
        mChk_fri.setChecked(mWeekItem.isFri());
        mChk_sat.setChecked(mWeekItem.isSat());
        mChk_sun.setChecked(mWeekItem.isSun());
    }

    @OnClick({R.id.linStartTime, R.id.linEndTime, R.id.linRefreshTime, R.id.linEtc, R.id.linWeek, R.id.linAddTime, R.id.linMonthPay, R.id.linSave})
    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.linStartTime:
                if (mPref.getValue(mPref.KEY_PICKER_THEME, 0) == 0) {
                    final com.rey.material.app.TimePickerDialog startDialog = new com.rey.material.app.TimePickerDialog(this);
                    startDialog.hour(Integer.parseInt(mTxtStartTime_hour.getText().toString()));
                    startDialog.minute(Integer.parseInt(mTxtStartTime_min.getText().toString()));
                    startDialog.positiveAction("확인");
                    startDialog.positiveActionClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mStartHour = startDialog.getHour();
                            mStartMin = startDialog.getMinute();
                            mTxtStartTime_hour.setText(Integer.toString(mStartHour));
                            mTxtStartTime_min.setText(Integer.toString(mStartMin));
                            workHourSet();
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
                } else if (mPref.getValue(mPref.KEY_PICKER_THEME, 0) == 1) {
                    TimePickerDialog startDialogICS = new TimePickerDialog(this, TimePickerDialog.THEME_HOLO_DARK, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mStartHour = hourOfDay;
                            mStartMin = minute;
                            mTxtStartTime_hour.setText(Integer.toString(hourOfDay));
                            mTxtStartTime_min.setText(Integer.toString(minute));
                            workHourSet();
                        }
                    }, Integer.parseInt(mTxtStartTime_hour.getText().toString()), Integer.parseInt(mTxtStartTime_min.getText().toString()), false);
                    startDialogICS.show();
                } else {
                    TimePickerDialog startDialogICS = new TimePickerDialog(this, TimePickerDialog.THEME_HOLO_DARK, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                            mStartHour = hourOfDay;
                            mStartMin = minute;
                            mTxtStartTime_hour.setText(Integer.toString(hourOfDay));
                            mTxtStartTime_min.setText(Integer.toString(minute));
                            workHourSet();
                        }
                    }, Integer.parseInt(mTxtStartTime_hour.getText().toString()), Integer.parseInt(mTxtStartTime_min.getText().toString()), true);
                    startDialogICS.show();
                }
                break;

            case R.id.linEndTime:
                if (mPref.getValue(mPref.KEY_PICKER_THEME, 0) == 0) {
                    final com.rey.material.app.TimePickerDialog endDialog = new com.rey.material.app.TimePickerDialog(this);
                    endDialog.hour(Integer.parseInt(mTxtEndTime_hour.getText().toString()));
                    endDialog.minute(Integer.parseInt(mTxtEndTime_min.getText().toString()));
                    endDialog.positiveAction("확인");
                    endDialog.positiveActionClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mEndHour = endDialog.getHour();
                            mEndMin = endDialog.getMinute();
                            mTxtEndTime_hour.setText(Integer.toString(mEndHour));
                            mTxtEndTime_min.setText(Integer.toString(mEndMin));
                            workHourSet();
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
                } else if (mPref.getValue(mPref.KEY_PICKER_THEME, 0) == 1) {
                    TimePickerDialog endDialogICS = new TimePickerDialog(this, TimePickerDialog.THEME_HOLO_DARK, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mEndHour = hourOfDay;
                            mEndMin = minute;
                            mTxtEndTime_hour.setText(Integer.toString(hourOfDay));
                            mTxtEndTime_min.setText(Integer.toString(minute));
                            workHourSet();
                        }
                    }, Integer.parseInt(mTxtEndTime_hour.getText().toString()), Integer.parseInt(mTxtEndTime_min.getText().toString()), false);
                    endDialogICS.show();
                } else {
                    TimePickerDialog endDialogICS = new TimePickerDialog(this, TimePickerDialog.THEME_HOLO_DARK, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mEndHour = hourOfDay;
                            mEndMin = minute;
                            mTxtEndTime_hour.setText(Integer.toString(hourOfDay));
                            mTxtEndTime_min.setText(Integer.toString(minute));
                            workHourSet();
                        }
                    }, Integer.parseInt(mTxtEndTime_hour.getText().toString()), Integer.parseInt(mTxtEndTime_min.getText().toString()), true);
                    endDialogICS.show();
                }
                break;
            case R.id.linRefreshTime:
                RefreshInfoDialog refreshTimeDialog = new RefreshInfoDialog();
                Bundle refreshBundle = new Bundle();
                refreshBundle.putInt("refeshType", mRefreshType);
                refreshBundle.putInt("refeshHour", mRefreshHour);
                refreshBundle.putInt("refeshMin", mRefreshMin);
                refreshTimeDialog.setArguments(refreshBundle);
                refreshTimeDialog.show(getSupportFragmentManager(), null);

                break;
            case R.id.linAddTime:
                AddInfoDialog addTimeDialog = new AddInfoDialog();
                Bundle addBundle = new Bundle();
                addBundle.putInt("addType", mAddType);
                addBundle.putInt("totalTime", mTotalTimeAll);
                addBundle.putInt("addHour", mAddHour);
                addBundle.putInt("addMin", mAddMin);
                addTimeDialog.setArguments(addBundle);
                addTimeDialog.show(getSupportFragmentManager(), null);

                break;
            case R.id.linEtc:
                EtcDialog etcDialog = new EtcDialog();
                Bundle etcBundle = new Bundle();
                etcBundle.putInt("etcMoney", mEtcMoney);
                etcBundle.putInt("etcNum", mEtcNum);
                etcDialog.setArguments(etcBundle);
                etcDialog.show(getSupportFragmentManager(), null);
                break;

            case R.id.linWeek:
                WeekDialog weekDialog = new WeekDialog();
                Bundle weekBundle = new Bundle();
                weekBundle.putInt("weekMoney", mWeekHourMoney);
                weekBundle.putDouble("weekTime", mWeekTime);
                weekDialog.setArguments(weekBundle);
                weekDialog.show(getSupportFragmentManager(), null);
                break;

            case R.id.linSave:
                switch (mTxtSave.getText().toString()) {
                    case "저장하기":
                        albaSave();
                        break;
                    case "수정하기":
                        albaUpdate();
                        break;
                }
                break;

            case R.id.linMonthPay:
                MonthPayDialog monthPayDialog = new MonthPayDialog();
                Bundle monthBundle = new Bundle();
                monthBundle.putInt("MonthDay", mMonthDay);
                monthBundle.putInt("WeekDay", mWeekDay);
                monthBundle.putInt("MonthWeekFlag", mMonthWeekFlag);
                monthPayDialog.setArguments(monthBundle);
                monthPayDialog.show(getSupportFragmentManager(), null);

                break;

        }
    }

    public void workAlarmSet() {
        Calendar currentCal = Calendar.getInstance();
        Calendar mAlbaCal = Calendar.getInstance();
        //Alram set
        mAlbaCal.set(Calendar.HOUR_OF_DAY, mStartHour);
        mAlbaCal.set(Calendar.MINUTE, mStartMin);
        mAlbaCal.set(Calendar.SECOND, 0);

        if (mAlbaCal.getTimeInMillis() < currentCal.getTimeInMillis()) {
            if (INFO) {
                Log.i(TAG, "기상 AlramSet (오늘)");
                Log.i(TAG, "현재시간 : " + currentCal.getTime());
                Log.i(TAG, "설정시각 : " + mAlbaCal.getTime());
                Log.i(TAG, "설정한 시각이 현재시간 보다 적음");
            }
            mAlarmMgr.setRepeating(AlarmManager.RTC, mAlbaCal.getTimeInMillis() + AlarmManager.INTERVAL_DAY, AlarmManager.INTERVAL_DAY, mAlarmSender);
        } else {
            if (INFO) {
                Log.i(TAG, "기상 AlramSet (내일)");
                Log.i(TAG, "현재시간 : " + currentCal.getTime());
                Log.i(TAG, "설정시각 : " + mAlbaCal.getTime());
                Log.i(TAG, "설정한 시각이 현재시간 보다 큼");
            }
            mAlarmMgr.setRepeating(AlarmManager.RTC, mAlbaCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, mAlarmSender);
        }

    }

    public void workHourSet() {
        //시작 시간
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.HOUR_OF_DAY, mStartHour);
        startCal.set(Calendar.MINUTE, mStartMin);
        startCal.set(Calendar.SECOND, 0);
        //끝 시간
        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.HOUR_OF_DAY, mEndHour);
        endCal.set(Calendar.MINUTE, mEndMin);
        startCal.set(Calendar.SECOND, 0);
        //기타수당 적용
        mTxtEtcMoney.setText(mNumFomat.format(mEtcMoney * mEtcNum) + " 원");
        //주휴수당 적용
        mTxtWeekMoney.setText(mNumFomat.format(weekPaySet()) + " 원");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_newalba, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_alba_save:
                switch (mTxtSave.getText().toString()) {
                    case "저장하기":
                        albaSave();
                        break;
                    case "수정하기":
                        albaUpdate();
                        break;
                }
                break;
            case R.id.action_alba_del:
                AlertDialog_AlbaDel();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void AlertDialog_AlbaDel() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("삭제");
        alert.setMessage("삭제하시겠습니까?");
        alert.setCancelable(false);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Yes 버튼을 눌렀을때 일어날 일을 서술한다.
                if (mEdtAlbaName.getText().toString().isEmpty()) {
                    showCrountonText("알바명을 입력하세요");

                } else if (mDB.selectInfo(DBConstant.COLUMN_ALBANAME, mEdtAlbaName.getText().toString()) instanceof PartTimeInfo) {
                    if (mDB.removeInfo(mEdtAlbaName.getText().toString())) {
                        mAlarmMgr.cancel(mAlarmSender);
                        Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        showCrountonText("삭제에 실패하였습니다. 재시도 해주세요");
                    }
                } else {
                    showCrountonText("이미 등록된 알바명가 존재하지 않습니다");
                }
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel(); // No 버튼을 눌렀을 경우이며 단순히 창을 닫아 버린다.
            }
        });
        alert.show();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (mTxtSave.getText().toString()) {
            case "저장하기":
                menu.findItem(R.id.action_alba_del).setVisible(false);
                break;
            case "수정하기":
                menu.findItem(R.id.action_alba_del).setVisible(true);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void albaUpdate() {
        if (mEdtAlbaName.getText().toString().isEmpty()) {
            showCrountonText("알바명을 입력하세요");
        } else if (mEdtHourMoney.getText().toString().isEmpty()) {
            showCrountonText("시급을 입력하세요");
        } else {
            dataSet();
            if (mDB.updatePartTimeInfo(mPartInfoData, mPartInfoData_Intent.getAlbaname())) {
                if (mFlag_Alarm) {
                    if (mDB.updateWeekInfo(mWeekItem, mPartInfoData_Intent.getAlbaname())) {
                        Log.i(TAG, "TABLE 존재 업데이트");
                        workAlarmSet();
                        Toast.makeText(getApplicationContext(), "알람셋팅", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "알람셋팅 실패", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mAlarmMgr.cancel(mAlarmSender);
                }
                finish();
            } else {
                showCrountonText("업데이트에 실패하였습니다. 재시도 해주세요");
            }
        }
    }

    public void albaSave() {
        if (CommonUtil.isNull(mEdtAlbaName.getText().toString())) {
            showCrountonText("알바명을 입력하세요");
        } else if (CommonUtil.isNull(mEdtHourMoney.getText().toString())) {
            showCrountonText("시급을 입력하세요");
        } else if (mDB.selectInfo(DBConstant.COLUMN_ALBANAME, mEdtAlbaName.getText().toString()) instanceof PartTimeInfo) {
            showCrountonText("이미 등록된 알바명이 존재합니다");
        } else {
            dataSet();
            if (mDB.insertPartTimeInfo(mPartInfoData)) {
                mWeekItem.setAlbaName(mPartInfoData.getAlbaname().toString());
                if (mFlag_Alarm) {
                    if (mDB.insertWeekInfo(mWeekItem)) {
                        workAlarmSet();
                        Toast.makeText(getApplicationContext(), "알람셋팅", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "알람셋팅 실패", Toast.LENGTH_SHORT).show();
                    }
                }
                finish();
            } else {
                showCrountonText("저장에 실패하였습니다. 재시도 해주세요");
            }
        }
    }

    public double weekPaySet() {
        Log.i("weekPaySet", Double.toString((mWeekTime / 40) * mWeekHourMoney * 8));
        return (mWeekTime / 40.0) * mWeekHourMoney * 8;
    }

    public void dataSet() {
        mPartInfoData = new PartTimeInfo();
        mPartInfoData.set_id(mID);
        mPartInfoData.setAlbaname(mEdtAlbaName.getText().toString());
        mPartInfoData.setHourMoney(mEdtHourMoney.getText().toString());
        mPartInfoData.setStartTimeHour(mTxtStartTime_hour.getText().toString());
        mPartInfoData.setStartTimeMin(mTxtStartTime_min.getText().toString());
        mPartInfoData.setEndTimeHour(mTxtEndTime_hour.getText().toString());
        mPartInfoData.setEndTimeMin(mTxtEndTime_min.getText().toString());
        mPartInfoData.setSimpleMemo(mEdtMemo.getText().toString());

        mPartInfoData.setWorkPayNight(Boolean.toString(mFlag_NightAdvance));
        mPartInfoData.setWorkRefresh(Boolean.toString(mFlag_RefreshTime));
        mPartInfoData.setWorkPayAdd(Boolean.toString(mFlag_AddPay));
        mPartInfoData.setWorkRefreshType(Integer.toString(mRefreshType));
        mPartInfoData.setWorkRefreshHour(Integer.toString(mRefreshHour));
        mPartInfoData.setWorkRefreshMin(Integer.toString(mRefreshMin));

        mPartInfoData.setWorkPayEtc(Boolean.toString(mFlag_EtcPay));
        mPartInfoData.setWorkPayEtcMoney(Integer.toString(mEtcMoney));
        mPartInfoData.setWorkPayEtcNum(Integer.toString(mEtcNum));

        mPartInfoData.setWorkPayWeek(Boolean.toString(mFlag_WeekPay));
        mPartInfoData.setWorkPayWeekMoney(mWeekHourMoney);
        mPartInfoData.setWorkPayWeekTime(mWeekTime);

        mPartInfoData.setWorkAddType(Integer.toString(mAddType));
        mPartInfoData.setWorkPayAddHour(Integer.toString(mAddHour));
        mPartInfoData.setWorkPayAddMin(Integer.toString(mAddMin));
        mPartInfoData.setWorkAlarm(mFlag_Alarm);
        mPartInfoData.setWorkMonthDay(mMonthDay);

        mPartInfoData.setWorkWeekDay(mWeekDay);
        mPartInfoData.setWorkMonthWeekFlag(mMonthWeekFlag);

        mWeekItem.setAlbaName(mEdtAlbaName.getText().toString());
        Log.i(TAG, Boolean.toString(mFlag_NightAdvance));
        Log.i(TAG, Boolean.toString(mFlag_RefreshTime));
        Log.i(TAG, Boolean.toString(mFlag_AddPay));
        Log.i(TAG, Integer.toString(mRefreshType));
        Log.i(TAG, Integer.toString(mRefreshHour));
        Log.i(TAG, Integer.toString(mRefreshMin));
    }

    @Override
    public void onFinishRefreshDialog(int flag, String hour, String min) {
        if (INFO) {
            Log.i(TAG, "RefreshType : " + flag);
            Log.i(TAG, "RefreshHour : " + hour);
            Log.i(TAG, "RefreshMin : " + min);
        }

        mRefreshType = flag;
        mRefreshHour = Integer.parseInt(hour);
        mRefreshMin = Integer.parseInt(min);
        switch (flag) {
            case 0:
                mTxtRefreshView1.setText("4시간 마다");
                mTxtRefreshView2.setText("30분 휴식");
                break;

            case 1:
                mTxtRefreshView1.setText(hour + " 시간");
                mTxtRefreshView2.setText(min + " 분 휴식");
                break;
        }
        workHourSet();
    }

    @Override
    public void onFinishWeekDialog(String weekMoney, String weekTime) {
        mWeekHourMoney = Integer.parseInt(weekMoney);
        mWeekTime = Double.parseDouble(weekTime);
        workHourSet();
    }

    @Override
    public void onFinishEtcDialog(String money, String num) {
        mEtcMoney = Integer.parseInt(money);
        mEtcNum = Integer.parseInt(num);
        workHourSet();
    }

    @Override
    public void onFinishAddDialog(int flag, String hour, String min) {
        mAddType = flag;
        mAddHour = Integer.parseInt(hour);
        mAddMin = Integer.parseInt(min);
        workHourSet();
        switch (mAddType) {
            case 0:
                mTxtAdd.setText("8시간 이상 근무 시급 1.5배 적용");
                break;
            case 1:
                mTxtAdd.setText("풀타임 시급 1.5배 적용");
                break;
            case 2:
                mTxtAdd.setText("입력한 시간만큼 시급 1.5배 적용");
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.chk_mon:
                mWeekItem.setMon(isChecked);
                break;
            case R.id.chk_thu:
                mWeekItem.setThu(isChecked);
                break;
            case R.id.chk_wed:
                mWeekItem.setWed(isChecked);
                break;
            case R.id.chk_thur:
                mWeekItem.setThur(isChecked);
                break;
            case R.id.chk_fri:
                mWeekItem.setFri(isChecked);
                break;
            case R.id.chk_sat:
                mWeekItem.setSat(isChecked);
                break;
            case R.id.chk_sun:
                mWeekItem.setSun(isChecked);
                break;
            case R.id.chk_Alaram:
                mFlag_Alarm = isChecked;
                initCheckBox();
                break;
            case R.id.chk_nightMoney:
                mFlag_NightAdvance = isChecked;
                initCheckBox();
                break;
            case R.id.chk_refreshTime:
                mFlag_RefreshTime = isChecked;
                initCheckBox();
                break;
            case R.id.chk_addMoney:
                mFlag_AddPay = isChecked;
                initCheckBox();
                break;
            case R.id.chk_etcMoney:
                mFlag_EtcPay = isChecked;
                initCheckBox();
                break;
            case R.id.chk_WeekMoney:
                mFlag_WeekPay = isChecked;
                initCheckBox();
                break;
        }
    }

    public void onArticleSelected(int flag, int weekDay, int monthDay) {
        String mWeekStr = "";
        mMonthDay = monthDay;
        mWeekDay = weekDay;
        mMonthWeekFlag = flag;
        switch (weekDay) {
            case 1:
                mWeekStr = "일";
                break;
            case 2:
                mWeekStr = "월";
                break;
            case 3:
                mWeekStr = "화";
                break;
            case 4:
                mWeekStr = "수";
                break;
            case 5:
                mWeekStr = "목";
                break;
            case 6:
                mWeekStr = "금";
                break;
            case 7:
                mWeekStr = "토";
                break;
        }

        switch (flag) {
            case 0:
                mTxtMonthPay.setText("매주 " + mWeekStr + "요일");
                break;
            case 1:
                mTxtMonthPay.setText("매달 " + monthDay + "일");
                break;
        }

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
