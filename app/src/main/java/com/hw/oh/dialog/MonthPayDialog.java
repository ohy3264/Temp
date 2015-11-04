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
import android.widget.TextView;
import android.widget.Toast;

import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;


public class MonthPayDialog extends DialogFragment {
  // Log
  private static final String TAG = "MonthPayDialog";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;
  private TextView dialog_BtnOk;
  private int mMonthDay = 1;
  private EditText edtMonthPayDay;
  // Utill
  private HYFont mFont;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
    LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
    View v = mLayoutInflater.inflate(R.layout.dialog_monthpay, null);

    mFont = new HYFont(getActivity());
    mFont.setGlobalFont((ViewGroup) v);

    Bundle bundle = getArguments();
    try {
      mMonthDay = bundle.getInt("MonthDay");
    } catch (Exception e) {
      Log.e(TAG, e.toString());
    }
    edtMonthPayDay = (EditText) v.findViewById(R.id.edtMonthDay);
    edtMonthPayDay.setText("" + mMonthDay);


    // Button
    dialog_BtnOk = (TextView) v.findViewById(R.id.btnOk);
    dialog_BtnOk.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        if (!edtMonthPayDay.getText().toString().equals("")) {
          int monthPayDay = Integer.parseInt(edtMonthPayDay.getText().toString());
          if (0 < monthPayDay && monthPayDay <= 31) {
            OnHeadlineSelectedListener activity = (OnHeadlineSelectedListener) getActivity();
            activity.onArticleSelected(Integer.parseInt(edtMonthPayDay.getText().toString()));
            dismiss();
          } else {
            Toast.makeText(getActivity(), "1~31 사이의 날짜를 선택하세요", Toast.LENGTH_SHORT).show();
          }
        } else {
          Toast.makeText(getActivity(), "빈칸으로 둘 수 없습니다", Toast.LENGTH_SHORT).show();
        }


      }
    });

    mBuilder.setView(v);

    return mBuilder.create();
  }

  public interface OnHeadlineSelectedListener {
    public void onArticleSelected(int monthDay);
  }

}
