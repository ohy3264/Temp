package com.hw.oh.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;


public class HourPayInfoDialog extends DialogFragment {

  // Log
  private static final String TAG = "HourPayInfoDialog";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;
  private TextView dialog_BtnOk;
  private View v;
  //flag
  private int mViewFlag;
  // Utill
  private HYFont mFont;


  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
    LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
    switch (getArguments().getInt("DialogKey")) {
      case 0:
        v = mLayoutInflater.inflate(R.layout.dialog_hourpayinfo0, null);
        break;
      case 1:
        v = mLayoutInflater.inflate(R.layout.dialog_hourpayinfo1, null);
        break;
      case 2:
        v = mLayoutInflater.inflate(R.layout.dialog_hourpayinfo2, null);
        break;
      case 3:
        v = mLayoutInflater.inflate(R.layout.dialog_hourpayinfo3, null);
        break;
    }


    // Utill
    mFont = new HYFont(getActivity());
    mFont.setGlobalFont((ViewGroup) v);
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
