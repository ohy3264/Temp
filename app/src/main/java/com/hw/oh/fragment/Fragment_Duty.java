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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hw.oh.temp.ApplicationClass;
import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;


/**
 * Created by oh on 2015-02-01.
 */
public class Fragment_Duty extends Fragment implements AdapterView.OnItemSelectedListener {
  public static final String TAG = "Fragment_Duty";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;
  //Crouton
  private View mCroutonView;
  private TextView mTxtCrouton;
  private CroutonHelper mCroutonHelper;

  //View
  private EditText mEdtMonthPay;
  private LinearLayout mBtnCal;
  private TextView mTxtMonthPay1;
  private TextView mTxtMonthPay2, mTxtYearPay2;
  private TextView mTxtWorkerDutyMinus3, mTxtWorkerDutyMinus3_1, mTxtWorkerDutyMinus3_2, mTxtWorkerDutyMinus3_3, mTxtWorkerDutyMinus3_4;
  private TextView mTxtWorkerDutyMinus4, mTxtWorkerDutyMinus4_1;
  private TextView mTxtPersonMinus5, mTxtPersonMinus5_1;
  private TextView mTxtInsuranceMinus6, mTxtInsuranceMinus6_1;
  private TextView mTxtSpecialMinus7, mTxtSpecialMinus7_1;
  private TextView mTxtDutyStandard8;
  private TextView mTxtDutyCalculation9, mTxtDutyCalculation9_1;
  private TextView mTxtDutyCalculationMinus10, mTxtDutyCalculationMinus10_1;
  private TextView mTxtDutyYearResult11;
  private TextView mTxtDutyMonthResult12;
  ;
  private TextView mTxtDutyResult1, mTxtDutyResult2, mTxtDutyResultTotal;


  //View
  private Spinner mSpinner1, mSpinner2;
  private long mMonthPay, mYearPay;

  //Data
  private String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
  private String[] numbers_zero = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
  private int mPersonNumber1 = 1, mPersonNumber2 = 0;

