package com.hw.oh.fragment;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hw.oh.temp.ApplicationClass;
import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by oh on 2015-02-01.
 */
public class Fragment_insurance3 extends BaseFragment {
  public static final String TAG = "Fragment_Info";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;

    //View
    @BindView(R.id.edtWokerPay)
    EditText mEdtWokerPay;
    @BindView(R.id.edtonwerPay)
    EditText mEdtOwnerPay;
    @BindView(R.id.edtTotalPay)
    EditText mEdtTotalPay;
    @BindView(R.id.edtInputMonthPay)
    EditText mEdtMonthPay;

  private LinearLayout mBtnCal;

  private long mMonthPay;
  private RadioGroup mRadioGroup;
  private int mFlag = 0;


    private Unbinder unbinder;
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_insurance3, container, false);
      unbinder = ButterKnife.bind(this, rootView);
      setFont(rootView);
      // 구글 통계
   Tracker mTracker = ((ApplicationClass) getActivity().getApplication()).getDefaultTracker();
    mTracker.setScreenName("고용보험");
    mTracker.send(new HitBuilders.AppViewBuilder().build());

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

      return rootView;
  }

    @OnClick({R.id.radBtn1, R.id.radBtn2, R.id.radBtn3, R.id.radBtn4})
    public void onRadioButtonClicked(RadioButton radioButton) {
        // Is the button now checked?
        boolean checked = radioButton.isChecked();

        // Check which radio button was clicked
        switch (radioButton.getId()) {
            case R.id.radBtn1:
                if (checked) {
                    mFlag = 0;
                }
                break;
            case R.id.radBtn2:
                if (checked) {
                    mFlag = 1;
                }
                break;
            case R.id.radBtn3:
                if (checked) {
                    mFlag = 2;
                }
                break;
            case R.id.radBtn4:
                if (checked) {
                    mFlag = 3;
                }
                break;
        }
    }

    @OnClick(R.id.btnCalculation)
    public void onClick() {
        Log.d(TAG, "클릭");
        if (mEdtMonthPay.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "월 급여를 입력해 주세요", Toast.LENGTH_SHORT).show();
        } else {
            mEdtMonthPay.setText(mNumFomat.format(mMonthPay));
            calculation(mMonthPay);
        }
    }


  public void calculation(long monthPay) {
    switch (mFlag) {
      case 0:
        mEdtTotalPay.setText(mNumFomat.format(monthPay * (0.0025 + 0.0065 + 0.0065)));
        mEdtWokerPay.setText(mNumFomat.format(monthPay * 0.0065));
        mEdtOwnerPay.setText(mNumFomat.format(monthPay * (0.0025 + 0.0065)));
        break;
      case 1:
        mEdtTotalPay.setText(mNumFomat.format(monthPay * (0.0045 + 0.0065 + 0.0065)));
        mEdtWokerPay.setText(mNumFomat.format(monthPay * 0.0065));
        mEdtOwnerPay.setText(mNumFomat.format(monthPay * (0.0045 + 0.0065)));
        break;
      case 2:
        mEdtTotalPay.setText(mNumFomat.format(monthPay * (0.0065 + 0.0065 + 0.0065)));
        mEdtWokerPay.setText(mNumFomat.format(monthPay * 0.0065));
        mEdtOwnerPay.setText(mNumFomat.format(monthPay * (0.0065 + 0.0065)));
        break;
      case 3:
        mEdtTotalPay.setText(mNumFomat.format(monthPay * (0.0085 + 0.0065 + 0.0065)));
        mEdtWokerPay.setText(mNumFomat.format(monthPay * 0.0065));
        mEdtOwnerPay.setText(mNumFomat.format(monthPay * (0.0085 + 0.0065)));
        break;
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    // 구글 통계
    GoogleAnalytics.getInstance(getActivity()).reportActivityStart(getActivity());
  };

  @Override
  public void onStop() {
    super.onStop();
    // 구글 통계
    GoogleAnalytics.getInstance(getActivity()).reportActivityStop(getActivity());
  };
}
