package com.hw.oh.temp.process.alba;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hw.oh.dialog.AddInfoDialog;
import com.hw.oh.dialog.EtcDialog;
import com.hw.oh.dialog.GabulDialog;
import com.hw.oh.dialog.RefreshInfoDialog;
import com.hw.oh.dialog.ResultInfoDialog;
import com.hw.oh.dialog.WeekDialog;
import com.hw.oh.model.DetailCalItem;
import com.hw.oh.model.PartTimeItem;
import com.hw.oh.sqlite.DBManager;
import com.hw.oh.temp.ApplicationClass;
import com.hw.oh.temp.BaseActivity;
import com.hw.oh.temp.R;
import com.hw.oh.utility.CommonUtil;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.DigitTextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by oh on 2015-02-21.
 */
public class NewWorkActivity extends BaseActivity implements GabulDialog.EditNameDialogListener, RefreshInfoDialog.RefreshDialogListener, EtcDialog.EtcDialogListener, WeekDialog.WeekDialogListener, AddInfoDialog.AddDialogListener {
    public static final String TAG = "NewPostActivity";
    public static final boolean DBUG = true;
    public static final boolean INFO = true;
    private int mPrevHour;

    //DataSet
    private String mYear;
    private String mMonth;
    private String mDay;
    private String mToday;
    private int mStartMin;
    private int mStartHour;
    private int mEndMin;
    private int mEndHour;
    private int mTotalTime;
    private int mTotalTimeAll;
    private int mNightTotalTime;
    private int mDefaultTotalTime;
    private int mAddTotalTime;
    private int mRefreshTotalTime;
    private int mEtcTotal;
    private int mWeekHourMoney;
    private Double mWeekTime;
    private int mEtcMoney;
    private int mEtcNum;
    private int mGabulMoney;
    private double mWeekTotal;
    private double mResult;
    private double mResult_gabul;
    private double mResult_refresh;
    private double mResult_add;
    private double mResult_night;
    private String mResult_st;
    private String mAlbaName;
    private Boolean mFlag_ReceiveInAdvance = false;
    private Boolean mFlag_NightAdvance = false;
    private Boolean mFlag_RefreshTime = false;
    private Boolean mFlag_AddPay = false;
    private Boolean mFlag_EtcPay = false;
    private Boolean mFlag_WeekPay = false;
    private int mRefreshType = 0;
    private int mRefreshHour = 0;
    private int mRefreshMin = 0;
    private int mAddType = 0;
    private int mAddHour = 0;
    private int mAddMin = 0;
    private DetailCalItem mDetailCalItem;


    //View
    @BindView(R.id.txtStartTime_hour)
    TextView mTxtStartTime_hour;
    @BindView(R.id.txtStartTime_min)
    TextView mTxtStartTime_min;
    @BindView(R.id.txtEndTime_hour)
    TextView mTxtEndTime_hour;
    @BindView(R.id.txtEndTime_min)
    TextView mTxtEndTime_min;
    @BindView(R.id.txtAddMoney)
    TextView mTxtAddMoney;
    @BindView(R.id.txtWorkedHour)
    TextView mTxtWorkHour;
    @BindView(R.id.txtWorkedMin)
    TextView mTxtWorkMin;
    @BindView(R.id.txtStartDay)
    TextView mTxtStartDay;
    @BindView(R.id.txtEndDay)
    TextView mTxtEndDay;
    @BindView(R.id.txtEtcMoney)
    TextView mTxtEtcMoney;
    @BindView(R.id.txtWeekMoney)
    TextView mTxtWeekMoney;
    @BindView(R.id.txtGabulMoney)
    TextView mTxtGabulMoney;
    @BindView(R.id.txtSave)
    TextView mTxtSave;
    @BindView(R.id.txtResult)
    TextView mTxtResult;
    @BindView(R.id.txtRemainWorkedTitle)
    TextView mTxtRemainWorkTimeTitle;
    @BindView(R.id.txtRefreshView1)
    TextView mTxtRefreshView1;
    @BindView(R.id.txtRefreshView2)
    TextView mTxtRefreshView2;
    @BindView(R.id.txtNightMoney)
    TextView mTxtNightMoney;

    @BindView(R.id.edtHourMoney)
    TextView mEdtHourMoney;
    @BindView(R.id.edtMemo)
    TextView mEdtMemo;

    @BindView(R.id.linRemainWorkedTime)
    LinearLayout linRemainWorkedTime;
    @BindView(R.id.linAddTimelebel)
    LinearLayout mLinAddTimelebel;
    @BindView(R.id.linNightlebel)
    LinearLayout mLinNightTimelebel;
    @BindView(R.id.linEtclebel)
    LinearLayout mLinEtcTimelebel;
    @BindView(R.id.linRefreshlebel)
    LinearLayout mLinRefreshTimelebel;
    @BindView(R.id.linGabullebel)
    LinearLayout mLinGabulTimelebel;
    @BindView(R.id.linWeeklebel)
    LinearLayout mLinWeekTimelebel;


