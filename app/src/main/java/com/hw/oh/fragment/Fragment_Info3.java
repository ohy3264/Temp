package com.hw.oh.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hw.oh.adapter.AlbaInfoAdapter;
import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import java.util.ArrayList;


/**
 * Created by oh on 2015-02-01.
 */
public class Fragment_Info3 extends Fragment implements View.OnClickListener {
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


  //Util
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

    mAlbaListData = new ArrayList<String>();/*
        mAlbaListData.add("근로계약서 작성");
        mAlbaListData.add("고용노동부의 도움받기");
        mAlbaListData.add("폭언, 성희롱 등의 부당대우");
        mAlbaListData.add("청소년 알바");
        mAlbaListData.add("청소년 알바 부당사례");
        mAlbaListData.add("산재보험과 보상");
        mAlbaListData.add("유급휴가와 휴일");
        mAlbaListData.add("부당해고");
        mAlbaListData.add("퇴직금");*/


    mAlbaInfoAdapter = new AlbaInfoAdapter(getActivity(), mAlbaListData);
    mListView.setAdapter(mAlbaInfoAdapter);
    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Toast.makeText(getActivity(), Integer.toString(position), Toast.LENGTH_SHORT).show();
      }
    });
    return rootView;
  }


  @Override
  public void onClick(View v) {


  }
}
