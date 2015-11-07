package com.hw.oh.temp;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

/**
 * Created by oh on 2015-02-21.
 */
public class NewWorkActivity extends ActionBarActivity implements View.OnClickListener, GabulDialog.EditNameDialogListener, RefreshInfoDialog.RefreshDialogListener, EtcDialog.EtcDialogListener, WeekDialog.WeekDialogListener, AddInfoDialog.AddDialogListener {
  public static final String TAG = "NewPostActivity";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;
  private int mPrevHour;

  //DataSet
  private String mYear;
  private String mMonth;
  private String mDay;
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

  //Circle Progress
  ProgressBar pb;
  ObjectAnimator animationA, animationB;

  //View
  private TextView mTxtStartTime_hour, mTxtStartTime_min, mTxtEndTime_hour, mTxtEndTime_min, mTxtAddMoney, mTxtNightMoney;
  private TextView mTxtWorkHour, mTxtWorkMin;
  private TextView mTxtStartDay, mTxtEndDay;
  private TextView mTxtRefreshView1, mTxtRefreshView2;
  private Toolbar mToolbar;
  private EditText mEdtHourMoney;
  private EditText mEdtMemo;
  private TextView mTxtResult;
  private TextView mTxtGabulMoney;
  private TextView mTxtEtcMoney;
  private TextView mTxtWeekMoney;
  private TextView mTxtSave, mTxt_newWorkGuide;
  private CheckBox mChkReceiveInAdvance, mChkNightMoney, mChkRefreshTime, mChkAddMoney, mChk_etcMoney, mChk_WeekMoney;
  private LinearLayout mLinStartTime, mLinEndTime, mLinGabulValue, mLinRefreshTime, mLinEtc, mLinWeek, mLinAddTime;
  private LinearLayout mLinSave, mLinAddTimelebel, mLinNightTimelebel, mLinEtcTimelebel, mLinWeekTimelebel, mLinRefreshTimelebel, mLinGabulTimelebel;
  private LinearLayout mLinNewWorkView;

  private RadioGroup mRadioRefresh;
  private int mRefreshState = 0;

  //Crouton
  private View mCroutonView;
  private TextView mTxtCrouton;
  private CroutonHelper mCroutonHelper;

