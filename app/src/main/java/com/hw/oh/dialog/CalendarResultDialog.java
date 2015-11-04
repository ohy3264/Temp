package com.hw.oh.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;


public class CalendarResultDialog extends DialogFragment {

  // Log
  private static final String TAG = "CalendarResultDialog";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;

  //View
  private RadioGroup mRadioGroupAdd;
  private LinearLayout mLinAddPersonallyView;

  private int mFlag;

  private int mAddHour;
  private int mAddMin;
  private int mTotalTime;
  private NumberPicker mNumberPicker1;
  private NumberPicker mNumberPicker2;

  // Utill
  private HYFont mFont;

  private TextView dialog_BtnOk;
  private NumberFormat mNumFomat = new DecimalFormat("###,###,###");

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
    LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
    View v = mLayoutInflater.inflate(R.layout.dialog_addinfo, null);

    // Utill
    mFont = new HYFont(getActivity());
    mFont.setGlobalFont((ViewGroup) v);

    mRadioGroupAdd = (RadioGroup) v.findViewById(R.id.radioGroupAdd);
    Bundle bundle = getArguments();/*
        mFlag = bundle.getInt("addType");

        mAddHour = bundle.getInt("addHour");
        mAddMin = bundle.getInt("addMin");
        mTotalTime = bundle.getInt("totalTime");*/


    mLinAddPersonallyView = (LinearLayout) v.findViewById(R.id.linAddPersonallyView);
    initRadio(mFlag);
    mRadioGroupAdd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
          case R.id.radioEightTime:
            Log.i(TAG, "8시간 이상");
            mLinAddPersonallyView.setVisibility(View.GONE);
            mFlag = 0;
            break;

          case R.id.radioAllTime:
            Log.i(TAG, "모든 시간");
            mLinAddPersonallyView.setVisibility(View.GONE);
            mFlag = 1;
            break;

          case R.id.radioPersonally:
            Log.i(TAG, "이후 시간");
            mLinAddPersonallyView.setVisibility(View.VISIBLE);
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

        AddDialogListener activity = (AddDialogListener) getActivity();

        Calendar totalCal = Calendar.getInstance();
        totalCal.set(0, 0, 0, 0, mTotalTime);
        Calendar addCal = Calendar.getInstance();
        addCal.set(0, 0, 0, getValue(mNumberPicker1), getValue(mNumberPicker2));

        if (addCal.getTimeInMillis() <= totalCal.getTimeInMillis()) {
          activity.onFinishAddDialog(mFlag, Integer.toString(getValue(mNumberPicker1)), Integer.toString(getValue(mNumberPicker2)));
          dismiss();
        } else {
          Toast.makeText(getActivity(), "일한시간보다 크게 설정할 수 없습니다", Toast.LENGTH_SHORT).show();
        }
      }
    });

    mNumberPicker1 = (NumberPicker) v.findViewById(R.id.numberPicker1);
    mNumberPicker1.setMinValue(0);
    mNumberPicker1.setMaxValue(24);
    mNumberPicker1.setValue(mAddHour);

    mNumberPicker2 = (NumberPicker) v.findViewById(R.id.numberPicker2);
    mNumberPicker2.setMinValue(0);
    mNumberPicker2.setMaxValue(59);
    mNumberPicker2.setValue(mAddMin);


    mBuilder.setView(v);

    return mBuilder.create();
  }

  public int getValue(NumberPicker np) {
    return ((NumberPicker) np).getValue();

  }

  public void initRadio(int flag) {
    switch (flag) {
      case 0:
        Log.i(TAG, "8시간 이상");
        mRadioGroupAdd.check(R.id.radioEightTime);
        mLinAddPersonallyView.setVisibility(View.GONE);
        break;

      case 1:
        Log.i(TAG, "모든 시간");
        mRadioGroupAdd.check(R.id.radioAllTime);
        mLinAddPersonallyView.setVisibility(View.GONE);
        break;
      case 2:
        Log.i(TAG, "직접 설정");
        mRadioGroupAdd.check(R.id.radioPersonally);
        mLinAddPersonallyView.setVisibility(View.VISIBLE);
        break;
    }
  }

  public interface AddDialogListener {
    void onFinishAddDialog(int flag, String hour, String min);
  }


  NumberPicker.Formatter mFormatter = new NumberPicker.Formatter() {        //포맷터 객체생성
    public String format(int value) {
      switch (value) {
        case 0:
          return "00";
        case 1:
          return "01";
        case 2:
          return "02";
        case 3:
          return "03";
        case 4:
          return "04";
        case 5:
          return "05";
        case 6:
          return "06";
        case 7:
          return "07";
        case 8:
          return "08";
        case 9:
          return "09";
        case 10:
          return "10";

        case 11:
          return "11";
        case 12:
          return "12";
        case 13:
          return "13";
        case 14:
          return "14";
        case 15:
          return "15";
        case 16:
          return "16";
        case 17:
          return "17";
        case 18:
          return "18";
        case 19:
          return "19";
        case 20:
          return "20";
        case 21:
          return "21";

        case 22:
          return "22";
        case 23:
          return "23";
        case 24:
          return "24";
        case 25:
          return "25";
        case 26:
          return "26";
        case 27:
          return "27";
        case 28:
          return "28";
        case 29:
          return "29";
        case 30:
          return "30";
        case 31:
          return "31";
        case 32:
          return "32";

        case 33:
          return "33";
        case 34:
          return "34";
        case 35:
          return "35";
        case 36:
          return "36";
        case 37:
          return "37";
        case 38:
          return "38";
        case 39:
          return "39";
        case 40:
          return "40";
        case 41:
          return "41";
        case 42:
          return "42";
        case 43:
          return "43";

        case 44:
          return "44";
        case 45:
          return "45";
        case 46:
          return "46";
        case 47:
          return "47";
        case 48:
          return "48";
        case 49:
          return "49";
        case 50:
          return "50";
        case 51:
          return "51";
        case 52:
          return "52";
        case 53:
          return "53";
        case 54:
          return "54";

        case 55:
          return "55";
        case 56:
          return "56";
        case 57:
          return "57";
        case 58:
          return "58";
        case 59:
          return "59";
      }
      return null;
    }
  };
}
