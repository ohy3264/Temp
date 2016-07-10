package com.hw.oh.fragment;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hw.oh.sqlite.DBManager;
import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYPreference;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

/**
 * Created by ohyowan on 16. 5. 22..
 */
public class BaseFragment extends Fragment {

    //Format
    public SimpleDateFormat mDateFormat = new SimpleDateFormat("HH:mm");
    public NumberFormat mNumFomat = new DecimalFormat("###,###,###");

    //Utill
    public HYPreference mPref;
    public HYFont mFont;
    public DBManager mDB;


    //Crouton
    private View mCroutonView;
    private TextView mTxtCrouton;
    private CroutonHelper mCroutonHelper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Utill
        mPref = new HYPreference(getActivity());
        mFont = new HYFont(getActivity());
        mDB = new DBManager(getActivity());


        //Crouton
        mCroutonHelper = new CroutonHelper(getActivity());
        mCroutonView = getActivity().getLayoutInflater().inflate(
                R.layout.crouton_custom_view, null);
        mTxtCrouton = (TextView) mCroutonView.findViewById(R.id.txt_crouton);
    }

    public void setFont(View rootView) {
        mFont.setGlobalFont((ViewGroup) rootView);

    }

    public void showCrountToast(final String msg, final int time) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTxtCrouton.setText(msg);
                mCroutonHelper.setCustomView(mCroutonView);
                mCroutonHelper.setDuration(time);
                mCroutonHelper.show();
            }
        });
    }

    public void showCrountToast(final String msg) {
        showCrountToast(msg, 1000);
    }

    public void clearCrountToast() {
        mCroutonHelper.clearCroutonsForActivity();
    }


    public int getThemeColor(int colorChoice) {
        TypedValue typedValue = new TypedValue();
        TypedArray a;
        int color = 0;
        switch (colorChoice) {
            case 0:
                a = getActivity().obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
                color = a.getColor(0, 0);
                a.recycle();
                return color;
            case 1:
                a = getActivity().obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
                color = a.getColor(0, 0);
                a.recycle();
                return color;
            case 2:
                a = getActivity().obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimaryDark});
                color = a.getColor(0, 0);
                a.recycle();
                return color;
        }
        return color;
    }
}
