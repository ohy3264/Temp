package com.hw.oh.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hw.oh.dialog.CalculSetDialog;
import com.hw.oh.dialog.DigStyleDialog;
import com.hw.oh.dialog.DigThemeStyleDialog;
import com.hw.oh.dialog.DutySetDialog;
import com.hw.oh.dialog.FontSelectDialog;
import com.hw.oh.dialog.InsuranceSetDialog;
import com.hw.oh.dialog.PassSetDialog;
import com.hw.oh.model.KmaCodeItem;
import com.hw.oh.network.RestClient;
import com.hw.oh.sqlite.DBConstant;
import com.hw.oh.sqlite.KmDBManager;
import com.hw.oh.temp.ApplicationClass;
import com.hw.oh.temp.LocationSelectActivity;
import com.hw.oh.temp.R;
import com.hw.oh.utility.CommonUtil;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.GpsInfo;
import com.hw.oh.utility.HYDatabaseBackup;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.NetworkUtil;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.InfoExtra;
import com.hw.oh.utility.OkHttpUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CountDownLatch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Fragment_Setting
 * 설정화면
 *
 * @author hwoh
 */
public class Fragment_Setting extends BaseFragment {
    public static final String TAG = "Fragment_Setting";
    public static final boolean DBUG = true;
    public static final boolean INFO = true;

    @BindView(R.id.txtBackUpCode)
    TextView mTxtBackupCode;
    @BindView(R.id.txtCurrentLocation)
    TextView mTxtCurrentLocation;
    @BindView(R.id.txtSelectFont)
    TextView mTxtSelectFont;
    @BindView(R.id.txtStyleName)
    TextView mTxtStyleName;
    @BindView(R.id.txtCal)
    TextView mTxtCal;
    @BindView(R.id.txtInsurance)
    TextView mTxtInsurance;
    @BindView(R.id.txt_duty)
    TextView mTxtDuty;
    @BindView(R.id.txtSelectTheme)
    TextView mTxtTheme;
    @BindView(R.id.txtSelectPass)
    TextView mTxtSelectPass;
    @BindView(R.id.edtBackUpCode)
    EditText mEditBackupCode;


    private ArrayList<KmaCodeItem> mKmaItemList = new ArrayList<KmaCodeItem>();
    //Flag
    private String mTextInsertFlag, mBackupCodeString;

