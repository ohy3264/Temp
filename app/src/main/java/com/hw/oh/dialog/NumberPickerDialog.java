package com.hw.oh.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.hw.oh.temp.R;

/**
 * Created by oh on 2015-05-22.
 */
public class NumberPickerDialog extends DialogFragment {
  // Log
  private static final String TAG = "NumberPickerDialog";

  private View mNp;
  private RequestQueue mRequestQueue;
  private EditText mEditText;

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    mRequestQueue = Volley.newRequestQueue(getActivity());
    builder.setMessage("가불금액 입력");
    Bundle bundle = getArguments();
    mNp = getNumberPickerWidget(bundle.getInt("TotalMoney"), bundle.getInt("GabulMoney"));
    builder.setView(mNp).setIcon(getResources().getDrawable(R.drawable.icon_gabulvalue))
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            mNp.clearFocus();
            EditNameDialogListener activity = (EditNameDialogListener) getActivity();
            activity.onFinishEditDialog(Integer.toString(getValue()));

          }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

          }
        });

    return builder.create();
  }

  /**
   * NumberPicker widget is added in API Level 11 (HONEYCOMB)
   *
   * @return NumberPicker View
   * @TODO add custom NumberPicker for older API
   */
  private View getNumberPickerWidget(int maxValue, int setValue) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      NumberPicker np = new NumberPicker(getActivity());
      np.setWrapSelectorWheel(false);
      np.setMaxValue(maxValue);
      np.setMinValue(0);
      np.setValue(setValue);
      return np;
    } else {
      mEditText = new EditText(getActivity());
      return mEditText;
    }
  }


  public int getValue() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      return ((NumberPicker) mNp).getValue();
    } else {
      return Integer.parseInt(((EditText) mNp).getText().toString());
    }
  }

  public interface EditNameDialogListener {
    void onFinishEditDialog(String inputText);
  }
}