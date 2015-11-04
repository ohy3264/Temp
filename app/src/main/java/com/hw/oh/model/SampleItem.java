package com.hw.oh.model;

/**
 * Created by oh on 2015-02-01.
 */
public class SampleItem {
  public String tag;
  public int iconRes;

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }


  public int getIconRes() {
    return iconRes;
  }

  public void setIconRes(int iconRes) {
    this.iconRes = iconRes;
  }

  public SampleItem(String tag, int iconRes) {
    this.tag = tag;
    this.iconRes = iconRes;
  }
}