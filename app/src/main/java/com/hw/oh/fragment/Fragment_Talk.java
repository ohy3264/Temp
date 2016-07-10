package com.hw.oh.fragment;

import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hw.oh.adapter.BoardListAdapter_Array;
import com.hw.oh.model.BoardItem;
import com.hw.oh.temp.ApplicationClass;
import com.hw.oh.temp.BaseActivity;
import com.hw.oh.temp.talk.TalkDetailActivity;
import com.hw.oh.temp.talk.NewPostActivity;
import com.hw.oh.temp.R;
import com.hw.oh.utility.CommonUtil;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYTime_Maximum;
import com.hw.oh.utility.NetworkUtil;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.OkHttpUtils;
import com.hw.oh.utility.ServerRequestUtils;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by oh on 2015-02-01.
 */
public class Fragment_Talk extends BaseFragment implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener, OnDismissCallback {
  public static final String TAG = "Fragment_Talk";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;
  private static final int INITIAL_DELAY_MILLIS = 300;

  //Flag
  private boolean mLastitemVisibleFlag = false;
  private boolean mLock = false;
  private Boolean mIsBound;


  private android.support.design.widget.FloatingActionButton mFabNewTalk;

  //List
  private ListView mListView;
  private BoardListAdapter_Array mBoardAdapter;
  private ArrayList<BoardItem> mBoardDataList = new ArrayList<BoardItem>();


