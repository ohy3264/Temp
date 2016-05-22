package com.hw.oh.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hw.oh.fragment.Fragment_Setting;
import com.hw.oh.model.BoardItem;
import com.hw.oh.temp.R;
import com.hw.oh.utility.CommonUtil;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.HYTime_Maximum;
import com.hw.oh.utility.InfoExtra;
import com.hw.oh.utility.JSONParserUtil;
import com.hw.oh.utility.OkHttpUtils;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by oh on 2015-06-14.
 */
public class PassSetDialog extends android.support.v4.app.DialogFragment implements TextWatcher {
  // Log
  private static final String TAG = "PassSetDialog";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;

  private TextView dialog_BtnOk;
  private Switch mSwPassOnOff;
  private LinearLayout mLinPass;
  private EditText mEdtPass1, mEdtPass2;
  private boolean mPassCheck = false;
  private int mFlag;

  // Utill
  private InfoExtra mInfoExtra;
  private HYFont mFont;
  private HYPreference mPref;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
    LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
    View v = mLayoutInflater.inflate(R.layout.dialog_pass_set, null);
    // Utill
    mInfoExtra = new InfoExtra(getActivity());
    mFont = new HYFont(getActivity());
    mFont.setGlobalFont((ViewGroup) v);
    mPref = new HYPreference(getActivity());

    mEdtPass1 = (EditText) v.findViewById(R.id.edtPass1);
    mEdtPass2 = (EditText) v.findViewById(R.id.edtPass2);
    mEdtPass1.addTextChangedListener(this);
    mEdtPass2.addTextChangedListener(this);

    mLinPass = (LinearLayout) v.findViewById(R.id.linPass);
    mSwPassOnOff = (Switch) v.findViewById(R.id.switchPass);
    mSwPassOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          mSwPassOnOff.setText("사용");
          mLinPass.setVisibility(View.VISIBLE);
          mPref.put(mPref.KEY_PASS_STATE, isChecked);
        } else {
          mSwPassOnOff.setText("사용안함");
          mLinPass.setVisibility(View.GONE);
          mPref.put(mPref.KEY_PASS_STATE, isChecked);
        }
      }
    });
    // Button
    dialog_BtnOk = (TextView) v.findViewById(R.id.btnOk);
    dialog_BtnOk.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        if (mSwPassOnOff.isChecked()) {
          if (mPassCheck) {
            if (mEdtPass1.length() == 4) {
              dismiss();
              ((Fragment_Setting) getTargetFragment()).onFinishPassDialog();
              mPref.put(mPref.KEY_PASSWORD, mEdtPass1.getText().toString());
              requestCallRest_Pass();
            } else {
              Toast.makeText(getActivity(), "4자리까지 입력해주세요", Toast.LENGTH_SHORT).show();
            }
          } else {
            Toast.makeText(getActivity(), "패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
          }
        } else {
          dismiss();
          ((Fragment_Setting) getTargetFragment()).onFinishPassDialog();
        }
      }
    });
    init();
    mBuilder.setView(v);

    return mBuilder.create();
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
    mPassCheck = mEdtPass1.getText().toString()
        .equals(mEdtPass2.getText().toString());
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override
  public void afterTextChanged(Editable s) {

  }

  public void init() {
    if (mPref.getValue(mPref.KEY_PASS_STATE, false)) {
      mSwPassOnOff.setChecked(true);
      mLinPass.setVisibility(View.VISIBLE);
    } else {
      mSwPassOnOff.setChecked(false);
      mLinPass.setVisibility(View.GONE);
    }
    mEdtPass1.setText(mPref.getValue(mPref.KEY_PASSWORD, ""));
  }


  public void requestCallRest_Pass() {
    if (INFO)
      Log.i(TAG, "requestCallRest_Pass()");
    RequestBody formBody = new FormBody.Builder()
            .add("MODE", "PassUpdate")
            .add("ANDROID_ID", CommonUtil.getAndroidID(getActivity()))
            .add("PASS", mEdtPass1.getText().toString())
            .build();
    try {
      OkHttpUtils.post(getActivity(), Constant.SERVER_URL, "/Anony/api/memberPass.php", formBody, new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
          Log.d(TAG, "requestCallRest_Pass - onFailure :: " + e.toString());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
          Log.i("Response", " " + response.code());
          final String results = response.body().string();
          Log.i("OkHTTP Results: ", results);
        }
      });
    } catch (Exception e) {
      Log.d(TAG, "requestCallRest_Pass - exception :: " + e.toString());

    }

  }
}
