package com.hw.oh.model;

/**
 * Created by oh on 2015-03-22.
 */
public class CalendarInfo {

  String albaname;
  String startYear;
  String startMonth;
  String startDay;
  String endYear;
  String endMonth;
  String endDay;


  public CalendarInfo(String albaname, String startYear, String startMonth, String startDay, String endYear, String endMonth, String endDay) {
    this.albaname = albaname;
    this.startYear = startYear;
    this.startMonth = startMonth;
    this.startDay = startDay;
    this.endYear = endYear;
    this.endMonth = endMonth;
    this.endDay = endDay;
  }

  public String getAlbaname() {
    return albaname;
  }

  public void setAlbaname(String albaname) {
    this.albaname = albaname;
  }

  public String getStartYear() {
    return startYear;
  }

  public void setStartYear(String startYear) {
    this.startYear = startYear;
  }

  public String getStartMonth() {
    return startMonth;
  }

  public void setStartMonth(String startMonth) {
    this.startMonth = startMonth;
  }

  public String getStartDay() {
    return startDay;
  }

  public void setStartDay(String startDay) {
    this.startDay = startDay;
  }

  public String getEndYear() {
    return endYear;
  }

  public void setEndYear(String endYear) {
    this.endYear = endYear;
  }

  public String getEndMonth() {
    return endMonth;
  }

  public void setEndMonth(String endMonth) {
    this.endMonth = endMonth;
  }

  public String getEndDay() {
    return endDay;
  }

  public void setEndDay(String endDay) {
    this.endDay = endDay;
  }
}
