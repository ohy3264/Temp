package com.hw.oh.model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by oh on 2015-05-04.
 */
public class WeekWeatherItem implements Serializable {
  String day;
  String date;
  String highTemp;
  String lowTemp;
  String tempStr;
  int tempCode;
  Bitmap tempImg;

  public Bitmap getTempImg() {
    return tempImg;
  }

  public void setTempImg(Bitmap tempImg) {
    this.tempImg = tempImg;
  }

  public String getDay() {
    return day;
  }

  public void setDay(String day) {
    this.day = day;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getHighTemp() {
    return highTemp;
  }

  public void setHighTemp(String highTemp) {
    this.highTemp = highTemp;
  }

  public String getLowTemp() {
    return lowTemp;
  }

  public void setLowTemp(String lowTemp) {
    this.lowTemp = lowTemp;
  }

  public String getTempStr() {
    return tempStr;
  }

  public void setTempStr(String tempStr) {
    this.tempStr = tempStr;
  }

  public int getTempCode() {
    return tempCode;
  }

  public void setTempCode(int tempCode) {
    this.tempCode = tempCode;
  }
}
