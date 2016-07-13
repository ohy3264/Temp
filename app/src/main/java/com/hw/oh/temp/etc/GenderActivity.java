package com.hw.oh.temp.etc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.hw.oh.temp.BaseActivity;
import com.hw.oh.temp.MainActivity;
import com.hw.oh.temp.R;
import com.hw.oh.utility.CommonUtil;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.OkHttpUtils;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * GenderActivity
 * 성별선택화면
 *
 * @author hwoh
 */

public class GenderActivity extends BaseActivity {
    private static final String TAG = "GenderActivity";
    private static final boolean DBUG = true;
    private static final boolean INFO = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);
        //ButterKnife
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btnMan, R.id.btnWoman})
    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.btnMan:
                requestUniqueID(0);

                break;
            case R.id.btnWoman:
                requestUniqueID(1);
                break;
        }
    }


    public void requestUniqueID(final int gender) {
        if (INFO)
            Log.i(TAG, "requestUniqueID()");
        RequestBody formBody = new FormBody.Builder()
                .add("MODE", "InsertUser")
                .add("ANDROID_ID", CommonUtil.getAndroidID(this))
                .add("GENDER", Integer.toString(gender))
                .build();
        try {
            OkHttpUtils.post(getApplicationContext(), Constant.SERVER_URL, "/Anony/api/insertMember.php", formBody, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "requestUniqueID - onFailure :: " + e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i("Response", " " + response.code());
                    final String results = response.body().string();
                    Log.i("OkHTTP Results: ", results);

                    Message msg = IntentHandler.obtainMessage();
                    IntentHandler.sendMessage(msg);
                    mPref.put(mPref.KEY_GENDER, Integer.toString(gender));
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "requestUniqueID - exception :: " + e.toString());
        }


    }


    final Handler IntentHandler = new Handler() {
        public void handleMessage(Message msg) {
            Intent intent_main = new Intent(GenderActivity.this, MainActivity.class);
            intent_main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent_main);
            overridePendingTransition(0, 0);
            finish();
        }

    };
}