  //Util
  private SwipeRefreshLayout mTopPullRefresh = null; // 상단 새로고침
  private ProgressBar mProgressBar;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_talk, container, false);
    // 구글 통계
    Tracker mTracker = ((ApplicationClass) getActivity().getApplication()).getDefaultTracker();
    mTracker.setScreenName("알바톡");
    mTracker.send(new HitBuilders.AppViewBuilder().build());

    //Util
    setFont(rootView);
    mPref = new HYPreference(getActivity());

    mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress);

    ThreeBounce threeBounce = new ThreeBounce();
    threeBounce.setColor(getThemeColor(1));
    mProgressBar.setIndeterminateDrawable(threeBounce);

    mListView = (ListView) rootView.findViewById(R.id.mainlistView);
    mListView.setOnScrollListener(this);
    mListView.setOnItemClickListener(this);

    // Top pull refresh
    mTopPullRefresh = (SwipeRefreshLayout) rootView
        .findViewById(R.id.swipe_container);

    mTopPullRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

      @Override
      public void onRefresh() {
        showCrountToast("새로고침", 5000);
        mTopPullRefresh.postDelayed(new Runnable() {
          @Override
          public void run() {
            if (INFO)
              Log.i(TAG, "TopPullRefresh");
            mProgressBar.setVisibility(View.VISIBLE);
            Constant.REFRESH_BOARD_FLAG = Constant.REQUEST_TOP_REFRESH;
            requestCallRest_BoardList();
            mTopPullRefresh.setRefreshing(false);
            clearCrountToast();
          }
        }, 1000);
      }
    });

    setAdapter();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

      mFabNewTalk = (android.support.design.widget.FloatingActionButton) rootView.findViewById(R.id.fabNewTalk);
      mFabNewTalk.setVisibility(View.VISIBLE);
      mFabNewTalk.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent_main = new Intent(getActivity(), NewPostActivity.class);
          intent_main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent_main);
          getActivity().overridePendingTransition(0, 0);
        }
      });
    } else {
      //Float Button
      int redActionButtonSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_size);
      int redActionButtonMargin = getResources().getDimensionPixelOffset(R.dimen.action_button_margin);
      int redActionButtonContentSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_size);
      int redActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_margin);
      ImageView fabIconStar = new ImageView(getActivity()); // Create an icon
      fabIconStar.setImageDrawable(getResources().getDrawable(R.drawable.ic_mode_edit_white_48dp));
      FloatingActionButton.LayoutParams starParams = new FloatingActionButton.LayoutParams(redActionButtonSize, redActionButtonSize);
      starParams.setMargins(redActionButtonMargin,
          redActionButtonMargin,
          redActionButtonMargin,
          redActionButtonMargin);
      fabIconStar.setLayoutParams(starParams);

      FloatingActionButton.LayoutParams fabIconStarParams = new FloatingActionButton.LayoutParams(redActionButtonContentSize, redActionButtonContentSize);
      fabIconStarParams.setMargins(redActionButtonContentMargin,
          redActionButtonContentMargin,
          redActionButtonContentMargin,
          redActionButtonContentMargin);


      FloatingActionButton actionButton = new FloatingActionButton.Builder(getActivity())
          .setContentView(fabIconStar, fabIconStarParams)
          .setContentView(fabIconStar)
          .setBackgroundDrawable(R.drawable.button_action_red_selector)
          .build();


      actionButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent_main = new Intent(getActivity(), NewPostActivity.class);
          intent_main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent_main);
          getActivity().overridePendingTransition(0, 0);
        }
      });
    }


    return rootView;
  }




  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

  }


  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Constant.REFRESH_DETAIL_FLAG = Constant.DETAIL_REQUEST_FIRST;
    Intent intent_detail = new Intent(getActivity(), TalkDetailActivity.class);
    //intent_detail.putExtra("BoardData", mBoardDataList.get(position));
    intent_detail.putExtra("ID", mBoardDataList.get(position).get_id());

    //intent_detail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent_detail);
    getActivity().overridePendingTransition(0, 0);
  }


  @Override
  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    mLastitemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);

    int count = totalItemCount - visibleItemCount;
    mLastitemVisibleFlag = firstVisibleItem >= count && totalItemCount != 0 && mLock == false;

  }

  @Override
  public void onScrollStateChanged(AbsListView view, int scrollState) {
    // mBottomPullRefresh 작동여부
    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
        && mLastitemVisibleFlag && !mTopPullRefresh.isRefreshing() && !Constant.RESPON_EMPTY) {
      Log.d(TAG, "allsync : BottomPullRefresh");
      mLock = true;
      Constant.REFRESH_BOARD_FLAG = Constant.REQUEST_BOTTOM_REFRESH;
      mProgressBar.setVisibility(View.VISIBLE);
      requestCallRest_BoardList();
    }
  }


  public void setAdapter() {
    mBoardAdapter = new BoardListAdapter_Array(getActivity(), mBoardDataList);
    if (mListView != null) {
      SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new SwipeDismissAdapter(mBoardAdapter, this));
      swingBottomInAnimationAdapter.setAbsListView(mListView);
      assert swingBottomInAnimationAdapter.getViewAnimator() != null;
      swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
      mListView.setAdapter(swingBottomInAnimationAdapter);
    }
  }
  @Override
  public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
    for (int position : reverseSortedPositions) {
      mBoardDataList.remove(position);
      mBoardAdapter.notifyDataSetChanged();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (INFO)
      Log.d(TAG, "onResume");
    if (Constant.REFRESH_BOARD_FLAG == Constant.REQUEST_NEWPOST) {
      Log.d(TAG, "REQUEST_NEWPOST");
      Constant.REFRESH_BOARD_FLAG = Constant.REQUEST_NONE;
    } else if (Constant.REFRESH_BOARD_FLAG == Constant.REQUEST_DELPOST) {
      Log.d(TAG, "REQUEST_DELPOST");
      Constant.REFRESH_BOARD_FLAG = Constant.REQUEST_NONE;
    } else if (Constant.REFRESH_BOARD_FLAG == Constant.REQUEST_BOTTOM_REFRESH) {
      Log.d(TAG, "REQUEST_BOTTOM_REFRESH");
    }
    requestCallRest_BoardList();
  }

  @Override
  public void onStart() {
    super.onStart();
    GoogleAnalytics.getInstance(getActivity()).reportActivityStart(getActivity());
  };
  @Override
  public void onStop() {
    super.onStop();
    if (INFO)
      Log.d(TAG, "onStop");
    mProgressBar.setVisibility(View.GONE);
    GoogleAnalytics.getInstance(getActivity()).reportActivityStop(getActivity());
  }

  @Override
  public void onDestroy() {
    if (INFO)
      Log.d(TAG, "onDestroy");
    super.onDestroy();
    Constant.REFRESH_BOARD_FLAG = Constant.REQUEST_NONE;
  }

  // Intent Handler
  final Handler listCallHandler = new android.os.Handler() {
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case -1:
          break;
        case 0:
          Log.d(TAG, "onResponse :: " + msg.obj);
          String response = msg.obj.toString();
          Constant.RESPON_EMPTY = false;
          if (response.trim().equals("null")) {
            Log.d(TAG, "null");
            Constant.RESPON_EMPTY = true;
          } else {
            try {
              java.lang.reflect.Type type = new TypeToken<List<BoardItem>>() {
              }.getType();

              ArrayList<BoardItem> value = new Gson()
                      .fromJson(response, type);

              for (BoardItem m : value) {
                BoardItem t = new BoardItem();
                t.set_id(m.get_id());
                t.setStrText(m.getStrText());
                t.setGender(m.getGender());
                t.setRegDate(HYTime_Maximum.formatTimeString(m.getRegDate()));
                t.setUniqueID(m.getUniqueID());
                t.setHitCNT(m.getHitCNT());
                t.setCommCNT(m.getCommCNT());
                t.setLikeCNT(m.getLikeCNT());
                t.setHateCNT(m.getHateCNT());
                t.setImgState(m.getImgState());
                mBoardDataList.add(t);
                Log.d(TAG, m.getStrText());
              }
              mBoardAdapter.notifyDataSetChanged();
              mProgressBar.setVisibility(View.GONE);
            } catch (Exception e) {
              Log.d(TAG, "allsync.Response : Gson Exception");
              mProgressBar.setVisibility(View.GONE);
            }
          }
          break;
      }//switch
    }
  };


  private void requestCallRest_BoardList() {
    switch (Constant.REFRESH_BOARD_FLAG) {
      case 0:
      case 1:
      case 2:
        Constant.LIMIT_START = 0;
        mBoardDataList.clear();
        break;
      case 3:
        Constant.LIMIT_START += Constant.LIMIT_ADD;
        break;
      case 4:
        Constant.LIMIT_START = 0;
        mBoardDataList.clear();
        break;
    }
    try {
      RequestBody formBody = new FormBody.Builder()
              .add("MODE", "BoardList")
              .add("LIMIT", Integer.toString(Constant.LIMIT_START))
              .build();
      OkHttpUtils.post(getActivity(), Constant.SERVER_URL, "/Anony/api/boardListAll.php", formBody, new Callback() {
        Handler mainHandler = new Handler();

        @Override
        public void onFailure(Call call, IOException e) {
          mainHandler.post(new Runnable() {
            @Override
            public void run() {
              Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.error_common), Toast.LENGTH_SHORT).show();
            }
          });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
          final String results = response.body().string();
          mainHandler.post(new Runnable() {
            @Override
            public void run() {
              Constant.RESPON_EMPTY = false;
              if (results.trim().equals("null")) {
                Log.d(TAG, "null");
                Constant.RESPON_EMPTY = true;
              } else {
                try {
                  java.lang.reflect.Type type = new TypeToken<List<BoardItem>>() {
                  }.getType();

                  ArrayList<BoardItem> value = new Gson()
                          .fromJson(results, type);

                  for (BoardItem m : value) {
                    BoardItem t = new BoardItem();
                    t.set_id(m.get_id());
                    t.setStrText(m.getStrText());
                    t.setGender(m.getGender());
                    t.setRegDate(HYTime_Maximum.formatTimeString(m.getRegDate()));
                    t.setUniqueID(m.getUniqueID());
                    t.setHitCNT(m.getHitCNT());
                    t.setCommCNT(m.getCommCNT());
                    t.setLikeCNT(m.getLikeCNT());
                    t.setHateCNT(m.getHateCNT());
                    t.setImgState(m.getImgState());
                    mBoardDataList.add(t);
                    Log.d(TAG, m.getStrText());
                  }
                  mBoardAdapter.notifyDataSetChanged();
                  mProgressBar.setVisibility(View.GONE);
                } catch (Exception e) {
                  Log.d(TAG, "allsync.Response : Gson Exception");
                  mProgressBar.setVisibility(View.GONE);
                }
              }
            }
          });
        }
      });
    } catch (Exception e) {
      Log.d(TAG, e.toString());
      Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.error_etc), Toast.LENGTH_SHORT).show();
    }
  }
}
