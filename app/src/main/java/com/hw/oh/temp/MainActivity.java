package com.hw.oh.temp;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.hw.oh.adapter.DrawerMenuAdapter;
import com.hw.oh.billing.IabHelper;
import com.hw.oh.billing.IabResult;
import com.hw.oh.billing.Purchase;
import com.hw.oh.fragment.Fragment_Alba;
import com.hw.oh.model.SampleItem;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYBackPressCloserHandler;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;

import java.util.ArrayList;


public class MainActivity extends BaseActivity implements View.OnClickListener{
    public static final String TAG = "MainActivity";
    public static final boolean DBUG = true;
    public static final boolean INFO = true;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private DrawerMenuAdapter mDrawerAdapter;
    private ActionBarDrawerToggle mDToggle;

    //inapp payment
    private IInAppBillingService mService;
    private IabHelper mHelper;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    //Fragment
    private Fragment_Alba mFragment_Alba;
    private FragmentManager mFragManager;

    //Utill
    private HYBackPressCloserHandler mBackPressCloseHandler; // 뒤로가기 종료 핸들러
    private HYFont mFont;
    private HYPreference mPref;

    //DataSet
    private ArrayList<SampleItem> mMenuArrayList = new ArrayList<SampleItem>();

    //Frag
    private Boolean mDToggleisShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 구글 통계
        Tracker mTracker = ((ApplicationClass) getApplication()).getDefaultTracker();
        mTracker.setScreenName("알바등록화면_(MainActivity)");
        mTracker.send(new HitBuilders.AppViewBuilder().build());

        mBackPressCloseHandler = new HYBackPressCloserHandler(this);
        mFont = new HYFont(this);
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        mFont.setGlobalFont(root);
        mPref = new HYPreference(this);
        //ActionBar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("알바매니저");
        setSupportActionBar(mToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mFont.setGlobalFont((ViewGroup) mToolbar);

        // In APP Payment
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        // 구글에서 발급받은 바이너리키(공개키)를 입력해줍니다
        String base64EncodedPublicKey = getResources().getString(R.string.base64EncodedPublicKey);
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true, "IAB");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (INFO)
                    Log.i(TAG, "인앱구매 준비 : " + result.isSuccess());
                if (!result.isSuccess()) {
                    Toast.makeText(getApplicationContext(), "구글 결재 초기화 오류", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mDToggleisShow = true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mDToggleisShow = false;
            }
        };
        mDrawerLayout.setDrawerListener(mDToggle);
        //DrawerList
        menuDataSet();
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        mDrawerAdapter = new DrawerMenuAdapter(this, mMenuArrayList);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


        //Fragment Container
        mFragment_Alba = new Fragment_Alba();
        mFragManager = getFragmentManager();
        mFragManager.beginTransaction().replace(R.id.container, mFragment_Alba).commit();

        //VersionChek
        verCheck();

