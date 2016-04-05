package com.hw.oh.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hw.oh.temp.R;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.InfoExtra;

/**
 * Created by oh on 2015-08-14.
 */
public class AlarmPopupDialog extends Activity {
  public static final String TAG = "GCMPopupDialog";
  private static final boolean DEBUG = true;
  private static final boolean INFO = true;
  public TextView popupName, popupMsg;


  private InfoExtra mInfo;
  private HYPreference mPref;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.dialog_scenario);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

    mPref = new HYPreference(this);
    mInfo = new InfoExtra(this);

    popupName = (TextView) findViewById(R.id.txtpopupName);
    popupMsg = (TextView) findViewById(R.id.txtpopupMsg);
    popupName.setText(getIntent().getStringExtra("AlbaName"));
    popupMsg.setText("알바시간입니다. 오늘도 화이팅^^");
    vibrator();


    TextView btnOk = (TextView) findViewById(R.id.btnOk);
    btnOk.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    TextView btnCancel = (TextView) findViewById(R.id.btnCancel);
    btnCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
  }


  private void vibrator() {
    Vibrator vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    //         long[] pattern = {1000, 200, 1000, 2000, 1200};
    //         vibe.vibrate(pattern, 0);
    vibe.vibrate(1000);
  }

}