  //Util
  private HYFont mFont;
  private HYPreference mPref;
  private NumberFormat mNumFomat = new DecimalFormat("###,###,###");

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_duty1, container, false);
    // 구글 통계
    Tracker mTracker = ((ApplicationClass) getActivity().getApplication()).getDefaultTracker();
    mTracker.setScreenName("근로소득공제");
    mTracker.send(new HitBuilders.AppViewBuilder().build());
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
    mSpinner1 = (Spinner) rootView.findViewById(R.id.spinner);
    mSpinner2 = (Spinner) rootView.findViewById(R.id.spinner2);
    mSpinner1.setAdapter(new com.hw.oh.adapter.SpinnerAdapter(getActivity(), R.layout.spinner_row_default, numbers));
    mSpinner2.setAdapter(new com.hw.oh.adapter.SpinnerAdapter(getActivity(), R.layout.spinner_row_default, numbers_zero));

    mSpinner1.setOnItemSelectedListener(this);
    mSpinner2.setOnItemSelectedListener(this);

    mTxtMonthPay1 = (TextView) rootView.findViewById(R.id.txtMonthPay1);

    mTxtMonthPay2 = (TextView) rootView.findViewById(R.id.txtMonthPay2);
    mTxtYearPay2 = (TextView) rootView.findViewById(R.id.txtYearPay2);

    mTxtWorkerDutyMinus3 = (TextView) rootView.findViewById(R.id.txtWorkerDutyMinus3);
    mTxtWorkerDutyMinus3_1 = (TextView) rootView.findViewById(R.id.txtWorkerDutyMinus3_1);
    mTxtWorkerDutyMinus3_2 = (TextView) rootView.findViewById(R.id.txtWorkerDutyMinus3_2);
    mTxtWorkerDutyMinus3_3 = (TextView) rootView.findViewById(R.id.txtWorkerDutyMinus3_3);
    mTxtWorkerDutyMinus3_4 = (TextView) rootView.findViewById(R.id.txtWorkerDutyMinus3_4);

    mTxtWorkerDutyMinus4_1 = (TextView) rootView.findViewById(R.id.txtWorkerDutyMinus4_1);
    mTxtWorkerDutyMinus4 = (TextView) rootView.findViewById(R.id.txtWorkerDutyMinus4);

    mTxtPersonMinus5 = (TextView) rootView.findViewById(R.id.txtPersonMinus5);
    mTxtPersonMinus5_1 = (TextView) rootView.findViewById(R.id.txtPersonMinus5_1);

    mTxtInsuranceMinus6 = (TextView) rootView.findViewById(R.id.txtInsurance6);
    mTxtInsuranceMinus6_1 = (TextView) rootView.findViewById(R.id.txtInsurance6_1);

    mTxtSpecialMinus7 = (TextView) rootView.findViewById(R.id.txtSpecialMinus7);
    mTxtSpecialMinus7_1 = (TextView) rootView.findViewById(R.id.txtSpecialMinus7_1);

    mTxtDutyStandard8 = (TextView) rootView.findViewById(R.id.txtDutyStandard8);

    mTxtDutyCalculation9 = (TextView) rootView.findViewById(R.id.txtDutyCalculation9);
    mTxtDutyCalculation9_1 = (TextView) rootView.findViewById(R.id.txtDutyCalculation9_1);

    mTxtDutyCalculationMinus10 = (TextView) rootView.findViewById(R.id.txtDutyCalculationMinus10);
    mTxtDutyCalculationMinus10_1 = (TextView) rootView.findViewById(R.id.txtDutyCalculationMinus10_1);

    mTxtDutyYearResult11 = (TextView) rootView.findViewById(R.id.txtDutyYearResult11);
    mTxtDutyMonthResult12 = (TextView) rootView.findViewById(R.id.txtDutyMonthResult12);

    mTxtDutyResult1 = (TextView) rootView.findViewById(R.id.txtDutyResult1);
    mTxtDutyResult2 = (TextView) rootView.findViewById(R.id.txtDutyResult2);
    mTxtDutyResultTotal = (TextView) rootView.findViewById(R.id.txtDutyResultTotal);

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
            mYearPay = mMonthPay * 12;
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


    mBtnCal = (LinearLayout) rootView.findViewById(R.id.btnCalculation);
    mBtnCal.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(TAG, "클릭");
        if (mMonthPay < 1060000) {
          Toast.makeText(getActivity(), "월급여 1,060,000원 미만은 원천징수할 세금이 없습니다.", Toast.LENGTH_SHORT).show();
        } else if (mMonthPay > 120000000) {
          Toast.makeText(getActivity(), "오류 개발자에게 문의하세요.", Toast.LENGTH_SHORT).show();

        } else {
          calculation();

        }

      }
    });

    return rootView;
  }

  public void calculation() {
    //월급여액
    mTxtMonthPay1.setText(mNumFomat.format(mMonthPay));

    //연간 총급여액
    mTxtMonthPay2.setText(mNumFomat.format(mMonthPay) + " x 12개월");
    mTxtYearPay2.setText(mNumFomat.format(mYearPay));

    //근로소득공제
    if (mYearPay <= 5000000) {
      Log.i(TAG, "근로소득공제 : 500만원 이하");
      mTxtWorkerDutyMinus3_1.setText(mNumFomat.format(mYearPay));
      mTxtWorkerDutyMinus3_2.setText("");
      mTxtWorkerDutyMinus3_3.setText("");
      mTxtWorkerDutyMinus3_4.setText(" x 70%");
      mTxtWorkerDutyMinus3.setText(mNumFomat.format(mMonthPay * 0.7));

    } else if (mYearPay <= 15000000) {
      Log.i(TAG, "근로소득공제 : 1,500만원 이하");
      mTxtWorkerDutyMinus3_1.setText("3,500,000원 + ");
      mTxtWorkerDutyMinus3_2.setText("(" + mNumFomat.format(mYearPay));
      mTxtWorkerDutyMinus3_3.setText(" - 5,000,000)");
      mTxtWorkerDutyMinus3_4.setText(" x 40%");
      mTxtWorkerDutyMinus3.setText(mNumFomat.format((Double) (3500000 + (mYearPay - 5000000) * 0.4)));

    } else if (mYearPay <= 45000000) {
      Log.i(TAG, "근로소득공제 : 4,500만원 이하");
      mTxtWorkerDutyMinus3_1.setText("7,500,000 + ");
      mTxtWorkerDutyMinus3_2.setText("(" + mNumFomat.format(mYearPay));
      mTxtWorkerDutyMinus3_3.setText(" - 15,000,000)");
      mTxtWorkerDutyMinus3_4.setText(" x 15%");
      mTxtWorkerDutyMinus3.setText(mNumFomat.format((Double) (7500000 + (mYearPay - 15000000) * 0.15)));

    } else if (mYearPay <= 100000000) {
      Log.i(TAG, "근로소득공제 : 1억원 이하");
      mTxtWorkerDutyMinus3_1.setText("12,000,000 + ");
      mTxtWorkerDutyMinus3_2.setText("(" + mNumFomat.format(mYearPay));
      mTxtWorkerDutyMinus3_3.setText(" - 45,000,000)");
      mTxtWorkerDutyMinus3_4.setText(" x 5%");
      mTxtWorkerDutyMinus3.setText(mNumFomat.format((Double) (12000000 + (mYearPay - 45000000) * 0.05)));

    } else {
      Log.i(TAG, "근로소득공제 : 1억원 초과");
      mTxtWorkerDutyMinus3_1.setText("14,750,000 + ");
      mTxtWorkerDutyMinus3_2.setText("(" + mNumFomat.format(mYearPay));
      mTxtWorkerDutyMinus3_3.setText(" - 100,000,000)");
      mTxtWorkerDutyMinus3_4.setText(" x 2%");
      mTxtWorkerDutyMinus3.setText(mNumFomat.format((Double) (14750000 + ((mYearPay) - 100000000) * 0.02)));

    }
    //근로소득금액
    mTxtWorkerDutyMinus4_1.setText(mNumFomat.format(mYearPay) + " - " + mTxtWorkerDutyMinus3.getText());
    mTxtWorkerDutyMinus4.setText(mNumFomat.format(mYearPay - Long.parseLong(mTxtWorkerDutyMinus3.getText().toString().replaceAll(",", ""))));

    //인적공제
    mTxtPersonMinus5_1.setText(mPersonNumber1 + "명 x 150만원 20세 이하(" + mPersonNumber2 + ")");
    mTxtPersonMinus5.setText(mNumFomat.format((mPersonNumber1 + mPersonNumber2) * 1500000));

    //연금보험료 공제
    if (mMonthPay > 3980000) {
      Log.i(TAG, "기준 월 소득세액의 최고공제금액은 3,980,000만원 까지의 범위로 합니다.");
      mTxtInsuranceMinus6_1.setText(mNumFomat.format(mMonthPay) + "원에 대한 국민연금부담금 " + mNumFomat.format(Math.floor((4210000 * 0.045) / 10) * 10) + "원 x 12개월(원단위 절사)");
      mTxtInsuranceMinus6.setText(mNumFomat.format((Math.floor((3980000 * 0.045) / 10) * 10) * 12));
    } else if (mMonthPay < 250000) {
      Log.i(TAG, "기준 월 소득세액의 최소금액은 25만원 까지의 범위로 합니다.");
      mTxtInsuranceMinus6_1.setText(mNumFomat.format(mMonthPay) + "원에 대한 국민연금부담금 " + mNumFomat.format(Math.floor((270000 * 0.045) / 10) * 10) + "원 x 12개월(원단위 절사)");
      mTxtInsuranceMinus6.setText(mNumFomat.format((Math.floor((250000 * 0.045) / 10) * 10) * 12));
    } else {
      mTxtInsuranceMinus6_1.setText(mNumFomat.format(mMonthPay) + "원에 대한 국민연금부담금 " + mNumFomat.format(Math.floor((mMonthPay * 0.045) / 10) * 10) + "원 x 12개월(원단위 절사)");
      mTxtInsuranceMinus6.setText(mNumFomat.format((Math.floor((mMonthPay * 0.045) / 10) * 10) * 12));
    }

    //특별 소득공제 등
    if (mPersonNumber1 == 1) {
      Log.i(TAG, "공제대상자가 1명인 경우");
      if (mYearPay <= 30000000) {
        Log.i(TAG, "3,000만원 이하");
        mTxtSpecialMinus7_1.setText("3,100,000 + (" + mNumFomat.format(mYearPay) + " * 4%)");
        mTxtSpecialMinus7.setText(mNumFomat.format(3100000 + (mYearPay * 0.04)));

      } else if (mYearPay <= 45000000) {
        Log.i(TAG, "3,000만원 초과 4,500만원 이하");
        mTxtSpecialMinus7_1.setText("3,100,000 + (" + mNumFomat.format(mYearPay) + " * 4%) - [(" + mNumFomat.format(mYearPay) + " - " + "30,000,000) * 5%]");
        mTxtSpecialMinus7.setText(mNumFomat.format((3100000 + (mYearPay * 0.04)) - ((mYearPay - 30000000) * 0.05)));

      } else if (mYearPay <= 70000000) {
        Log.i(TAG, "4,500만원 초과 7,000만원 이하");
        mTxtSpecialMinus7_1.setText("3,100,000 + (" + mNumFomat.format(mYearPay) + " * 1.5%)");
        mTxtSpecialMinus7.setText(mNumFomat.format(3100000 + (mYearPay * 0.015)));

      } else if (mYearPay <= 120000000) {
        Log.i(TAG, "7,000만원 초과 1억 2,000만원 이하");
        mTxtSpecialMinus7_1.setText("3,100,000 + (" + mNumFomat.format(mYearPay) + " * 0.5%)");
        mTxtSpecialMinus7.setText(mNumFomat.format(3100000 + (mYearPay * 0.005)));
      }
    } else if (mPersonNumber1 == 2) {
      Log.i(TAG, "공제대상자가 2명인 경우");
      if (mYearPay <= 30000000) {
        Log.i(TAG, "3,000만원 이하");
        mTxtSpecialMinus7_1.setText("3,600,000 + (" + mNumFomat.format(mYearPay) + " * 4%)");
        mTxtSpecialMinus7.setText(mNumFomat.format(3600000 + (mYearPay * 0.04)));

      } else if (mYearPay <= 45000000) {
        Log.i(TAG, "3,000만원 초과 4,500만원 이하");
        mTxtSpecialMinus7_1.setText("3,600,000 + (" + mNumFomat.format(mYearPay) + " * 4%) - [(" + mNumFomat.format(mYearPay) + " - " + "30,000,000) * 5%]");
        mTxtSpecialMinus7.setText(mNumFomat.format((3600000 + (mYearPay * 0.04)) - ((mYearPay - 30000000) * 0.05)));

      } else if (mYearPay <= 70000000) {
        Log.i(TAG, "4,500만원 초과 7,000만원 이하");
        mTxtSpecialMinus7_1.setText("3,600,000 + (" + mNumFomat.format(mYearPay) + " * 2%)");
        mTxtSpecialMinus7.setText(mNumFomat.format(3600000 + (mYearPay * 0.02)));

      } else if (mYearPay <= 120000000) {
        Log.i(TAG, "7,000만원 초과 1억 2,000만원 이하");
        mTxtSpecialMinus7_1.setText("3,600,000 + (" + mNumFomat.format(mYearPay) + " * 1%)");
        mTxtSpecialMinus7.setText(mNumFomat.format(3600000 + (mYearPay * 0.01)));
      }

    } else if (mPersonNumber1 >= 3) {
      Log.i(TAG, "공제대상자가 3명 이상인 경우");
      if (mYearPay <= 30000000) {
        Log.i(TAG, "3,000만원 이하");
        mTxtSpecialMinus7_1.setText("5,000,000 + (" + mNumFomat.format(mYearPay) + " * 7%)");
        mTxtSpecialMinus7.setText(mNumFomat.format(5000000 + (mYearPay * 0.07)));

      } else if (mYearPay <= 45000000) {
        Log.i(TAG, "3,000만원 초과 4,500만원 이하");
        if (mYearPay > 40000000) {
          mTxtSpecialMinus7_1.setText("5,000,000 + (" + mNumFomat.format(mYearPay) + " * 7%) - [(" + mNumFomat.format(mYearPay) + " - " + "30,000,000) * 5%]+ (" + mYearPay + " - 40,000,000) * 4%");
          mTxtSpecialMinus7.setText(mNumFomat.format((5000000 + (mYearPay * 0.07)) - ((mYearPay - 30000000) * 0.05) + (mYearPay - 40000000) * 0.04));
        } else {
          mTxtSpecialMinus7_1.setText("5,000,000 + (" + mNumFomat.format(mYearPay) + " * 7%) - [(" + mNumFomat.format(mYearPay) + " - " + "30,000,000) * 5%]");
          mTxtSpecialMinus7.setText(mNumFomat.format((5000000 + (mYearPay * 0.07)) - ((mYearPay - 30000000) * 0.05)));
        }

      } else if (mYearPay <= 70000000) {
        Log.i(TAG, "4,500만원 초과 7,000만원 이하");
        mTxtSpecialMinus7_1.setText("5,000,000 + (" + mNumFomat.format(mYearPay) + " * 5%) + (" + mYearPay + " - 40,000,000) * 4%");
        mTxtSpecialMinus7.setText(mNumFomat.format(5000000 + (mYearPay * 0.05) + (mYearPay - 40000000) * 0.04));

      } else if (mYearPay <= 120000000) {
        Log.i(TAG, "7,000만원 초과 1억 2,000만원 이하");
        mTxtSpecialMinus7_1.setText("5,000,000 + (" + mNumFomat.format(mYearPay) + " * 3%) + (" + mYearPay + " - 40,000,000) * 4%");
        mTxtSpecialMinus7.setText(mNumFomat.format(5000000 + (mYearPay * 0.03) + (mYearPay - 40000000) * 0.04));
      }
    }
    //과세표준
    // - 근로소득금액
    Long standard_1 = Long.parseLong(mTxtWorkerDutyMinus4.getText().toString().replaceAll(",", ""));
    // - 인적공제
    Long standard_2 = Long.parseLong(mTxtPersonMinus5.getText().toString().replaceAll(",", ""));
    // - 연금보험료공제
    Long standard_3 = Long.parseLong(mTxtInsuranceMinus6.getText().toString().replaceAll(",", ""));
    // - 특별소등공제
    Long standard_4 = Long.parseLong(mTxtSpecialMinus7.getText().toString().replaceAll(",", ""));
    // - total
    Long standard_total = standard_1 - standard_2 - standard_3 - standard_4;
    mTxtDutyStandard8.setText(mNumFomat.format(standard_total));

    //산출세액
    if (standard_total <= 12000000) {
      mTxtDutyCalculation9_1.setText(mNumFomat.format(standard_total) + "의 6%");
      mTxtDutyCalculation9.setText(mNumFomat.format(standard_total * 0.06));
    } else if (standard_total <= 46000000) {
      mTxtDutyCalculation9_1.setText("72만원 + (" + mNumFomat.format(standard_total) + " - 12,000,000) x 15%");
      mTxtDutyCalculation9.setText(mNumFomat.format(720000 + (standard_total - 12000000) * 0.15));

    } else if (standard_total <= 88000000) {
      mTxtDutyCalculation9_1.setText("582만원 + (" + mNumFomat.format(standard_total) + " - 46,000,000) x 24%");
      mTxtDutyCalculation9.setText(mNumFomat.format(5820000 + (standard_total - 46000000) * 0.24));

    } else if (standard_total <= 150000000) {
      mTxtDutyCalculation9_1.setText("72만원 + (" + mNumFomat.format(standard_total) + " - 88,000,000) x 35%");
      mTxtDutyCalculation9.setText(mNumFomat.format(15900000 + (standard_total - 88000000) * 0.35));

    } else if (standard_total > 150000000) {
      mTxtDutyCalculation9_1.setText("72만원 + (" + mNumFomat.format(standard_total) + " - 150,000,000) x 38%");
      mTxtDutyCalculation9.setText(mNumFomat.format(37600000 + (standard_total - 150000000) * 0.38));

    }

    // 근로소득세액
    if (Integer.parseInt(mTxtDutyCalculation9.getText().toString().replaceAll(",", "")) <= 500000) {
      mTxtDutyCalculationMinus10_1.setText(mTxtDutyCalculation9.getText().toString() + "* 55%");
      mTxtDutyCalculationMinus10.setText(mNumFomat.format(Integer.parseInt(mTxtDutyCalculation9.getText().toString().replaceAll(",", "")) * 0.55));
    } else {
      mTxtDutyCalculationMinus10_1.setText("275,000 + (" + mTxtDutyCalculation9.getText().toString() + " - 500,000)* 30%");
      mTxtDutyCalculationMinus10.setText(mNumFomat.format(275000 + (Integer.parseInt(mTxtDutyCalculation9.getText().toString().replaceAll(",", "")) - 500000) * 0.30));
    }
    // 근로소득세액 한도체크
    if (mYearPay <= 55000000) {
      if (Integer.parseInt(mTxtDutyCalculationMinus10.getText().toString().replaceAll(",", "")) > 660000) {
        mTxtDutyCalculationMinus10.setText(mNumFomat.format(660000));
      }
    } else if (mYearPay <= 70000000) {
      if (Integer.parseInt(mTxtDutyCalculationMinus10.getText().toString().replaceAll(",", "")) > 630000) {
        mTxtDutyCalculationMinus10.setText(mNumFomat.format(630000));
      }
    } else if (mYearPay > 70000000) {
      if (Integer.parseInt(mTxtDutyCalculationMinus10.getText().toString().replaceAll(",", "")) > 500000) {
        mTxtDutyCalculationMinus10.setText(mNumFomat.format(500000));
      }
    }
    //결정세액
    mTxtDutyYearResult11.setText(mNumFomat.format(Integer.parseInt(mTxtDutyCalculation9.getText().toString().replace(",", "")) - Integer.parseInt(mTxtDutyCalculationMinus10.getText().toString().replace(",", ""))));
    //간이세액
    mTxtDutyMonthResult12.setText(mNumFomat.format(Math.floor((Integer.parseInt(mTxtDutyYearResult11.getText().toString().replace(",", "")) / 12) / 10) * 10));

    //소득세
    mTxtDutyResult1.setText(mTxtDutyMonthResult12.getText().toString());
    //지방소득세
    mTxtDutyResult2.setText(mNumFomat.format(Math.floor((Integer.parseInt(mTxtDutyMonthResult12.getText().toString().replace(",", "")) * 0.1) / 10) * 10));
    //납부세액합계
    mTxtDutyResultTotal.setText(mNumFomat.format(Integer.parseInt(mTxtDutyResult1.getText().toString().replace(",", "")) + Integer.parseInt(mTxtDutyResult2.getText().toString().replace(",", ""))));
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
