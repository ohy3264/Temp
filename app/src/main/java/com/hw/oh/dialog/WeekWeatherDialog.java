package com.hw.oh.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.hw.oh.adapter.WeekWeatherAdapter;
import com.hw.oh.model.WeekWeatherItem;
import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;

import java.util.ArrayList;


public class WeekWeatherDialog extends DialogFragment {

  // Log
  private static final String TAG = "WeekDialog";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;

  //View
  private ListView mWeekWeatherList;
  private WeekWeatherAdapter mWeekWeatherAdapter;
  private ArrayList<WeekWeatherItem> mWeekWeatherDataList = new ArrayList<WeekWeatherItem>();
  private TextView dialog_BtnOk, mTxtWeekWeatherLocation;
  //flag
  private Boolean mRule1 = false, mRule2 = false;
  // Utill
  private HYFont mFont;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
    LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
    View v = mLayoutInflater.inflate(R.layout.dialog_weather, null);

    // Utill
    mFont = new HYFont(getActivity());
    mFont.setGlobalFont((ViewGroup) v);


    Bundle bundle = getArguments();
    mWeekWeatherDataList = (ArrayList<WeekWeatherItem>) bundle.getSerializable("weekWeather");
    mTxtWeekWeatherLocation = (TextView) v.findViewById(R.id.txtWeatherLocation);
    mTxtWeekWeatherLocation.setText(bundle.getString("weekWeatherLocation"));
    mWeekWeatherList = (ListView) v.findViewById(R.id.list_weekweather);
    setAdapter();
    // Button
    dialog_BtnOk = (TextView) v.findViewById(R.id.btnOk);
    dialog_BtnOk.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        dismiss();
      }
    });
    mBuilder.setView(v);

    return mBuilder.create();
  }

  public void setAdapter() {
    mWeekWeatherAdapter = new WeekWeatherAdapter(getActivity(), mWeekWeatherDataList);
    if (mWeekWeatherList != null) {
      mWeekWeatherList.setAdapter(mWeekWeatherAdapter);
    }
  }


}
