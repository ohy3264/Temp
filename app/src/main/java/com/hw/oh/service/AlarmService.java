package com.hw.oh.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.hw.oh.dialog.AlarmPopupDialog;
import com.hw.oh.model.CalendarNoti;
import com.hw.oh.model.PartTimeInfo;
import com.hw.oh.sqlite.DBConstant;
import com.hw.oh.sqlite.DBManager;
import com.hw.oh.temp.process.alba.CalendarActivity;
import com.hw.oh.temp.R;

import java.util.Calendar;

/**
 * Created by hyowan on 2015-10-28.
 */
public class AlarmService extends IntentService {
  public static final String TAG = "AlarmService";

  private CalendarNoti mWeekItem;
  private DBManager mDB;
  private PartTimeInfo mInfo;

  @Override
  public void onCreate() {
    super.onCreate();
    mDB = new DBManager(getApplicationContext());

  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    setDBData(intent.getStringExtra("AlbaName"));
    return super.onStartCommand(intent, flags, startId);
  }

  public AlarmService() {
    super(TAG);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Calendar calendar = Calendar.getInstance();
    mWeekItem = new CalendarNoti();
    if (mDB.selectInfo(DBConstant.COLUMN_ALBANAME, intent.getStringExtra("AlbaName")).getWorkAlarm()) {
      Log.i(TAG, "알람 활성화 됨");
      initWeekInfo(intent);
      switch (calendar.get(Calendar.DAY_OF_WEEK)) {
        case 1: //sun
          if (mWeekItem.isSun())
            sendNotification(intent);
          break;
        case 2:
          if (mWeekItem.isMon())
            sendNotification(intent);
          break;
        case 3:
          if (mWeekItem.isThu())
            sendNotification(intent);
          break;
        case 4:
          if (mWeekItem.isWed())
            sendNotification(intent);
          break;
        case 5:
          if (mWeekItem.isThur())
            sendNotification(intent);
          break;
        case 6:
          if (mWeekItem.isFri())
            sendNotification(intent);
          break;
        case 7:
          if (mWeekItem.isSat())
            sendNotification(intent);
          break;
      }
    } else {
      Log.i(TAG, "알람 비활성화");
    }
  }

  public void initWeekInfo(Intent intent) {
    try {
      Cursor result = mDB.selectWEEKSeachData(DBConstant.COLUMN_ALBANAME, intent.getStringExtra("AlbaName"));
      if (result.moveToFirst()) {
        result.getInt(result.getColumnIndex("_id"));
        Log.i(TAG, Integer.toString(result.getColumnIndex("_id")));
        mWeekItem.setAlbaName(result.getString(result.getColumnIndex("albaname")));
        Log.i(TAG, mWeekItem.getAlbaName());
        mWeekItem.setSun(Boolean.parseBoolean(result.getString(result.getColumnIndex("sun"))));
        Log.i(TAG, result.getString(result.getColumnIndex("sun")));
        mWeekItem.setMon(Boolean.parseBoolean(result.getString(result.getColumnIndex("mon"))));
        Log.i(TAG, result.getString(result.getColumnIndex("mon")));
        mWeekItem.setThu(Boolean.parseBoolean(result.getString(result.getColumnIndex("thu"))));
        Log.i(TAG, result.getString(result.getColumnIndex("thu")));
        mWeekItem.setWed(Boolean.parseBoolean(result.getString(result.getColumnIndex("wed"))));
        Log.i(TAG, result.getString(result.getColumnIndex("wed")));
        mWeekItem.setThur(Boolean.parseBoolean(result.getString(result.getColumnIndex("thur"))));
        Log.i(TAG, result.getString(result.getColumnIndex("thur")));
        mWeekItem.setFri(Boolean.parseBoolean(result.getString(result.getColumnIndex("fri"))));
        Log.i(TAG, result.getString(result.getColumnIndex("fri")));
        mWeekItem.setSat(Boolean.parseBoolean(result.getString(result.getColumnIndex("sat"))));
        Log.i(TAG, result.getString(result.getColumnIndex("sat")));
        mWeekItem.setRequestCode(result.getInt(result.getColumnIndex("requestCode")));
        Log.i(TAG, Integer.toString(result.getColumnIndex("requestCode")));
      }
      result.close();
    } catch (Exception e) {
      Log.e(TAG, e.toString());
    } finally {
      Log.i(TAG, "Cursor close");
    }
  }