  //Util
  private DBManager mDBManager;
  private NumberFormat mNumFomat = new DecimalFormat("###,###,###");
  private HYFont mFont;
  private HYPreference mPref;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_work_imsi);

    //Utill
    mPref = new HYPreference(this);
    mFont = new HYFont(this);
    ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
    mFont.setGlobalFont(root);
    mDBManager = new DBManager(this);
    //Crouton
    mCroutonHelper = new CroutonHelper(this);
    mCroutonView = getLayoutInflater().inflate(
        R.layout.crouton_custom_view, null);
    mTxtCrouton = (TextView) mCroutonView.findViewById(R.id.txt_crouton);

    mRadioRefresh = (RadioGroup) findViewById(R.id.radioGroupRefresh);

    //View Set
    mEdtHourMoney = (EditText) findViewById(R.id.edtHourMoney);
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
    mTxt_newWorkGuide = (TextView) findViewById(R.id.txt_newWorkGuide);
    mTxt_newWorkGuide.setText(Constant.GUIDE_MSG3);
    mTxt_newWorkGuide.setSelected(true); //포커싱
    mTxtStartDay = (TextView) findViewById(R.id.txtStartDay);
    mTxtEndDay = (TextView) findViewById(R.id.txtEndDay);

    mTxtWorkHour = (TextView) findViewById(R.id.txtWorkedHour);
    mTxtWorkMin = (TextView) findViewById(R.id.txtWorkedMin);

    mTxtRefreshView1 = (TextView) findViewById(R.id.txtRefreshView1);
    mTxtRefreshView2 = (TextView) findViewById(R.id.txtRefreshView2);

    mTxtStartTime_hour = (TextView) findViewById(R.id.txtStartTime_hour);
    mTxtStartTime_min = (TextView) findViewById(R.id.txtStartTime_min);
    mTxtEndTime_hour = (TextView) findViewById(R.id.txtEndTime_hour);
    mTxtEndTime_min = (TextView) findViewById(R.id.txtEndTime_min);

    mTxtEtcMoney = (TextView) findViewById(R.id.txtEtcMoney);
    mTxtWeekMoney = (TextView) findViewById(R.id.txtWeekMoney);
    mTxtGabulMoney = (TextView) findViewById(R.id.txtGabulMoney);
    mTxtSave = (TextView) findViewById(R.id.txtSave);


    mTxtAddMoney = (TextView) findViewById(R.id.txtAddMoney);
    mTxtNightMoney = (TextView) findViewById(R.id.txtNightMoney);

    mEdtMemo = (EditText) findViewById(R.id.edtMemo);
    mTxtResult = (TextView) findViewById(R.id.txtResult);

    mLinStartTime = (LinearLayout) findViewById(R.id.linStartTime);
    mLinEndTime = (LinearLayout) findViewById(R.id.linEndTime);
    mLinGabulValue = (LinearLayout) findViewById(R.id.linGabulValue);
    mLinRefreshTime = (LinearLayout) findViewById(R.id.linRefreshTime);
    mLinAddTime = (LinearLayout) findViewById(R.id.linAddTime);
    mLinEtc = (LinearLayout) findViewById(R.id.linEtc);
    mLinWeek = (LinearLayout) findViewById(R.id.linWeek);
    mLinSave = (LinearLayout) findViewById(R.id.linSave);

    mLinAddTimelebel = (LinearLayout) findViewById(R.id.linAddTimelebel);
    mLinNightTimelebel = (LinearLayout) findViewById(R.id.linNightlebel);
    mLinEtcTimelebel = (LinearLayout) findViewById(R.id.linEtclebel);
    mLinWeekTimelebel = (LinearLayout) findViewById(R.id.linWeeklebel);
    mLinRefreshTimelebel = (LinearLayout) findViewById(R.id.linRefreshlebel);
    mLinGabulTimelebel = (LinearLayout) findViewById(R.id.linGabullebel);


    mLinSave.setOnClickListener(this);
    mLinWeek.setOnClickListener(this);
    mLinEtc.setOnClickListener(this);
    mLinRefreshTime.setOnClickListener(this);
    mLinGabulValue.setOnClickListener(this);
    mLinStartTime.setOnClickListener(this);
    mLinEndTime.setOnClickListener(this);
    mLinAddTime.setOnClickListener(this);

    //PB Set
    pb = (ProgressBar) findViewById(R.id.progressDemo);
    setProgressBarIndeterminateVisibility(false);

    //Intent Get
    mAlbaName = getIntent().getStringExtra("ALBA_NAME");
    mYear = getIntent().getStringExtra("YEAR");
    mMonth = getIntent().getStringExtra("MONTH");
    mDay = getIntent().getStringExtra("DAY");
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


    mTxtStartDay.setText("오늘");
    mTxtEndDay.setText("오늘");

    if (mEdtHourMoney.getText().toString().equals("")) {
      mEdtHourMoney.setText("0");
    }
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
    mChkReceiveInAdvance = (CheckBox) findViewById(R.id.chk_receviceInAdvance);
    mChkReceiveInAdvance.setChecked(mFlag_ReceiveInAdvance);

    mChkReceiveInAdvance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mFlag_ReceiveInAdvance = isChecked;
        workHourSet();
      }
    });

    // 야간 체크
    mChkNightMoney = (CheckBox) findViewById(R.id.chk_nightMoney);
    mChkNightMoney.setChecked(mFlag_NightAdvance);

    mChkNightMoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mFlag_NightAdvance = isChecked;
        workHourSet();
      }
    });

    // 휴식 체크
    mChkRefreshTime = (CheckBox) findViewById(R.id.chk_refreshTime);
    mChkRefreshTime.setChecked(mFlag_RefreshTime);

    mChkRefreshTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mFlag_RefreshTime = isChecked;
        workHourSet();
      }
    });


    // 기타 체크
    mChk_etcMoney = (CheckBox) findViewById(R.id.chk_etcMoney);
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


    //ActionBar
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    if (mToolbar != null) {
      mToolbar.setTitle(mYear + "년 " + mMonth + "월 " + mDay + "일");
      setSupportActionBar(mToolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      mFont.setGlobalFont((ViewGroup) mToolbar);
    }


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

  public double weekPaySet() {
    Log.i("weekPaySet", Double.toString((mWeekTime / 40) * mWeekHourMoney * 8));
    return (mWeekTime / 40.0) * mWeekHourMoney * 8;
  }

  @Override
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
      case R.id.btnSave:
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
      mLinRefreshTimelebel.setBackgroundColor(getResources().getColor(R.color.red_lebel));
    } else {
      mResult_refresh = 0.0;
      mRefreshTotalTime = 0;
      mLinRefreshTimelebel.setBackgroundColor(getResources().getColor(R.color.black_lebel));
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
      mLinNightTimelebel.setBackgroundColor(getResources().getColor(R.color.red_lebel));
    } else {
      mResult = dayTimeMoney(Double.parseDouble(mEdtHourMoney.getText().toString()), Integer.parseInt(mTxtWorkHour.getText().toString()), Integer.parseInt(mTxtWorkMin.getText().toString()));
      mResult_night = 0.0;
      mTxtNightMoney.setText(Integer.toString((int) mResult_night));
      mNightTotalTime = 0;
      mLinNightTimelebel.setBackgroundColor(getResources().getColor(R.color.black_lebel));
    }


    //기타수당 적용
    mTxtEtcMoney.setText(mNumFomat.format(mEtcMoney * mEtcNum));
    if (mFlag_EtcPay) {
      mEtcTotal = mEtcMoney * mEtcNum;
      mLinEtcTimelebel.setBackgroundColor(getResources().getColor(R.color.red_lebel));
    } else {
      mEtcTotal = 0;
      mLinEtcTimelebel.setBackgroundColor(getResources().getColor(R.color.black_lebel));
    }
    //주휴수당 적용
    mTxtWeekMoney.setText(mNumFomat.format(weekPaySet()));
    if (mFlag_WeekPay) {
      mWeekTotal = weekPaySet();
      mLinWeekTimelebel.setBackgroundColor(getResources().getColor(R.color.red_lebel));
    } else {
      mWeekTotal = 0;
      mLinWeekTimelebel.setBackgroundColor(getResources().getColor(R.color.black_lebel));
    }
    //추가근무 적용
    int time_add = 0;
    mDefaultTotalTime = 0;
    if (mFlag_AddPay) {

      mLinAddTimelebel.setBackgroundColor(getResources().getColor(R.color.red_lebel));
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
      mLinAddTimelebel.setBackgroundColor(getResources().getColor(R.color.black_lebel));
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
      mLinGabulTimelebel.setBackgroundColor(getResources().getColor(R.color.red_lebel));
    } else {
      mLinGabulTimelebel.setBackgroundColor(getResources().getColor(R.color.black_lebel));
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
      pbAniSet();

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


  public void AlertDialog_AlbaDel() {
    AlertDialog.Builder alert = new AlertDialog.Builder(this);

    // 제목, 메시지, icon, 버튼
    alert.setTitle("삭제");
    alert.setMessage("삭제하시겠습니까?");
    // cancel : false = 단말기 back button으로 취소되지 않음.
    alert.setCancelable(false);
    // yes
    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        //Yes 버튼을 눌렀을때 일어날 일을 서술한다.
        if (mDBManager.removeData(mYear + "-" + mMonth + "-" + mDay, mAlbaName)) {
          Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
          finish();
        } else {
          mTxtCrouton.setText("삭제에 실패하였습니다. 재시도 해주세요");
          mCroutonHelper.setCustomView(mCroutonView);
          mCroutonHelper.show();
        }
      }
    });

    // no
    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        dialog.cancel(); // No 버튼을 눌렀을 경우이며 단순히 창을 닫아 버린다.
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


  public void pbAniSet() {
    int workTotal = Integer.parseInt(mTxtWorkHour.getText().toString()) * 60 + Integer.parseInt(mTxtWorkMin.getText().toString());
    Log.i("workTotal : ", "" + workTotal);
    if (workTotal <= 720) {
      Log.i("startTime : ", "" + mStartHour * 60 + mStartMin);
      pb.setRotation(((mStartHour * 30) + (int) (mStartMin * 0.5)) - 1); //1시간에 30도 1분에 0.5도
      pb.setMax(720);
      pb.setProgress(0);
      pb.setSecondaryProgress(0);
      animationA = ObjectAnimator.ofInt(pb, "progress", -1, workTotal);
      animationA.setDuration(5000); //in milliseconds
      animationA.setInterpolator(new DecelerateInterpolator());
      animationA.start();
    } else if (workTotal > 720) {
      Log.i("startTime : ", "" + Integer.toString(mStartHour * 60 + mStartMin));
      Log.i("progress : ", "" + 720);
      pb.setRotation((mStartHour * 30) + (int) (mStartMin * 0.5)); //1시간에 30도 1분에 0.5도
      pb.setMax(720);
      pb.setProgress(0);
      animationA = ObjectAnimator.ofInt(pb, "progress", 0, 720);
      animationA.setDuration(5000); //in milliseconds
      animationA.setInterpolator(new DecelerateInterpolator());
      animationA.start();

      Log.i("secProgress : ", Integer.toString(workTotal - 720));
      pb.setSecondaryProgress(0);
      animationB = ObjectAnimator.ofInt(pb, "secondaryProgress", -1, workTotal - 720);
      animationB.setDuration(5000); //in milliseconds
      animationB.setInterpolator(new DecelerateInterpolator());
      animationB.setStartDelay(5000);
      animationB.start();
    }
  }
}
