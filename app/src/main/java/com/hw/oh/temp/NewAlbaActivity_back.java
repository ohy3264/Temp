package com.hw.oh.temp;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.hw.oh.dialog.AddInfoDialog;
import com.hw.oh.dialog.EtcDialog;
import com.hw.oh.dialog.RefreshInfoDialog;
import com.hw.oh.dialog.WeekDialog;
import com.hw.oh.model.PartTimeInfo;
import com.hw.oh.sqlite.DBConstant;
import com.hw.oh.sqlite.DBManager;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.InfoExtra;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

/**
 * Created by oh on 2015-02-02.
 */
public class NewAlbaActivity_back extends BaseActivity implements View.OnClickListener, RefreshInfoDialog.RefreshDialogListener, WeekDialog.WeekDialogListener, EtcDialog.EtcDialogListener, AddInfoDialog.AddDialogListener {
  public static final String TAG = "NewPostActivity";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;
  //View
  private Toolbar mToolbar;
  CollapsingToolbarLayout collapsingToolbarLayout;
  private EditText mEdtAlbaName, mEdtHourMoney, mEdtMemo;
  private LinearLayout mLinSave;
  private TextView mTxtSave;

  private TextView mTxtStartTime_hour, mTxtStartTime_min, mTxtEndTime_hour, mTxtEndTime_min;

  private LinearLayout mLinStartTime, mLinEndTime, mLinRefreshTime, mLinEtc, mLinWeek, mLinAddTime;

  private CheckBox mChkNightMoney, mChkRefreshTime, mChkAddMoney, mChkEtcPay, mChkWeekPay;

  //Data
  private PartTimeInfo mPartInfoData, mPartInfoData_Intent;
  private int mStartMin;
  private int mStartHour;
  private int mEndMin;
  private int mEndHour;

  private int mTotalTimeAll = 600;
  private int mWeekHourMoney = 0;
  private Double mWeekTime = 0.0;
  private int mEtcMoney = 0;
  private int mEtcNum = 0;

  private int mAddType = 0;
  private int mAddHour = 0;
  private int mAddMin = 0;
  private int mRefreshType = 0;
  private int mRefreshHour = 0;
  private int mRefreshMin = 0;
  private TextView mTxtRefreshView1, mTxtRefreshView2;
  private TextView mTxtEtcMoney;
  private TextView mTxtWeekMoney;
  private TextView mTxtAdd;

  //Flag
  private Boolean mFlag_NightAdvance = false;
  private Boolean mFlag_RefreshTime = false;
  private Boolean mFlag_AddPay = false;
  private Boolean mFlag_EtcPay = false;
  private Boolean mFlag_WeekPay = false;

  //Crouton
  private View mCroutonView;
  private TextView mTxtCrouton;
  private CroutonHelper mCroutonHelper;
  private TextView mTxtStartDay, mTxtEndDay;

