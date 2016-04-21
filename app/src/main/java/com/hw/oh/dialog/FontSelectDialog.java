package com.hw.oh.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hw.oh.fragment.Fragment_Setting;
import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;

/**
 * Created by oh on 2015-06-14.
 */
public class FontSelectDialog extends DialogFragment {
  // Log
  private static final String TAG = "FontSelectDialog";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;

  private TextView dialog_BtnOk;
  private RadioGroup mRadioGroupFont;
  private int mFlag;

  // Utill
  private HYFont mFont;
  private HYPreference mPref;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
    LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
    View v = mLayoutInflater.inflate(R.layout.dialog_font_select, null);
// Utill

    mFont = new HYFont(getActivity());
    mFont.setGlobalFont((ViewGroup) v);
    mPref = new HYPreference(getActivity());
    mFlag = mPref.getValue(mPref.KEY_FONT, 0);
    mRadioGroupFont = (RadioGroup) v.findViewById(R.id.radioGroupFont);
    mRadioGroupFont.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
          case R.id.radioDefault:
            Log.i(TAG, "기본");
            mFlag = 0;
            break;

          case R.id.radioSangSang:
            Log.i(TAG, "상상체");
            mFlag = 1;
            break;
          case R.id.radioGodo:
            Log.i(TAG, "고도체");
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
        mPref.put(mPref.KEY_FONT, mFlag);
        dismiss();
        ((Fragment_Setting) getTargetFragment()).onFinishFontDialog();
      }
    });

    initRadio(mFlag);
    mBuilder.setView(v);

    return mBuilder.create();
  }

  public void initRadio(int flag) {
    switch (flag) {
      case 0:
        Log.i(TAG, "defalut");
        // Constant.FONT_NAME = getString(R.string.roboto);
        mRadioGroupFont.check(R.id.radioDefault);
        break;
      case 1:
        Log.i(TAG, "상상체");
        // Constant.FONT_NAME = getString(R.string.sangsang);
        mRadioGroupFont.check(R.id.radioSangSang);
        break;
      case 2:
        Log.i(TAG, "고도체");
        // Constant.FONT_NAME = getString(R.string.godo);
        mRadioGroupFont.check(R.id.radioGodo);
        break;
    }
  }

}
