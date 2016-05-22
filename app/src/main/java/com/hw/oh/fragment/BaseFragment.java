package com.hw.oh.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.hw.oh.temp.R;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

/**
 * Created by ohyowan on 16. 5. 22..
 */
public class BaseFragment extends Fragment {
    //Crouton
    private View mCroutonView;
    private TextView mTxtCrouton;
    private CroutonHelper mCroutonHelper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Crouton
        mCroutonHelper = new CroutonHelper(getActivity());
        mCroutonView = getActivity().getLayoutInflater().inflate(
                R.layout.crouton_custom_view, null);
        mTxtCrouton = (TextView) mCroutonView.findViewById(R.id.txt_crouton);
    }

    public void showCrountToast(final String msg){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTxtCrouton.setText(msg);
                mCroutonHelper.setCustomView(mCroutonView);
                mCroutonHelper.setDuration(1000);
                mCroutonHelper.show();
            }
        });
    }
}