  //Utill
  private RequestQueue mRequestQueue;
  private InfoExtra mInfoExtra;
  private HYPreference mPref;
  private HYFont mFont;
  private DBManager mDB;
  private NumberFormat mNumFomat = new DecimalFormat("###,###,###");


  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_newalba_imsi);
    //Util
    mInfoExtra = new InfoExtra(this);
    mPref = new HYPreference(this);
    mRequestQueue = Volley.newRequestQueue(this);
    mFont = new HYFont(this);
    mDB = new DBManager(this);

    ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
    mFont.setGlobalFont(root);

    //Crouton
    mCroutonHelper = new CroutonHelper(this);
    mCroutonView = getLayoutInflater().inflate(
        R.layout.crouton_custom_view, null);
    mTxtCrouton = (TextView) mCroutonView.findViewById(R.id.txt_crouton);

    //ActionBar
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    if (mToolbar != null) {
      setSupportActionBar(mToolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      mFont.setGlobalFont((ViewGroup) mToolbar);
/*
            collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
            collapsingToolbarLayout.setTitle("Design Library");*/
    }

    mEdtAlbaName = (EditText) findViewById(R.id.edtAlbaName);
    mEdtHourMoney = (EditText) findViewById(R.id.edtHourMoney);
    mEdtMemo = (EditText) findViewById(R.id.edtMemo);

    mTxtStartTime_hour = (TextView) findViewById(R.id.txtStartTime_hour);
    mTxtStartTime_min = (TextView) findViewById(R.id.txtStartTime_min);
    mTxtEndTime_hour = (TextView) findViewById(R.id.txtEndTime_hour);
    mTxtEndTime_min = (TextView) findViewById(R.id.txtEndTime_min);
    mTxtRefreshView1 = (TextView) findViewById(R.id.txtRefreshView1);
    mTxtRefreshView2 = (TextView) findViewById(R.id.txtRefreshView2);
    mTxtAdd = (TextView) findViewById(R.id.txtAddMoney);

    mLinSave = (LinearLayout) findViewById(R.id.linSave);
    mLinSave.setOnClickListener(this);

    mLinStartTime = (LinearLayout) findViewById(R.id.linStartTime);
    mLinEndTime = (LinearLayout) findViewById(R.id.linEndTime);
    mLinRefreshTime = (LinearLayout) findViewById(R.id.linRefreshTime);
    mLinEtc = (LinearLayout) findViewById(R.id.linEtc);
    mLinWeek = (LinearLayout) findViewById(R.id.linWeek);
    mLinAddTime = (LinearLayout) findViewById(R.id.linAddTime);

    mLinWeek.setOnClickListener(this);
    mLinEtc.setOnClickListener(this);
    mLinRefreshTime.setOnClickListener(this);
    mLinStartTime.setOnClickListener(this);
    mLinEndTime.setOnClickListener(this);
    mLinAddTime.setOnClickListener(this);

    mTxtEtcMoney = (TextView) findViewById(R.id.txtEtcMoney);
    mTxtWeekMoney = (TextView) findViewById(R.id.txtWeekMoney);
    mTxtSave = (TextView) findViewById(R.id.txtSave);
    mTxtStartDay = (TextView) findViewById(R.id.txtStartDay);
    mTxtEndDay = (TextView) findViewById(R.id.txtEndDay);
    mTxtStartDay.setText("오늘");
    mTxtEndDay.setText("오늘");
    mTxtAdd.setText("8시간 이상");


    switch (getIntent().getStringExtra("Flag")) {
      case "NEW":
        mTxtSave.setText("저장하기");
        mToolbar.setTitle("저장하기");
        break;
      case "UPDATE":
        mTxtSave.setText("수정하기");
        mToolbar.setTitle("수정하기");
        mPartInfoData_Intent = (PartTimeInfo) getIntent().getSerializableExtra("ObjectData");
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
        //mFlag_WeekPay = Boolean.parseBoolean(mPartInfoData_Intent.getWorkPayWeek());
        mWeekHourMoney = mPartInfoData_Intent.getWorkPayWeekMoney();
        mWeekTime = mPartInfoData_Intent.getWorkPayWeekTime();
        mAddType = Integer.parseInt(mPartInfoData_Intent.getWorkAddType());
        mAddHour = Integer.parseInt(mPartInfoData_Intent.getWorkPayAddHour());
        mAddMin = Integer.parseInt(mPartInfoData_Intent.getWorkPayAddMin());
        switch (mPartInfoData_Intent.getWorkAddType()) {
          case "0":
            mTxtAdd.setText("8시간 이상");
            break;
          case "1":
            mTxtAdd.setText("풀타임");
            break;
          case "2":
            mTxtAdd.setText("직접입력");
            break;
        }
        workHourSet();

        break;
    }

    // 야간 체크
    mChkNightMoney = (CheckBox) findViewById(R.id.chk_nightMoney);
    mChkNightMoney.setChecked(mFlag_NightAdvance);

    mChkNightMoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mFlag_NightAdvance = isChecked;
        //workHourSet();
      }
    });

    // 휴식 체크
    mChkRefreshTime = (CheckBox) findViewById(R.id.chk_refreshTime);
    mChkRefreshTime.setChecked(mFlag_RefreshTime);

    mChkRefreshTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mFlag_RefreshTime = isChecked;
        //workHourSet();
      }
    });
    //추가 체크
    mChkAddMoney = (CheckBox) findViewById(R.id.chk_addMoney);
    mChkAddMoney.setChecked(mFlag_AddPay);

    mChkAddMoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mFlag_AddPay = isChecked;
      }
    });

    //기타체크
    mChkEtcPay = (CheckBox) findViewById(R.id.chk_etcMoney);
    mChkEtcPay.setChecked(mFlag_EtcPay);

    mChkEtcPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mFlag_EtcPay = isChecked;
      }
    });

    //주휴체크
    mChkWeekPay = (CheckBox) findViewById(R.id.chk_WeekMoney);
    mChkWeekPay.setChecked(mFlag_WeekPay);

    mChkWeekPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mFlag_WeekPay = isChecked;
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


  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.linStartTime:
        if (mPref.getValue(mPref.KEY_PICKER_THEME, 0) == 0) {
          final com.rey.material.app.TimePickerDialog startDialog = new com.rey.material.app.TimePickerDialog(this);

          startDialog.hour(Integer.parseInt(mTxtEndTime_hour.getText().toString()));
          startDialog.minute(Integer.parseInt(mTxtEndTime_min.getText().toString()));
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


    //종료시간이 시작시간 보다 작을 때
    if (mEndHour < mStartHour) {
      mTxtEndDay.setText("내일");
    } else if (mEndHour == mStartHour) {
      mTxtEndDay.setText("내일");
      if (!(mEndMin < mStartMin)) {
        mTxtEndDay.setText("오늘");
      }
    } else {
      //Log.d("종료시간이 시작시간 보다 클 때", "");
      mTxtEndDay.setText("오늘");
    }


    //기타수당 적용
    mTxtEtcMoney.setText(mNumFomat.format(mEtcMoney * mEtcNum));
    //주휴수당 적용
    mTxtWeekMoney.setText(mNumFomat.format(weekPaySet()));

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

    // 제목, 메시지, icon, 버튼
    alert.setTitle("삭제");
    alert.setMessage("삭제하시겠습니까?");
    // cancel : false = 단말기 back button으로 취소되지 않음.
    alert.setCancelable(false);
    // yes
    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        //Yes 버튼을 눌렀을때 일어날 일을 서술한다.
        if (mEdtAlbaName.getText().toString().isEmpty()) {
          mTxtCrouton.setText("알바명을 입력하세요");
          mCroutonHelper.setCustomView(mCroutonView);
          mCroutonHelper.show();
        } else if (mDB.selectInfo(DBConstant.COLUMN_ALBANAME, mEdtAlbaName.getText().toString()) instanceof PartTimeInfo) {
          if (mDB.removeInfo(mEdtAlbaName.getText().toString())) {
            Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
          } else {
            mTxtCrouton.setText("삭제에 실패하였습니다. 재시도 해주세요");
            mCroutonHelper.setCustomView(mCroutonView);
            mCroutonHelper.show();
          }

        } else {
          mTxtCrouton.setText("이미 등록된 알바명가 존재하지 않습니다");
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
  public boolean onPrepareOptionsMenu(Menu menu) {
    switch (mTxtSave.getText().toString()) {
      case "저장하기":
        //menu.findItem(R.id.action_kakao_share).setVisible(false);
        menu.findItem(R.id.action_alba_del).setVisible(false);
        // menu.findItem(R.id.action_detail_info).setVisible(false);
        break;
      case "수정하기":
        //  menu.findItem(R.id.action_kakao_share).setVisible(false);
        menu.findItem(R.id.action_alba_del).setVisible(true);
        //   menu.findItem(R.id.action_detail_info).setVisible(false);
        break;
    }
    return super.onPrepareOptionsMenu(menu);
  }

  public void albaUpdate() {
    if (mEdtAlbaName.getText().toString().isEmpty()) {
      mTxtCrouton.setText("알바명을 입력하세요");
      mCroutonHelper.setCustomView(mCroutonView);
      mCroutonHelper.show();
    } else if (mEdtHourMoney.getText().toString().isEmpty()) {

      mTxtCrouton.setText("시급을 입력하세요");
      mCroutonHelper.setCustomView(mCroutonView);
      mCroutonHelper.show();
    } else {
      dataSet();
      if (mDB.updatePartTimeInfo(mPartInfoData, mPartInfoData_Intent.getAlbaname())) {
        finish();
      } else {
        mTxtCrouton.setText("업데이트에 실패하였습니다. 재시도 해주세요");
        mCroutonHelper.setCustomView(mCroutonView);
        mCroutonHelper.show();
      }
    }
  }

  public void albaSave() {
    if (mEdtAlbaName.getText().toString().isEmpty()) {
      mTxtCrouton.setText("알바명을 입력하세요");
      mCroutonHelper.setCustomView(mCroutonView);
      mCroutonHelper.show();
    } else if (mEdtHourMoney.getText().toString().isEmpty()) {

      mTxtCrouton.setText("시급을 입력하세요");
      mCroutonHelper.setCustomView(mCroutonView);
      mCroutonHelper.show();
    } else if (mDB.selectInfo(DBConstant.COLUMN_ALBANAME, mEdtAlbaName.getText().toString()) instanceof PartTimeInfo) {

      mTxtCrouton.setText("이미 등록된 알바명이 존재합니다");
      mCroutonHelper.setCustomView(mCroutonView);
      mCroutonHelper.show();
    } else {
      dataSet();
      if (mDB.insertPartTimeInfo(mPartInfoData)) {
        finish();
      } else {
        mTxtCrouton.setText("저장에 실패하였습니다. 재시도 해주세요");
        mCroutonHelper.setCustomView(mCroutonView);
        mCroutonHelper.show();
      }
    }
  }

  public double weekPaySet() {
    Log.i("weekPaySet", Double.toString((mWeekTime / 40) * mWeekHourMoney * 8));
    return (mWeekTime / 40.0) * mWeekHourMoney * 8;
  }

  public void dataSet() {
    mPartInfoData = new PartTimeInfo();
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
        mTxtAdd.setText("8시간 이상");
        break;
      case 1:
        mTxtAdd.setText("풀타임");
        break;
      case 2:
        mTxtAdd.setText("직접입력");
        break;
    }
  }
}
