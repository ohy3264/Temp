package com.hw.oh.utility;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;


public class HYAnimation {


  public static void UP(View v, int fromY, int toY) {
    final View mView = v;
    TranslateAnimation upAnimation = new TranslateAnimation(0, 0, fromY, toY);

    upAnimation.setDuration(150);
    upAnimation.setFillAfter(true);

    upAnimation.setAnimationListener(new AnimationListener() {

      @Override
      public void onAnimationStart(Animation animation) {
        Constant.ANI_STATE = Constant.ANI_STATE_UP;
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {
      }
    });

    mView.startAnimation(upAnimation);
  }

  public static void DOWN(View v, int fromY, int toY) {
    final View mView = v;
    TranslateAnimation upAnimation = new TranslateAnimation(0, 0, fromY, toY);
    upAnimation.setDuration(150);
    upAnimation.setFillAfter(true);
    upAnimation.setAnimationListener(new AnimationListener() {

      @Override
      public void onAnimationStart(Animation animation) {

        Constant.ANI_STATE = Constant.ANI_STATE_DOWN;
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {

      }
    });

    mView.startAnimation(upAnimation);
  }

  public static void GONE(View v) {

    final View mView = v;
    AlphaAnimation fadeOutAnimation = new AlphaAnimation(1, 0);

    fadeOutAnimation.setDuration(500);
    fadeOutAnimation.setFillAfter(true);
    fadeOutAnimation.setAnimationListener(new AnimationListener() {
      @Override
      public void onAnimationEnd(Animation animation) {
        mView.setVisibility(View.GONE);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {
      }

      @Override
      public void onAnimationStart(Animation animation) {
      }
    });

    mView.startAnimation(fadeOutAnimation);
  }

  public static void VISIBLE(View v) {

    final View mView = v;
    AlphaAnimation fadeInAnimation = new AlphaAnimation(0, 1);
    fadeInAnimation.setDuration(500);
    fadeInAnimation.setFillAfter(true);
    fadeInAnimation.setAnimationListener(new AnimationListener() {
      @Override
      public void onAnimationEnd(Animation animation) {
        mView.setVisibility(View.VISIBLE);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {
      }

      @Override
      public void onAnimationStart(Animation animation) {
      }
    });
    mView.startAnimation(fadeInAnimation);

  }
}
