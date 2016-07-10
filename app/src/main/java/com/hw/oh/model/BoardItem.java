package com.hw.oh.model;

import java.io.Serializable;

/**
 * Created by oh on 2015-02-03.
 */
public class BoardItem implements Serializable {

  /**
   * _id : 553
   * UniqueID : 63351a4fa07de6d0
   * Gender : 0
   * strText : 라이더, 배달 시급 얼마예요?  7500받거나 6130에 건당400원 받는데
   * 제 친구는 7500원에 건당 받는데서요
   * hitCNT : 0
   * likeCNT : 0
   * hateCNT : 0
   * commCNT : 1
   * imgState : 0
   * regDate : 2016-06-29 09:44:15
   */

  private String _id;
  private String UniqueID;
  private String Gender;
  private String strText;
  private String hitCNT;
  private String likeCNT;
  private String hateCNT;
  private String commCNT;
  private String imgState;
  private String regDate;

  public String get_id() {
    return _id;
  }

  public void set_id(String _id) {
    this._id = _id;
  }

  public String getUniqueID() {
    return UniqueID;
  }

  public void setUniqueID(String UniqueID) {
    this.UniqueID = UniqueID;
  }

  public String getGender() {
    return Gender;
  }

  public void setGender(String Gender) {
    this.Gender = Gender;
  }

  public String getStrText() {
    return strText;
  }

  public void setStrText(String strText) {
    this.strText = strText;
  }

  public String getHitCNT() {
    return hitCNT;
  }

  public void setHitCNT(String hitCNT) {
    this.hitCNT = hitCNT;
  }

  public String getLikeCNT() {
    return likeCNT;
  }

  public void setLikeCNT(String likeCNT) {
    this.likeCNT = likeCNT;
  }

  public String getHateCNT() {
    return hateCNT;
  }

  public void setHateCNT(String hateCNT) {
    this.hateCNT = hateCNT;
  }

  public String getCommCNT() {
    return commCNT;
  }

  public void setCommCNT(String commCNT) {
    this.commCNT = commCNT;
  }

  public String getImgState() {
    return imgState;
  }

  public void setImgState(String imgState) {
    this.imgState = imgState;
  }

  public String getRegDate() {
    return regDate;
  }

  public void setRegDate(String regDate) {
    this.regDate = regDate;
  }
}