        // ADmob
        if (Constant.ADMOB) {
            //if (true) {
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
    public void verCheck(){
        String version;
        try {
            PackageInfo i = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = i.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "";
        }
        mPref.put(mPref.KEY_CHECK_VERSION, version);
        String check_version = mPref.getValue(mPref.KEY_CHECK_VERSION, "");
        String check_status = mPref.getValue(mPref.KEY_CHECK_VERSION_STATUS, "");

        //check_version와  check_status를 비교해서 값이 다르면 공지를 띄움
        if (!check_version.equals(check_status)) {
            AlertDialog alert = new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon)
                    .setTitle(R.string.app_notice)
                    .setMessage(R.string.app_notice_update)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("다시보지않기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String version;
                            try {
                                PackageInfo i = getPackageManager().getPackageInfo(getPackageName(), 0);
                                version = i.versionName;
                            } catch (PackageManager.NameNotFoundException e) {
                                version = "";
                            }
                            mPref.put(mPref.KEY_CHECK_VERSION_STATUS, version);
                            dialog.cancel();
                        }
                    }).show();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void menuDataSet() {
        SampleItem item1 = new SampleItem("급여저장", R.drawable.menu_icon_calendar);
        //SampleItem item2 = new SampleItem("급여계산", R.drawable.menu_icon_calculation);
        SampleItem item2 = new SampleItem("알바톡", R.drawable.menu_icon_talk);
        // SampleItem item4 = new SampleItem("알바정보", R.drawable.menu_icon_info);
        SampleItem item3 = new SampleItem("모의계산", R.drawable.menu_icon_calculation);
        SampleItem item4 = new SampleItem("설정", R.drawable.menu_icon_setting);
        SampleItem item5 = new SampleItem("광고제거", R.drawable.menu_icon_payment);
        SampleItem item6 = new SampleItem("문의하기", R.drawable.menu_icon_mail);
        SampleItem item7 = new SampleItem("APP 평가하기", R.drawable.menu_icon_market);
        mMenuArrayList.add(item1);
        mMenuArrayList.add(item2);
        // mMenuArrayList.add(item4);
        mMenuArrayList.add(item3);
        mMenuArrayList.add(item4);
        mMenuArrayList.add(item5);
        mMenuArrayList.add(item6);
        mMenuArrayList.add(item7);
    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position,
                                long id) {

            closeDrawer();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (position) {
                        case 0:
                            mFragment_Alba = new Fragment_Alba();
                            mFragManager = getFragmentManager();
                            mFragManager.beginTransaction().replace(R.id.container, mFragment_Alba).commit();
                            break;

                       /* case 1:
                            Intent intent_Calculation = new Intent(getApplicationContext(), CalculationActivity.class);
                            intent_Calculation.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent_Calculation);
                            overridePendingTransition(0, 0);
                            break;*/

                        case 1:
                            Intent intent_Talk = new Intent(getApplicationContext(), TalkActivity.class);
                            intent_Talk.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent_Talk);
                            overridePendingTransition(0, 0);
                            break;
                    /*    case 2:
                            Intent intent_AlbaInfo = new Intent(getApplicationContext(), AlbaInfoActivity.class);
                            intent_AlbaInfo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent_AlbaInfo);
                            overridePendingTransition(0, 0);
                            break;*/
                        case 2:

                            Intent intent_Duty = new Intent(getApplicationContext(), DutyActivity.class);
                            intent_Duty.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent_Duty);
                            overridePendingTransition(0, 0);
                            break;
                        case 3:
                            Intent intent_Setting = new Intent(getApplicationContext(), SettingActivity.class);
                            intent_Setting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent_Setting);
                            overridePendingTransition(0, 0);
                            break;
                        case 4:
                        /*    Intent intent_payment = new Intent(getApplicationContext(), SettingActivity.class);
                            intent_payment.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent_payment);
                            overridePendingTransition(0, 0);*/
                            InAppBuyItem(Constant.ADDMOB_REMOVE);
                            break;
                        case 5:

                            Intent emailSend = new Intent("android.intent.action.SEND");
                            emailSend.setType("plain/text");
                            emailSend.putExtra("android.intent.extra.EMAIL",
                                    new String[]{"ohy3264@gmail.com"});
                            startActivity(emailSend);
                            break;
                        case 6:
                            Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                            marketLaunch
                                    .setData(Uri
                                            .parse("https://market.android.com/details?id=com.hw.oh.temp"));
                            startActivity(marketLaunch);
                            break;
                    }
                }
            }, 250);
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    // 단말기의 BACK버튼
                    if (mDToggleisShow) {
                        //mDrawerLayout.closeDrawer(mDrawerList);
                        closeDrawer();
                    } else {
                        mBackPressCloseHandler.onBackPressed();
                    }
                    return true;
                case KeyEvent.KEYCODE_MENU:
                    // 단말기의 메뉴버튼
                    if (mDToggleisShow) {
                        //mDrawerLayout.closeDrawer(mDrawerList);
                        closeDrawer();
                    } else {
                        mDrawerLayout.openDrawer(mDrawerList);
                    }
                    return true;
            }

        }
        return super.dispatchKeyEvent(event);
    }

    protected void closeDrawer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                });
            }
        }).start();
    }

    // 구매
    public void InAppBuyItem(String id_item) {
        if (INFO)
            Log.i(TAG, "InAppBuyItem");
       mHelper.launchPurchaseFlow(this, id_item, "subs", 1001, mPurchaseFinishedListener, "");
        // mHelper.launchSubscriptionPurchaseFlow(this, id_item, 1001, mPurchaseFinishedListener, "");
        if (DBUG)
            Log.d(TAG, "InAppBuyItem_U " + id_item);
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (DBUG)
                Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            if (result.isFailure()) {
                // 구매 실패시 수행할 이벤트 작성
                Log.d("In-App-Purchase", "Error purchasing: " + result); // 로그출력
                Toast.makeText(getApplicationContext(), "purchase failed",
                        Toast.LENGTH_SHORT).show(); // 구매실패 메시지 출력
                return; // 리턴
            } else if (purchase.getSku().equals(Constant.ADDMOB_REMOVE)) {
                // SKU_PREMIUM 아이템 구매 성공시 수행할 이벤트 코드 작성
                Toast.makeText(getApplicationContext(), "결제 성공! 앱을 재실행 해주세요",
                        Toast.LENGTH_SHORT).show();
                finish();
                System.exit(0);
                if (DBUG)
                    Log.d("In-App-Purchase", "Purchase SKU_PREMIUM: " + result); // 로그출력
            }else{
                if (DBUG)
                    Log.d("In-App-Purchase", "Purchase SKU_PREMIUM: " + result); // 로그출력
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mServiceConn != null) {
            try {
                unbindService(mServiceConn);
            }catch (Exception e){
                Log.i(TAG, e.toString());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "requestCode : " + requestCode + ", resultCode : " + resultCode + ", data : " + data);
        if (mHelper == null) return; //iabHelper가 null값인경우 리턴
        if (requestCode == 1001) {
            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) { //iabHelper가 데이터를 핸들링하도록 데이터 전달
                super.onActivityResult(requestCode, resultCode, data);
            } else {
                Log.d(TAG, "onActivityResult handled by IABUtil.");
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // 구글 통계
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    };

    @Override
    public void onStop() {
        super.onStop();
        // 구글 통계
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    };
}
