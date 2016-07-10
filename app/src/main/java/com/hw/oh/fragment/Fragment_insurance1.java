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
import android.widget.ListView;
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
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by oh on 2015-02-01.
 */
public class Fragment_insurance1 extends BaseFragment {
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

  //Data
  private long mMonthPay;
  private Unbinder unbinder;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_insurance1, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    setFont(rootView);
    // 구글 통계
    Tracker mTracker = ((ApplicationClass) getActivity().getApplication()).getDefaultTracker();
    mTracker.setScreenName("연금보험");
    mTracker.send(new HitBuilders.AppViewBuilder().build());

    //View
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

  @OnClick(R.id.btnCalculation)
  public void onClick() {
    if (mEdtMonthPay.getText().toString().equals("")) {
      Toast.makeText(getActivity(), "월 급여를 입력해 주세요", Toast.LENGTH_SHORT).show();
    } else if (mMonthPay > 4210000) {
      Toast.makeText(getActivity(), "기준 월 소득세액의 최고금액은 421만원 까지의 범위로 합니다. ", Toast.LENGTH_SHORT).show();
      mMonthPay = 4210000;
      mEdtMonthPay.setText(mNumFomat.format(mMonthPay));
      calculation(mMonthPay);
    } else if (mMonthPay < 270000) {
      Toast.makeText(getActivity(), "기준 월 소득세액의 최소금액은 27만원 까지의 범위로 합니다. ", Toast.LENGTH_SHORT).show();
      mMonthPay = 270000;
      mEdtMonthPay.setText(mNumFomat.format(mMonthPay));
      calculation(mMonthPay);
    } else {
      mEdtMonthPay.setText(mNumFomat.format(mMonthPay));
      calculation(mMonthPay);
    }
  }

  public void calculation(long monthPay) {
    mEdtTotalPay.setText(mNumFomat.format(monthPay * 0.09));
    mEdtOwnerPay.setText(mNumFomat.format(monthPay * 0.045));
    mEdtWokerPay.setText(mNumFomat.format(monthPay * 0.045));
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
