package com.hw.oh.temp.etc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hw.oh.temp.ApplicationClass;
import com.hw.oh.temp.BaseActivity;
import com.hw.oh.temp.MainActivity;
import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;

/**
 * Created by oh on 2015-02-26.
 */
public class PassActivity extends BaseActivity {
    // Log
    private static final String TAG = "PassActivity";
    private static final boolean DEBUG = true;
    private static final boolean INFO = true;

    // ActionBar
    private Toolbar mToolbar;
    private Button mBtnLogin;
    private EditText mEdtPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);

        // 구글 통계
        Tracker mTracker = ((ApplicationClass) getApplication()).getDefaultTracker();
        mTracker.setScreenName("잠금해제화면_(PassActivity)");
        mTracker.send(new HitBuilders.AppViewBuilder().build());

        //Util
        mFont = new HYFont(this);
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        mFont.setGlobalFont(root);
        mPref = new HYPreference(this);

        //ActionBar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setTitle("잠금화면");
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mFont.setGlobalFont((ViewGroup) mToolbar);
        }

        mBtnLogin = (Button) findViewById(R.id.btn_Login);
        mEdtPass = (EditText) findViewById(R.id.edtPass);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEdtPass.getText().toString().equals(mPref.getValue(mPref.KEY_PASSWORD, ""))) {
                    Intent intent_main = new Intent(PassActivity.this, MainActivity.class);
                    intent_main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent_main);
                    overridePendingTransition(0, 0);
                    finish();
                } else {
                    Toast.makeText(PassActivity.this, "패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        // 구글 통계
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    ;

    @Override
    public void onStop() {
        super.onStop();
        // 구글 통계
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    ;

}

