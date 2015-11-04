package com.hw.oh.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by oh on 2015-02-03.
 */
public class BoardItem implements Parcelable, Serializable {
  String _id;
  String UniqueID;
  String Gender;
  String strText;
  String hitCNT;
  String likeCNT;
  String hateCNT;
  String commCNT;
  String imgState;
  String regDate;

  public BoardItem() {
  }

  public BoardItem(Parcel in) {
    readFromParcel(in);
  }

  public BoardItem(String _id, String uniqueID, String gender, String strText, String hitCNT, String likeCNT, String hateCNT, String commCNT, String imgState, String regDate) {
    this._id = _id;
    UniqueID = uniqueID;
    Gender = gender;
    this.strText = strText;
    this.hitCNT = hitCNT;
    this.likeCNT = likeCNT;
    this.hateCNT = hateCNT;
    this.commCNT = commCNT;
    this.imgState = imgState;
    this.regDate = regDate;
  }

  public String getImgState() {
    return imgState;
  }

  public void setImgState(String imgState) {
    this.imgState = imgState;
  }

  public String get_id() {
    return _id;
  }

  public void set_id(String _id) {
    this._id = _id;
  }

  public String getGender() {
    return Gender;
  }

  public void setGender(String gender) {
    this.Gender = gender;
  }

  public String getStrText() {
    return strText;
  }

  public void setStrText(String strText) {
    this.strText = strText;
  }

  public String getUniqueID() {
    return UniqueID;
  }

  public void setUniqueID(String uniqueID) {
    UniqueID = uniqueID;
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

  public String getRegDate() {
    return regDate;
  }

  public void setRegDate(String regDate) {
    this.regDate = regDate;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(_id);
    dest.writeString(UniqueID);
    dest.writeString(Gender);
    dest.writeString(strText);
    dest.writeString(hitCNT);
    dest.writeString(likeCNT);
    dest.writeString(hateCNT);
    dest.writeString(commCNT);
    dest.writeString(imgState);
    dest.writeString(regDate);

  }

  private void readFromParcel(Parcel in) {
    this._id = in.readString();
    UniqueID = in.readString();
    Gender = in.readString();
    this.strText = in.readString();
    this.hitCNT = in.readString();
    this.likeCNT = in.readString();
    this.hateCNT = in.readString();
    this.commCNT = in.readString();
    this.imgState = in.readString();
    this.regDate = in.readString();
  }

  public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    public BoardItem createFromParcel(Parcel in) {
      return new BoardItem(in);
    }

    public BoardItem[] newArray(int size) {
      return new BoardItem[size];
    }
  };

  public class CustomCreator implements Parcelable.Creator<BoardItem> {
    public BoardItem createFromParcel(Parcel src) {
      return new BoardItem(src);
    }

    public BoardItem[] newArray(int size) {
      return new BoardItem[size];
    }
  }
}

