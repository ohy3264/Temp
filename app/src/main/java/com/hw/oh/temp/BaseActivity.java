package com.hw.oh.temp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ohyowan on 2016. 3. 27..
 */
public class BaseActivity extends AppCompatActivity {
    public HYPreference mPref;
    public HYFont mFont;
    public AlertDialog alertDialog = null;
    public SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("yyyy년 MMM", Locale.getDefault());
    public NumberFormat numberFomat = new DecimalFormat("###,###,###");

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Utill
        mPref = new HYPreference(this);
        mFont = new HYFont(this);
        //Theme
        setTheme();

        //Font
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        mFont.setGlobalFont(root);
        super.onCreate(savedInstanceState);
    }


    public void setTheme(){
        if (mPref.getValue(mPref.KEY_PICKER_THEME_STYLE, 0) == 0) {
            setTheme(R.style.RedTheme);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.my_primary_red_dark));
            };
        } else if (mPref.getValue(mPref.KEY_PICKER_THEME_STYLE, 0) == 1) {
            setTheme(R.style.BlueTheme);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.my_primary_blue_dark));
            };
        } else if(mPref.getValue(mPref.KEY_PICKER_THEME_STYLE, 0) == 2){
            setTheme(R.style.GreenTheme);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.my_primary_green_dark));
            };
        }
    }

    public StateListDrawable getStates(){
        StateListDrawable states = (StateListDrawable)getResources().getDrawable(R.drawable.circleview_newwork);
        states.addState(new int[] {android.R.attr.state_pressed},
                getResources().getDrawable(R.color.my_primary_green_dark));
        states.addState(new int[]{android.R.attr.state_focused},
                getResources().getDrawable(R.color.my_primary_green_dark));
        states.addState(new int[]{},
                getResources().getDrawable(R.color.my_primary_green_dark));

        return states;
    }

    /**
     * 툴바
     *
     * @param message 타이틀 내용
     */
    public ActionBar setToolbar(String message) {
        //ActionBar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(message);
        setSupportActionBar(mToolbar);
        mFont.setGlobalFont((ViewGroup) mToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        return ab;
    }

    /**
     * 커스텀 크라우톤 토스트
     *
     * @param message 메시지 내용
     */
    public void showCrountonText(String message) {
        CroutonHelper mCroutonHelper = new CroutonHelper(this);
        View mCroutonView = getLayoutInflater().inflate(
                R.layout.crouton_custom_view, null);
        TextView mTxtCrouton = (TextView) mCroutonView.findViewById(R.id.txt_crouton);

        mTxtCrouton.setText(message);
        mCroutonHelper.setCustomView(mCroutonView);
        mCroutonHelper.show();
    }


    /**
     * 버전체크
     */
    public void alertVersionMessage() {
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
            alertDialog = new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon)
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

    /**
     * 각 Activity 에서 메세지창 호출시
     */
    public void alertMessage(CharSequence message) {
        alertMessage(getResources().getString(R.string.alert_title), message);
    }

    public void alertMessage(CharSequence title, CharSequence message) {
        alertDialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.confirm_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * activity 전환 공통 함수이다 (intent를 생성하여 전달)
     *
     * @param intent
     */
    public void startFadeActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * activity 전환 공통 함수이다
     *
     * @param ctx
     * @param c   (class)
     */
    public void startFadeActivity(Context ctx, @SuppressWarnings("rawtypes") Class c) {
        Intent intent = new Intent(ctx, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public int getThemeColor(int colorChoice) {
        TypedValue typedValue = new TypedValue();
        TypedArray a;
        int color = 0;
        switch (colorChoice) {
            case 0:
                a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
                color = a.getColor(0, 0);
                a.recycle();
                return color;
            case 1:
                a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
                color = a.getColor(0, 0);
                a.recycle();
                return color;
            case 2:
                a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimaryDark});
                color = a.getColor(0, 0);
                a.recycle();
                return color;
        }
        return color;
    }
}