    @BindView(R.id.chk_receviceInAdvance)
    CheckBox mChkReceiveInAdvance;
    @BindView(R.id.chk_nightMoney)
    CheckBox mChkNightMoney;
    @BindView(R.id.chk_refreshTime)
    CheckBox mChkRefreshTime;
    @BindView(R.id.chk_addMoney)
    CheckBox mChkAddMoney;
    @BindView(R.id.chk_etcMoney)
    CheckBox mChk_etcMoney;
    @BindView(R.id.chk_WeekMoney)
    CheckBox mChk_WeekMoney;

    private DigitTextView digit1, digit2, digit3;

    @BindView(R.id.radioGroupRefresh)
    RadioGroup mRadioRefresh;
    private int mRefreshState = 0;

    //Util
    private DBManager mDBManager;
    private NumberFormat mNumFomat = new DecimalFormat("###,###,###");

    //flag
    boolean timeCountFlag = false;
    boolean commuteFlag = false; //출근 : true 출근안함 : false

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_work);
        //ButterKnife
        ButterKnife.bind(this);

        // 구글 통계
        Tracker mTracker = ((ApplicationClass) getApplication()).getDefaultTracker();
        mTracker.setScreenName("새로운 일급 등록_(NewWorkActivity)");
        mTracker.send(new HitBuilders.AppViewBuilder().build());

        //Utill
        mDBManager = new DBManager(this);

        //View Set
        mEdtHourMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    workHourSet();
                    mTxtResult.setText(mResult_st);
                }
            }
        });

        digit1 = (DigitTextView) findViewById(R.id.digitTextView1);
        digit2 = (DigitTextView) findViewById(R.id.digitTextView2);
        digit3 = (DigitTextView) findViewById(R.id.digitTextView3);

        //Intent Get
        getIntentData();

        //ActionBar
        setToolbar(mYear + "년 " + mMonth + "월 " + mDay + "일");

        mTxtStartDay.setText("오늘");
        mTxtEndDay.setText("오늘");

        if (CommonUtil.isNull(mEdtHourMoney.getText().toString()))
            mEdtHourMoney.setText("0");

        if (Constant.NEWWORK_INTENT_FLAG == Constant.NEWWORK_INTENT_UPDATE) {
            mTxtSave.setText("수정하기");
        } else {
            mTxtSave.setText("저장하기");
        }

        mRadioRefresh.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {

                    case R.id.chk_day:
                        if (INFO)
                            Log.i(TAG, "refresh DAY");
                        mRefreshState = 0;
                        workHourSet();
                        break;

                    case R.id.chk_night:
                        if (INFO)
                            Log.i(TAG, "refresh Night");
                        mRefreshState = 1;
                        workHourSet();
                        break;
                }
            }
        });
        if (mRefreshState == 0) {
            mRadioRefresh.check(R.id.chk_day);
        } else {
            mRadioRefresh.check(R.id.chk_night);
        }

        //가불 체크
        mChkReceiveInAdvance.setChecked(mFlag_ReceiveInAdvance);
        mChkReceiveInAdvance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFlag_ReceiveInAdvance = isChecked;
                workHourSet();
            }
        });

        // 야간 체크
        mChkNightMoney.setChecked(mFlag_NightAdvance);

        mChkNightMoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFlag_NightAdvance = isChecked;
                workHourSet();
            }
        });

        // 휴식 체크
        mChkRefreshTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFlag_RefreshTime = isChecked;
                workHourSet();
            }
        });


        // 기타 체크
        mChk_etcMoney.setChecked(mFlag_EtcPay);
        mChk_etcMoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFlag_EtcPay = isChecked;
                workHourSet();
            }
        });


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

        //추가 체크
        mChkAddMoney = (CheckBox) findViewById(R.id.chk_addMoney);
        mChkAddMoney.setChecked(mFlag_AddPay);

        mChkAddMoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFlag_AddPay = isChecked;
                workHourSet();
            }
        });

        //주휴 체크
        mChk_WeekMoney = (CheckBox) findViewById(R.id.chk_WeekMoney);
        mChk_WeekMoney.setChecked(mFlag_WeekPay);

        mChk_WeekMoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFlag_WeekPay = isChecked;
                workHourSet();
            }
        });

        mResult = dayTimeMoney(Double.parseDouble(mEdtHourMoney.getText().toString()), Integer.parseInt(mTxtWorkHour.getText().toString()), Integer.parseInt(mTxtWorkMin.getText().toString()));
        mResult_st = mNumFomat.format(mResult);
        mTxtResult.setText(mResult_st);


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

        workHourSet();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timeCountFlag = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        timeCountFlag = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeCountFlag = false;
    }

    public void getIntentData() {
        mAlbaName = getIntent().getStringExtra("ALBA_NAME");
        mYear = getIntent().getStringExtra("YEAR");
        mMonth = getIntent().getStringExtra("MONTH");
        mDay = getIntent().getStringExtra("DAY");
        mToday = getIntent().getStringExtra("TODAY");
        mWeekHourMoney = getIntent().getIntExtra("WEEK_HOURKMONEY", 0);
        mWeekTime = getIntent().getDoubleExtra("WEEK_TIME", 15);
        mGabulMoney = Integer.parseInt(getIntent().getStringExtra("GABUL_PAY"));
        mEdtMemo.setText(getIntent().getStringExtra("MEMO"));
        mEdtHourMoney.setText(getIntent().getStringExtra("HOURMONEY"));
        mTxtStartTime_hour.setText(getIntent().getStringExtra("START_HOUR"));
        mTxtStartTime_min.setText(getIntent().getStringExtra("START_MIN"));
        mTxtEndTime_hour.setText(getIntent().getStringExtra("END_HOUR"));

        mTxtEndTime_min.setText(getIntent().getStringExtra("END_MIN"));
        mFlag_EtcPay = Boolean.parseBoolean(getIntent().getStringExtra("ETC_PAY"));
        mEtcMoney = Integer.parseInt(getIntent().getStringExtra("ETC_MONEY"));
        mEtcNum = Integer.parseInt(getIntent().getStringExtra("ETC_NUM"));

        mStartHour = Integer.parseInt(getIntent().getStringExtra("START_HOUR"));
        mStartMin = Integer.parseInt(getIntent().getStringExtra("START_MIN"));
        mEndHour = Integer.parseInt(getIntent().getStringExtra("END_HOUR"));
        mEndMin = Integer.parseInt(getIntent().getStringExtra("END_MIN"));
        mFlag_ReceiveInAdvance = Boolean.parseBoolean(getIntent().getStringExtra("GABUL"));
        mFlag_NightAdvance = Boolean.parseBoolean(getIntent().getStringExtra("NIGHT_PAY"));
        mFlag_RefreshTime = Boolean.parseBoolean(getIntent().getStringExtra("REFRESH_TIME"));
        mFlag_AddPay = Boolean.parseBoolean(getIntent().getStringExtra("ADD_PAY"));
        mFlag_WeekPay = Boolean.parseBoolean(getIntent().getStringExtra("WEEK_PAY"));
        mAddType = Integer.parseInt(getIntent().getStringExtra("ADD_PAY_TYPE"));
        mAddHour = Integer.parseInt(getIntent().getStringExtra("ADD_PAY_HOUR"));
        mAddMin = Integer.parseInt(getIntent().getStringExtra("ADD_PAY_MIN"));
        mRefreshType = Integer.parseInt(getIntent().getStringExtra("REFRESH_TYPE"));
        mRefreshHour = Integer.parseInt(getIntent().getStringExtra("REFRESH_HOUR"));
        mRefreshMin = Integer.parseInt(getIntent().getStringExtra("REFRESH_MIN"));
        mRefreshState = getIntent().getIntExtra("REFRESH_STATE", 0);
    }

    public double weekPaySet() {
        Log.i("weekPaySet", Double.toString((mWeekTime / 40) * mWeekHourMoney * 8));
        return (mWeekTime / 40.0) * mWeekHourMoney * 8;
    }

    @OnClick({R.id.linStartTime, R.id.linEndTime, R.id.linGabulValue, R.id.linAddTime, R.id.linRefreshTime, R.id.linEtc, R.id.linWeek, R.id.linSave})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linStartTime:
                if (mPref.getValue(mPref.KEY_PICKER_THEME, 0) == 0) {
                    final com.rey.material.app.TimePickerDialog startDialog = new com.rey.material.app.TimePickerDialog(this);

                    startDialog.hour(mStartHour);
                    startDialog.minute(mStartMin);
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
             /*   TimePickerDialog endDialog = new TimePickerDialog(this, endListener, Integer.parseInt(mTxtEndTime_hour.getText().toString()), Integer.parseInt(mTxtEndTime_min.getText().toString()), false);
                endDialog.show();*/

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
            case R.id.linGabulValue:
                GabulDialog gabulValuePicker = new GabulDialog();
                Bundle gabulValueBundle = new Bundle();
                gabulValueBundle.putInt("TotalMoney", (int) mResult_gabul);
                gabulValueBundle.putInt("GabulMoney", mGabulMoney);
                gabulValuePicker.setArguments(gabulValueBundle);
                gabulValuePicker.show(getSupportFragmentManager(), null);
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
            case R.id.linRefreshTime:
                RefreshInfoDialog refreshTimeDialog = new RefreshInfoDialog();
                Bundle refreshBundle = new Bundle();
                refreshBundle.putInt("refeshType", mRefreshType);
                refreshBundle.putInt("refeshHour", mRefreshHour);
                refreshBundle.putInt("refeshMin", mRefreshMin);
                refreshTimeDialog.setArguments(refreshBundle);
                refreshTimeDialog.show(getSupportFragmentManager(), null);

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
                saveClick();
                break;
        }
    }

    public void saveClick() {
        PartTimeItem item = new PartTimeItem();
        item.setAlbaname(mAlbaName);
        item.setDate(mYear + "-" + mMonth + "-" + mDay);
        item.setHourMoney(mEdtHourMoney.getText().toString());
        item.setStartTimeHour(mTxtStartTime_hour.getText().toString());
        item.setStartTimeMin(mTxtStartTime_min.getText().toString());
        item.setEndTimeHour(mTxtEndTime_hour.getText().toString());
        item.setEndTimeMin(mTxtEndTime_min.getText().toString());
        item.setSimpleMemo(mEdtMemo.getText().toString());
        item.setWorkTimeHour(mTxtWorkHour.getText().toString());
        item.setWorkTimeMin(mTxtWorkMin.getText().toString());
        item.setWorkPayGabul(Boolean.toString(mFlag_ReceiveInAdvance));
        item.setWorkPayGabulVal(Integer.toString(mGabulMoney));
        item.setWorkRefreshTime(Boolean.toString(mFlag_RefreshTime));
        item.setWorkPayNight(Boolean.toString(mFlag_NightAdvance));
        item.setWorkPayTotal(Double.toString(mResult));
        item.setWorkPayAdd(Boolean.toString(mFlag_AddPay));
        item.setWorkRefreshType(Integer.toString(mRefreshType));
        item.setWorkRefreshHour(Integer.toString(mDetailCalItem.getTime_refresh() / 60));
        item.setWorkRefreshMin(Integer.toString(mDetailCalItem.getTime_refresh() % 60));
        item.setWorkPayTotalTime(mTotalTime);
        item.setWorkPayNightTotalTime(mDetailCalItem.getTime_night());
        item.setWorkPayAddTotalTime(mDetailCalItem.getTime_add());
        item.setWorkPayRefreshTotalTime(mRefreshTotalTime);
        item.setWorkEtc(Boolean.toString(mFlag_EtcPay));
        item.setWorkEtcMoeny(Integer.toString(mEtcMoney));
        item.setWorkEtcNum(Integer.toString(mEtcNum));
        item.setWorkPayWeekMoney(mWeekHourMoney);
        item.setWorkPayWeekTime(mWeekTime);
        item.setWorkPayWeek(Boolean.toString(mFlag_WeekPay));
        item.setWorkAddType(Integer.toString(mAddType));
        item.setWorkAddHour(Integer.toString(mAddHour));
        item.setWorkAddMin(Integer.toString(mAddMin));
        item.setWorkRefreshState(mRefreshState);

        if (Constant.NEWWORK_INTENT_FLAG == Constant.NEWWORK_INTENT_NEW) {
            if (mDBManager.insertPartTimeData(item)) {
                if (INFO)
                    Log.i(TAG, "Insert 성공");
                finish();
            } else {
                if (INFO)
                    Log.i(TAG, "Insert 실패");

            }
        } else {
            if (mDBManager.updatePartTimeData(item, mYear + "-" + mMonth + "-" + mDay)) {
                if (INFO)
                    Log.i(TAG, "Update 성공");
                finish();
            } else {
                if (INFO)
                    Log.i(TAG, "Update 실패");

            }
        }
    }

    public void workHourSet() {
        mTotalTimeAll = 0;

        int total = 0;
        int night = -1;
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

        //전일 PM 10:00 ~ 금일 AM 6:00
        //야간 시작1 (전일 PM 10:00)
        Calendar nightStartCal = Calendar.getInstance();
        nightStartCal.add(Calendar.DATE, -1);
        nightStartCal.set(Calendar.HOUR_OF_DAY, 22);
        nightStartCal.set(Calendar.MINUTE, 0);
        nightStartCal.set(Calendar.SECOND, 0);

        //야간 끝1 (금일 AM 6:00)
        Calendar nightEndCal = Calendar.getInstance();
        nightEndCal.set(Calendar.HOUR_OF_DAY, 6);
        nightEndCal.set(Calendar.MINUTE, 0);
        nightEndCal.set(Calendar.SECOND, 0);

        //금일 PM 10:00 ~ 명일 AM 6:00
        //야간 시작2 (금일 PM 10:00)
        Calendar nightStartCal2 = Calendar.getInstance();
        nightStartCal2.set(Calendar.HOUR_OF_DAY, 22);
        nightStartCal2.set(Calendar.MINUTE, 0);
        nightStartCal2.set(Calendar.SECOND, 0);
        //야간 끝2 (명일 AM 6:00)
        Calendar nightEndCal2 = Calendar.getInstance();
        nightEndCal2.add(Calendar.DATE, 1);
        nightEndCal2.set(Calendar.HOUR_OF_DAY, 6);
        nightEndCal2.set(Calendar.MINUTE, 0);
        nightEndCal2.set(Calendar.SECOND, 0);

        //종료시간이 시작시간 보다 작을 때
        if (mEndHour < mStartHour) {
            //  Log.d("종료시간이 시작시간 보다 작을 때", "");
            mTxtEndDay.setText("내일");
            endCal.add(Calendar.DATE, 1);
            if (mEndMin < mStartMin) { // 종료 분이 시작분 보다 작을 때
                //  Log.d("종료 분이 시작분 보다 작을 때", "");
                endCal.add(Calendar.HOUR_OF_DAY, -1);
            }
            //종료시간이 시작시간과 같을 때
        } else if (mEndHour == mStartHour) {
            // Log.d("종료시간이 시작시간과 같을 때", "");
            mTxtEndDay.setText("내일");
            if (mEndMin < mStartMin) { // 종료 분이 시작분 보다 작을 때
                //   Log.d("종료 분이 시작분 보다 작을 때", "");
                endCal.add(Calendar.HOUR_OF_DAY, 23); // 23을 더함
            } else {
                // Log.d("종료 분이 시작분 보다 작지않을 때", "");
                mTxtEndDay.setText("오늘");
            }
            //종료시간이 시작시간 보다 클 때
        } else {
            //Log.d("종료시간이 시작시간 보다 클 때", "");
            mTxtEndDay.setText("오늘");
            if (mEndMin < mStartMin) { // 종료 분이 시작분 보다 작을 때
                endCal.add(Calendar.HOUR_OF_DAY, -1);
            }
        }
        if (mEndMin < mStartMin) {
            endCal.add(Calendar.MINUTE, 60);
        }

        startCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.SECOND, 0);
        Calendar current = Calendar.getInstance();
        //현재시간이 시작시간보다 클때
        if ("TODAY".equals(mToday)) {
            Log.d("hwoh", "Today true " + mToday);
            if (current.getTimeInMillis() > startCal.getTimeInMillis()) {
                Log.d("hwoh", "현재시간이 시작시간보다 클때");
                long diffTime = timeDiff(current, endCal);
                commuteFlag = true; // 출근 후 : 퇴근까지 남은시간
                diffTimeRequest(diffTime);
            } else {
                Log.d("hwoh", "현재시간이 시작시간보다 크지않음");
                long diffTime = timeDiff(current, startCal);
                commuteFlag = false; //출근 전 : 출근까지 남은시간
                diffTimeRequest(diffTime);
            }
        }else{
            Log.d("hwoh", "Today false " + mToday);
        }

        Log.d("시작시간", startCal.getTime().toString());
        Log.d("종료시간", endCal.getTime().toString());
        Log.d("야간시작시간1", nightStartCal.getTime().toString());
        Log.d("야간종료시간1", nightEndCal.getTime().toString());
        Log.d("야간시작시간2", nightStartCal.getTime().toString());
        Log.d("야간종료시간2", nightEndCal.getTime().toString());

        if (nightStartCal2.getTimeInMillis() >= startCal.getTimeInMillis()) {
            night = 0;
        }

        //시작 시간이 종료시간보다 작을떄 까지 반복
        while (startCal.getTimeInMillis() <= endCal.getTimeInMillis()) {
            //야간시작 < 시작시간 < 야간종료 시간
            if (nightStartCal.getTimeInMillis() < startCal.getTimeInMillis() && startCal.getTimeInMillis() < nightEndCal.getTimeInMillis()) {
                night++;
                // Log.d("야간시작1 < 시작시간 < 야간종료 시간1 : " + Integer.toString(night), startCal.getTime().toString()+ " == " + nightEndCal.getTime().toString());
                //  Log.d("야간시작1 < 시작시간 < 야간종료 시간1 : " + Integer.toString(night), startCal.getTimeInMillis()+ " == " + nightEndCal.getTimeInMillis());
                if (startCal.getTimeInMillis() == nightEndCal.getTimeInMillis() - 1) {
                    //  Log.d("시작시간 == 야간종료 시간1 : " + Integer.toString(night), startCal.getTime().toString());
                    night--;
                }

            } else if (nightStartCal2.getTimeInMillis() < startCal.getTimeInMillis() && startCal.getTimeInMillis() <= nightEndCal2.getTimeInMillis()) {
                night++;
                //  Log.d("야간시작2 < 시작시간 < 야간종료 시간2 : " + Integer.toString(night), startCal.getTime().toString());
                //Log.d("야간시작2 < 시작시간 < 야간종료 시간2 : " + Integer.toString(night), startCal.getTimeInMillis()+ " == " + nightEndCal2.getTimeInMillis());

            }
            //Log.d("해당없음", startCal.getTime().toString());
            startCal.add(Calendar.MINUTE, 1);
            total++;
        }
        if (night == -1) {
            night = 0;
        }
        total = total - 1;
        mTotalTimeAll = total;
        Log.d("토탈시간", Integer.toString(total / 60) + "시간 " + Integer.toString(total % 60) + "분");
        Log.d("야간 : " + Integer.toString(night), Integer.toString(night / 60) + "시간 " + Integer.toString(night % 60) + "분");

        if (mFlag_RefreshTime) {
            switch (mRefreshType) {
                case 0:
                    Log.d("토탈 % 4", Double.toString((total / 60) % 4));
                    Log.d("토탈 / 4", Double.toString((total / 60) / 4));
                    Calendar refreshCal = Calendar.getInstance();
                    int totalRefresh = ((total / 60) / 4) * 30;
                    refreshCal.set(0, 0, 0, 0, totalRefresh);
                    // mResult = mResult - ((Double.parseDouble(mEdtHourMoney.getText().toString())/2) * ((total / 60) / 4));
                    mRefreshHour = totalRefresh / 60;
                    mRefreshMin = totalRefresh % 60;
                    mRefreshTotalTime = totalRefresh;
                    mResult_refresh = ((Double.parseDouble(mEdtHourMoney.getText().toString()) / 2) * ((total / 60) / 4));
                    break;

                case 1:
                    mResult_refresh = dayTimeMoney(Double.parseDouble(mEdtHourMoney.getText().toString()), mRefreshHour, mRefreshMin);
                    mRefreshTotalTime = mRefreshHour * 60 + mRefreshMin;
                    break;
            }
            mLinRefreshTimelebel.setVisibility(View.VISIBLE);
        } else {
            mResult_refresh = 0.0;
            mRefreshTotalTime = 0;
            mLinRefreshTimelebel.setVisibility(View.INVISIBLE);
        }
        //총시간에서 야간시간을 뺀 시간이 휴식시간 보다 크지않을 때 즉 주간시간에서 차감을 할수 없을 때를 의미

        Log.d("주간시간", Integer.toString(total - night));
        Log.d("야간시간", Integer.toString(night));
        Log.d("휴게시간", Integer.toString(mRefreshTotalTime));
        //주간시간이 휴계시간보다 크거나 같고 야간시간보다 휴계시간이 클때
        if ((total - night) >= mRefreshTotalTime && night < mRefreshTotalTime) {
            Log.d("휴게시간 차감", "주간시간이 휴계시간보다 크거나 같고 야간시간보다 휴계시간이 클때");
            total -= mRefreshTotalTime;
            mRadioRefresh.check(R.id.chk_day);
            //주간시간이 휴계시간보다 작고 야간시간이 휴계시간보다 크거나 같을떄
        } else if ((total - night) < mRefreshTotalTime && night >= mRefreshTotalTime) {
            Log.d("휴게시간 차감", "주간시간이 휴계시간보다 작고 야간시간이 휴계시간보다 크거나 같을떄");
            night -= mRefreshTotalTime;
            total -= mRefreshTotalTime;
            mRadioRefresh.check(R.id.chk_night);
        } else {
            if (mRefreshState == 0) {
                Log.d("휴게시간 차감", "휴계시간을 주간시간에서 차감함");
                total -= mRefreshTotalTime;
            } else {
                Log.d("휴게시간 차감", "휴계시간을 야간시간에서 차감함");
                night -= mRefreshTotalTime;
                total -= mRefreshTotalTime;
            }
        }

        mTxtWorkHour.setText(Integer.toString(total / 60));
        mTxtWorkMin.setText(Integer.toString(total % 60));
        // 주간근무 시간이 휴식시간보다 크거나 같지 않을 때

        //야간시급 적용
        if (mFlag_NightAdvance) {
            Double total_day = dayTimeMoney(Double.parseDouble(mEdtHourMoney.getText().toString()), total / 60, total % 60);
            Double none_nightMoney_day = dayTimeMoney(Double.parseDouble(mEdtHourMoney.getText().toString()), (total - night) / 60, (total - night) % 60);
            Double nightMoney_day = dayTimeMoney_night(Double.parseDouble(mEdtHourMoney.getText().toString()), night / 60, night % 60);
            //mResult = none_nightMoney_day + nightMoney_day;
            mResult = total_day;
            mResult_night = nightMoney_day;
            mNightTotalTime = night;
            mTxtNightMoney.setText(Integer.toString((int) mResult_night));
            Log.d("비야간", Double.toString(none_nightMoney_day));
            Log.d("야간", Double.toString(nightMoney_day));
            Log.d("토탈", Double.toString(mResult));
            mLinNightTimelebel.setVisibility(View.VISIBLE);
        } else {
            mResult = dayTimeMoney(Double.parseDouble(mEdtHourMoney.getText().toString()), Integer.parseInt(mTxtWorkHour.getText().toString()), Integer.parseInt(mTxtWorkMin.getText().toString()));
            mResult_night = 0.0;
            mTxtNightMoney.setText(Integer.toString((int) mResult_night));
            mNightTotalTime = 0;
            mLinNightTimelebel.setVisibility(View.INVISIBLE);
        }


        //기타수당 적용
        mTxtEtcMoney.setText(mNumFomat.format(mEtcMoney * mEtcNum));
        if (mFlag_EtcPay) {
            mEtcTotal = mEtcMoney * mEtcNum;
            mLinEtcTimelebel.setVisibility(View.VISIBLE);
        } else {
            mEtcTotal = 0;
            mLinEtcTimelebel.setVisibility(View.INVISIBLE);
        }
        //주휴수당 적용
        mTxtWeekMoney.setText(mNumFomat.format(weekPaySet()));
        if (mFlag_WeekPay) {
            mWeekTotal = weekPaySet();
            mLinWeekTimelebel.setVisibility(View.VISIBLE);
        } else {
            mWeekTotal = 0;
            mLinWeekTimelebel.setVisibility(View.INVISIBLE);
        }
        //추가근무 적용
        int time_add = 0;
        mDefaultTotalTime = 0;
        if (mFlag_AddPay) {
            mLinAddTimelebel.setVisibility(View.VISIBLE);
            switch (mAddType) {
                case 0:
                    if (total >= 480) {
                        time_add = total - 480;
                        mDefaultTotalTime = 480;

                        mResult_add = dayTimeMoney_add(Double.parseDouble(mEdtHourMoney.getText().toString()), time_add / 60, time_add % 60);
                        mResult = dayTimeMoney(Double.parseDouble(mEdtHourMoney.getText().toString()), 8, 0);
                        mTxtAddMoney.setText(Integer.toString((int) mResult_add));

                    } else {
                        mDefaultTotalTime = total;
                        time_add = 0;
                        mResult_add = 0.0;
                        mAddTotalTime = 0;
                    }
                    break;
                case 1:
                    time_add = total;
                    mDefaultTotalTime = total;
                    mAddTotalTime = time_add;
                    mResult_add = dayTimeMoney_add(Double.parseDouble(mEdtHourMoney.getText().toString()), time_add / 60, time_add % 60);
                    //mResult = dayTimeMoney(Double.parseDouble(mEdtHourMoney.getText().toString()), 8, 0);
                    mResult = 0;
                    mTxtAddMoney.setText(Integer.toString((int) mResult_add));
                    break;
                case 2:
                    time_add = mAddHour * 60 + mAddMin;
                    if (mFlag_RefreshTime) {
                        mDefaultTotalTime = mTotalTimeAll - time_add;
                        if (time_add == mTotalTimeAll) { //총시간과 같을 때
                            Toast.makeText(getApplicationContext(), "휴식시간 : 연장에서 차감", Toast.LENGTH_SHORT).show();
                            time_add = time_add - mRefreshTotalTime;
                            Log.i(TAG, "totalCal" + mTotalTimeAll);
                            Log.i(TAG, "addCal" + time_add);
                            Log.i(TAG, "refreshTime" + mRefreshTotalTime);
                        } else if (mDefaultTotalTime < mRefreshTotalTime) {
                            // Toast.makeText(getApplicationContext(), "휴식시간 : 일반과 연장에서 분산차감", Toast.LENGTH_SHORT).show();
                            //ex) 휴식시간 : 180분
                            // 주간 160분
                            // 연장 20분
                            // 180 - 160 = 20
                            int refreshSub = mRefreshTotalTime - mDefaultTotalTime;
                            mDefaultTotalTime = 0;
                            time_add = time_add - refreshSub;
                            Log.i(TAG, "totalCal" + mTotalTimeAll);
                            Log.i(TAG, "addCal" + time_add);
                            Log.i(TAG, "refreshTime" + mRefreshTotalTime);
                        } else {
                            // Toast.makeText(getApplicationContext(), "휴식시간 : 일반에서 차감", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "totalCal" + mTotalTimeAll);
                            Log.i(TAG, "addCal" + time_add);
                            Log.i(TAG, "refreshTime" + mRefreshTotalTime);
                        }
                    }
                    mDefaultTotalTime = total - time_add;
                    mResult_add = dayTimeMoney_add(Double.parseDouble(mEdtHourMoney.getText().toString()), time_add / 60, time_add % 60);
                    mResult = dayTimeMoney(Double.parseDouble(mEdtHourMoney.getText().toString()), mDefaultTotalTime / 60, mDefaultTotalTime % 60);
                    mTxtAddMoney.setText(Integer.toString((int) mResult_add));
                    break;
            }
        } else {
            mLinAddTimelebel.setVisibility(View.INVISIBLE);
            mResult_add = 0.0;
            mAddTotalTime = 0;
            mTxtAddMoney.setText(Integer.toString((int) mResult_add));
        }

        Log.d(TAG, "기본수당 : " + mResult);
        Log.d(TAG, "야간수당 : " + mResult_night);
        Log.d(TAG, "추가수당 : " + mResult_add);
        Log.d(TAG, "휴계제외 : " + mResult_refresh);
        mResult = (mResult + mResult_night + mResult_add + mEtcTotal + mWeekTotal);
        //가불 안내메시지
        mTxtGabulMoney.setText(mNumFomat.format(mGabulMoney));
        mResult_gabul = mResult;
        if (mFlag_ReceiveInAdvance) {
            mResult = mResult - mGabulMoney;
            mLinGabulTimelebel.setVisibility(View.VISIBLE);
        } else {
            mLinGabulTimelebel.setVisibility(View.INVISIBLE);
        }


        mTotalTime = total;
        mResult_st = mNumFomat.format(mResult);
        mTxtResult.setText(mResult_st);

        mDetailCalItem = new DetailCalItem();
        mDetailCalItem.setEtcMoney((double) mEtcMoney * mEtcNum);
        mDetailCalItem.setHourMoney_dayTime((Double.parseDouble(mEdtHourMoney.getText().toString())));
        mDetailCalItem.setHourMoney_night((Double.parseDouble(mEdtHourMoney.getText().toString())) * 0.5);
        mDetailCalItem.setHourMoney_add((Double.parseDouble(mEdtHourMoney.getText().toString())) * 1.5);
        mDetailCalItem.setTotalTime(total);
        mDetailCalItem.setTime_night(night);
        mDetailCalItem.setTime_dayTime(mDefaultTotalTime);
        mDetailCalItem.setTime_refresh(mRefreshTotalTime);
        mDetailCalItem.setTime_add(time_add);

        if (mPrevHour != mTotalTime)
            //pbAniSet();

            mPrevHour = mTotalTime;
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

    public Double dayTimeMoney_add(Double hourmoney, int hour, int min) {
        hourmoney = hourmoney * 1.5;
        Double minmoney = (hourmoney) / 60;
        return (hourmoney * hour + minmoney * min);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alba, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (mTxtSave.getText().toString()) {
            case "저장하기":
                menu.findItem(R.id.action_alba_save).setVisible(true);
                menu.findItem(R.id.action_alba_del).setVisible(false);
                menu.findItem(R.id.action_detail_info).setVisible(true);
                break;
            case "수정하기":
                menu.findItem(R.id.action_alba_save).setVisible(true);
                menu.findItem(R.id.action_alba_del).setVisible(true);
                menu.findItem(R.id.action_detail_info).setVisible(true);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_detail_info:
                ResultInfoDialog resultInfoDialog = new ResultInfoDialog();
                Bundle resultbundle = new Bundle();
                resultbundle.putSerializable("DetailItem", mDetailCalItem);
                resultInfoDialog.setArguments(resultbundle);
                resultInfoDialog.show(getSupportFragmentManager(), "MYTAG0");
                break;

            case R.id.action_alba_del:
                AlertDialog_AlbaDel();
                break;

            case R.id.action_alba_save:
                //AlertDialog_AlbaDel();
                saveClick();


                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void diffTimeRequest(long diffTime) {
        timeCountHandler.removeMessages(0);
        Message msg = timeCountHandler.obtainMessage();
        msg.arg1 = (int) diffTime;
        msg.arg2 = 0; //첫번째 요청 0 재귀하면서 1로 변경
        timeCountHandler.sendMessageDelayed(msg, 1000);

    }


    public void AlertDialog_AlbaDel() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("삭제");
        alert.setMessage("삭제하시겠습니까?");
        alert.setCancelable(false);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mDBManager.removeData(mYear + "-" + mMonth + "-" + mDay, mAlbaName)) {
                    Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    showCrountonText("삭제에 실패하였습니다. 재시도 해주세요");
                }
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alert.show();
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
    public void onFinishEtcDialog(String money, String num) {
        mEtcMoney = Integer.parseInt(money);
        mEtcNum = Integer.parseInt(num);
        workHourSet();
    }

    @Override
    public void onFinishWeekDialog(String weekMoney, String weekTime) {
        mWeekHourMoney = Integer.parseInt(weekMoney);
        mWeekTime = Double.parseDouble(weekTime);
        workHourSet();
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        if (inputText.equals("")) {
            inputText = "0";
        }
        Log.i(TAG, inputText);
        mGabulMoney = Integer.parseInt(inputText);
        workHourSet();
    }

    @Override
    public void onFinishAddDialog(int flag, String hour, String min) {
        mAddType = flag;
        mAddHour = Integer.parseInt(hour);
        mAddMin = Integer.parseInt(min);
        workHourSet();
    }


    @Override
    public void onStart() {
        super.onStart();
        // 구글 통계
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // 구글 통계
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    public long timeDiff(Calendar currentTime, Calendar finishTime) {
        Log.d("hwoh", "현재시간 " + currentTime.getTime().toString());
        Log.d("hwoh", "종료시간 " + finishTime.getTime().toString());

        long diffSec = (finishTime.getTimeInMillis() - currentTime.getTimeInMillis());

        return diffSec;
    }

    Handler timeCountHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int diffsec = msg.arg1;
            int fristCheck = msg.arg2;
            diffsec = diffsec - 1000;
            Message tempMsg = obtainMessage();
            tempMsg.arg1 = diffsec;
            ArrayList<Integer> time = convertArrayList(diffsec);

            if (fristCheck == 0) {
                digit1.resetValue(time.get(0));
                digit2.resetValue(time.get(1));
                digit3.resetValue(time.get(2));
            } else {
                digit1.setValue(time.get(0));
                digit2.setValue(time.get(1));
                if (time.get(2) == 59) {
                    digit3.resetValue(time.get(2));
                } else {
                    digit3.setValue(time.get(2));
                }
            }

            tempMsg.arg2 = 1;
            if (diffsec > 0 && timeCountFlag) {
                timeCountHandler.sendMessageDelayed(tempMsg, 1000);
                linRemainWorkedTime.setVisibility(View.VISIBLE);
                if(commuteFlag){
                    mTxtRemainWorkTimeTitle.setText("퇴근까지\n남은시간\n");
                }else{
                    mTxtRemainWorkTimeTitle.setText("출근까지\n남은시간\n");
                }
            } else {
                //재귀탈출
                linRemainWorkedTime.setVisibility(View.GONE);
                if(!commuteFlag){ //재귀탈출 시 출근상태일때 -> 퇴근상태로 재귀설정
                    workHourSet();
                }
                Log.i("hwoh", "재귀탈출 current Timecount : " + diffsec);
            }
        }
    };

    public ArrayList<Integer> convertArrayList(long millis) {
        long s = (millis / 1000) % 60;
        long m = (millis / (1000 * 60)) % 60;
        long h = (millis / (1000 * 60 * 60)) % 24;
        ArrayList<Integer> time = new ArrayList<Integer>();
        time.add((int) h);
        time.add((int) m);
        time.add((int) s);
        return time;
    }


}
