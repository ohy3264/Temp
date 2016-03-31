package com.hw.oh.temp;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.hw.oh.utility.HYPreference;

/**
 * Created by ohyowan on 2016. 3. 27..
 */
public class BaseActivity extends AppCompatActivity {
    private HYPreference mPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPref = new HYPreference(this);
        setTheme();
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
        states.addState(new int[] {android.R.attr.state_focused},
                getResources().getDrawable(R.color.my_primary_green_dark));
        states.addState(new int[]{},
                getResources().getDrawable(R.color.my_primary_green_dark));

        return states;
    }


}