    //Util
    private HYFont mFont;
    private ProgressDialog mLoadingDialog;
    private HYPreference mPref;
    private KmDBManager kmDBManager;
    private CountDownLatch CDLGpsLatch;
    private CountDownLatch CDLKmaDistanceLatch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        //ButterKnife
        ButterKnife.bind(Fragment_Setting.this, rootView);
        // 구글 통계
        Tracker mTracker = ((ApplicationClass) getActivity().getApplication()).getDefaultTracker();
        mTracker.setScreenName("설정화면");
        mTracker.send(new HitBuilders.AppViewBuilder().build());
        //Utill
        mFont = new HYFont(getActivity());
        mFont.setGlobalFont((ViewGroup) rootView);
        mPref = new HYPreference(getActivity());
        kmDBManager = new KmDBManager(getActivity());
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        inits();
    }

    @Override
    public void onStart() {
        super.onStart();
        // 구글 통계
        GoogleAnalytics.getInstance(getActivity()).reportActivityStart(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        // 구글 통계
        GoogleAnalytics.getInstance(getActivity()).reportActivityStop(getActivity());
    }

    /**
     * 초기설정 값을 통한 세팅
     */
    public void inits() {
        //백업코드
        mBackupCodeString = mPref.getValue(mPref.KEY_BACKUP_CODE, "");
        if (!CommonUtil.isNull(mBackupCodeString))
            mTxtBackupCode.setText("BackupCode : " + mBackupCodeString);
        //위치정보
        mTxtCurrentLocation.setText(mPref.getValue(mPref.KEY_WEATHER_LOCATION, ""));
        //폰트정보
        switch (mPref.getValue(mPref.KEY_FONT, 1)) {
            case 0:
                mTxtSelectFont.setText("기본");
                break;
            case 1:
                mTxtSelectFont.setText("상상체");
                break;
            case 2:
                mTxtSelectFont.setText("고도체");
                break;
        }

        //패스상태
        onFinishPassDialog();

        // 캘리더 기간선택 상태
        onFinishCalDialog();

        //날짜설정스타일일
        onFinishStyleDialog();

        //4대보험 적용여부
        onFinishInsuranceDialog();

        //세금적용여부
        onFinishDutyDialog();

        //테마설정여부
        onFinishThemeDialog();
    }


    @OnClick({R.id.linSave, R.id.linLoad, R.id.linFontChange, R.id.linPassSet,
            R.id.lin_weatherLocation, R.id.linDialogStyle, R.id.linThemeSet,
            R.id.linCal, R.id.linInsurance, R.id.linDuty})
    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.linSave:
                if (NetworkUtil.isConnect(getActivity())) {
                    requetBackupDBSend();
                } else {
                    showCrountToast("네트워크를 확인해 주세요");
                }
                break;

            case R.id.linLoad:
                if (NetworkUtil.isConnect(getActivity())) {
                    if (!mEditBackupCode.getText().toString().isEmpty()) {
                        requestBackupFileName();
                    } else {
                        showCrountToast("코드를 입력하세요");
                    }
                } else {
                    showCrountToast("네트워크를 확인해주세요");
                }
                break;

            case R.id.linFontChange:
                FontSelectDialog fontSelectDialog = new FontSelectDialog();
                fontSelectDialog.setTargetFragment(this, 0);
                fontSelectDialog.setCancelable(false);
                fontSelectDialog.show(getFragmentManager(), null);
                break;

            case R.id.linPassSet:
                PassSetDialog passSetDialog = new PassSetDialog();
                passSetDialog.setTargetFragment(this, 0);
                passSetDialog.setCancelable(false);
                passSetDialog.show(getFragmentManager(), null);
                break;

            case R.id.lin_weatherLocation:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("위치추가 방식").setIcon(R.drawable.icon_location).setItems(R.array.location_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                myLocation();
                                asyncTask_Location_Call();
                                break;

                            case 1:
                                Intent intent_Location = new Intent(getActivity(), LocationSelectActivity.class);
                                intent_Location.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent_Location);
                                getActivity().overridePendingTransition(0, 0);
                                break;
                        }
                    }
                });
                builder.create().show();
                break;

            case R.id.linDialogStyle:

                DigStyleDialog styleDialog = new DigStyleDialog();
                styleDialog.setTargetFragment(this, 0);
                styleDialog.setCancelable(false);
                styleDialog.show(getFragmentManager(), null);
                break;

            case R.id.linThemeSet:
                DigThemeStyleDialog themeDialog = new DigThemeStyleDialog();
                themeDialog.setTargetFragment(this, 0);
                themeDialog.setCancelable(false);
                themeDialog.show(getFragmentManager(), null);
                break;

            case R.id.linCal:
                CalculSetDialog calSetDialog = new CalculSetDialog();
                calSetDialog.setTargetFragment(this, 0);
                calSetDialog.setCancelable(false);
                calSetDialog.show(getFragmentManager(), null);
                break;

            case R.id.linInsurance:
                InsuranceSetDialog insuranceSetDialog = new InsuranceSetDialog();
                insuranceSetDialog.setTargetFragment(this, 0);
                insuranceSetDialog.setCancelable(false);
                insuranceSetDialog.show(getFragmentManager(), null);
                break;

            case R.id.linDuty:
                DutySetDialog dutySetDialog = new DutySetDialog();
                dutySetDialog.setTargetFragment(this, 0);
                dutySetDialog.setCancelable(false);
                dutySetDialog.show(getFragmentManager(), null);
                break;

        }
    }

    /**
     * 코드값을 통해 백업파일을 내려받음
     */
    public void requestBackupFileName() {
        mLoadingDialog = showLoadingDialog(getActivity(), true);
        mLoadingDialog.show();
        if (INFO)
            Log.i(TAG, "requestBackupFileName()");
        RequestBody formBody = new FormBody.Builder()
                .add("BACKUP_CODE", mEditBackupCode.getText().toString())
                .build();
        try {
            OkHttpUtils.post(getActivity(), Constant.SERVER_URL, "/Anony/api/backupDB_FileName.php", formBody, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i(TAG, "onFailure : " + e.toString());
                    mLoadingDialog.dismiss();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i("Response", " " + response.code());
                    final String results = response.body().string();
                    Log.i("OkHTTP Results: ", results);
                    Log.d(TAG, "value - valueid :: " + results);
                    mLoadingDialog.dismiss();
                    if (!CommonUtil.isNull(results)) {
                        asyncTask_FileDown_Call(results);
                    } else {
                        showCrountToast("코드를 다시 확인해주세요");
                    }
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "requestBackupFileName - exception :: " + e.toString());
            mLoadingDialog.dismiss();
        }

    }

    /**
     * Backup DB 서버로 전송
     */
    public void requetBackupDBSend() {
        mLoadingDialog = showLoadingDialog(getActivity(), true);
        mLoadingDialog.show();
        RequestParams params = new RequestParams();
        params.put("ANDROID_ID", CommonUtil.getAndroidID(getActivity())); //전송정보 : AndroidID
        try {
            File myFile = new File(getActivity().getDatabasePath(DBConstant.DATABASE_NAME).toString()); // DB파일 객체화
            if (!myFile.exists()) {
                if (INFO)
                    Log.i(TAG, "DB 파일 없음");
            } else {
                if (INFO)
                    Log.i(TAG, "DB 파일 있음");
                params.put("uploadedfile", myFile); //전송정보 : hwDB.db
            }
        } catch (Exception e) {
            Log.d(TAG, "FileNotFoundException :: " + e.toString());
        }
        RestClient.post("backupDB.php", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                Log.i(TAG, "onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("onSuccess - object", response.toString());
                try {
                    mTextInsertFlag = (String) response.get("TextResult"); //DB에 정보 입력 성공시 InsertSuccess 리턴..
                    mBackupCodeString = (String) response.get("BackupCode"); //
                    if (INFO) {
                        Log.i(TAG, "mTextInsertFlag :: " + mTextInsertFlag);
                        Log.i(TAG, "backupCode :: " + mBackupCodeString);
                    }
                } catch (JSONException e) {
                    if (DBUG)
                        Log.d(TAG, "JSONException");
                } catch (NullPointerException e) {
                    if (DBUG)
                        Log.d(TAG, "NullPointException");

                }
                if (mTextInsertFlag.equals("InsertSuccess")) {
                    if (INFO)
                        Log.i(TAG, "DB Backup 성공");
                    if (CommonUtil.isNull(mBackupCodeString)) {
                        Message msg = backupCodeSetHandler.obtainMessage();
                        msg.obj = mBackupCodeString;
                        backupCodeSetHandler.sendMessage(msg);
                    }
                } else {
                    if (INFO)
                        Log.i(TAG, "DB Backup 실패");
                    mBackupCodeString = "";
                    Toast.makeText(getActivity(), "백업을 실패하였습니다. 개발자에게 문의하세요", Toast.LENGTH_SHORT).show();
                    mLoadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray
                    errorResponse) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (INFO)
                    Log.i(TAG, "onFinish");
            }
        });
    }

    /**
     * 백업성공 시 프리프런스에 코드값 저장
     */
    final Handler backupCodeSetHandler = new Handler() {
        public void handleMessage(Message msg) {
            mTxtBackupCode.setText("BackupCode : " + msg.obj);
            mEditBackupCode.setText(msg.obj.toString());
            mPref.put(mPref.KEY_BACKUP_CODE, msg.obj.toString());
            mLoadingDialog.dismiss();
        }
    };

    /**
     * 백업파일 다운로드 요청
     */
    public void asyncTask_FileDown_Call(String fileName) {
        if (INFO)
            Log.i(TAG, "asyncTask_FileDown_Call");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new asyncTask_FileDown().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, fileName);
        } else {
            new asyncTask_FileDown().execute(fileName);
        }
    }

    private class asyncTask_FileDown extends AsyncTask<String, Integer, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub
            downloadFile(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            mLoadingDialog.dismiss();
            HYDatabaseBackup.importDB(getActivity());

        }
    }

    public ProgressDialog showLoadingDialog(Context context, boolean cancelable) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Loding..");
        dialog.setIndeterminate(true);
        dialog.setCancelable(cancelable);
        dialog.show();
        return dialog;
    }
    /**
     * 백업파일 다운로드
     */
    void downloadFile(String _name) {
        try {
            URL u = new URL(Constant.SERVER_URL + "/Anony/api/backup/" + _name);
            DataInputStream stream = new DataInputStream(u.openStream());
            byte[] buffer = IOUtils.toByteArray(stream);
            FileOutputStream fos = getActivity().openFileOutput("hwDB.db", Context.MODE_PRIVATE);
            fos.write(buffer);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * @author : oh
     * @MethodName : myLocation
     * @Day : 2014. 11. 4.
     * @Time : 오후 3:06:33
     * @Explanation : GPS기반으로 현재 위경도를 알아냄
     */
    private GpsInfo mGps;

    void myLocation() {
        CDLGpsLatch = new CountDownLatch(1);
        mGps = new GpsInfo(getActivity());
        if (mGps.isGetLocation()) {
            if (INFO)
                Log.i(TAG, "GPS 상태 : ON");

            Constant.LAT = mGps.getLatitude();
            Constant.LNG = mGps.getLongitude();

            //tta 위경도 - 현재 기상청 지역코드 파싱시 에러남
            /*Config.LAT = 37.383750;
            Config.LNG = 127.120598;*/

            if (DBUG) {
                Log.d(TAG, "GEO_X" + Double.toString(Constant.LAT));
                Log.d(TAG, "GEO_Y" + Double.toString(Constant.LNG));
            }
            CDLGpsLatch.countDown();
        } else {
            if (INFO)
                Log.i(TAG, "GPS 상태 : OFF");
            mGps.showSettingsAlert();
            CDLGpsLatch.countDown();
        }
    }

    /**
     * 기상청 가까운 지역코드 찾기
     */
    public void requestCallDB_KmaDistance() {
        try {
            CDLGpsLatch.await();
            CDLKmaDistanceLatch = new CountDownLatch(1);
            Cursor cursor = kmDBManager.selectKMAllData();

            Cursor results = cursor;
            results.moveToFirst();

            while (!results.isAfterLast()) {
                // Log.d(TAG, results.getString(0) + " area : " + results.getString(1) + " " + results.getString(2) + " " + results.getString(3) + " code : " + results.getString(4) + " kmaX :" + results.getString(5) + " kmaY : " + results.getString(6) + " lat :" + results.getString(7) + " lng : " + results.getString(8) + " 거리 : " + Float.toString(calcDistance(37.482619299999996, 126.89393600000001, Double.parseDouble(results.getString(7)), Double.parseDouble(results.getString(8)))));

                KmaCodeItem data = new KmaCodeItem();
                data.setArea(results.getString(1));
                data.setLocality(results.getString(2));
                data.setThoroughfare(results.getString(3));
                data.setKmacode(results.getString(4));
                data.setKmaX(results.getString(5));
                data.setKmaY(results.getString(6));
                data.setDistance(calcDistance(Constant.LAT, Constant.LNG, Double.parseDouble(results.getString(7)), Double.parseDouble(results.getString(8))));
                mKmaItemList.add(data);
                results.moveToNext();
            }
            results.close();
            Collections.sort(mKmaItemList, new NoAscCompare());
            mPref.put(mPref.KEY_WEATHER_LOCATION, mKmaItemList.get(0).getArea() + " " + mKmaItemList.get(0).getLocality() + " " + mKmaItemList.get(0).getThoroughfare());
            //  Constant.FORMATTED_ADDR = mKmaItemList.get(0).getArea() + " " + mKmaItemList.get(0).getLocality() + " " + mKmaItemList.get(0).getThoroughfare();
            Log.i("위치", mKmaItemList.get(0).getArea() + " " + mKmaItemList.get(0).getLocality() + " " + mKmaItemList.get(0).getThoroughfare());
            CDLKmaDistanceLatch.countDown();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            CDLKmaDistanceLatch.countDown();
        }
    }

    /**
     * 거리비교
     */
    public float calcDistance(double lat1, double lon1, double lat2,
                              double lon2) {
        float[] distance = new float[2];
        float result;
        Location.distanceBetween(lat1, lon1, lat2, lon2, distance);
        result = distance[0];
        return result;
    }

    class NoAscCompare implements Comparator<KmaCodeItem> {

        /**
         * 오름차순(ASC)
         */
        @Override
        public int compare(KmaCodeItem arg0, KmaCodeItem arg1) {
            // TODO Auto-generated method stub
            return arg0.getDistance() < arg1.getDistance() ? -1 : arg0.getDistance() > arg1.getDistance() ? 1 : 0;
        }
    }

    /**
     * 가까운 위치 요청
     */
    public void asyncTask_Location_Call() {
        if (INFO)
            Log.i(TAG, "asyncTask_Location_Call");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new asyncTask_Location().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new asyncTask_Location().execute();
        }
    }

    private class asyncTask_Location extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            requestCallDB_KmaDistance();

            try {
                CDLKmaDistanceLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mTxtCurrentLocation.setText(mKmaItemList.get(0).getArea() + " " + mKmaItemList.get(0).getLocality() + " " + mKmaItemList.get(0).getThoroughfare());
                    } catch (Exception e) {
                        Log.i(TAG, e.toString());
                    }
                }
            });
        }
    }

    /**
     * 폰트정보 콜백
     */
    public void onFinishFontDialog() {
        switch (mPref.getValue(mPref.KEY_FONT, 1)) {
            case 0:
                mTxtSelectFont.setText("기본");
                break;
            case 1:
                mTxtSelectFont.setText("상상체");
                break;
            case 2:
                mTxtSelectFont.setText("고도체");
                break;
        }
    }
    /**
     * 패스상태 콜백
     */
    public void onFinishPassDialog() {
        if (mPref.getValue(mPref.KEY_PASS_STATE, false)) {
            mTxtSelectPass.setText("사용");
        } else {
            mTxtSelectPass.setText("사용안함");
        }
    }

    /**
     * 계산방법 콜백
     */
    public void onFinishCalDialog() {
        if (mPref.getValue(mPref.KEY_BAR_PERIOD_CHECK, false)) {
            mTxtCal.setText("자동");
        } else {
            mTxtCal.setText("수동");
        }
    }

    /**
     * 날짜다이어로그 상태 콜백
     */
    public void onFinishStyleDialog() {
        if (mPref.getValue(mPref.KEY_PICKER_THEME, 0) == 0) {
            Log.i(TAG, "아날로그");
            mTxtStyleName.setText("아날로그");
        } else if (mPref.getValue(mPref.KEY_PICKER_THEME, 0) == 1) {
            Log.i(TAG, "디지털");
            mTxtStyleName.setText("디지털");
        } else {
            mTxtStyleName.setText("디지털 24h");
        }
    }

    /**
     * 4대보험 상태 콜백
     */
    public void onFinishInsuranceDialog() {
        if (mPref.getValue(mPref.KEY_INSURANCE_STATE, false)) {
            mTxtInsurance.setText("적용");
        } else {
            mTxtInsurance.setText("미적용");
        }
    }

    /**
     * 세금 상태 콜백
     */
    public void onFinishDutyDialog() {
        if (mPref.getValue(mPref.KEY_DUTY_STATE, false)) {
            mTxtDuty.setText("적용");
        } else {
            mTxtDuty.setText("미적용");
        }
    }

    /**
     * 테마 상태 콜백
     */
    public void onFinishThemeDialog() {
        if (mPref.getValue(mPref.KEY_PICKER_THEME_STYLE, 0) == 0) {
            mTxtTheme.setText("Red");
        } else if (mPref.getValue(mPref.KEY_PICKER_THEME_STYLE, 0) == 1) {
            mTxtTheme.setText("Blue");
        } else if (mPref.getValue(mPref.KEY_PICKER_THEME_STYLE, 0) == 2) {
            mTxtTheme.setText("Green");
        }
    }
}
