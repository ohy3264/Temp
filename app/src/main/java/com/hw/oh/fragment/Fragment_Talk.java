package com.hw.oh.fragment;

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
import android.widget.TextView;

import com.hw.oh.adapter.BoardListAdapter_Array;
import com.hw.oh.model.BoardItem;
import com.hw.oh.service.BoardListService;
import com.hw.oh.temp.ApplicationClass;
import com.hw.oh.temp.DetailActivity;
import com.hw.oh.temp.NewPostActivity;
import com.hw.oh.temp.R;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYNetworkInfo;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.InfoExtra;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by oh on 2015-02-01.
 */
public class Fragment_Talk extends Fragment implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener, OnDismissCallback {
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
  private ArrayList<BoardItem> mServiceDatalist = new ArrayList<BoardItem>();

  //Crouton
  private View mCroutonView;
  private TextView mTxtCrouton;
  private CroutonHelper mCroutonHelper;

  //Util
  private InfoExtra mInfoExtra;
  private HYFont mFont;
  private HYPreference mPref;
  private SwipeRefreshLayout mTopPullRefresh = null; // 상단 새로고침
  private HYNetworkInfo mNet;
  private com.rey.material.widget.ProgressView mProgressBar;


  // Messenger
  private final Messenger mMessenger = new Messenger(new IncomingHandler());
  private Messenger mService = null;

