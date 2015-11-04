package com.hw.oh.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;


/**
 * Created by oh on 2015-02-01.
 */
public class Fragment_insurance2 extends Fragment {
  public static final String TAG = "Fragment_Info";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;
  //Crouton
  private View mCroutonView;
  private TextView mTxtCrouton;
  private CroutonHelper mCroutonHelper;

  //View
  private EditText mEdtWokerPay, mEdtOwnerPay, mEdtTotalPay, mEdtMonthPay;
  private Button mBtnCal;

  private long mMonthPay;
  private RadioGroup mRadioGroup;
  private int mFlag = 0;

  private HYFont mFont;
  private HYPreference mPref;
  private NumberFormat mNumFomat = new DecimalFormat("###,###,###");

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_insurance2, container, false);
    //Util
    mFont = new HYFont(getActivity());
    mFont.setGlobalFont((ViewGroup) rootView);
    mPref = new HYPreference(getActivity());

    mFont.setGlobalFont((ViewGroup) rootView);

    //Crouton
    mCroutonHelper = new CroutonHelper(getActivity());
    mCroutonView = getActivity().getLayoutInflater().inflate(
        R.layout.crouton_custom_view, null);
    mTxtCrouton = (TextView) mCroutonView.findViewById(R.id.txt_crouton);

    //View
    mEdtMonthPay = (EditText) rootView.findViewById(R.id.edtInputMonthPay);
    mEdtMonthPay.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {

          if (!s.toString().equals("")) {
            mMonthPay = Long.parseLong(s.toString().replaceAll(",", ""));
            Log.d(TAG, s.toString());
          }
        } catch (NumberFormatException e) {
          Toast.makeText(getActivity(), "유효한 수의 범위를 넘었습니다. ", Toast.LENGTH_SHORT).show();
        }
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });
    mEdtOwnerPay = (EditText) rootView.findViewById(R.id.edtonwerPay);
    mEdtTotalPay = (EditText) rootView.findViewById(R.id.edtTotalPay);
    mEdtWokerPay = (EditText) rootView.findViewById(R.id.edtWokerPay);

    mBtnCal = (Button) rootView.findViewById(R.id.btnCalculation);
    mBtnCal.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(TAG, "클릭");
        if (mEdtMonthPay.getText().toString().equals("")) {
          Toast.makeText(getActivity(), "월 급여를 입력해 주세요", Toast.LENGTH_SHORT).show();
        } else if (mMonthPay > 7810000) {
          Toast.makeText(getActivity(), "기준 월 소득세액의 최고금액은 781만원 까지의 범위로 합니다. ", Toast.LENGTH_SHORT).show();
          mMonthPay = 7810000;
          mEdtMonthPay.setText(mNumFomat.format(mMonthPay));
          calculation(mMonthPay);
        } else if (mMonthPay < 280000) {
          Toast.makeText(getActivity(), "기준 월 소득세액의 최소금액은 28만원 까지의 범위로 합니다. ", Toast.LENGTH_SHORT).show();
          mMonthPay = 280000;
          mEdtMonthPay.setText(mNumFomat.format(mMonthPay));
          calculation(mMonthPay);
        } else {
          mEdtMonthPay.setText(mNumFomat.format(mMonthPay));
          calculation(mMonthPay);
        }
      }
    });

    mRadioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroudChoice);
    mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
          case R.id.radBtn1:
            mFlag = 0;
            break;
          case R.id.radBtn2:
            mFlag = 1;
            break;
        }
      }
    });

    return rootView;
  }

  public void calculation(long monthPay) {
    switch (mFlag) {
      case 0:
        mEdtTotalPay.setText(mNumFomat.format(monthPay * 0.0607));
        mEdtOwnerPay.setText(mNumFomat.format(monthPay * 0.03035));
        mEdtWokerPay.setText(mNumFomat.format(monthPay * 0.03035));
        break;
      case 1:
        mEdtTotalPay.setText(mNumFomat.format((monthPay * 0.0607) * 0.0655));
        mEdtOwnerPay.setText(mNumFomat.format(((monthPay * 0.0607) * 0.0655) / 2));
        mEdtWokerPay.setText(mNumFomat.format(((monthPay * 0.0607) * 0.0655) / 2));
        break;
    }
  }

}
