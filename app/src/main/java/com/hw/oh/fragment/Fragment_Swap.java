package com.hw.oh.fragment;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.hw.oh.adapter.WorkAlbaInfoAdapter_swip;
import com.hw.oh.model.PartTimeInfo;
import com.hw.oh.sqlite.DBConstant;
import com.hw.oh.sqlite.DBManager;
import com.hw.oh.temp.ApplicationClass;
import com.hw.oh.temp.R;
import com.hw.oh.utility.DndListView;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import java.util.ArrayList;


/**
 * Created by oh on 2015-02-01.
 */
public class Fragment_Swap extends BaseFragment implements DndListView.DragListener, DndListView.DropListener {
  public static final String TAG = "Fragment_Swap";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;

  // DND ListView
  private DndListView listView;
  private ArrayList<PartTimeInfo> mAlbaInfoList = new ArrayList<PartTimeInfo>();
  private ArrayList<String> mID = new ArrayList<String>();
  private WorkAlbaInfoAdapter_swip mBoardAdapter;

  //util
  private DBManager mDB;

  //CroutonView
  private View mCroutonView;
  private TextView mTxtCrouton;
  private CroutonHelper mCroutonHelper;

  public Fragment_Swap(ArrayList<PartTimeInfo> albalist) {
    this.mAlbaInfoList = albalist;
    for(int i = 0; i < albalist.size(); i++){
      mID.add(Integer.toString(albalist.get(i).get_id()));
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_swap, container, false);
    // 구글 통계
    Tracker mTracker = ((ApplicationClass) getActivity().getApplication()).getDefaultTracker();
    mTracker.setScreenName("알바리스트 순서변경");
    mTracker.send(new HitBuilders.AppViewBuilder().build());
    //Util
    mDB = new DBManager(getActivity());
    //Crouton
    mCroutonHelper = new CroutonHelper(getActivity());
    mCroutonView = getActivity().getLayoutInflater().inflate(
        R.layout.crouton_custom_view, null);
    mTxtCrouton = (TextView) mCroutonView.findViewById(R.id.txt_crouton);

    listView = (DndListView) rootView.findViewById(R.id.listview);
    listView.setDragListener(this);
    listView.setDropListener(this);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      }
    });

    mBoardAdapter = new WorkAlbaInfoAdapter_swip(getActivity(), mAlbaInfoList);
    listView.setAdapter(mBoardAdapter);

    setHasOptionsMenu(true);
    return rootView;
  }
  @Override
  public void onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);

  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_swip, menu);

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        getActivity().finish();
        break;

      case R.id.action_swip_save:
        updateData();
        break;
    }
    getActivity().invalidateOptionsMenu();
    return super.onOptionsItemSelected(item);
  }


  public void drag(int from, int to) {
    Log.i("drag", "FROM : " + from + " TO : " + to);
    // 드래그 이벤트가 발생시 구현해야 할 것들을 기술한다.
  }

  public void drop(int from, int to) {
    Log.i("drop", "FROM : " + from + " TO : " + to);
    // 드롭 이벤트 발생시 구현해야 할 것들을 기술한다.
    PartTimeInfo item= mAlbaInfoList.get(from);
    mAlbaInfoList.remove(item);
    mAlbaInfoList.add(to, item);
    listView.setAdapter(mBoardAdapter);

    for(int i = 0; i< mID.size(); i++){
      Log.i(TAG, "ori" + mID.get(i));
    }
    for(int i = 0; i< mAlbaInfoList.size(); i++){
      Log.i(TAG, Integer.toString(mAlbaInfoList.get(i).get_id()));
    }
  }

  public void updateData(){
    mDB.removeALLData(DBConstant.TABLE_PARTTIMEINFO);
    if(mAlbaInfoList.isEmpty()){
    }
    for(int i = mAlbaInfoList.size()-1; i >=0; i-- ){
      if (mDB.updatePartTimeInfoSwap(mAlbaInfoList.get(i), mID.get(i))) {
        Log.i(TAG, mAlbaInfoList.get(i).getAlbaname() + " : update sucess!");
        getActivity().finish();
      } else {
        Log.i(TAG, mAlbaInfoList.get(i).getAlbaname() + " : update fail!");
        Toast.makeText(getActivity(), "변경에 실패하였습니다. 개발자에게 문의하세요", Toast.LENGTH_SHORT).show();
      }
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
