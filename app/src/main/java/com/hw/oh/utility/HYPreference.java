package com.hw.oh.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;


public class HYPreference extends Preference {
  private final String PREF_NAME = "com.anony.pref";
  public static final String KEY_BACKUP_CODE = "backup_code";
  public static final String KEY_GENDER = "gender";
  public static final String KEY_BAR_PERIOD_CHECK = "bar_period_check";
  public static final String KEY_BAR_PERIOD_START = "bar_period_start";
  public static final String KEY_BAR_PERIOD_END = "bar_period_end";
  public static final String KEY_PIE_PERIOD_START = "pie_period_start";
  public static final String KEY_PIE_PERIOD_END = "pie_period_end";
  public static final String KEY_SOUND = "soundSet";
  public static final String KEY_VIBRATE = "vibrateSet";
  public static final String KEY_ALARM_STATE = "alarm_state";
  public static final String KEY_FONT = "font";
  public static final String KEY_FONT_STR = "font_str";
  public static final String KEY_ETC_MONEY = "etc_money";
  public static final String KEY_WEATHER_LOCATION = "weather_location";

  public static final String KEY_PASS_STATE = "pass_state";
  public static final String KEY_INSURANCE_STATE = "insurance_state";
  public static final String KEY_DUTY_STATE = "duty_state";
  public static final String KEY_DUTY_PERSON_1 = "duty_person1";
  public static final String KEY_DUTY_PERSON_2 = "duty_person2";
  public static final String KEY_PASSWORD = "passwrod";
  public static final String KEY_CHECK_VERSION = "check_version";
  public static final String KEY_CHECK_VERSION_STATUS = "check_version_status";
  //public static final String KEY_MONTH_PAY = "month_pay";
  public static final String KEY_PICKER_THEME = "picker_theme";
  public static final String KEY_DUTY_SELECTOR = "selector_duty";
  public static final String KEY_DUTY_INPUT = "duty_input";
  public static final String KEY_PICKER_THEME_STYLE = "theme_style";


  private static Context mContext;

  public HYPreference(Context context) {
    super(context);
    // TODO Auto-generated constructor stub
    mContext = context;
  }

  public void put(String key, String value) {
    SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
        Activity.MODE_PRIVATE);
    SharedPreferences.Editor editor = pref.edit();

    editor.putString(key, value);
    editor.commit();
  }

  public void put(String key, boolean value) {
    SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
        Activity.MODE_PRIVATE);
    SharedPreferences.Editor editor = pref.edit();

    editor.putBoolean(key, value);
    editor.commit();
  }

  public void put(String key, int value) {
    SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
        Activity.MODE_PRIVATE);
    SharedPreferences.Editor editor = pref.edit();

    editor.putInt(key, value);
    editor.commit();
  }

  public String getValue(String key, String dftValue) {
    SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
        Activity.MODE_PRIVATE);

    try {
      return pref.getString(key, dftValue);
    } catch (Exception e) {
      return dftValue;
    }

  }

  public int getValue(String key, int dftValue) {
    SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
        Activity.MODE_PRIVATE);

    try {
      return pref.getInt(key, dftValue);
    } catch (Exception e) {
      return dftValue;
    }

  }

  public boolean getValue(String key, boolean dftValue) {
    SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
        Activity.MODE_PRIVATE);

    try {
      return pref.getBoolean(key, dftValue);
    } catch (Exception e) {
      return dftValue;
    }
  }

  public void allClear() {
    SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
        Activity.MODE_PRIVATE);
    SharedPreferences.Editor editor = pref.edit();
    editor.clear();
    editor.commit();

  }

  public void setClear(String key) {
    SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
        Activity.MODE_PRIVATE);
    SharedPreferences.Editor editor = pref.edit();
    editor.remove(key);
    editor.commit();

  }
}
