package com.hw.oh.utility;

import android.os.Handler;

import com.dd.processbutton.ProcessButton;

import java.util.Random;

public class ProgressGenerator {

  public interface OnCompleteListener {

    public void onComplete();
  }

  private OnCompleteListener mListener;
  private int mProgress;
  final Handler mHandler = new Handler();
  private Boolean flag = false;

  public ProgressGenerator(OnCompleteListener listener) {
    mListener = listener;
  }

  public void start(final ProcessButton button) {
    mHandler.postDelayed(new Runnable() {
      @Override
      public void run() {
        mProgress += 10;
        button.setProgress(mProgress);
        if (mProgress < 100) {
          mHandler.postDelayed(this, generateDelay());
        } else {
          mListener.onComplete();
        }
      }
    }, generateDelay());
  }

  public void stop() {
    mProgress = 90;
    mListener.onComplete();
  }

  private Random random = new Random();

  private int generateDelay() {
    return random.nextInt(1000);
  }
}
