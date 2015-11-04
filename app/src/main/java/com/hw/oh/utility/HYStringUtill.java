package com.hw.oh.utility;

import java.util.Random;

/**
 * Created by oh on 2015-03-20.
 */
public class HYStringUtill {
  public static String[] splitFunction(String ktype) {     //ktype을 받는다.

    String[] array = ktype.split("-");     //콤마 구분자로 배열에 ktype저장
    return array;
  }

  public static String[] spaceFunction(String ktype) {     //ktype을 받는다.

    String[] array = ktype.split(" ");     //콤마 구분자로 배열에 ktype저장
    return array;
  }

  public static String dateFunction(String year, String month, String day) {

    String date = year + "-" + month + "-" + day;
    return date;
  }

  public static String timeFunction(String hour, String min) {

    String time = hour + ":" + min;
    return time;
  }

  public static int getRandomKey(int len) {
    int nSeed = 0;
    int nSeedSize = 10;
    String strSrc = "0123456789";
    String strKey = "";

    for (int i = 0; i < len; i++) {
      nSeed = (int) ((Math.random() * nSeedSize) + 1);
      strKey += String.valueOf(strSrc.charAt(nSeed - 1));
    }

    return Integer.parseInt(strKey);
  }

  public static String monthFunction(String yoil) {
    switch (yoil) {
      case "Jan":
        return "1월";
      case "Feb":
        return "2월";
      case "Mar":
        return "3월";
      case "Apr":
        return "4월";
      case "May":
        return "5월";
      case "Jun":
        return "6월";
      case "Jul":
        return "7월";
      case "Aug":
        return "8월";
      case "Sep":
        return "9월";
      case "Oct":
        return "10월";
      case "Nov":
        return "11월";
      case "Dec":
        return "12월";
    }
    return "";
  }


  public static String yoilFunction(String yoil) {
    switch (yoil) {
      case "Sun":
        return "[월]";

      case "Mon":
        return "[화]";

      case "Tue":
        return "[수]";

      case "Wed":
        return "[목]";

      case "Thu":
        return "[금]";

      case "Fri":
        return "[토]";

      case "Sat":
        return "[일]";
    }
    return "";
  }

  public static String getRandomString(int length) {
    StringBuffer buffer = new StringBuffer();
    Random random = new Random();

    String chars[] = "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",");

    for (int i = 0; i < length; i++) {
      buffer.append(chars[random.nextInt(chars.length)]);
    }
    return buffer.toString();
  }


}
