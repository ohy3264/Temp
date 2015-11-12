package com.hw.oh.fragment;

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

import com.hw.oh.adapter.WorkAlbaInfoAdapter_swip;
import com.hw.oh.model.PartTimeInfo;
import com.hw.oh.sqlite.DBManager;
import com.hw.oh.temp.R;
import com.hw.oh.utility.DndListView;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import java.util.ArrayList;


/**
 * Created by oh on 2015-02-01.
 */
public class Fragment_Swap extends android.app.Fragment implements DndListView.DragListener, DndListView.DropListener {
  public static final String TAG = "Fragment_Swap";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;

  // DND ListView
  private DndListView listView;
  private ArrayList<PartTimeInfo> mAlbaInfoList = new ArrayList<PartTimeInfo>();
  private WorkAlbaInfoAdapter_swip mBoardAdapter;

  //util
  private DBManager mDB;

  //CroutonView
  private View mCroutonView;
  private TextView mTxtCrouton;
  private CroutonHelper mCroutonHelper;

  public Fragment_Swap(ArrayList<PartTimeInfo> albalist) {
    this.mAlbaInfoList = albalist;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.frag, container, false);
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
      case R.id.action_swap:
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
  }

  public void updateData(){
    for(int i = 0; i < mAlbaInfoList.size(); i++ ){
      if (mDB.updatePartTimeInfo(mAlbaInfoList.get(i), mAlbaInfoList.get(i).get_id())) {
        Log.i(TAG, mAlbaInfoList.get(i).getAlbaname() + " : update sucess!");
      } else {
        Log.i(TAG, mAlbaInfoList.get(i).getAlbaname() + " : update fail!");
      }
    }

  }
}
