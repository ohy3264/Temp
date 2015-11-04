package com.hw.oh.model;

import java.io.Serializable;

/**
 * Created by oh on 2015-02-21.
 */
public class PartTimeInfo implements Serializable {
  int _id;
  String albaname;
  String hourMoney;
  String startTimeHour;
  String startTimeMin;
  String endTimeHour;
  String endTimeMin;
  String simpleMemo;
  String workPayNight;
  String workRefresh;
  String workPayAdd;
  String workPayAddHour;
  String workAddType;
  String workPayAddMin;
  String workRefreshType;
  String workRefreshHour;
  String workRefreshMin;
  String workPayEtc;
  String workPayEtcNum;
  String workPayEtcMoney;
  Boolean workAlarm;

  public int getWorkMonthDay() {
    return workMonthDay;
  }

  public void setWorkMonthDay(int workMonthDay) {
    this.workMonthDay = workMonthDay;
  }

  int workMonthDay;

  public void setWorkAlarm(Boolean workAlarm) {
    this.workAlarm = workAlarm;
  }

  public Boolean getWorkAlarm() {
    return workAlarm;
  }

  public int get_id() {
    return _id;
  }

  public void set_id(int _id) {
    this._id = _id;
  }

  public String getAlbaname() {
    return albaname;
  }

  public void setAlbaname(String albaname) {
    this.albaname = albaname;
  }

  public String getHourMoney() {
    return hourMoney;
  }

  public void setHourMoney(String hourMoney) {
    this.hourMoney = hourMoney;
  }

  public String getStartTimeHour() {
    return startTimeHour;
  }

  public void setStartTimeHour(String startTimeHour) {
    this.startTimeHour = startTimeHour;
  }

  public String getStartTimeMin() {
    return startTimeMin;
  }

  public void setStartTimeMin(String startTimeMin) {
    this.startTimeMin = startTimeMin;
  }

  public String getEndTimeHour() {
    return endTimeHour;
  }

  public void setEndTimeHour(String endTimeHour) {
    this.endTimeHour = endTimeHour;
  }

  public String getEndTimeMin() {
    return endTimeMin;
  }

  public void setEndTimeMin(String endTimeMin) {
    this.endTimeMin = endTimeMin;
  }

  public String getSimpleMemo() {
    return simpleMemo;
  }

  public void setSimpleMemo(String simpleMemo) {
    this.simpleMemo = simpleMemo;
  }

  public String getWorkPayNight() {
    return workPayNight;
  }

  public void setWorkPayNight(String workPayNight) {
    this.workPayNight = workPayNight;
  }

  public String getWorkRefresh() {
    return workRefresh;
  }

  public void setWorkRefresh(String workRefresh) {
    this.workRefresh = workRefresh;
  }

  public String getWorkPayAdd() {
    return workPayAdd;
  }

  public void setWorkPayAdd(String workPayAdd) {
    this.workPayAdd = workPayAdd;
  }

  public String getWorkPayAddHour() {
    return workPayAddHour;
  }

  public void setWorkPayAddHour(String workPayAddHour) {
    this.workPayAddHour = workPayAddHour;
  }

  public String getWorkAddType() {
    return workAddType;
  }

  public void setWorkAddType(String workAddType) {
    this.workAddType = workAddType;
  }

  public String getWorkPayAddMin() {
    return workPayAddMin;
  }

  public void setWorkPayAddMin(String workPayAddMin) {
    this.workPayAddMin = workPayAddMin;
  }

  public String getWorkRefreshType() {
    return workRefreshType;
  }

  public void setWorkRefreshType(String workRefreshType) {
    this.workRefreshType = workRefreshType;
  }

  public String getWorkRefreshHour() {
    return workRefreshHour;
  }

  public void setWorkRefreshHour(String workRefreshHour) {
    this.workRefreshHour = workRefreshHour;
  }

  public String getWorkRefreshMin() {
    return workRefreshMin;
  }

  public void setWorkRefreshMin(String workRefreshMin) {
    this.workRefreshMin = workRefreshMin;
  }

  public String getWorkPayEtc() {
    return workPayEtc;
  }

  public void setWorkPayEtc(String workPayEtc) {
    this.workPayEtc = workPayEtc;
  }

  public String getWorkPayEtcNum() {
    return workPayEtcNum;
  }

  public void setWorkPayEtcNum(String workPayEtcNum) {
    this.workPayEtcNum = workPayEtcNum;
  }

  public String getWorkPayEtcMoney() {
    return workPayEtcMoney;
  }

  public void setWorkPayEtcMoney(String workPayEtcMoney) {
    this.workPayEtcMoney = workPayEtcMoney;
  }

  public Double getWorkPayWeekTime() {
    return workPayWeekTime;
  }

  public void setWorkPayWeekTime(Double workPayWeekTime) {
    this.workPayWeekTime = workPayWeekTime;
  }

  public int getWorkPayWeekMoney() {
    return workPayWeekMoney;
  }

  public void setWorkPayWeekMoney(int workPayWeekMoney) {
    this.workPayWeekMoney = workPayWeekMoney;
  }

  public String getWorkPayWeek() {
    return workPayWeek;
  }

  public void setWorkPayWeek(String workPayWeek) {
    this.workPayWeek = workPayWeek;
  }

  Double workPayWeekTime;
  int workPayWeekMoney;
  String workPayWeek;


}
