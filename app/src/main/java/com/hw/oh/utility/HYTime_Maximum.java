package com.hw.oh.utility;

import java.text.ParseException;
import java.util.Date;


public class HYTime_Maximum {
  public static final int SEC = 60;
  public static final int MIN = 60;
  public static final int HOUR = 24;
  public static final int DAY = 30;
  public static final int MONTH = 12;

  public HYTime_Maximum() {
    // TODO Auto-generated constructor stub
  }

  public static String formatTimeString(String stringDate) {

    java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss");
    Date tempDate = null;
    try {
      tempDate = format.parse(stringDate);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    long curTime = System.currentTimeMillis();
    long regTime = tempDate.getTime();
    long diffTime = (curTime - regTime) / 1000;

    String msg = null;
    if (diffTime < HYTime_Maximum.SEC) {
      // sec
      msg = "방금 전";
    } else if ((diffTime /= HYTime_Maximum.SEC) < HYTime_Maximum.MIN) {
      // min
      msg = diffTime + "분 전";
    } else if ((diffTime /= HYTime_Maximum.MIN) < HYTime_Maximum.HOUR) {
      // hour
      msg = (diffTime) + "시간 전";
    } else if ((diffTime /= HYTime_Maximum.HOUR) < HYTime_Maximum.DAY) {
      // day
      msg = (diffTime) + "일 전";
    } else if ((diffTime /= HYTime_Maximum.DAY) < HYTime_Maximum.MONTH) {
      // day
      msg = (diffTime) + "달 전";
    } else {
      msg = (diffTime) + "년 전";
    }


    return msg;
  }
}
