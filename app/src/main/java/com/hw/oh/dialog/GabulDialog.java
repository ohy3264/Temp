package com.hw.oh.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class GabulDialog extends DialogFragment {

  // Log
  private static final String TAG = "GabulDialog";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;

  //View
  private EditText edtGabulMoney;

  // Utill
  private HYFont mFont;

  private TextView dialog_BtnOk;
  private NumberFormat mNumFomat = new DecimalFormat("###,###,###");

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
    LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
    View v = mLayoutInflater.inflate(R.layout.dialog_gabul, null);

    // Utill
    mFont = new HYFont(getActivity());
    mFont.setGlobalFont((ViewGroup) v);

    Bundle bundle = getArguments();
    edtGabulMoney = (EditText) v.findViewById(R.id.edtGabulMoney);
    edtGabulMoney.setText(Integer.toString(bundle.getInt("GabulMoney")));

    // Button
    dialog_BtnOk = (TextView) v.findViewById(R.id.btnOk);
    dialog_BtnOk.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        dismiss();
        EditNameDialogListener activity = (EditNameDialogListener) getActivity();
        activity.onFinishEditDialog(edtGabulMoney.getText().toString());

      }
    });
    mBuilder.setView(v);

    return mBuilder.create();
  }

  public interface EditNameDialogListener {
    void onFinishEditDialog(String inputText);
  }
}
