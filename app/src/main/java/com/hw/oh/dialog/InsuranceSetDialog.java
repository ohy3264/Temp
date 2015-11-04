package com.hw.oh.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.hw.oh.fragment.Fragment_Setting;
import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.InfoExtra;

/**
 * Created by oh on 2015-06-14.
 */
public class InsuranceSetDialog extends DialogFragment {
  // Log
  private static final String TAG = "InsuranceSetDialog";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;

  private TextView dialog_BtnOk;
  private Switch mSwInsuranceOnOff;
  private int mFlag;

  // Utill
  private InfoExtra mInfoExtra;
  private RequestQueue mRequestQueue;
  private HYFont mFont;
  private HYPreference mPref;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
    LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
    View v = mLayoutInflater.inflate(R.layout.dialog_lnsurance_set, null);
    // Utill
    mInfoExtra = new InfoExtra(getActivity());
    mRequestQueue = Volley.newRequestQueue(getActivity());
    mFont = new HYFont(getActivity());
    mFont.setGlobalFont((ViewGroup) v);
    mPref = new HYPreference(getActivity());


    mSwInsuranceOnOff = (Switch) v.findViewById(R.id.switchInsurance);
    mSwInsuranceOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          mSwInsuranceOnOff.setText("적용");
          mPref.put(mPref.KEY_INSURANCE_STATE, isChecked);
        } else {
          mSwInsuranceOnOff.setText("미적용");
          mPref.put(mPref.KEY_INSURANCE_STATE, isChecked);
        }
      }
    });
    // Button
    dialog_BtnOk = (TextView) v.findViewById(R.id.btnOk);
    dialog_BtnOk.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        if (mSwInsuranceOnOff.isChecked()) {
          dismiss();
          ((Fragment_Setting) getTargetFragment()).onFinishInsuranceDialog();
        } else {
          dismiss();
          ((Fragment_Setting) getTargetFragment()).onFinishInsuranceDialog();
        }
      }
    });
    init();
    mBuilder.setView(v);

    return mBuilder.create();
  }

  public void init() {
    if (mPref.getValue(mPref.KEY_INSURANCE_STATE, false)) {
      mSwInsuranceOnOff.setChecked(true);
    } else {
      mSwInsuranceOnOff.setChecked(false);
    }
  }
}
