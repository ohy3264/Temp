package com.hw.oh.temp;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hw.oh.adapter.CommentAdapter;
import com.hw.oh.model.BoardItem;
import com.hw.oh.popupWindow.ActionItem;
import com.hw.oh.popupWindow.QuickAction;
import com.hw.oh.service.BoardDetailService;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYNetworkInfo;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.HYTime_Maximum;
import com.hw.oh.utility.InfoExtra;
import com.hw.oh.utility.ServerRequestUtils;
import com.hw.oh.volley.utility.DiskBitmapCache;
import com.hw.oh.volley.utility.FadeInImageListener;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by oh on 2015-02-05.
 */
public class DetailActivity extends BaseActivity implements AdapterView.OnItemClickListener {
  //Tag
  private static Toast toast;
  private static final String TAG = "DetailActivity";
  private static final boolean DBUG = true;
  private static final boolean INFO = true;
  //View
  private Toolbar mToolbar;
  private Button mBtnFloating;

  //Flag
  private Boolean mMenuFlag = true;
  private String mSelectCommentID;
  private String mSelectBoardID;
  private Boolean mIsBound;

  // List View
  private ListView mCommentlistView;
  private EditText mEdtNewPost;

  // List Header View
  private View mlistViewHeader;
  private TextView mTxtTitle;
  private TextView mTxtCommCNT;
  private TextView mTxtLikeCNT;
  private TextView mTxtHateCNT;
  private TextView mTxtRegDate;
  private ImageView mImageGender;
  private ImageView mImageDetail;

  //Crouton
  private View mCroutonView;
  private TextView mTxtCrouton;
  private CroutonHelper mCroutonHelper;

  //PopupWindow
  private QuickAction mCommentMenuQuick;

  // Utill
  private HYNetworkInfo mNet;
  private InfoExtra mInfor;
  private HYFont mFont;
  private InfoExtra mInfoExtra;
  private HYPreference mPref;
  private RequestQueue mRequestQueue;
  private ImageLoader mImageLoader;
  private final int max_cache_size = 1000000;
  private com.rey.material.widget.ProgressView mProgressBar;

  //DataSet
  private ArrayList<BoardItem> mCommentList = new ArrayList<BoardItem>();
  private BoardItem mHeaderData;
  private CommentAdapter mCommentAdapter;

  // Latch
  private CountDownLatch mCDL_HeaderSet;
  private CountDownLatch mCDL_BoardPostDel;
  private CountDownLatch mCDL_CommentDel;


  // Messenger
  private final Messenger mMessenger = new Messenger(new IncomingHandler());
  private Messenger mService = null;

  // IncomingHandler - 서비스측으로 부터 응답 메시지 수신할 핸들러
  class IncomingHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case BoardDetailService.MSG_SET_DETAIL_HEADER_VALUE:
          Log.i(TAG, "MSG_SET_DETAIL_HEADER_VALUE");
          mHeaderData = new BoardItem();
          mCommentList.clear();

          mHeaderData = (BoardItem) msg.getData().getSerializable("HEADER_DATA");
          mCommentList = (ArrayList<BoardItem>) msg.getData().getSerializable("COMMENT_LIST");
          for (int i = 0; i < mCommentList.size(); i++) {
            Log.i(TAG, mCommentList.get(i).getStrText());
          }
          try {
            if (mHeaderData instanceof BoardItem) {
              mProgressBar.setVisibility(View.GONE);
              Log.i(TAG, "데이터 존재함..");
              headerDataSet();
              setAdapter();
            } else {
              Log.i(TAG, "null");
              mProgressBar.setVisibility(View.GONE);
              Toast.makeText(getApplicationContext(), "서버가 불안정합니다.", Toast.LENGTH_SHORT).show();
            }
          } catch (Exception e) {
            Log.e(TAG, e.toString());
          }
          break;

