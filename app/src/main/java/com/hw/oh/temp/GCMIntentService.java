package com.hw.oh.temp;

import com.google.android.gcm.GCMBaseIntentService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *
 * @author : oh
 * @Day : 2014. 10. 13.
 * @Time : 오전 10:21:45
 * @Explanation : GCMIntentService </pre>
 */
public class GCMIntentService extends GCMBaseIntentService {
  private static final String TAG = "GCMIntentService";
  private String mGcm_msg = null;
  private NotificationManager mNotiManager;
  private Vibrator mVibrator;
  private HYPreference mPref;
  private PendingIntent mPendingIntent;
  private Intent mIntent;

  public GCMIntentService() {
    super(Constant.PROJECT_ID);
    Log.d(TAG, "GCM서비스 생성자 실행");
  }

  /**
   * @author : oh
   * @MethodName : onError
   * @Day : 2014. 10. 13.
   * @Time : 오전 10:08:50
   * @Explanation : gcm 에러 발생시 action
   */
  protected void onError(Context paramContext, String paramString) {
    Log.d(TAG, "onError()");
  }

  /**
   * @author : oh
   * @MethodName : onMessage
   * @Day : 2014. 10. 13.
   * @Time : 오전 10:08:50
   * @Explanation : gcm 수신 message
   */
  protected void onMessage(Context context, Intent intent) {
    Log.d(TAG, "수신 Message: " + intent.getExtras().getString("msg"));
    mGcm_msg = intent.getExtras().getString("msg");
    generateNotification(context, mGcm_msg);
  }

  /**
   * @author : oh
   * @MethodName : onRegistered
   * @Day : 2014. 10. 13.
   * @Time : 오전 10:08:50
   * @Explanation : 해당 gcmProject에 registrationID 등록
   */
  protected void onRegistered(Context paramContext, String paramString) {
    Log.d(TAG, "등록 ID:" + paramString);
    registerGCM(paramString);
  }

  /**
   * @author : oh
   * @MethodName : onUnregistered
   * @Day : 2014. 10. 13.
   * @Time : 오전 10:08:50
   * @Explanation : 해당 gcmProject에 registrationID 등록 해지
   */
  protected void onUnregistered(Context paramContext, String paramString) {
    Log.d(TAG, "해지 ID:" + paramString);
  }

  /**
   * @author : oh
   * @MethodName : generateNotification
   * @Day : 2014. 10. 13.
   * @Time : 오전 10:08:50
   * @Explanation : 상태바에 알림 생성
   */
  private void generateNotification(Context context, String gcm_msg) {
    this.mPref = new HYPreference(context);
    this.mVibrator = ((Vibrator) context.getSystemService(VIBRATOR_SERVICE));
    this.mNotiManager = ((NotificationManager) context
        .getSystemService(NOTIFICATION_SERVICE));

    //gcmMessages 파싱(글 번호, 댓글내용)
    if (!(gcm_msg instanceof String)) {
      try {
        JSONObject obj = new JSONObject(gcm_msg);
        Log.i(TAG, "BSEQ :: " + obj.getString("gcmBoardNum").toString());
        mIntent = new Intent(context, DetailActivity.class).addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mIntent.putExtra("ID", obj.getString("gcmBoardNum").toString());

        mPendingIntent = PendingIntent.getActivity(context, Integer.parseInt(obj.getString("gcmCommentNum").toString()),
            mIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(
            context).setSmallIcon(R.drawable.app_icon)
            .setContentTitle(obj.getString("message").toString())
            .setContentText(obj.getString("gcmTitle").toString())
            .setProgress(0, 0, false).setAutoCancel(true)
            .setContentIntent(mPendingIntent).setDefaults(4);

        if (mPref.getValue("soundSet", false))
          notiBuilder.setDefaults(Notification.DEFAULT_SOUND);
        if (mPref.getValue("vibrateSet", false))
          notiBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        if (mPref.getValue("alarm_state", false))
          mNotiManager.notify(Integer.parseInt(obj.getString("gcmCommentNum").toString()), notiBuilder.build());
      } catch (JSONException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * @param regId : GCM 통신을 위한 유저 고유 regID
   * @author : oh
   * @MethodName : sendAPIkey
   * @Day : 2014. 10. 12.
   * @Time : 오후 5:03:52
   * @Explanation : regID 서버에 등록 요청
   */
  public void registerGCM(final String regId) {
    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
    String url = "http://ohy3264.cafe24.com/Anony/api/registerGCM.php";
    StringRequest postReq = new StringRequest(Request.Method.POST,
        url, new Response.Listener<String>() {
      public void onResponse(String paramAnonymousString) {
        Log.d(TAG, "senAPIkey.Response:" + paramAnonymousString);
        if (paramAnonymousString == null)
          Log.d(TAG, "senAPIkey.Response : response null");
      }
    }, new Response.ErrorListener() {
      public void onErrorResponse(
          VolleyError paramAnonymousVolleyError) {
        Log.d(TAG, "senAPIkey.Error.Response : "
            + paramAnonymousVolleyError.getMessage());
      }

    }) {
      protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("REG_ID", regId);
        //  params.put("USER_ID", Constant.LOGIN_ID);
        return params;
      }
    };
    requestQueue.add(postReq);
  }
}