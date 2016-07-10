package com.hw.oh.temp.talk;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.ThreeBounce;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.hw.oh.adapter.CommentAdapter;
import com.hw.oh.model.BoardItem;
import com.hw.oh.popupWindow.ActionItem;
import com.hw.oh.popupWindow.QuickAction;
import com.hw.oh.service.BoardDetailService;
import com.hw.oh.temp.ApplicationClass;
import com.hw.oh.temp.BaseActivity;
import com.hw.oh.temp.R;
import com.hw.oh.utility.CommonUtil;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.ImageLoader;
import com.hw.oh.utility.NetworkUtil;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.HYTime_Maximum;
import com.hw.oh.utility.OkHttpUtils;
import com.hw.oh.utility.ServerRequestUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by oh on 2015-02-05.
 */
public class TalkDetailActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    //Tag
    private static Toast toast;
    private static final String TAG = "TalkDetailActivity";
    private static final boolean DBUG = true;
    private static final boolean INFO = true;

    private String mBseq = "";
    private String mUniqueID = "";

    //Flag
    private Boolean mMenuFlag = true;
    private String mSelectCommentID;
    private String mSelectBoardID;
    private Boolean mIsBound;

    @BindView(R.id.btnFloating)
    Button mBtnFloating;
    // List View
    @BindView(R.id.commentlistView)
    ListView mCommentlistView;
    @BindView(R.id.edtNewPost)
    EditText mEdtNewPost;

    // List Header View
    private View mlistViewHeader;
    private TextView mTxtTitle;
    private TextView mTxtCommCNT;
    private TextView mTxtLikeCNT;
    private TextView mTxtHateCNT;
    private TextView mTxtRegDate;
    private ImageView mImageGender;
    private ImageView mImageDetail;

    //PopupWindow
    private QuickAction mCommentMenuQuick;

    // Utill
    private ProgressBar mProgressBar;
    private ImageLoader mTalkDetailImageLoader;

    //DataSet
    private ArrayList<BoardItem> mCommentList = new ArrayList<BoardItem>();
    private CommentAdapter mCommentAdapter;

    // Latch
    private CountDownLatch mCDL_HeaderSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        // 구글 통계
        Tracker mTracker = ((ApplicationClass) getApplication()).getDefaultTracker();
        mTracker.setScreenName("게시글 상세보기");
        mTracker.send(new HitBuilders.AppViewBuilder().build());

        //Util
        mTalkDetailImageLoader = new ImageLoader(this);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);

        ThreeBounce threeBounce = new ThreeBounce();
        threeBounce.setColor(getThemeColor(1));
        mProgressBar.setIndeterminateDrawable(threeBounce);

        setToolbar("");

        //댓글쓰기
        mBtnFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtil.isConnect(getApplicationContext())) {
                    if (!mEdtNewPost.getText().toString().isEmpty()) {
                        mBtnFloating.setEnabled(false);
                        mProgressBar.setVisibility(View.VISIBLE);
                        requestNewCommentSend();

                    } else {
                        showCrountonText("아무글이나 써주세요");
                    }
                } else {
                    showCrountonText("네트워크를 확인해주세요");
                }
            }
        });

        // detail listview header
        mlistViewHeader = LayoutInflater.from(getApplicationContext()).inflate(R.layout.header_detail, null, false);
        mTxtTitle = ButterKnife.findById(mlistViewHeader, R.id.txtStrTitle);
        mTxtCommCNT = ButterKnife.findById(mlistViewHeader, R.id.txtCommCNT);
        mTxtLikeCNT = ButterKnife.findById(mlistViewHeader, R.id.txtLikeCNT);
        mTxtHateCNT = ButterKnife.findById(mlistViewHeader, R.id.txtHateCNT);
        mTxtRegDate = ButterKnife.findById(mlistViewHeader, R.id.txtRagDate);
        mImageGender = ButterKnife.findById(mlistViewHeader, R.id.imageGender);
        mImageDetail = ButterKnife.findById(mlistViewHeader, R.id.imgDetail);
        mFont.setGlobalFont((ViewGroup) mlistViewHeader);


        mTxtTitle.setHeight(getWindowManager().getDefaultDisplay().getHeight() / 3);
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

        // Comment ListView
        mCommentlistView.addHeaderView(mlistViewHeader);
        mCommentlistView.setOnItemClickListener(this);
        setAdapter();
        requestCall_Header_Info();

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
                    requestCall_CommentDel();
                    break;
                case 1:
                    mProgressBar.setVisibility(View.VISIBLE);
                    requestCall_HateState("HateState");
                    break;

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        switch (Constant.REFRESH_DETAIL_FLAG) {
            case 0:
                if (INFO)
                    Log.i(TAG, "DETAIL_REQUEST_NONE Call!");
                mCommentlistView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
                mProgressBar.setVisibility(View.VISIBLE);
                requestCall_CommentList();
                Constant.REFRESH_DETAIL_FLAG = Constant.DETAIL_REQUEST_NONE;
                break;
            case 1:
                if (INFO)
                    Log.i(TAG, "DETAIL_REQUEST_FIRST Call!");
                mCommentlistView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
                mProgressBar.setVisibility(View.VISIBLE);
                requestCall_CommentList();
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
    }

    @Override
    protected void onStop() {
        super.onStop();
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
            if (CommonUtil.getAndroidID(this).equals(mCommentList.get(position - 1).getUniqueID()))
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
                if (NetworkUtil.isConnect(this)) {
                    AlertDialog_BoardDel();
                } else {
                    showCrountonText("네트워크를 확인해주세요");
                }
                break;
            case R.id.action_like:
                if (NetworkUtil.isConnect(this)) {
                    requestCall_HateState("LikeState");
                } else {
                    showCrountonText("네트워크를 확인해주세요");
                }

                break;
            case R.id.action_report:
                if (NetworkUtil.isConnect(this)) {
                    requestCall_HateState("HateState");
                } else {
                    showCrountonText("네트워크를 확인해주세요");
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
    public void headerDataSet(BoardItem headerData) {
        if (INFO)
            Log.i(TAG, "headerDataSet");

        if (DBUG) {
            Log.d(TAG, "ID : " + headerData.get_id());
            Log.d(TAG, "Unique : " + headerData.getUniqueID());
            Log.d(TAG, "Gender : " + headerData.getGender());
            Log.d(TAG, "StrText : " + headerData.getStrText());
            Log.d(TAG, "LIKE : " + headerData.getLikeCNT());
            Log.d(TAG, "COMMENT : " + headerData.getCommCNT());
            Log.d(TAG, "HATE : " + headerData.getHateCNT());
        }
        mBseq = headerData.get_id();
        mUniqueID = headerData.getUniqueID();
        mTxtLikeCNT.setText(headerData.getLikeCNT());
        mTxtCommCNT.setText(headerData.getCommCNT());
        mTxtRegDate.setText(headerData.getRegDate());
        mTxtHateCNT.setText(headerData.getHateCNT());
        // header Data set


        if (Integer.parseInt(headerData.getHateCNT()) < 5) {

            mTxtTitle.setText(headerData.getStrText());
        } else {
            mTxtTitle.setText("다수의 신고로 블라인드 처리된 글입니다.");
        }

        // Detail Image set
        if (headerData.getGender().equals("0")) {
            if (INFO)
                Log.i(TAG, headerData.getGender());
            mImageGender.setImageDrawable(getResources().getDrawable(R.drawable.icon_man));
        } else {
            if (INFO)
                Log.i(TAG, headerData.getGender());
            mImageGender.setImageDrawable(getResources().getDrawable(R.drawable.icon_woman));
        }
        if (headerData.getImgState().equals("1") && Integer.parseInt(headerData.getHateCNT()) < 5) {
            mImageDetail.setVisibility(View.VISIBLE);
            mTalkDetailImageLoader.loadImage(Constant.IMG_UPLOAD_URL + headerData.get_id() + ".jpg", mImageDetail);
            mImageDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constant.REFRESH_DETAIL_FLAG = Constant.DETAIL_REQUEST_IMAGE_CLICK;
                    Intent intent = new Intent(TalkDetailActivity.this, ImageClickActivity.class);
                    intent.putExtra("id", mBseq);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });
        }
        if (!headerData.getUniqueID().equals(CommonUtil.getAndroidID(this))) {
            mMenuFlag = true;
        } else {
            mMenuFlag = false;
        }

        //메뉴 갱신
        invalidateOptionsMenu();

    }

    // Request BoardPost Delete
    public void requestCall_BoardPostDel() {

        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("MODE", "BoardPostDelete");
            params.put("BSEQ", mBseq);
            ServerRequestUtils.request(this, "http://ohy3264.cafe24.com", "/Anony/api/boardPostDelete.php", params, IntentDelHandler);
        } catch (Exception e) {
            Log.d(TAG, "Splash:handler2: " + e.getMessage());
        }
    }

    public void requestCall_HateState(final String mode) {
        try {
            RequestBody formBody = new FormBody.Builder()
                    .add("MODE", mode)
                    .add("BSEQ", mBseq)
                    .add("ANDROID_ID", CommonUtil.getAndroidID(this))
                    .build();
            OkHttpUtils.post(this, Constant.SERVER_URL, "/Anony/api/UserLikeHateState.php", formBody, new Callback() {
                Handler mainHandler = new Handler(getMainLooper());

                @Override
                public void onFailure(Call call, IOException e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(TalkDetailActivity.this, TalkDetailActivity.this.getResources().getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i(TAG, "requestCall_HateState : " + response.code());
                    final String results = response.body().string();
                    Log.i(TAG, "requestCall_HateState Results: " + results);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.GONE);
                            if (results.trim().equals("InsertSuccess")) {
                                mProgressBar.setVisibility(View.GONE);
                                requestCall_Header_Info();

                            } else {
                                mProgressBar.setVisibility(View.GONE);
                                if ("HateState".equals(mode)) {
                                    showCrountonText("이미 신고한 글입니다");
                                } else if ("LikeState".equals(mode)) {
                                    showCrountonText("이미 추천하였습니다");
                                }
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(TalkDetailActivity.this, TalkDetailActivity.this.getResources().getString(R.string.error_common), Toast.LENGTH_SHORT).show();
        }
    }


    // Intent Handler
    final Handler IntentDelHandler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            Constant.REFRESH_BOARD_FLAG = Constant.REQUEST_DELPOST;
            finish();
        }
    };


    /*
   utill
    */
    // Intent Handler

    // Board Delete Dialog
    public void AlertDialog_BoardDel() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("삭제");
        alert.setMessage("삭제하시겠습니까?");
        alert.setCancelable(false);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Yes 버튼을 눌렀을때 일어날 일을 서술한다.
                requestCall_BoardPostDel();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel(); // No 버튼을 눌렀을 경우이며 단순히 창을 닫아 버린다.
            }
        });

        alert.show();

    }

    public static String getString(final Object jsonStr, final String searchWord) {

        Object res = null;

        try {
            JSONObject obj = new JSONObject((String) jsonStr);
            res = obj.get(searchWord);

            if (res instanceof Object)
                return "";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(res);
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
    }


    public void requestCall_Header_Info() {
        try {
            RequestBody formBody = new FormBody.Builder()
                    .add("MODE", "HeaderIfo")
                    .add("BSEQ", mSelectBoardID)
                    .build();
            OkHttpUtils.post(this, Constant.SERVER_URL, "/Anony/api/boardDetail.php", formBody, new Callback() {
                Handler mainHandler = new Handler(getMainLooper());

                @Override
                public void onFailure(Call call, IOException e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(TalkDetailActivity.this, TalkDetailActivity.this.getResources().getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i(TAG, "requestCall_Header_Info : " + response.code());
                    final String results = response.body().string();
                    Log.i(TAG, "requestCall_Header_Info Results: " + results);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.GONE);
                            BoardItem value = new Gson().fromJson(results, BoardItem.class);
                            headerDataSet(value);
                        }
                    });
                }
            });
        } catch (Exception e) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(TalkDetailActivity.this, TalkDetailActivity.this.getResources().getString(R.string.error_common), Toast.LENGTH_SHORT).show();
        }
    }

    public void requestCall_CommentDel() {
        if (INFO)
            Log.i(TAG, "requestCall_CommentDel");
        mCommentList.clear();
        RequestBody formBody = new FormBody.Builder()
                .add("ID", mSelectCommentID)
                .add("BSEQ", mSelectBoardID)
                .build();
        try {
            OkHttpUtils.post(this, Constant.SERVER_URL, "/Anony/api/commentDeleteTest.php", formBody, new Callback() {
                Handler mainHandler = new Handler();

                @Override
                public void onFailure(Call call, IOException e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(TalkDetailActivity.this, TalkDetailActivity.this.getResources().getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.i(TAG, "onFailure : " + e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String results = response.body().string();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.GONE);

                            java.lang.reflect.Type type = new TypeToken<List<BoardItem>>() {
                            }.getType();

                            ArrayList<BoardItem> value = new Gson()
                                    .fromJson(results, type);
                            if (value != null) {
                                for (BoardItem m : value) {
                                    BoardItem t = new BoardItem();

                                    t.set_id(m.get_id());
                                    t.setStrText(m.getStrText());
                                    t.setGender(m.getGender());
                                    t.setHateCNT(m.getHateCNT());
                                    t.setRegDate(HYTime_Maximum.formatTimeString(m.getRegDate()));
                                    t.setUniqueID(m.getUniqueID());
                                    mCommentList.add(t);
                                }
                            }
                            mCommentAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
        } catch (Exception e) {
            mProgressBar.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }


    // Request CommentData
    public void requestCall_CommentList() {
        if (INFO)
            Log.i(TAG, "requestCall_CommentList");
        mCommentList.clear();
        RequestBody formBody = new FormBody.Builder()
                .add("MODE", "CommentList")
                .add("BSEQ", mSelectBoardID)
                .build();
        try {
            OkHttpUtils.post(this, Constant.SERVER_URL, "/Anony/api/commentListAll.php", formBody, new Callback() {
                Handler mainHandler = new Handler();

                @Override
                public void onFailure(Call call, IOException e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(TalkDetailActivity.this, TalkDetailActivity.this.getResources().getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.i(TAG, "onFailure : " + e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String results = response.body().string();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.GONE);
                            Log.i(TAG, "requestCall_CommentList : " + results);

                            java.lang.reflect.Type type = new TypeToken<List<BoardItem>>() {
                            }.getType();

                            ArrayList<BoardItem> value = new Gson()
                                    .fromJson(results, type);
                            if (value != null) {
                                for (BoardItem m : value) {
                                    BoardItem t = new BoardItem();

                                    t.set_id(m.get_id());
                                    t.setStrText(m.getStrText());
                                    t.setGender(m.getGender());
                                    t.setHateCNT(m.getHateCNT());
                                    t.setRegDate(HYTime_Maximum.formatTimeString(m.getRegDate()));
                                    t.setUniqueID(m.getUniqueID());

                                    mCommentList.add(t);
                                }
                                mCommentAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            mProgressBar.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    // Request CommentData
    public void requestNewCommentSend() {
        if (INFO)
            Log.i(TAG, "requestCall_CommentList");
        mCommentList.clear();
        RequestBody formBody = new FormBody.Builder()
                .add("MODE", "NewPostSend")
                .add("BSEQ", mBseq)
                .add("B_ANDROID_ID", mUniqueID)
                .add("ANDROID_ID", CommonUtil.getAndroidID(getApplicationContext()))
                .add("GENDER", mPref.getValue(mPref.KEY_GENDER, "0"))
                .add("NEW_COMMENT", mEdtNewPost.getText().toString())
                .build();
        try {
            OkHttpUtils.post(this, Constant.SERVER_URL, "/Anony/api/newCommentSaveTest.php", formBody, new Callback() {
                Handler mainHandler = new Handler();

                @Override
                public void onFailure(Call call, IOException e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.GONE);
                            mBtnFloating.setEnabled(true);
                            Toast.makeText(TalkDetailActivity.this, TalkDetailActivity.this.getResources().getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.i(TAG, "onFailure : " + e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String results = response.body().string();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.GONE);
                            mBtnFloating.setEnabled(true);
                            Log.i(TAG, "requestCall_CommentList : " + results);

                            java.lang.reflect.Type type = new TypeToken<List<BoardItem>>() {
                            }.getType();

                            ArrayList<BoardItem> value = new Gson()
                                    .fromJson(results, type);

                            if (value != null) {
                                for (BoardItem m : value) {
                                    BoardItem t = new BoardItem();

                                    t.set_id(m.get_id());
                                    t.setStrText(m.getStrText());
                                    t.setGender(m.getGender());
                                    t.setHateCNT(m.getHateCNT());
                                    t.setRegDate(HYTime_Maximum.formatTimeString(m.getRegDate()));
                                    t.setUniqueID(m.getUniqueID());
                                    mCommentList.add(t);
                                }
                                mCommentAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            mProgressBar.setVisibility(View.GONE);
            mBtnFloating.setEnabled(true);
            e.printStackTrace();
        }
    }

}
