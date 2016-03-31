package com.hw.oh.dialog;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hw.oh.fragment.Fragment_Setting;
import com.hw.oh.temp.IntroActivity;
import com.hw.oh.temp.MainActivity;
import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;


public class DigThemeStyleDialog extends DialogFragment {

  // Log
  private static final String TAG = "DigThemeStyleDialog";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;

  //View
  private RadioGroup mRadioGroupTheme;

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
    View v = mLayoutInflater.inflate(R.layout.dialog_theme, null);

    mPref = new HYPreference(getActivity());
    // Utill
    mFont = new HYFont(getActivity());
    mFont.setGlobalFont((ViewGroup) v);

    mRadioGroupTheme = (RadioGroup) v.findViewById(R.id.radioGroupTheme);
    init();


    mRadioGroupTheme.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
          case R.id.radioRed:
            Log.i(TAG, "red");
            mFlag = 0;
            break;

          case R.id.radioBlue:
            Log.i(TAG, "blue");
            mFlag = 1;
            break;
          case R.id.radioGreen:
            Log.i(TAG, "green");
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
        mPref.put(mPref.KEY_PICKER_THEME_STYLE, mFlag);
        ((Fragment_Setting) getTargetFragment()).onFinishThemeDialog();
        dismiss();
        getActivity().moveTaskToBack(true);
        getActivity().finish();
        Intent intent = new Intent( getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent i = PendingIntent.getActivity(getActivity().getApplicationContext(), 0, intent, getActivity().getIntent().getFlags());
        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, i);
//System.exit(2)
        android.os.Process.killProcess(android.os.Process.myPid());

      }
    });
    mBuilder.setView(v);

    return mBuilder.create();
  }

  public void init() {
    if (mPref.getValue(mPref.KEY_PICKER_THEME_STYLE, 0) == 0) {
      Log.i(TAG, "red");
      mFlag = 0;
      mRadioGroupTheme.check(R.id.radioRed);
    } else if (mPref.getValue(mPref.KEY_PICKER_THEME_STYLE, 0) == 1) {
      Log.i(TAG, "blue");
      mFlag = 1;
      mRadioGroupTheme.check(R.id.radioBlue);
    } else if(mPref.getValue(mPref.KEY_PICKER_THEME_STYLE, 0) == 2){
      Log.i(TAG, "green");
      mFlag = 2;
      mRadioGroupTheme.check(R.id.radioGreen);
    }
  }


}
