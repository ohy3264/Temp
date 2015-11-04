package com.hw.oh.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hw.oh.model.DetailCalItem;
import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;


public class ResultInfoDialog extends DialogFragment {

  // Log
  private static final String TAG = "HourPayInfoDialog3";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;
  private TextView dialog_BtnOk;

  //View
  private TextView mTxtHourMoney_daytime, mTxtHourMoney_night, mTxtHourMoney_add;
  private TextView mTxtTotalTime, mTxtTime_night;
  private TextView mTxtTime_daytime, mTxtTime_refresh;
  private TextView mTxtTime_add, mTxtEtc_Money;
  //Data
  private DetailCalItem mDetailCalItem = new DetailCalItem();

  //flag
  private int mViewFlag;
  // Utill
  private HYFont mFont;


  private NumberFormat mNumFomat = new DecimalFormat("###,###,###");

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
    LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
    View v = mLayoutInflater.inflate(R.layout.dialog_resultinfo, null);

    // Utill
    mFont = new HYFont(getActivity());
    mFont.setGlobalFont((ViewGroup) v);

    mTxtHourMoney_daytime = (TextView) v.findViewById(R.id.txtHourMoney_daytime);
    mTxtHourMoney_night = (TextView) v.findViewById(R.id.txtHourMoney_night);
    mTxtHourMoney_add = (TextView) v.findViewById(R.id.txtHourMoney_add);

    mTxtTotalTime = (TextView) v.findViewById(R.id.txtTotalTime);
    mTxtTime_daytime = (TextView) v.findViewById(R.id.txtTime_daytime);
    mTxtTime_night = (TextView) v.findViewById(R.id.txtTime_night);
    mTxtTime_refresh = (TextView) v.findViewById(R.id.txtTime_refresh);
    mTxtTime_add = (TextView) v.findViewById(R.id.txtTime_add);
    mTxtEtc_Money = (TextView) v.findViewById(R.id.txtEtcMoney);

    mDetailCalItem = (DetailCalItem) getArguments().getSerializable("DetailItem");
    mTxtHourMoney_daytime.setText(mNumFomat.format(mDetailCalItem.getHourMoney_dayTime()) + " 원");
    mTxtHourMoney_night.setText(mNumFomat.format(mDetailCalItem.getHourMoney_night()) + " 원 ");
    mTxtHourMoney_add.setText(mNumFomat.format(mDetailCalItem.getHourMoney_add()) + " 원");
    mTxtEtc_Money.setText(mNumFomat.format(mDetailCalItem.getEtcMoney()) + " 원");

    Calendar totalCal = Calendar.getInstance();
    totalCal.set(0, 0, 0, 0, mDetailCalItem.getTotalTime());
    mTxtTotalTime.setText(Integer.toString(totalCal.get(Calendar.HOUR_OF_DAY)) + "시간 " + Integer.toString(totalCal.get(Calendar.MINUTE)) + "분");

    Calendar daytimeCal = Calendar.getInstance();
    daytimeCal.set(0, 0, 0, 0, mDetailCalItem.getTime_dayTime());
    mTxtTime_daytime.setText(Integer.toString(daytimeCal.get(Calendar.HOUR_OF_DAY)) + "시간 " + Integer.toString(daytimeCal.get(Calendar.MINUTE)) + "분");

    Calendar nightCal = Calendar.getInstance();
    nightCal.set(0, 0, 0, 0, mDetailCalItem.getTime_night());
    mTxtTime_night.setText(Integer.toString(nightCal.get(Calendar.HOUR_OF_DAY)) + "시간 " + Integer.toString(nightCal.get(Calendar.MINUTE)) + "분");

    Calendar refreshCal = Calendar.getInstance();
    Log.i(TAG, Integer.toString(mDetailCalItem.getTime_refresh()));
    refreshCal.set(0, 0, 0, 0, mDetailCalItem.getTime_refresh());
    mTxtTime_refresh.setText(Integer.toString(refreshCal.get(Calendar.HOUR_OF_DAY)) + "시간 " + Integer.toString(refreshCal.get(Calendar.MINUTE)) + "분");

    Log.i(TAG, Integer.toString(mDetailCalItem.getTime_add()));
    Calendar addCal = Calendar.getInstance();
    Log.i(TAG, addCal.toString());
    addCal.set(0, 0, 0, 0, mDetailCalItem.getTime_add());
    mTxtTime_add.setText(Integer.toString(addCal.get(Calendar.HOUR_OF_DAY)) + "시간 " + Integer.toString(addCal.get(Calendar.MINUTE)) + "분");

    // Button
    dialog_BtnOk = (TextView) v.findViewById(R.id.btnOk);
    dialog_BtnOk.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        dismiss();
      }
    });


    mBuilder.setView(v);

    return mBuilder.create();
  }
}