  // IncomingHandler - 서비스측으로 부터 응답 메시지 수신할 핸들러
  class IncomingHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case BoardListService.MSG_SET_REQUEST_VALUE:
          Log.d(TAG, "MSG_SET_STRING_VALUE");
          mServiceDatalist.clear();
          mServiceDatalist = msg.getData().getParcelableArrayList("Board_Data");
          if (!mServiceDatalist.isEmpty()) {
            switch (Constant.REFRESH_BOARD_FLAG) {
              case 0:
              case 1:
              case 2:
                mBoardDataList.clear();
                break;
              case 3:
                Constant.LIMIT_START += Constant.LIMIT_ADD;
                break;
              case 4:
                mBoardDataList.clear();
                break;
            }
            for (int i = 0; i < mServiceDatalist.size(); i++) {
              mBoardDataList.add(mServiceDatalist.get(i));
            }
          }
          mBoardAdapter.notifyDataSetChanged();
          mProgressBar.setVisibility(View.GONE);
          mLock = false;
          break;
        default:
          Log.d(TAG, "default");
          super.handleMessage(msg);
          mProgressBar.setVisibility(View.GONE);
          mLock = false;
      }
    }
  }

  // Service Conn
  private ServiceConnection mConnection = new ServiceConnection() {
    public void onServiceConnected(ComponentName className, IBinder service) {
      mService = new Messenger(service); // 서비스에서 전달받은 IBinder 객체를 기반으로 Messenger 인스턴스 생성
      try {
        Message msg = Message.obtain(null, BoardListService.MSG_REGISTER_CLIENT); //handler, arg1
        msg.replyTo = mMessenger; //서비스측으로 부터 응답을 받길 원할 경우 사용
        mService.send(msg);
      } catch (RemoteException e) {
        // In this case the service has crashed before we could even do anything with it
      }
    }

    public void onServiceDisconnected(ComponentName className) {
      // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
      mService = null;
    }


  };


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_talk, container, false);
    // 구글 통계
    Tracker mTracker = ((ApplicationClass) getActivity().getApplication()).getDefaultTracker();
    mTracker.setScreenName("알바톡");
    mTracker.send(new HitBuilders.AppViewBuilder().build());

    //Util
    mFont = new HYFont(getActivity());
    mFont.setGlobalFont((ViewGroup) rootView);
    mNet = new HYNetworkInfo(getActivity());
    mInfoExtra = new InfoExtra(getActivity());
    mPref = new HYPreference(getActivity());
    //Crouton
    mCroutonHelper = new CroutonHelper(getActivity());
    mCroutonView = getActivity().getLayoutInflater().inflate(
        R.layout.crouton_custom_view, null);
    mTxtCrouton = (TextView) mCroutonView.findViewById(R.id.txt_crouton);


    mProgressBar = (com.rey.material.widget.ProgressView) rootView.findViewById(R.id.progressBar);

    mListView = (ListView) rootView.findViewById(R.id.mainlistView);
    mListView.setOnScrollListener(this);
    mListView.setOnItemClickListener(this);

    // Top pull refresh
    mTopPullRefresh = (SwipeRefreshLayout) rootView
        .findViewById(R.id.swipe_container);

    mTopPullRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

      @Override
      public void onRefresh() {
        mTxtCrouton.setText("새로고침");
        mCroutonHelper.setCustomView(mCroutonView);
        mCroutonHelper.setDuration(5000);
        mCroutonHelper.show();
        mTopPullRefresh.postDelayed(new Runnable() {
          @Override
          public void run() {
            if (INFO)
              Log.i(TAG, "TopPullRefresh");

            mProgressBar.setVisibility(View.VISIBLE);
            Constant.REFRESH_BOARD_FLAG = Constant.REQUEST_TOP_REFRESH;
            sendRefreshToService();
            mTopPullRefresh.setRefreshing(false);
            mCroutonHelper.clearCroutonsForActivity();

          }
        }, 1000);
      }
    });

    inits();
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

  public void inits() {
    if (!mNet.networkgetInfo()) {
      registerGCM();
      setAdapter();
      // asyncTask_BoardList_Call();
    } else {
      mTxtCrouton.setText("네트워크를 확인해주세요");
      mCroutonHelper.setCustomView(mCroutonView);
      mCroutonHelper.setDuration(1000);
      mCroutonHelper.show();
      mProgressBar.setVisibility(View.GONE);
    }
  }


  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

  }


  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Constant.REFRESH_DETAIL_FLAG = Constant.DETAIL_REQUEST_FIRST;
    Intent intent_detail = new Intent(getActivity(), DetailActivity.class);
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
      sendRefreshToService();
      mProgressBar.setVisibility(View.VISIBLE);
    }
  }


  public void setAdapter() {
  /*  mBoardAdapter = new BoardListAdapter_Array(getActivity(), mBoardDataList);
    if (mListView != null) {
      AnimationAdapter  mAnimAdapter = new AlphaInAnimationAdapter(mBoardAdapter);
      mAnimAdapter.setAbsListView(mListView);
      mListView.setAdapter(mAnimAdapter);
    }*/

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
    mProgressBar.setVisibility(View.VISIBLE);
    doBindService();
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
    if (INFO)
      Log.d(TAG, "onStop");
    // mTimer.cancel();
    doUnbindService();
    mProgressBar.setVisibility(View.GONE);
    // mRequestQueue.cancelAll(TAG);
    // 구글 통계
    GoogleAnalytics.getInstance(getActivity()).reportActivityStop(getActivity());
  }

  @Override
  public void onDestroy() {
    if (INFO)
      Log.d(TAG, "onDestroy");
    super.onDestroy();
    Constant.REFRESH_BOARD_FLAG = Constant.REQUEST_NONE;
  }

  // 서비스  바인드
  void doBindService() {
    Log.i(TAG, "doBindService");
    mIsBound = true;
    Log.i(TAG, "Binding");
    getActivity().bindService(new Intent(getActivity(), BoardListService.class), mConnection, Context.BIND_AUTO_CREATE);

  }

  // 서비스 언 바인드
  void doUnbindService() {
    Log.i(TAG, "doUnbindService");
    if (mIsBound) {
      try {
        Message msg = Message.obtain(null, BoardListService.MSG_UNREGISTER_CLIENT);
        msg.replyTo = mMessenger;
        mService.send(msg);
      } catch (RemoteException e) {
        // There is nothing special we need to do if the service has crashed.
      }
      Log.i(TAG, "un Binding");
      getActivity().unbindService(mConnection);
      mIsBound = false;
    }
  }

  // 서비스 메시지 전송
  private void sendRefreshToService() {
    if (mIsBound) {
      if (mService != null) {
        try {
          Message msg = Message.obtain(null, BoardListService.MSG_REQ_REFRESH_CLIENT);
          msg.replyTo = mMessenger;
          mService.send(msg);
        } catch (RemoteException e) {
        }
      }
    }
  }


  /**
   * @author : oh
   * @MethodName : registerGCM
   * @Day : 2014. 10. 12.
   * @Time : 오후 5:03:12
   * @Explanation : GCM Registrar(유저 regID 생성)
   */
  public void registerGCM() {
    try {
      GCMRegistrar.checkDevice(getActivity());
      Log.i("checkDevice", "GCM ID 발급 요청");
      GCMRegistrar.checkManifest(getActivity());
      Log.i("checkManifest", "GCM ID 발급 요청");
      String regId = GCMRegistrar.getRegistrationId(getActivity());
      if (regId.isEmpty()) {
        GCMRegistrar.register(getActivity(), Constant.PROJECT_ID);
        Log.i("LoginActivity", "GCM ID 발급 요청 :" + regId);
      } else {
        Log.i("LoginActivity", "이미 등록되어 있는 단말 : " + regId);
        registerGCM(regId, mInfoExtra.getAndroidID());
      }
    } catch (Exception e) {
      Log.e(TAG, "This Device can't use GCM");
    }
  }

  /**
   * @param regId : GCM 통신을 위한 유저 고유 regID
   * @param andId : 유저 아이디
   * @author : oh
   * @MethodName : sendAPIkey
   * @Day : 2014. 10. 12.
   * @Time : 오후 5:03:52
   * @Explanation : regID 서버에 등록 요청
   */
  public void registerGCM(final String regId, final String andId) {
    String url = "http://ohy3264.cafe24.com/Anony/api/registerGCM.php";
    try {
      OkHttpClient client = new OkHttpClient();
      RequestBody formBody = new FormBody.Builder()
              .add("REG_ID", regId)
              .add("GENDER", mPref.getValue(mPref.KEY_GENDER, "0"))
              .add("AND_ID", andId)
              .build();

      Request request = new Request.Builder()
              .url(url)
              .post(formBody)
              .build();

      Response response = client.newCall(request).execute();

      Log.d(TAG, "onResponse :: " + response.body().string());
    } catch (Exception e) {
      Log.i(TAG, e.toString());
    }
  }





}
