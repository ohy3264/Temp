package com.hw.oh.model;

import java.io.Serializable;

/**
 * Created by oh on 2015-06-01.
 */
public class DetailCalItem implements Serializable {
  Double hourMoney_dayTime;
  Double hourMoney_night;
  Double hourMoney_add;
  Double etcMoney;
  int totalTime;
  int time_dayTime;
  int time_night;
  int time_refresh;
  int time_add;

  public Double getEtcMoney() {
    return etcMoney;
  }

  public void setEtcMoney(Double etcMoney) {
    this.etcMoney = etcMoney;
  }

  public Double getHourMoney_add() {
    return hourMoney_add;
  }

  public void setHourMoney_add(Double hourMoney_add) {
    this.hourMoney_add = hourMoney_add;
  }

  public int getTime_add() {
    return time_add;
  }

  public void setTime_add(int time_add) {
    this.time_add = time_add;
  }

  public Double getHourMoney_dayTime() {
    return hourMoney_dayTime;
  }

  public void setHourMoney_dayTime(Double hourMoney_dayTime) {
    this.hourMoney_dayTime = hourMoney_dayTime;
  }

  public Double getHourMoney_night() {
    return hourMoney_night;
  }

  public void setHourMoney_night(Double hourMoney_night) {
    this.hourMoney_night = hourMoney_night;
  }

  public int getTotalTime() {
    return totalTime;
  }

  public void setTotalTime(int totalTime) {
    this.totalTime = totalTime;
  }

  public int getTime_dayTime() {
    return time_dayTime;
  }

  public void setTime_dayTime(int time_dayTime) {
    this.time_dayTime = time_dayTime;
  }

  public int getTime_night() {
    return time_night;
  }

  public void setTime_night(int time_night) {
    this.time_night = time_night;
  }

  public int getTime_refresh() {
    return time_refresh;
  }

  public void setTime_refresh(int time_refresh) {
    this.time_refresh = time_refresh;
  }


}
