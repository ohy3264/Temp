package com.hw.oh.utility;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by ohyowan on 16. 5. 22..
 */
public class CommonUtil {

    public static String getAndroidID(Context context) {

        return android.provider.Settings.Secure.getString(
                context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }


    /**
     * 문자열이 null 이면 대치문자열로 대치
     *
     * @param str 문자열
     * @param replaceStr 대치 문자열
     * @return String
     */
    public static String nullToString(String str, String replaceStr) {
        return (str == null || "".equals(str))? replaceStr:str;
    }

    /**
     * null 여부 체크
     *
     * @param str 문자열
     * @return boolean
     */
    public static boolean isNull(String str) {
        return (str == null || "".equals(str))? true:false;
    }

    /**
     * null 여부 체크
     *
     * @param str Object 객체
     * @return boolean
     */
    public static boolean isNull(Object str) {
        return (str == null || "".equals(str))? true:false;
    }

}
