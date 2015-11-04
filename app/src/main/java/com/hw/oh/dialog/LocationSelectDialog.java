package com.hw.oh.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;


public class LocationSelectDialog extends DialogFragment {

  // Log
  private static final String TAG = "LocationSelectDialog";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;

  //View

  // Utill
  private HYFont mFont;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
    LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
    View v = mLayoutInflater.inflate(R.layout.dialog_weather, null);

    // Utill
    mFont = new HYFont(getActivity());
    mFont.setGlobalFont((ViewGroup) v);


    mBuilder.setView(v);

    return mBuilder.create();
  }


}
