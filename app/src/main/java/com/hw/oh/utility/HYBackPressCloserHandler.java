package com.hw.oh.utility;

import android.app.Activity;
import android.view.View;

import com.hw.oh.temp.R;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;


public class HYBackPressCloserHandler {
  private long backKeyPressedTime = 0;
  private Activity mActivity;
  private CroutonHelper mCroutonHelper;
  private View mCroutonView;

  public HYBackPressCloserHandler(Activity paramActivity) {
    mActivity = paramActivity;
    mCroutonHelper = new CroutonHelper(mActivity);
    mCroutonView = mActivity.getLayoutInflater().inflate(
        R.layout.crouton_custom_view, null);

  }

  public void onBackPressed() {
    if (System.currentTimeMillis() > 2000 + this.backKeyPressedTime) {
      backKeyPressedTime = System.currentTimeMillis();

      mCroutonHelper.setCustomView(mCroutonView);
      mCroutonHelper.show();
      return;
    } else if (System.currentTimeMillis() <= 2000 + this.backKeyPressedTime) {
      mActivity.finish();
      System.exit(0);
    }
  }
}