        case BoardDetailService.MSG_SET_DETAIL_COMMENT_ALL:
          Log.i(TAG, "MSG_SET_DETAIL_COMMENT_VALUE");
          try {
            mCommentList = (ArrayList<BoardItem>) msg.getData().getSerializable("COMMENT_LIST");
            mCommentAdapter.notifyDataSetChanged();
            Message newmsg = NewCommentHandler.obtainMessage();
            NewCommentHandler.sendMessage(newmsg);
            mProgressBar.setVisibility(View.GONE);
            break;
          } catch (Exception e) {
            Log.e(TAG, e.toString());
            mProgressBar.setVisibility(View.GONE);
          }
        default:
          mProgressBar.setVisibility(View.GONE);
          super.handleMessage(msg);
      }
    }
  }

  // Service Conn
  private ServiceConnection mConnection = new ServiceConnection() {
    public void onServiceConnected(ComponentName className, IBinder service) {
      mService = new Messenger(service); // 서비스에서 전달받은 IBinder 객체를 기반으로 Messenger 인스턴스 생성
      try {
        Message msg = Message.obtain(null, BoardDetailService.MSG_REGISTER_CLIENT); //handler, arg1
        Bundle bundle = new Bundle();
        bundle.putString("BSEQ", mSelectBoardID);
        msg.setData(bundle);
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
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    // 구글 통계
    /*Tracker mTracker = ((ApplicationClass) getApplication()).getDefaultTracker();
    mTracker.setScreenName("게시글 상세보기");
    mTracker.send(new HitBuilders.AppViewBuilder().build());
*/
    //Util
    mNet = new HYNetworkInfo(this);
    mInfoExtra = new InfoExtra(this);
    mPref = new HYPreference(this);
    mFont = new HYFont(this);
    mInfor = new InfoExtra(this);
    mRequestQueue = Volley.newRequestQueue(this);
    mImageLoader = new ImageLoader(mRequestQueue, new DiskBitmapCache(getCacheDir(), max_cache_size));
    mProgressBar = (com.rey.material.widget.ProgressView) findViewById(R.id.progressBar);

    //ActionBar
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    if (mToolbar != null) {
      mToolbar.setTitle("");
      setSupportActionBar(mToolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      mFont.setGlobalFont((ViewGroup) mToolbar);
    }

    //Crouton
    mCroutonHelper = new CroutonHelper(this);
    mCroutonView = getLayoutInflater().inflate(
        R.layout.crouton_custom_view, null);
    mTxtCrouton = (TextView) mCroutonView.findViewById(R.id.txt_crouton);

    mEdtNewPost = (EditText) findViewById(R.id.edtNewPost);

    //댓글쓰기
    mBtnFloating = (Button) findViewById(R.id.btnFloating);
    mBtnFloating.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!mNet.networkgetInfo()) {
          if (!mEdtNewPost.getText().toString().isEmpty()) {
            mBtnFloating.setEnabled(false);
            mProgressBar.setVisibility(View.VISIBLE);
            newCommentSendService();
          } else {
            mTxtCrouton.setText("아무글이나 써주세요");
            mCroutonHelper.setCustomView(mCroutonView);
            mCroutonHelper.setDuration(1000);
            mCroutonHelper.show();
          }
        } else {
          mTxtCrouton.setText("네트워크를 확인해주세요");
          mCroutonHelper.setCustomView(mCroutonView);
          mCroutonHelper.setDuration(1000);
          mCroutonHelper.show();
        }
      }
    });

    // detail listview header
    mlistViewHeader = getLayoutInflater().inflate(
        R.layout.header_detail, null, false);
    mTxtTitle = (TextView) mlistViewHeader.findViewById(R.id.txtStrTitle);
    mTxtTitle
        .setHeight(getWindowManager().getDefaultDisplay().getHeight() / 3);
    mTxtTitle.setMovementMethod(new ScrollingMovementMethod());
    mTxtTitle.setOnTouchListener(new View.OnTouchListener() {
      public boolean onTouch(View paramAnonymousView,
                             MotionEvent paramAnonymousMotionEvent) {
        if (paramAnonymousMotionEvent.getAction() == 1) {
          mCommentlistView.requestDisallowInterceptTouchEvent(false);
          return false;
        }
        mCommentlistView.requestDisallowInterceptTouchEvent(true);
        return false;
      }
    });

    mTxtTitle.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    mSelectBoardID = getIntent().getStringExtra("ID");

    mImageGender = (ImageView) mlistViewHeader
        .findViewById(R.id.imageGender);
    mTxtLikeCNT = (TextView) mlistViewHeader.findViewById(R.id.txtLikeCNT);
    mTxtCommCNT = (TextView) mlistViewHeader.findViewById(R.id.txtCommCNT);
    mTxtRegDate = (TextView) mlistViewHeader.findViewById(R.id.txtRagDate);
    mTxtHateCNT = (TextView) mlistViewHeader.findViewById(R.id.txtHateCNT);
    mImageDetail = (ImageView) mlistViewHeader.findViewById(R.id.imgDetail);


    // Comment ListView
    mCommentlistView = (ListView) findViewById(R.id.commentlistView);
    mCommentlistView.addHeaderView(mlistViewHeader);
    mCommentlistView.setOnItemClickListener(this);
    setAdapter();

    ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
    mFont.setGlobalFont(root);
    mFont.setGlobalFont((ViewGroup) mlistViewHeader);

    // ADmob
    if (Constant.ADMOB) {
      LinearLayout layout = (LinearLayout) findViewById(R.id.mainLayout);
      layout.setVisibility(View.VISIBLE);
      AdView ad = new AdView(this);
      ad.setAdUnitId("ca-app-pub-8578540426651700/9047026070");
      ad.setAdSize(AdSize.BANNER);
      layout.addView(ad);
      AdRequest adRequest = new AdRequest.Builder().build();
      ad.loadAd(adRequest);
    }
  }

  /**
   * @author : oh
   * @MethodName : quick_Clicked
   * @Day : 2014. 10. 13.
   * @Time : 오전 10:08:50
   * @Explanation : QuickMenu EventListener
   */
  private QuickAction.OnActionItemClickListener quick_Clicked = new QuickAction.OnActionItemClickListener() {
    public void onItemClick(QuickAction source, int pos, int actionId) {
      switch (actionId) {
        case 0:
          mProgressBar.setVisibility(View.VISIBLE);
          delCommentSendService();
          //requestCall_CommentDel();
          break;
        case 1:
          requestCall_CommentHateState();
          break;

      }
    }
  };


  // 서비스  바인드
  void doBindService() {
    Log.i(TAG, "doBindService");
    mIsBound = true;
    Log.i(TAG, "Binding");
    bindService(new Intent(this, BoardDetailService.class), mConnection, Context.BIND_AUTO_CREATE);

  }

  // 서비스 언 바인드
  void doUnbindService() {
    Log.i(TAG, "doUnbindService");
    if (mIsBound) {
      try {
        Message msg = Message.obtain(null, BoardDetailService.MSG_UNREGISTER_CLIENT);
        msg.replyTo = mMessenger;
        mService.send(msg);
      } catch (RemoteException e) {
        // There is nothing special we need to do if the service has crashed.
      }
      Log.i(TAG, "un Binding");
      unbindService(mConnection);
      mIsBound = false;
    }
  }


  void newCommentSendService() {
    try {
      Message msg = Message.obtain(null, BoardDetailService.MSG_REGISTER_COMMENT);
      Bundle bundle = new Bundle();
      bundle.putString("COMMENT", mEdtNewPost.getText().toString());
      msg.setData(bundle);
      msg.replyTo = mMessenger;
      mService.send(msg);
    } catch (RemoteException e) {
      // There is nothing special we need to do if the service has crashed.
    }
  }

  void delCommentSendService() {
    try {
      Message msg = Message.obtain(null, BoardDetailService.MSG_DELETE_COMMENT);
      Bundle bundle = new Bundle();
      bundle.putString("COMMENT_ID", mSelectCommentID);
      msg.setData(bundle);
      msg.replyTo = mMessenger;
      mService.send(msg);
    } catch (RemoteException e) {
      // There is nothing special we need to do if the service has crashed.
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (!mNet.networkgetInfo()) {
      switch (Constant.REFRESH_DETAIL_FLAG) {
        case 0:
          if (INFO)
            Log.i(TAG, "DETAIL_REQUEST_NONE Call!");
          mCommentlistView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
          //asyncTask_CommentList_Call();
          mProgressBar.setVisibility(View.VISIBLE);
          doBindService();
          Constant.REFRESH_DETAIL_FLAG = Constant.DETAIL_REQUEST_NONE;
          break;
        case 1:
          if (INFO)
            Log.i(TAG, "DETAIL_REQUEST_FIRST Call!");
          mCommentlistView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
          // asyncTask_CommentList_Call();
          mProgressBar.setVisibility(View.VISIBLE);
          doBindService();
          Constant.REFRESH_DETAIL_FLAG = Constant.DETAIL_REQUEST_NONE;
          break;
        case 2:
          if (INFO)
            Log.i(TAG, "DETAIL_REQUEST_NEW_COMMENT Call!");
          break;
        case 3:
          if (INFO)
            Log.i(TAG, "DETAIL_REQUEST_IMAGE_CLICK Call!");
          mCommentlistView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
          Constant.REFRESH_DETAIL_FLAG = Constant.DETAIL_REQUEST_NONE;
          break;
      }

    } else {
      mTxtCrouton.setText("네트워크를 확인해주세요");
      mCroutonHelper.setCustomView(mCroutonView);
      mCroutonHelper.setDuration(1000);
      mCroutonHelper.show();
    }

  }

  @Override
  protected void onStop() {
    super.onStop();
    doUnbindService();
    // 구글 통계
    GoogleAnalytics.getInstance(this).reportActivityStop(this);
  }


  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    // mCommentListPosition = position - 1;
    // QuickMenu
    ActionItem actionItem1 = new ActionItem(0, getString(R.string.delete),
        null);
    ActionItem actionItem2 = new ActionItem(1, getString(R.string.report),
        null);
    mCommentMenuQuick = new QuickAction(this, QuickAction.HORIZONTAL);

    if (this.mCommentMenuQuick != null) {
      if (mInfoExtra.getAndroidID().equals(mCommentList.get(position - 1).getUniqueID()))
        this.mCommentMenuQuick.addActionItem(actionItem1);
      this.mCommentMenuQuick.addActionItem(actionItem2);
      this.mCommentMenuQuick
          .setOnActionItemClickListener(this.quick_Clicked);
    }
    mCommentMenuQuick.show(view);
    mSelectCommentID = mCommentList.get(position - 1).get_id();
  }

  public void setAdapter() {
    mCommentAdapter = new CommentAdapter(this, mCommentList);

    if (mCommentlistView != null) {
      mCommentlistView.setAdapter(mCommentAdapter);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_detail, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        overridePendingTransition(0, 0);
        finish();

        break;
      case R.id.action_delete:
        if (!mNet.networkgetInfo()) {
          AlertDialog_BoardDel();
        } else {

          mTxtCrouton.setText("네트워크를 확인해주세요");
          mCroutonHelper.setCustomView(mCroutonView);
          mCroutonHelper.setDuration(1000);
          mCroutonHelper.show();
        }
        break;
      case R.id.action_like:
        if (!mNet.networkgetInfo()) {
          requestCall_LikeState();
        } else {

          mTxtCrouton.setText("네트워크를 확인해주세요");
          mCroutonHelper.setCustomView(mCroutonView);
          mCroutonHelper.setDuration(1000);
          mCroutonHelper.show();
        }

        break;
      case R.id.action_report:
        if (!mNet.networkgetInfo()) {
          requestCall_HateState();
        } else {
          mTxtCrouton.setText("네트워크를 확인해주세요");
          mCroutonHelper.setCustomView(mCroutonView);
          mCroutonHelper.setDuration(1000);
          mCroutonHelper.show();
        }
        break;

    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {

    if (mMenuFlag)
      menu.getItem(0).setVisible(false);
    return super.onPrepareOptionsMenu(menu);
  }

  // ListView Header DataSet
  public void headerDataSet() {
    if (INFO)
      Log.i(TAG, "headerDataSet");

    if (DBUG) {
      Log.d(TAG, "ID : " + mHeaderData.get_id());
      Log.d(TAG, "Unique : " + mHeaderData.getUniqueID());
      Log.d(TAG, "Gender : " + mHeaderData.getGender());
      Log.d(TAG, "StrText : " + mHeaderData.getStrText());
      Log.d(TAG, "LIKE : " + mHeaderData.getLikeCNT());
      Log.d(TAG, "COMMENT : " + mHeaderData.getCommCNT());
      Log.d(TAG, "HATE : " + mHeaderData.getHateCNT());
    }

    mTxtLikeCNT.setText(mHeaderData.getLikeCNT());
    mTxtCommCNT.setText(mHeaderData.getCommCNT());
    mTxtRegDate.setText(mHeaderData.getRegDate());
    mTxtHateCNT.setText(mHeaderData.getHateCNT());
    // header Data set


    if (Integer.parseInt(mHeaderData.getHateCNT()) < 5) {

      mTxtTitle.setText(mHeaderData.getStrText());
    } else {
      mTxtTitle.setText("다수의 신고로 블라인드 처리된 글입니다.");
    }

    // Detail Image set
    if (mHeaderData.getGender().equals("0")) {
      if (INFO)
        Log.i(TAG, mHeaderData.getGender());
      mImageGender.setImageDrawable(getResources().getDrawable(R.drawable.icon_man));
    } else {
      if (INFO)
        Log.i(TAG, mHeaderData.getGender());
      mImageGender.setImageDrawable(getResources().getDrawable(R.drawable.icon_woman));
    }
    if (mHeaderData.getImgState().equals("1") && Integer.parseInt(mHeaderData.getHateCNT()) < 5) {
      mImageDetail.setVisibility(View.VISIBLE);
      mImageLoader.get(Constant.IMG_UPLOAD_URL + mHeaderData.get_id() + ".jpg", new FadeInImageListener(mImageDetail, this));
      mImageDetail.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Constant.REFRESH_DETAIL_FLAG = Constant.DETAIL_REQUEST_IMAGE_CLICK;
          Intent intent = new Intent(DetailActivity.this, ImageClickActivity.class);
          intent.putExtra("id", mHeaderData.get_id());
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);
          overridePendingTransition(0, 0);
        }
      });
    }

    if (!mHeaderData.getUniqueID().equals(mInfor.getAndroidID())) {
      mMenuFlag = true;
    } else {
      mMenuFlag = false;
    }

    //메뉴 갱신
    invalidateOptionsMenu();

  }

  /*

  Request method

  */
  // Request Comment Delete
  public void requestCall_CommentDel() {
    if (INFO)
      Log.i(TAG, "requestCall_CommentDel");

    mCDL_CommentDel = new CountDownLatch(1);
    String url = "http://ohy3264.cafe24.com/Anony/api/commentDelete.php";
    StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
      @Override
      public void onResponse(String response) {
        if (DBUG)
          Log.d(TAG, "requestCall_CommentDel - onResponse :: " + response.toString());
        mCDL_CommentDel.countDown();
        Message msg = commentDelHandler.obtainMessage();
        commentDelHandler.sendMessage(msg);

      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError e) {
        Log.d(TAG,
            "onErrorResponse :: " + e.getLocalizedMessage());
        e.printStackTrace();
        mCDL_CommentDel.countDown();
      }
    }) {
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        // TODO Auto-generated method stub
        if (DBUG) {
          Log.d(TAG, "CommentID :: " + mSelectCommentID);
          Log.d(TAG, "BSEQ :: " + mHeaderData.get_id());
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ID", mSelectCommentID);
        params.put("BSEQ", mSelectBoardID);
        return params;
      }
    };
    mRequestQueue.add(request);
  }

  // 신고하기 요청
  public void requestCall_CommentHateState() {
    if (INFO)
      Log.i(TAG, "requestCall_CommentHateState");

    String url = "http://ohy3264.cafe24.com/Anony/api/commentHateState.php";
    StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
      @Override
      public void onResponse(String response) {
        if (DBUG)
          Log.d(TAG, "requestCall_CommentHateState - onResponse :: " + response.toString());
        if (response.trim().equals("InsertSuccess")) {
          mTxtCrouton.setText("신고하였습니다");
          mCroutonHelper.setCustomView(mCroutonView);
          mCroutonHelper.setDuration(1000);
          mCroutonHelper.show();

        } else {
          mTxtCrouton.setText("이미 신고하였습니다");
          mCroutonHelper.setCustomView(mCroutonView);
          mCroutonHelper.setDuration(1000);
          mCroutonHelper.show();
        }
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError e) {
        Log.d(TAG,
            "onErrorResponse :: " + e.getLocalizedMessage());
        e.printStackTrace();
      }
    }) {
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        // TODO Auto-generated method stub
        if (DBUG) {
          Log.d(TAG, "BSEQ :: " + mSelectBoardID);
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ID", mSelectCommentID);
        params.put("ANDROID_ID", mInfor.getAndroidID());
        return params;
      }
    };
    mRequestQueue.add(request);
  }

  // Request HeaderData
  public void requestCall_Header_Info() {
    mCDL_HeaderSet = new CountDownLatch(1);
    if (INFO)
      Log.i(TAG, "requestCall_Header_Info");
    String url = "http://ohy3264.cafe24.com/Anony/api/boardDetail.php";
    StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
      @Override
      public void onResponse(String response) {
        Log.d(TAG, "requestCall_Header_Info - onResponse :: " + response.toString());


        try {
          BoardItem value = new Gson().fromJson(response, BoardItem.class);
          Log.d(TAG, "value - valueid :: " + value.get_id());
          mHeaderData.set_id(value.get_id());
          mHeaderData.setUniqueID(value.getUniqueID());
          mHeaderData.setGender(value.getGender());
          mHeaderData.setStrText(value.getStrText());
          mHeaderData.setHitCNT(value.getHitCNT());
          mHeaderData.setHateCNT(value.getHateCNT());
          mHeaderData.setLikeCNT(value.getLikeCNT());
          mHeaderData.setCommCNT(value.getCommCNT());
          mHeaderData.setImgState(value.getImgState());
          mHeaderData.setRegDate(HYTime_Maximum.formatTimeString(value.getRegDate()));
          headerDataSet();
          mCDL_HeaderSet.countDown();

        } catch (Exception e) {
          Log.d(TAG, "onResponse : Gson Exception");
          mCDL_HeaderSet.countDown();
        }

      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError e) {
        Log.d(TAG,
            "onErrorResponse :: " + e.getLocalizedMessage());
        mCDL_HeaderSet.countDown();
        e.printStackTrace();
      }
    }) {
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        // TODO Auto-generated method stub
        if (DBUG) {
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("MODE", "HeaderIfo");
        params.put("BSEQ", mSelectBoardID);
        return params;
      }
    };
    mRequestQueue.add(request);
  }

  // Request BoardPost Delete
  public void requestCall_BoardPostDel() {

    try {
      HashMap<String, String> params = new HashMap<String, String>();
      params.put("MODE", "BoardPostDelete");
      params.put("BSEQ", mHeaderData.get_id());
      ServerRequestUtils.request(this, "http://ohy3264.cafe24.com", "/Anony/api/boardPostDelete.php", params, IntentDelHandler);
    } catch (Exception e) {
      Log.d(TAG, "Splash:handler2: "+e.getMessage());
    }

    /*if (INFO)
      Log.i(TAG, "requestCall_BoardPostDel");

    mCDL_BoardPostDel = new CountDownLatch(1);
    String url = "http://ohy3264.cafe24.com/Anony/api/boardPostDelete.php";
    StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
      @Override
      public void onResponse(String response) {
        if (DBUG)
          Log.d(TAG, "requestCall_BoardPostDel - onResponse :: " + response.toString());
        mCDL_BoardPostDel.countDown();
        Message msg = IntentDelHandler.obtainMessage();
        IntentDelHandler.sendMessage(msg);

      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError e) {
        Log.d(TAG,
            "onErrorResponse :: " + e.getLocalizedMessage());
        e.printStackTrace();
        mCDL_BoardPostDel.countDown();
      }
    }) {
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        // TODO Auto-generated method stub
        if (DBUG) {
          Log.d(TAG, "BSEQ :: " + mHeaderData.get_id());
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("MODE", "BoardPostDelete");
        params.put("BSEQ", mHeaderData.get_id());
        return params;
      }
    };
    mRequestQueue.add(request);*/
  }





  public void requestCall_LikeState() {
    if (INFO)
      Log.i(TAG, "requestCall_LikeState");

    String url = "http://ohy3264.cafe24.com/Anony/api/UserLikeHateState.php";
    StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
      @Override
      public void onResponse(String response) {
        if (DBUG)
          Log.d(TAG, "requestCall_LikeState - onResponse :: " + response.toString());
        if (response.trim().equals("InsertSuccess")) {
          requestCall_Header_Info();

        } else {
          mTxtCrouton.setText("이미 추천하였습니다");
          mCroutonHelper.setCustomView(mCroutonView);
          mCroutonHelper.setDuration(1000);
          mCroutonHelper.show();
        }


      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError e) {
        Log.d(TAG,
            "onErrorResponse :: " + e.getLocalizedMessage());
        e.printStackTrace();
      }
    }) {
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        // TODO Auto-generated method stub
        if (DBUG) {
          Log.d(TAG, "BSEQ :: " + mHeaderData.get_id());
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("MODE", "LikeState");
        params.put("BSEQ", mHeaderData.get_id());
        params.put("ANDROID_ID", mInfor.getAndroidID());
        return params;
      }
    };
    mRequestQueue.add(request);
  }

  public void requestCall_HateState() {
    if (INFO)
      Log.i(TAG, "requestCall_HateState");

    String url = "http://ohy3264.cafe24.com/Anony/api/UserLikeHateState.php";
    StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
      @Override
      public void onResponse(String response) {
        if (DBUG)
          Log.d(TAG, "requestCall_HateState - onResponse :: " + response.toString());
        if (response.trim().equals("InsertSuccess")) {
          requestCall_Header_Info();

        } else {
          mTxtCrouton.setText("이미 신고한 글입니다");
          mCroutonHelper.setCustomView(mCroutonView);
          mCroutonHelper.setDuration(1000);
          mCroutonHelper.show();
        }


      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError e) {
        Log.d(TAG,
            "onErrorResponse :: " + e.getLocalizedMessage());
        e.printStackTrace();
      }
    }) {
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        // TODO Auto-generated method stub
        if (DBUG) {
          Log.d(TAG, "BSEQ :: " + mHeaderData.get_id());
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("MODE", "HateState");
        params.put("BSEQ", mHeaderData.get_id());
        params.put("ANDROID_ID", mInfor.getAndroidID());
        return params;
      }
    };
    mRequestQueue.add(request);
  }

  // Intent Handler
  final Handler IntentDelHandler = new android.os.Handler() {
    public void handleMessage(Message msg) {
     /* try {
        mCDL_BoardPostDel.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }*/

      Constant.REFRESH_BOARD_FLAG = Constant.REQUEST_DELPOST;
      finish();
    }
  };


  /*
 utill
  */
  // Intent Handler
  final Handler commentDelHandler = new android.os.Handler() {
    public void handleMessage(Message msg) {
      try {
        mCDL_CommentDel.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }


      //asyncTask_CommentList_Call();
      Constant.REFRESH_DETAIL_FLAG = Constant.DETAIL_REQUEST_NONE;
      mCommentlistView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);

    }
  };

  // Board Delete Dialog
  public void AlertDialog_BoardDel() {
    AlertDialog.Builder alert = new AlertDialog.Builder(this);

    // 제목, 메시지, icon, 버튼
    alert.setTitle("삭제");
    alert.setMessage("삭제하시겠습니까?");
    // cancel : false = 단말기 back button으로 취소되지 않음.
    alert.setCancelable(false);
    // yes
    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        //Yes 버튼을 눌렀을때 일어날 일을 서술한다.
        requestCall_BoardPostDel();
      }
    });

    // no
    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        dialog.cancel(); // No 버튼을 눌렀을 경우이며 단순히 창을 닫아 버린다.
      }
    });

    alert.show();

  }
  /**
   * front notice 서버에서 상태값을 가져온다.
   * {"statusCode":0, "versionCode":11, "eventType":"MOBILE"}
   *
   * - statusCode 0:일반 | 1:서버점검(html공지)
   * - versionCode app 버전코드
   * - eventType: WEB:webView 이벤트 | MOBILE:App 친구초대 이벤트
   */
  @SuppressLint("HandlerLeak")
  private Handler introStatusHandler = new Handler(){
    public void handleMessage(Message msg) {
      switch(msg.what){
        case -1:
          alertMessage(getApplicationContext(), getResources().getString(R.string.error_etc));
          break;
        case 0:
          Log.i(TAG, msg.toString());
          if(Constant.SUCCESS.equals(getString(msg.obj, "result"))){

          } else {
            if(toast == null){
              toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_network), Toast.LENGTH_LONG);
            } else {
              toast.cancel();
              toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_network), Toast.LENGTH_LONG);
            }
            toast.show();
          }
          break;
      }
    }
  };
  public static String getString(final Object jsonStr, final String searchWord) {

    Object res = null;

    try {
      JSONObject obj = new JSONObject((String)jsonStr);
      res = obj.get(searchWord);

      if(res instanceof Object)
        return "";

    } catch (Exception e) {
      e.printStackTrace();
    }
    return String.valueOf(res);
  }
  /**
   *
   * 각 Activity 에서 에러 메세지창 호출시
   *
   */
  public void alertMessage(Context context, CharSequence message) {
    AlertDialog alert;
    alert = new AlertDialog.Builder(context)
        .setIconAttribute(android.R.attr.alertDialogIcon)
        .setTitle("알림")
        .setMessage(message)
        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            dialog.dismiss();
          }
        })
        .show();
  }
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_MENU) {
      if (INFO)
        Log.i(TAG, "MENU KEY");
      return false;
    }
    return super.onKeyDown(keyCode, event);
  }


  public boolean dispatchKeyEvent(KeyEvent event) {
    if (event.getAction() == KeyEvent.ACTION_DOWN) {
      switch (event.getKeyCode()) {
        case KeyEvent.KEYCODE_BACK:
          // 단말기의 BACK버튼
          Log.d(TAG, "BACK");
          finish();
          overridePendingTransition(0, 0);
          finish();
          return true;
        case KeyEvent.KEYCODE_MENU:
          // 단말기의 메뉴버튼
          Log.d(TAG, "MENU");

          invalidateOptionsMenu();
          return true;
      }

    }
    return super.dispatchKeyEvent(event);
  }


  final Handler NewCommentHandler = new Handler() {
    public void handleMessage(Message msg) {
      Constant.REFRESH_DETAIL_FLAG = Constant.DETAIL_REQUEST_NEW_COMMENT;
      mBtnFloating.setEnabled(true);
      //asyncTask_CommentList_Call();
      mEdtNewPost.setText("");
      hideKeypad(mEdtNewPost);
      Constant.REFRESH_DETAIL_FLAG = Constant.DETAIL_REQUEST_NONE;
      mCommentlistView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

    }
  };

  /**
   * @author : oh
   * @MethodName : hideKeypad
   * @Day : 2014. 10. 12.
   * @Time : 오후 8:43:05
   * @Explanation : 키패드 숨김
   */
  private void hideKeypad(EditText editText) {
    InputMethodManager manager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
    manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
  }
  @Override
  public void onStart() {
    super.onStart();
    // 구글 통계
    GoogleAnalytics.getInstance(this).reportActivityStart(this);
  };

}
