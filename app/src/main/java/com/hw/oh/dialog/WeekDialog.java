package com.hw.oh.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class WeekDialog extends DialogFragment {

  // Log
  private static final String TAG = "WeekDialog";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;

  //View
  private EditText mEdtWeekTime, mEdtWeekMoney;

  private int mWeekMoney = 0;
  private Double mWeekTime = 0.0;

  //flag
  private Boolean mRule1 = false, mRule2 = false;
  // Utill
  private HYFont mFont;

  private TextView dialog_BtnOk;
  private NumberFormat mNumFomat = new DecimalFormat("###,###,###");
  private CheckBox mChkRule1, mChkRule2;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
    LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
    View v = mLayoutInflater.inflate(R.layout.dialog_week, null);

    // Utill
    mFont = new HYFont(getActivity());
    mFont.setGlobalFont((ViewGroup) v);


    Bundle bundle = getArguments();
    mWeekMoney = bundle.getInt("weekMoney");
    mWeekTime = bundle.getDouble("weekTime");


    mEdtWeekTime = (EditText) v.findViewById(R.id.edtWeekHour);
    mEdtWeekTime.setText(Double.toString(mWeekTime));
    mEdtWeekMoney = (EditText) v.findViewById(R.id.edtHourMoney);
    mEdtWeekMoney.setText(Integer.toString(mWeekMoney));
    mChkRule1 = (CheckBox) v.findViewById(R.id.chk_weekrule1);
    mChkRule2 = (CheckBox) v.findViewById(R.id.chk_weekrule2);
    mChkRule1.setChecked(false);
    mChkRule2.setChecked(false);

    mChkRule1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mRule1 = isChecked;
      }
    });
    mChkRule2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mRule2 = isChecked;
      }
    });
    // Button
    dialog_BtnOk = (TextView) v.findViewById(R.id.btnOk);
    dialog_BtnOk.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        try {
          if (mRule1 && mRule2) {
            if (Double.parseDouble(mEdtWeekTime.getText().toString()) >= 15 && Double.parseDouble(mEdtWeekTime.getText().toString()) <= 40) {
              dismiss();
              WeekDialogListener activity = (WeekDialogListener) getActivity();
              activity.onFinishWeekDialog(mEdtWeekMoney.getText().toString(), mEdtWeekTime.getText().toString());
            } else {
              Toast.makeText(getActivity(), "한주간의 통상 근로시간은 최소 15시간에서 최대 40시간까지가 한도이며, 그이상 근로를 하여도 주휴수당은 40시간까지만 적용합니다.", Toast.LENGTH_LONG).show();
            }
          } else {
            Toast.makeText(getActivity(), "위 항목을 모두 체크한 후 적용해주세요.", Toast.LENGTH_SHORT).show();
          }
        } catch (Exception e) {
          Log.i(TAG, e.toString());
        }
      }
    });
    mBuilder.setView(v);

    return mBuilder.create();
  }

  public interface WeekDialogListener {
    void onFinishWeekDialog(String weekMoney, String weekTime);
  }


}
