package com.hw.oh.utility;

import java.util.Calendar;

/**
 * Created by oh on 2015-03-20.
 */
public class HYTimeUtill {
  public static java.util.Date timeReturn(int hour, int min) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, min);
    return cal.getTime();
  }

}
