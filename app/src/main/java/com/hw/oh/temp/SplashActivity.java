package com.hw.oh.temp;

import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hw.oh.billing.IabHelper;
import com.hw.oh.billing.IabResult;
import com.hw.oh.billing.Inventory;
import com.hw.oh.billing.Purchase;
import com.hw.oh.sqlite.DBManager;
import com.hw.oh.temp.etc.GenderActivity;
import com.hw.oh.temp.etc.PassActivity;
import com.hw.oh.utility.CommonUtil;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.JSONParserUtil;
import com.hw.oh.utility.NetworkUtil;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.OkHttpUtils;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * SplashActivity
 * 로딩화면
 *
 * @author hwoh
 */

public class SplashActivity extends BaseActivity {
    public static final String TAG = "SplashActivity";
    public static final boolean DBUG = true;
    public static final boolean INFO = true;

    private IabHelper mHelper;

    private HYPreference mPref;
    private ImageView mIntroDot;
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Log.i("hwoh", CommonUtil.getAndroidID(this));
        //util
        mPref = new HYPreference(this);

        //InApp
        String base64EncodedPublicKey = getResources().getString(R.string.base64EncodedPublicKey);
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true, "IAB");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (INFO)
                    Log.i(TAG, "인앱구매 준비 : " + result.isSuccess());
                if (!result.isSuccess()) {
                    Toast.makeText(getApplicationContext(), "구글 결재 초기화 오류", Toast.LENGTH_SHORT).show();
                } else {
                    if (INFO)
                        Log.i(TAG, "구매정보 조회 : " + result.isSuccess());
                    mHelper.queryInventoryAsync(mQueryFinishedListener);
                }
            }
        });

        // DB세팅
        dbinit();

        // 인트로 애니메이션
        // introAnimation();

        // 구글 통계
        Tracker mTracker = ((ApplicationClass) getApplication()).getDefaultTracker();
        mTracker.setScreenName("스플래쉬");
        mTracker.send(new HitBuilders.AppViewBuilder().build());

        //시작분기
        init();


        ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.progress);
        ThreeBounce threeBounce = new ThreeBounce();
        threeBounce.setColor(getResources().getColor(R.color.white));
        mProgressBar.setIndeterminateDrawable(threeBounce);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void init() {
        Log.i(TAG, "init");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "네트워크 연결안됨");
                if (NetworkUtil.isConnect(getApplicationContext())) {
                    Log.i(TAG, "네트워크 연결됨");
                    //requestCallRest_Guide();
                    requestUniqueID();
                } else {

                    if (mPref.getValue(mPref.KEY_PASS_STATE, false)) {
                        Log.i(TAG, "PassWord");
                        Intent intent_pass = new Intent(SplashActivity.this, PassActivity.class);
                        intent_pass.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent_pass);
                        overridePendingTransition(0, 0);
                        finish();
                    } else {
                        Log.i(TAG, "IntentMain");
                        Intent intent_main = new Intent(SplashActivity.this, MainActivity.class);
                        intent_main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent_main);
                        overridePendingTransition(0, 0);
                        finish();
                    }
                }
            }
        }, 1500);
    }

    private void introAnimation() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mIntroDot = (ImageView) findViewById(R.id.intro_dot);

                AnimationDrawable animation = new AnimationDrawable();
                animation.addFrame(getResources().getDrawable(R.drawable.intro_img1), 700); //ms(밀리세컨드 1/1000초)
                animation.addFrame(getResources().getDrawable(R.drawable.intro_img2), 700);
                animation.addFrame(getResources().getDrawable(R.drawable.intro_img3), 700);
                animation.addFrame(getResources().getDrawable(R.drawable.intro_img1), 700);

                animation.setOneShot(false);
                mIntroDot.setBackgroundDrawable(animation);
                animation.start();
            }
        });

    }


    /**
     * 초기 db 세팅
     */
    public void dbinit() {
        if (INFO)
            Log.i(TAG, "dbinit()");
        DBManager dbManager = new DBManager(this);
        dbManager.getWritableDatabase();
    }

    /**
     * 인앱결제 상태 체크
     */
    private IabHelper.QueryInventoryFinishedListener
            mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                // handle error
                if (DBUG)
                    Log.d(TAG, "구매 이력조회 실패: " + result);
                return;
            }
            Purchase premiumPurchase = inventory.getPurchase(Constant.ADDMOB_REMOVE);
            if (premiumPurchase != null) {
                if (INFO)
                    Log.i(TAG, "구매 이력이 있음 ");
                if (premiumPurchase.getPurchaseState() == 0) {
                    if (DBUG)
                        Log.i(TAG, "구매");
                    Constant.ADMOB = false;
                } else if (premiumPurchase.getPurchaseState() == 1) {
                    if (INFO)
                        Log.i(TAG, "취소");
                } else if (premiumPurchase.getPurchaseState() == 2) {
                    if (INFO)
                        Log.i(TAG, "환불");
                }
            }
            if (DBUG)
                Log.d("myLog", "Query inventory was successful.");
        }
    };

    /**
     * 로그인 체크
     */
    public void requestUniqueID() {
        if (INFO)
            Log.i(TAG, "requestUserInfoSend()");
        RequestBody formBody = new FormBody.Builder()
                .add("MODE", "LoginCheck")
                .add("ANDROID_ID", CommonUtil.getAndroidID(this))
                .build();
        try {
            OkHttpUtils.post(getApplicationContext(), Constant.SERVER_URL, "/Anony/api/memberCheck.php", formBody, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "requestUserInfoSend - onFailure :: " + e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i("Response", " " + response.code());
                    final String results = response.body().string();
                    Log.i("OkHTTP Results: ", results);

                    Message msg = IntentHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("IntentFlag", JSONParserUtil.getString(results, "IntentFlag"));
                    bundle.putString("Gender", JSONParserUtil.getString(results, "Gender"));
                    msg.setData(bundle);
                    IntentHandler.sendMessage(msg);
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "requestUserInfoSend - exception :: " + e.toString());

        }
    }

    /**
     * 로딩화면에서 이동분기 처리
     */
    final Handler IntentHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.getData().getString("IntentFlag")) {
                case "IntentGender":
                    Log.i(TAG, "IntentGender");
                    Intent intent_gender = new Intent(SplashActivity.this, GenderActivity.class);
                    intent_gender.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent_gender);
                    overridePendingTransition(0, 0);
                    finish();

                    break;
                case "IntentMain":
                    if (mPref.getValue(mPref.KEY_PASS_STATE, false)) {
                        Log.i(TAG, "PassWord");
                        mPref.put(mPref.KEY_GENDER, msg.getData().getString("Gender"));
                        Intent intent_pass = new Intent(SplashActivity.this, PassActivity.class);
                        intent_pass.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent_pass);
                        overridePendingTransition(0, 0);
                        finish();
                    } else {
                        Log.i(TAG, "IntentMain");
                        mPref.put(mPref.KEY_GENDER, msg.getData().getString("Gender"));
                        Intent intent_main = new Intent(SplashActivity.this, MainActivity.class);
                        intent_main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent_main);
                        overridePendingTransition(0, 0);
                        finish();
                    }
                    break;
                default:
                    if (mPref.getValue(mPref.KEY_PASS_STATE, false)) {
                        Log.i(TAG, "PassWord");
                        mPref.put(mPref.KEY_GENDER, msg.getData().getString("Gender"));
                        Intent intent_pass = new Intent(SplashActivity.this, PassActivity.class);
                        intent_pass.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent_pass);
                        overridePendingTransition(0, 0);
                        finish();
                    } else {
                        Log.i(TAG, "IntentMain");
                        mPref.put(mPref.KEY_GENDER, msg.getData().getString("Gender"));
                        Intent intent_main = new Intent(SplashActivity.this, MainActivity.class);
                        intent_main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent_main);
                        overridePendingTransition(0, 0);
                        finish();
                    }
                    break;
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Intro Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.hw.oh.temp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);

        // 구글 통계
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Intro Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.hw.oh.temp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        // 구글 통계
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        client.disconnect();
    }
}
