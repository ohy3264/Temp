package com.hw.oh.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.CountDownLatch;

/**
 * Created by oh on 2015-02-01.
 */
public class Fragment_Calculation extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {
  public static final String TAG = "Fragment_Calculation";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;

  private int mMin;
  private int mHour;
  private int mDay;
  private int mMonth;
  private double mHourMoney;
  private double mResult;
  private String mResult_st;

  private TextView mTxtResult;
  private EditText mEdtTimeMoney;
  private Button mBtnCal;
  private Button mBtnReCal;

  //spinner
  private Spinner mSpChange;
  private Spinner mSpDayMin;
  private Spinner mSpDayHour;
  private Spinner mSpMonthDay;
  private Spinner mSpYearMonth;

  //Crouton
  private View mCroutonView;
  private TextView mTxtCrouton;
  private CroutonHelper mCroutonHelper;

  //Utill
  private NumberFormat mNumFomat = new DecimalFormat("###,###,###");
  private HYFont mFont;
  private CountDownLatch mCountDownLatch_BoardList;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_calculation, container, false);
    //Utill
    mFont = new HYFont(getActivity());

    mFont.setGlobalFont((ViewGroup) rootView);

    //Crouton
    mCroutonHelper = new CroutonHelper(getActivity());
    mCroutonView = getActivity().getLayoutInflater().inflate(
        R.layout.crouton_custom_view, null);
    mTxtCrouton = (TextView) mCroutonView.findViewById(R.id.txt_crouton);

    //View Set
    mEdtTimeMoney = (EditText) rootView.findViewById(R.id.edtTimeMoney);
    mTxtResult = (TextView) rootView.findViewById(R.id.txtResult);
    mSpChange = (Spinner) rootView.findViewById(R.id.spChange);
    mSpDayMin = (Spinner) rootView.findViewById(R.id.spDayMin);
    mSpDayHour = (Spinner) rootView.findViewById(R.id.spDayHour);
    mSpMonthDay = (Spinner) rootView.findViewById(R.id.spMonthDay);
    mSpYearMonth = (Spinner) rootView.findViewById(R.id.spYeatMonth);

    String[] change = getActivity().getResources().getStringArray(R.array.change);
    mSpChange.setAdapter(new com.hw.oh.adapter.SpinnerAdapter(getActivity(), R.layout.spinner_row_default, change));
    String[] minute = getActivity().getResources().getStringArray(R.array.minute);
    mSpDayMin.setAdapter(new com.hw.oh.adapter.SpinnerAdapter(getActivity(), R.layout.spinner_row_default, minute));
    String[] hour = getActivity().getResources().getStringArray(R.array.time);
    mSpDayHour.setAdapter(new com.hw.oh.adapter.SpinnerAdapter(getActivity(), R.layout.spinner_row_default, hour));
    String[] day = getActivity().getResources().getStringArray(R.array.day);
    mSpMonthDay.setAdapter(new com.hw.oh.adapter.SpinnerAdapter(getActivity(), R.layout.spinner_row_default, day));
    String[] month = getActivity().getResources().getStringArray(R.array.month);
    mSpYearMonth.setAdapter(new com.hw.oh.adapter.SpinnerAdapter(getActivity(), R.layout.spinner_row_default, month));


    mSpChange.setOnItemSelectedListener(this);
    mSpDayMin.setOnItemSelectedListener(this);
    mSpDayHour.setOnItemSelectedListener(this);
    mSpMonthDay.setOnItemSelectedListener(this);
    mSpYearMonth.setOnItemSelectedListener(this);

    mBtnCal = (Button) rootView.findViewById(R.id.btnCalculation);
    mBtnReCal = (Button) rootView.findViewById(R.id.btnReCal);
    mBtnCal.setOnClickListener(this);
    mBtnReCal.setOnClickListener(this);
    return rootView;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btnCalculation:
        mHourMoney = Double.parseDouble(mEdtTimeMoney.getText().toString());
        Log.d(TAG, mHourMoney + " 시급");
        Log.d(TAG, mHour + " 시간");
        Log.d(TAG, mMin + " 분");
        Log.d(TAG, mDay + " 일");
        Log.d(TAG, mMonth + " 개월");


        switch (mSpChange.getSelectedItemPosition()) {
          case 0:
            mResult = dayTimeMoney(mHourMoney, mHour, mMin);
            break;
          case 1:
            mResult = monthTimeMoney(mHourMoney, mHour, mMin, mDay);
            break;
          case 2:
            mResult = yearTimeMoney(mHourMoney, mHour, mMin, mDay, mMonth);
            break;

        }

        mResult_st = mNumFomat.format(mResult);
        mTxtResult.setText(mResult_st);
        break;
      case R.id.btnReCal:
        mSpChange.setSelection(0);
        mSpYearMonth.setSelection(0);
        mSpMonthDay.setSelection(0);
        mSpDayHour.setSelection(0);
        mSpDayMin.setSelection(0);
        mTxtResult.setText("0");
        mEdtTimeMoney.setText("0");
        break;
    }

  }

  public Double dayTimeMoney(Double hourmoney, int hour, int min) {
    Double minmoney = hourmoney / 60;
    return (hourmoney * hour + minmoney * min);

  }

  public Double monthTimeMoney(Double hourmoney, int hour, int min, int day) {
    Double minmoney = hourmoney / 60;

    return (hourmoney * hour + minmoney * min) * day;
  }

  public Double yearTimeMoney(Double hourmoney, int hour, int min, int day, int month) {
    Double minmoney = hourmoney / 60;

    return ((hourmoney * hour + minmoney * min) * day) * month;
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    switch (parent.getId()) {
      case R.id.spChange:
        if (position == 0) {
          this.mSpMonthDay.setEnabled(false);
          this.mSpYearMonth.setEnabled(false);
        } else if (position == 1) {
          this.mSpMonthDay.setEnabled(true);
          this.mSpYearMonth.setEnabled(false);
        } else if (position == 2) {
          this.mSpMonthDay.setEnabled(true);
          this.mSpYearMonth.setEnabled(true);
        }

        break;
      case R.id.spDayHour:
        if (position == 23) {
          mSpDayMin.setSelection(0);
        }
        mHour = position + 1;
        break;
      case R.id.spDayMin:
        mMin = position;
        break;
      case R.id.spMonthDay:
        mDay = position + 1;
        break;
      case R.id.spYeatMonth:
        mMonth = position + 1;
        break;
    }

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {

  }

  @Override
  public void onResume() {
    super.onResume();

  }



}
