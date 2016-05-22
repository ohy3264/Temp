package com.hw.oh.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.hw.oh.fragment.Fragment_Setting;
import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.InfoExtra;

/**
 * Created by oh on 2015-06-14.
 */
public class DutySetDialog extends android.support.v4.app.DialogFragment implements AdapterView.OnItemSelectedListener {
  // Log
  private static final String TAG = "CalculSetDialog";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;

  //View
  private TextView dialog_BtnOk;
  private Switch mSwDutyOnOff;
  private LinearLayout mLinDutySelector, mLinDutyPerson, mLinDutyCustom, mLinDutyOwner;
  private Spinner mSpinner1, mSpinner2;
  private RadioGroup mRadioGroupDuty;
  private EditText mEdtDuty;

  //Data
  private String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
  private String[] numbers_zero = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
  private int mPersonNumber1 = 1, mPersonNumber2 = 0;
  private int dutySelectorFlag = 0;

  // Utill
  private InfoExtra mInfoExtra;
  private HYFont mFont;
  private HYPreference mPref;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
    LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
    View v = mLayoutInflater.inflate(R.layout.dialog_duty, null);
    // Utill
    mInfoExtra = new InfoExtra(getActivity());
    mFont = new HYFont(getActivity());
    mFont.setGlobalFont((ViewGroup) v);
    mPref = new HYPreference(getActivity());

    //View
    mSpinner1 = (Spinner) v.findViewById(R.id.spinner);
    mSpinner2 = (Spinner) v.findViewById(R.id.spinner2);
    mSpinner1.setAdapter(new com.hw.oh.adapter.SpinnerAdapter(getActivity(), R.layout.spinner_row_default, numbers));
    mSpinner2.setAdapter(new com.hw.oh.adapter.SpinnerAdapter(getActivity(), R.layout.spinner_row_default, numbers_zero));

    mEdtDuty = (EditText) v.findViewById(R.id.edtDuty);
    mLinDutySelector = (LinearLayout) v.findViewById(R.id.dutySelector);
    mLinDutyPerson = (LinearLayout) v.findViewById(R.id.dutyPerson);
    mLinDutyCustom = (LinearLayout) v.findViewById(R.id.dutyCustom);
    mLinDutyOwner = (LinearLayout) v.findViewById(R.id.dutyOnwer);
    mSpinner1.setOnItemSelectedListener(this);
    mSpinner2.setOnItemSelectedListener(this);
    mRadioGroupDuty = (RadioGroup) v.findViewById(R.id.radioGroupDuty);
    mRadioGroupDuty.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
          case R.id.chk_duty1:
            dutySelectorFlag = 0;
            mPref.put(mPref.KEY_DUTY_SELECTOR, dutySelectorFlag);
            mLinDutyPerson.setVisibility(View.VISIBLE);
            mLinDutyOwner.setVisibility(View.GONE);
            mLinDutyCustom.setVisibility(View.GONE);
            break;

          case R.id.chk_duty2:
            dutySelectorFlag = 1;
            mPref.put(mPref.KEY_DUTY_SELECTOR, dutySelectorFlag);
            mLinDutyPerson.setVisibility(View.GONE);
            mLinDutyOwner.setVisibility(View.VISIBLE);
            mLinDutyCustom.setVisibility(View.GONE);
            break;

          case R.id.chk_duty3:
            dutySelectorFlag = 2;
            mPref.put(mPref.KEY_DUTY_SELECTOR, dutySelectorFlag);
            mLinDutyPerson.setVisibility(View.GONE);
            mLinDutyOwner.setVisibility(View.GONE);
            mLinDutyCustom.setVisibility(View.VISIBLE);
            break;
        }
      }
    });

    mSwDutyOnOff = (Switch) v.findViewById(R.id.switchDuty);
    mSwDutyOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          mSwDutyOnOff.setText("적용");
          mPref.put(mPref.KEY_DUTY_STATE, isChecked);
          mLinDutySelector.setVisibility(View.VISIBLE);
        } else {
          mSwDutyOnOff.setText("미적용");
          mPref.put(mPref.KEY_DUTY_STATE, isChecked);
          mLinDutySelector.setVisibility(View.GONE);
        }
        switch (dutySelectorFlag) {
          case 0:
            mLinDutyPerson.setVisibility(View.VISIBLE);
            mLinDutyOwner.setVisibility(View.GONE);
            mLinDutyCustom.setVisibility(View.GONE);
            break;

          case 1:
            mLinDutyPerson.setVisibility(View.GONE);
            mLinDutyOwner.setVisibility(View.VISIBLE);
            mLinDutyCustom.setVisibility(View.GONE);
            break;

          case 2:
            mLinDutyPerson.setVisibility(View.GONE);
            mLinDutyOwner.setVisibility(View.GONE);
            mLinDutyCustom.setVisibility(View.VISIBLE);
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
        if (mSwDutyOnOff.isChecked()) {
          mPref.put(mPref.KEY_DUTY_SELECTOR, dutySelectorFlag);
          switch (dutySelectorFlag) {
            case 0:
              mPref.put(mPref.KEY_DUTY_PERSON_1, mPersonNumber1);
              mPref.put(mPref.KEY_DUTY_PERSON_2, mPersonNumber2);
              break;
            case 1:
              break;
            case 2:
              mPref.put(mPref.KEY_DUTY_INPUT, mEdtDuty.getText().toString());
              break;
          }
          dismiss();
          ((Fragment_Setting) getTargetFragment()).onFinishDutyDialog();
        } else {
          dismiss();
          ((Fragment_Setting) getTargetFragment()).onFinishDutyDialog();
        }
      }
    });
    init();
    mBuilder.setView(v);
    return mBuilder.create();
  }

  public void init() {
    if (mPref.getValue(mPref.KEY_DUTY_STATE, false)) {
      mSwDutyOnOff.setChecked(true);
    } else {
      mSwDutyOnOff.setChecked(false);
    }
    switch (mPref.getValue(mPref.KEY_DUTY_SELECTOR, 0)) {
      case 0:
        dutySelectorFlag = 0;
        mRadioGroupDuty.check(R.id.chk_duty1);
        break;
      case 1:
        dutySelectorFlag = 1;
        mRadioGroupDuty.check(R.id.chk_duty2);
        break;
      case 2:
        dutySelectorFlag = 2;
        mRadioGroupDuty.check(R.id.chk_duty3);
        break;
    }
    mEdtDuty.setText(mPref.getValue(mPref.KEY_DUTY_INPUT, "0"));
    mSpinner1.setSelection(mPref.getValue(mPref.KEY_DUTY_PERSON_1, mPersonNumber1) - 1);
    mSpinner2.setSelection(mPref.getValue(mPref.KEY_DUTY_PERSON_2, mPersonNumber2));


  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    switch (parent.getId()) {
      case R.id.spinner:
        mPersonNumber1 = position + 1;
        break;
      case R.id.spinner2:
        mPersonNumber2 = position;
        break;
    }

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {

  }
}