  public void sendNotification(Intent intent) {

    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
        0,
        new Intent(getApplicationContext(), CalendarActivity.class).putExtra("ObjectData", mInfo),
        PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
        .setContentTitle("알바시작 알림")
        .setContentText(intent.getStringExtra("AlbaName"))
        .setSmallIcon(R.drawable.app_icon)
        .setContentIntent(contentIntent)
        .setAutoCancel(true)
        .setWhen(System.currentTimeMillis())
        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);

    Notification n = builder.build();
    nm.notify(1234, n);


  }

  public void sendIntent(Intent intent) {
    Intent scenarioPopup = new Intent(getApplicationContext(), AlarmPopupDialog.class);
    scenarioPopup.putExtra("AlbaName", intent.getStringExtra("AlbaName"));
    scenarioPopup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    getApplicationContext().startActivity(scenarioPopup);
  }

  public void setDBData(String albaName) {
    Cursor results = mDB.selectAllData(DBConstant.TABLE_PARTTIMEINFO, DBConstant.COLUMN_ALBANAME, albaName);
    results.moveToFirst();
    mInfo = new PartTimeInfo();
    while (!results.isAfterLast()) {
      mInfo.setAlbaname(results.getString(results.getColumnIndex("albaname")));
      mInfo.setHourMoney(results.getString(results.getColumnIndex("hourMoney")));
      mInfo.setStartTimeHour(results.getString(results.getColumnIndex("startTimeHour")));
      mInfo.setStartTimeMin(results.getString(results.getColumnIndex("startTimeMin")));
      mInfo.setEndTimeHour(results.getString(results.getColumnIndex("endTimeHour")));
      mInfo.setEndTimeMin(results.getString(results.getColumnIndex("endTimeMin")));
      mInfo.setSimpleMemo(results.getString(results.getColumnIndex("simpleMemo")));
      mInfo.setWorkPayNight(results.getString(results.getColumnIndex("workPayNight")));
      mInfo.setWorkRefresh(results.getString(results.getColumnIndex("workRefresh")));
      mInfo.setWorkPayAdd(results.getString(results.getColumnIndex("workPayAdd")));
      mInfo.setWorkRefreshType(results.getString(results.getColumnIndex("workRefreshType")));
      mInfo.setWorkRefreshHour(results.getString(results.getColumnIndex("workRefreshHour")));
      mInfo.setWorkRefreshMin(results.getString(results.getColumnIndex("workRefreshMin")));

      mInfo.setWorkPayEtc(results.getString(results.getColumnIndex("workPayEtc")));
      mInfo.setWorkPayEtcNum(results.getString(results.getColumnIndex("workPayEtcNum")));
      mInfo.setWorkPayEtcMoney(results.getString(results.getColumnIndex("workPayEtcMoney")));

      mInfo.setWorkPayWeekMoney(results.getInt(results.getColumnIndex("workPayWeekMoney")));
      mInfo.setWorkPayWeek(results.getString(results.getColumnIndex("workPayWeek")));
      mInfo.setWorkPayWeekTime(results.getDouble(results.getColumnIndex("workPayWeekTime")));
      mInfo.setWorkAddType(results.getString(results.getColumnIndex("workAddType")));
      mInfo.setWorkPayAddHour(results.getString(results.getColumnIndex("workPayAddHour")));
      mInfo.setWorkPayAddMin(results.getString(results.getColumnIndex("workPayAddMin")));
      mInfo.setWorkAlarm(Boolean.parseBoolean(results.getString(results.getColumnIndex("workAlarm"))));
      mInfo.setWorkMonthDay(results.getInt(results.getColumnIndex("workMonthDay")));
      results.moveToNext();
    }
    results.close();
  }

}
