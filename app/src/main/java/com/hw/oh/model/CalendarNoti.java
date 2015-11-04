package com.hw.oh.model;

/**
 * Created by hyowan on 2015-10-05.
 */
public class CalendarNoti {
  String albaName;
  boolean mon;
  boolean thu;
  boolean wed;
  boolean thur;
  boolean fri;
  boolean sat;
  boolean sun;
  int requestCode;

  public CalendarNoti() {
  }

  public CalendarNoti(String albaName, boolean mon, boolean thu, boolean wed, boolean thur, boolean fri, boolean sat, boolean sun, int requestCode) {
    this.albaName = albaName;
    this.mon = mon;
    this.thu = thu;
    this.wed = wed;
    this.thur = thur;
    this.fri = fri;
    this.sat = sat;
    this.sun = sun;
    this.requestCode = requestCode;
  }

  public String getAlbaName() {
    return albaName;
  }

  public void setAlbaName(String albaName) {
    this.albaName = albaName;
  }

  public boolean isMon() {
    return mon;
  }

  public void setMon(boolean mon) {
    this.mon = mon;
  }

  public boolean isThu() {
    return thu;
  }

  public void setThu(boolean thu) {
    this.thu = thu;
  }

  public boolean isWed() {
    return wed;
  }

  public void setWed(boolean wed) {
    this.wed = wed;
  }

  public boolean isThur() {
    return thur;
  }

  public void setThur(boolean thur) {
    this.thur = thur;
  }

  public boolean isFri() {
    return fri;
  }

  public void setFri(boolean fri) {
    this.fri = fri;
  }

  public boolean isSat() {
    return sat;
  }

  public void setSat(boolean sat) {
    this.sat = sat;
  }

  public boolean isSun() {
    return sun;
  }

  public void setSun(boolean sun) {
    this.sun = sun;
  }

  public int getRequestCode() {
    return requestCode;
  }

  public void setRequestCode(int requestCode) {
    this.requestCode = requestCode;
  }
}
