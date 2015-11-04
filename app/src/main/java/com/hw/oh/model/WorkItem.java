package com.hw.oh.model;

import java.io.Serializable;

/**
 * Created by oh on 2015-03-19.
 */
public class WorkItem implements Serializable {
  public int hourMoney; // 시급


  public String year; //년
  public String month; //월
  public String day; //일
  public String startTimeHour; //시작시간
  public String startTimeMin; // 종료분
  public String endTimeHour; //종료시간
  public String endTimeMin; //종료분
  public String dayTimeHour; //주간 일한시간
  public String dayTimeMin; //주간 일한분
  public String nightTimeHour; //야간 일한시간
  public String nightTimeMin; //야간 일한분
  public String addTimeHour; //연장시간
  public String addTimeMin; //연장한분
  public String refreshTimeHour; //휴식시간
  public String refreshTimeMin; //휴식한분
  public String workTimeHour; //일한시간
  public String workTimeMin; //일한분
  public String workEtc;//기타수당 체크
  public String workEtcNum; // 기타 건수
  public String workEtcMoney; //기타 건당수당
  public String workPayDay; // 주간 수당


  public String workNight; // 야간수당체크
  public String workPayNight; // 야간수당
  public String workAdd; // 연장수당체크
  public String workPayAdd; // 연장수당
  public String workWeek; // 주휴수당
  public String workPayWeek; // 주휴수당
  public String workPayRefresh; // 휴식수당
  public String workPayGabul; // 가불체크
  public String workPayGabulValue; // 가불수당
  public int totalMoney; // 총수당
  public String simpleMemo; // 메모

  public int getHourMoney() {
    return hourMoney;
  }

  public void setHourMoney(int hourMoney) {
    this.hourMoney = hourMoney;
  }

  public String getWorkNight() {
    return workNight;
  }

  public void setWorkNight(String workNight) {
    this.workNight = workNight;
  }

  public String getWorkAdd() {
    return workAdd;
  }

  public void setWorkAdd(String workAdd) {
    this.workAdd = workAdd;
  }

  public String getWorkPayWeek() {
    return workPayWeek;
  }

  public void setWorkPayWeek(String workPayWeek) {
    this.workPayWeek = workPayWeek;
  }

  public String getDayTimeHour() {
    return dayTimeHour;
  }

  public void setDayTimeHour(String dayTimeHour) {
    this.dayTimeHour = dayTimeHour;
  }

  public String getDayTimeMin() {
    return dayTimeMin;
  }

  public void setDayTimeMin(String dayTimeMin) {
    this.dayTimeMin = dayTimeMin;
  }

  public String getNightTimeHour() {
    return nightTimeHour;
  }

  public void setNightTimeHour(String nightTimeHour) {
    this.nightTimeHour = nightTimeHour;
  }

  public String getNightTimeMin() {
    return nightTimeMin;
  }

  public void setNightTimeMin(String nightTimeMin) {
    this.nightTimeMin = nightTimeMin;
  }

  public String getAddTimeHour() {
    return addTimeHour;
  }

  public void setAddTimeHour(String addTimeHour) {
    this.addTimeHour = addTimeHour;
  }

  public String getAddTimeMin() {
    return addTimeMin;
  }

  public void setAddTimeMin(String addTimeMin) {
    this.addTimeMin = addTimeMin;
  }

  public String getRefreshTimeHour() {
    return refreshTimeHour;
  }

  public void setRefreshTimeHour(String refreshTimeHour) {
    this.refreshTimeHour = refreshTimeHour;
  }

  public String getRefreshTimeMin() {
    return refreshTimeMin;
  }

  public void setRefreshTimeMin(String refreshTimeMin) {
    this.refreshTimeMin = refreshTimeMin;
  }

  public String getWorkTimeHour() {
    return workTimeHour;
  }

  public void setWorkTimeHour(String workTimeHour) {
    this.workTimeHour = workTimeHour;
  }

  public String getWorkTimeMin() {
    return workTimeMin;
  }

  public void setWorkTimeMin(String workTimeMin) {
    this.workTimeMin = workTimeMin;
  }

  public String getWorkPayDay() {
    return workPayDay;
  }

  public void setWorkPayDay(String workPayDay) {
    this.workPayDay = workPayDay;
  }

  public String getWorkPayRefresh() {
    return workPayRefresh;
  }

  public void setWorkPayRefresh(String workPayRefresh) {
    this.workPayRefresh = workPayRefresh;
  }

  public String getWorkWeekMoney() {
    return workWeekMoney;
  }

  public void setWorkWeekMoney(String workWeekMoney) {
    this.workWeekMoney = workWeekMoney;
  }

  public String getWorkWeek() {
    return workWeek;
  }

  public void setWorkWeek(String workWeek) {
    this.workWeek = workWeek;
  }

  public String workWeekMoney;

  public String getWorkEtcNum() {
    return workEtcNum;
  }

  public void setWorkEtcNum(String workEtcNum) {
    this.workEtcNum = workEtcNum;
  }

  public String getWorkEtc() {
    return workEtc;
  }

  public void setWorkEtc(String workEtc) {
    this.workEtc = workEtc;
  }

  public String getWorkEtcMoney() {
    return workEtcMoney;
  }

  public void setWorkEtcMoney(String workEtcMoney) {
    this.workEtcMoney = workEtcMoney;
  }


  public String getWorkPayAdd() {
    return workPayAdd;
  }

  public void setWorkPayAdd(String workPayAdd) {
    this.workPayAdd = workPayAdd;
  }


  public String getWorkPayGabulValue() {
    return workPayGabulValue;
  }

  public void setWorkPayGabulValue(String workPayGabulValue) {
    this.workPayGabulValue = workPayGabulValue;
  }


  public String getWorkRefresh() {
    return workRefresh;
  }

  public void setWorkRefresh(String workRefresh) {
    this.workRefresh = workRefresh;
  }

  public String workRefresh;

  public String getWorkPayNight() {
    return workPayNight;
  }

  public void setWorkPayNight(String workPayNight) {
    this.workPayNight = workPayNight;
  }


  public String getWorkPayGabul() {
    return workPayGabul;
  }

  public void setWorkPayGabul(String workPayGabul) {
    this.workPayGabul = workPayGabul;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public String getMonth() {
    return month;
  }

  public void setMonth(String month) {
    this.month = month;
  }

  public String getDay() {
    return day;
  }

  public void setDay(String day) {
    this.day = day;
  }

  public int getTotalMoney() {
    return totalMoney;
  }

  public void setTotalMoney(int totalMoney) {
    this.totalMoney = totalMoney;
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
}
