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
import android.widget.NumberPicker;
import android.widget.TextView;

import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class EtcDialog extends DialogFragment {

  // Log
  private static final String TAG = "EtcDialog";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;

  //View
  private EditText mEdtEtcMoney;
  private NumberPicker mNumberPicker;

  private int mEtcMoney = 0;
  private int mEtcNum = 1;

  // Utill
  private HYFont mFont;

  private TextView dialog_BtnOk;
  private HYPreference mPref;
  private NumberFormat mNumFomat = new DecimalFormat("###,###,###");

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
    LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
    View v = mLayoutInflater.inflate(R.layout.dialog_etc, null);

    // Utill
    mFont = new HYFont(getActivity());
    mFont.setGlobalFont((ViewGroup) v);
    mPref = new HYPreference(getActivity());

    Bundle bundle = getArguments();
    mEtcMoney = bundle.getInt("etcMoney");
    mEtcNum = bundle.getInt("etcNum");

    if (INFO) {
      Log.i(TAG, "mEtcMoney : " + mEtcMoney);
      Log.i(TAG, "mEtcNum : " + mEtcNum);
    }


    mEdtEtcMoney = (EditText) v.findViewById(R.id.edtEtcMoney);
    mEdtEtcMoney.setText(Integer.toString(mEtcMoney));
    mNumberPicker = (NumberPicker) v.findViewById(R.id.numberPicker1);
    mNumberPicker.setMinValue(1);
    mNumberPicker.setMaxValue(100);
    mNumberPicker.setValue(mEtcNum);

    // Button
    dialog_BtnOk = (TextView) v.findViewById(R.id.btnOk);
    dialog_BtnOk.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        dismiss();
        EtcDialogListener activity = (EtcDialogListener) getActivity();

        if (mEdtEtcMoney.getText().toString().equals(""))
          mEdtEtcMoney.setText("0");
        activity.onFinishEtcDialog(mEdtEtcMoney.getText().toString(), Integer.toString(getValue(mNumberPicker)));
        mPref.put(mPref.KEY_ETC_MONEY, mEdtEtcMoney.getText().toString());


      }
    });
    mBuilder.setView(v);

    return mBuilder.create();
  }

  public int getValue(NumberPicker np) {
    return ((NumberPicker) np).getValue();

  }

  public interface EtcDialogListener {
    void onFinishEtcDialog(String money, String num);
  }


}
