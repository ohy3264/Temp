package com.hw.oh.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hw.oh.adapter.AlbaInfoAdapter;
import com.hw.oh.dialog.HourPayInfoDialog;
import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import java.util.ArrayList;


/**
 * Created by oh on 2015-02-01.
 */
public class Fragment_Info1 extends Fragment implements View.OnClickListener {
  public static final String TAG = "Fragment_Info";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;
  //Crouton
  private View mCroutonView;
  private TextView mTxtCrouton;
  private CroutonHelper mCroutonHelper;
  //View
  private ListView mListView;
  private AlbaInfoAdapter mAlbaInfoAdapter;
  private ArrayList<String> mAlbaListData;
  private HYFont mFont;
  private HYPreference mPref;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_info1, container, false);
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

    mListView = (ListView) rootView.findViewById(R.id.mainlistView);

    mAlbaListData = new ArrayList<String>();
    mAlbaListData.add("최저임금제도");
    mAlbaListData.add("2015년 최저임금");
    mAlbaListData.add("야간수당");
    mAlbaListData.add("주휴수당");
    mAlbaListData.add("추가연장수당");
        /*mAlbaListData.add("도급인의 연대책임");
        mAlbaListData.add("최저임금액이상 지급의무");
        mAlbaListData.add("근로자 권리 구제");*/


    mAlbaInfoAdapter = new AlbaInfoAdapter(getActivity(), mAlbaListData);
    mListView.setAdapter(mAlbaInfoAdapter);
    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        HourPayInfoDialog hourPayInfoDig = new HourPayInfoDialog();
        switch (position) {
          case 0:
            bundle.putInt("DialogKey", 0);
            break;
          case 1:
            bundle.putInt("DialogKey", 1);
            break;
          case 2:
            bundle.putInt("DialogKey", 2);
            break;
          case 3:
            bundle.putInt("DialogKey", 3);
            break;
          case 4:
            bundle.putInt("DialogKey", 3);
            break;
        }
        hourPayInfoDig.setArguments(bundle);
        hourPayInfoDig.show(getFragmentManager(), "MYTAG0");
      }
    });
    return rootView;
  }


  @Override
  public void onClick(View v) {


  }
}
