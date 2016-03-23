package com.hw.oh.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;


public class MonthPayDialog extends DialogFragment {
  // Log
  private static final String TAG = "MonthPayDialog";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;
  private TextView dialog_BtnOk;
  private int mMonthDay = 1;
  private int mWeekDay = 2;
  private int mMonthWeekSelector = 1; //month :: 1, week :: 0
  private EditText edtMonthPayDay;
 private RadioGroup mRgMonthWeekSelect;
  private RadioGroup mRgWeekSelect;
  private LinearLayout mLinMonthPay;
  private LinearLayout mLinWeekPay;

  // Utill
  private HYFont mFont;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
    LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
    View v = mLayoutInflater.inflate(R.layout.dialog_monthpay, null);

    mFont = new HYFont(getActivity());
    mFont.setGlobalFont((ViewGroup) v);

    Bundle bundle = getArguments();
    try {
      mMonthDay = bundle.getInt("MonthDay");
      mWeekDay = bundle.getInt("WeekDay");
      mMonthWeekSelector = bundle.getInt("MonthWeekFlag");
    } catch (Exception e) {
      Log.e(TAG, e.toString());
    }
    mLinMonthPay = (LinearLayout) v.findViewById(R.id.linMonthPay);
    mLinWeekPay = (LinearLayout) v.findViewById(R.id.linWeekPay);

    edtMonthPayDay = (EditText) v.findViewById(R.id.edtMonthDay);
    edtMonthPayDay.setText("" + mMonthDay);
    mRgMonthWeekSelect = (RadioGroup) v.findViewById(R.id.radioGroupMonth);
    mRgMonthWeekSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
          case R.id.radioWeek:
            mMonthWeekSelector = 0;
            mLinMonthPay.setVisibility(View.GONE);
            mLinWeekPay.setVisibility(View.VISIBLE);
            break;
          case R.id.radioMonth:
            mMonthWeekSelector = 1;
            mLinMonthPay.setVisibility(View.VISIBLE);
            mLinWeekPay.setVisibility(View.GONE);
            break;
        }
      }
    });
    mRgWeekSelect = (RadioGroup) v.findViewById(R.id.radioGroupWeek);
    mRgWeekSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
          case R.id.chk_mon:
            mWeekDay = 2;
            break;
          case R.id.chk_thu:
            mWeekDay = 3;
            break;

          case R.id.chk_wed:
            mWeekDay = 4;
            break;

          case R.id.chk_thur:
            mWeekDay = 5;
            break;

          case R.id.chk_fri:
            mWeekDay = 6;
            break;

          case R.id.chk_sat:
            mWeekDay = 7;
            break;

          case R.id.chk_sun:
            mWeekDay = 1;
            break;
        }
      }
    });



    // Button
    dialog_BtnOk = (TextView) v.findViewById(R.id.btnOk);
    dialog_BtnOk.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        if (!edtMonthPayDay.getText().toString().equals("")) {
          int monthPayDay = Integer.parseInt(edtMonthPayDay.getText().toString());
          if (0 < monthPayDay && monthPayDay <= 31) {
            OnHeadlineSelectedListener activity = (OnHeadlineSelectedListener) getActivity();
            activity.onArticleSelected(mMonthWeekSelector, mWeekDay, Integer.parseInt(edtMonthPayDay.getText().toString()));

            dismiss();
          } else {
            Toast.makeText(getActivity(), "1~31 사이의 날짜를 선택하세요", Toast.LENGTH_SHORT).show();
          }
        } else {
          Toast.makeText(getActivity(), "빈칸으로 둘 수 없습니다", Toast.LENGTH_SHORT).show();
        }


        switch (mMonthWeekSelector) {
          case 0:

            break;

          case 1:

            break;
        }

      }
    });
    inits();
    mBuilder.setView(v);

    return mBuilder.create();
  }

  public void inits(){
    switch (mMonthWeekSelector){
      case 0:
        mRgMonthWeekSelect.check(R.id.radioWeek);
        mLinMonthPay.setVisibility(View.GONE);
        mLinWeekPay.setVisibility(View.VISIBLE);
        break;
      case 1:
        mRgMonthWeekSelect.check(R.id.radioMonth);
        mLinMonthPay.setVisibility(View.VISIBLE);
        mLinWeekPay.setVisibility(View.GONE);
        break;
    }
    switch (mWeekDay){
      case 2:
        mRgWeekSelect.check(R.id.chk_mon);
        break;
      case 3:
        mRgWeekSelect.check(R.id.chk_thu);
        break;

      case 4:
        mRgWeekSelect.check(R.id.chk_wed);
        break;

      case 5:
        mRgWeekSelect.check(R.id.chk_thur);
        break;

      case 6:
        mRgWeekSelect.check(R.id.chk_fri);
        break;

      case 7:
        mRgWeekSelect.check(R.id.chk_sat);
        break;

      case 1:
        mRgWeekSelect.check(R.id.chk_sun);
        break;
    }

  }

  public interface OnHeadlineSelectedListener {
    public void onArticleSelected(int flag, int weekDay, int monthDay);
  }
}
