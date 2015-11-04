package com.hw.oh.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hw.oh.fragment.Fragment_Setting;
import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;


public class DigStyleDialog extends DialogFragment {

  // Log
  private static final String TAG = "DigStyleDialog";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;

  //View
  private LinearLayout mLinAnalogTimeView, mLinDigitalTimeView, mLinDigitalTime24View;
  private RadioGroup mRadioGroupStyle;

  private int mFlag;

  // Utill
  private HYFont mFont;
  private HYPreference mPref;

  private TextView dialog_BtnOk;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
    LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
    View v = mLayoutInflater.inflate(R.layout.dialog_style, null);

    mPref = new HYPreference(getActivity());
    // Utill
    mFont = new HYFont(getActivity());
    mFont.setGlobalFont((ViewGroup) v);

    mLinAnalogTimeView = (LinearLayout) v.findViewById(R.id.linAnalog);
    mLinDigitalTimeView = (LinearLayout) v.findViewById(R.id.linDigital);
    mLinDigitalTime24View = (LinearLayout) v.findViewById(R.id.linDigital24);
    mRadioGroupStyle = (RadioGroup) v.findViewById(R.id.radioGroupStyle);
    init();


    mRadioGroupStyle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
          case R.id.radioAnalog:
            Log.i(TAG, "아날로그");
            mLinAnalogTimeView.setVisibility(View.VISIBLE);
            mLinDigitalTimeView.setVisibility(View.GONE);
            mLinDigitalTime24View.setVisibility(View.GONE);
            mFlag = 0;
            break;

          case R.id.radioDigital:
            Log.i(TAG, "디지털");
            mLinAnalogTimeView.setVisibility(View.GONE);
            mLinDigitalTime24View.setVisibility(View.GONE);
            mLinDigitalTimeView.setVisibility(View.VISIBLE);
            mFlag = 1;
            break;
          case R.id.radioDigital24:
            Log.i(TAG, "디지털24");
            mLinAnalogTimeView.setVisibility(View.GONE);
            mLinDigitalTimeView.setVisibility(View.GONE);
            mLinDigitalTime24View.setVisibility(View.VISIBLE);
            mFlag = 2;
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
        mPref.put(mPref.KEY_PICKER_THEME, mFlag);
        ((Fragment_Setting) getTargetFragment()).onFinishStyleDialog();
        dismiss();

      }
    });
    mBuilder.setView(v);

    return mBuilder.create();
  }

  public void init() {
    if (mPref.getValue(mPref.KEY_PICKER_THEME, 0) == 0) {
      Log.i(TAG, "아날로그");
      mFlag = 0;
      mRadioGroupStyle.check(R.id.radioAnalog);
      mLinAnalogTimeView.setVisibility(View.VISIBLE);
      mLinDigitalTimeView.setVisibility(View.GONE);
      mLinDigitalTime24View.setVisibility(View.GONE);
    } else if (mPref.getValue(mPref.KEY_PICKER_THEME, 0) == 1) {
      Log.i(TAG, "디지털");
      mFlag = 1;
      mRadioGroupStyle.check(R.id.radioDigital);
      mLinAnalogTimeView.setVisibility(View.GONE);
      mLinDigitalTime24View.setVisibility(View.GONE);
      mLinDigitalTimeView.setVisibility(View.VISIBLE);
    } else {

      Log.i(TAG, "디지털 24h");
      mFlag = 2;
      mRadioGroupStyle.check(R.id.radioDigital24);
      mLinAnalogTimeView.setVisibility(View.GONE);
      mLinDigitalTimeView.setVisibility(View.GONE);
      mLinDigitalTime24View.setVisibility(View.VISIBLE);
    }
  }


}
