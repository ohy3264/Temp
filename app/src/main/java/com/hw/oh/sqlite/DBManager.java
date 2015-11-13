package com.hw.oh.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hw.oh.model.CalendarInfo;
import com.hw.oh.model.CalendarNoti;
import com.hw.oh.model.PartTimeInfo;
import com.hw.oh.model.PartTimeItem;

/**
 * Created by oh on 2015-02-21.
 */
public class DBManager extends SQLiteOpenHelper {
  private static final String TAG = "DBManager";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;
  // Database Version
  private static final int DATABASE_VERSION = 14; //2015.10.27 hwoh :: 13에서 14로 변경함

  // Database Column
  private final String[] PARTTIME_DATA_COLUMN = {"_id", "albaname", "date", "hourMoney", "startTimeHour",
      "startTimeMin", "endTimeHour", "endTimeMin", "simpleMemo", "workTimeHour", "workTimeMin", "workPaygabul",
      "workPayNight", "workPayTotal", "workPayAdd", "workPaygabulVal", "workRefresh", "workRefreshType", "workRefreshHour",
      "workRefreshMin", "workPayTotalTime", "workPayNightTotalTime", "workPayAddTotalTime", "workPayRefreshTotalTime", "workPayEtc",
      "workPayEtcNum", "workPayEtcMoney", "workPayWeekTime", "workPayWeekMoney", "workPayWeek", "workRefreshState", "workAddType", "workPayAddHour", "workPayAddMin"};

  private final String[] PARTTIME_INFO_COLUMN = {"_id", "albaname", "hourMoney", "startTimeHour",
      "startTimeMin", "endTimeHour", "endTimeMin", "simpleMemo", "workPayNight", "workPayAdd", "workRefresh", "workRefreshType", "workRefreshHour", "workRefreshMin", "workPayEtc",
      "workPayEtcNum", "workPayEtcMoney", "workPayWeekTime", "workPayWeekMoney", "workPayWeek", "workAddType", "workPayAddHour", "workPayAddMin", "workAlarm", "workMonthDay"};

  public DBManager(Context context) {
    super(context, DBConstant.DATABASE_NAME, null, DATABASE_VERSION);
  }


  @Override
  public void onCreate(SQLiteDatabase db) {
    if (INFO)
      Log.i(TAG, "onCreate DataBase");
    String createTable1 = "create table " + DBConstant.TABLE_PARTTIMEDATA + " ("
        + "_id integer primary key autoincrement, " + "albaname VARCHAR, "
        + "date VARCHAR, "
        + "hourMoney VARCHAR, "
        + "startTimeHour VARCHAR, "
        + "startTimeMin VARCHAR, "
        + "endTimeHour VACHAR, "
        + "endTimeMin VACHAR, "
        + "simpleMemo VACHAR, "
        + "workTimeHour VACHAR, "
        + "workTimeMin VACHAR, "
        + "workPaygabul VACHAR DEFAULT false, "
        + "workPayNight VACHAR DEFAULT false, "
        + "workPayTotal VACHAR DEFAULT 0, "
        + "workRefresh VACHAR DEFAULT false, "
        + "workPaygabulVal VACHAR DEFAULT 0, "
        + "workPayAdd VACHAR DEFAULT false, "
        + "workAddType VACHAR DEFAULT 0, "
        + "workPayAddHour VACHAR DEFAULT 0, "
        + "workPayAddMin VACHAR DEFAULT 0, "
        + "workRefreshType VACHAR DEFAULT 0, "
        + "workRefreshHour VACHAR DEFAULT 0, "
        + "workRefreshMin VACHAR DEFAULT 0, "
        + "workPayTotalTime integer DEFAULT 0, "
        + "workPayNightTotalTime integer DEFAULT 0, "
        + "workPayAddTotalTime integer DEFAULT 0, "
        + "workPayRefreshTotalTime integer DEFAULT 0, "
        + "workPayEtcMoney VACHAR DEFAULT 0, "
        + "workPayEtcNum VACHAR DEFAULT 1, "
        + "workPayEtc VACHAR DEFAULT false, "
        + "workPayWeekTime integer DEFAULT 0, "
        + "workPayWeekMoney integer DEFAULT 0, "
        + "workPayWeek VACHAR DEFAULT false, "
        + "workRefreshState integer DEFAULT 0, "
        + "unique(albaname, date))";
    db.execSQL(createTable1);

    String createTable2 = "create table " + DBConstant.TABLE_PARTTIMEINFO + " ("
        + "_id integer primary key autoincrement, " + "albaname VARCHAR, "
        + "hourMoney VARCHAR, "
        + "startTimeHour VARCHAR, "
        + "startTimeMin VARCHAR, "
        + "endTimeHour VACHAR, "
        + "endTimeMin VACHAR, "
        + "simpleMemo VACHAR, "
        + "workPayNight VACHAR DEFAULT false, "
        + "workRefresh VACHAR DEFAULT false, "
        + "workPayAdd VACHAR DEFAULT false, "
        + "workRefreshType VACHAR DEFAULT 0, "
        + "workRefreshHour VACHAR DEFAULT 0, "
        + "workRefreshMin VACHAR DEFAULT 0, "
        + "workPayEtcMoney VACHAR DEFAULT 0, "
        + "workPayEtcNum VACHAR DEFAULT 1, "
        + "workPayEtc VACHAR DEFAULT false, "
        + "workPayWeekTime integer DEFAULT 0, "
        + "workPayWeekMoney integer DEFAULT 0, "
        + "workPayWeek VACHAR DEFAULT false, "
        + "workAddType VACHAR DEFAULT 0, "
        + "workPayAddHour VACHAR DEFAULT 0, "
        + "workPayAddMin VACHAR DEFAULT 0, "
        + "workAlarm VACHAR DEFAULT false, "
        + "workMonthDay integer DEFAULT 1, "
        + "unique(albaname))";
    db.execSQL(createTable2);
    String createTable3 = "create table " + DBConstant.TABLE_CALENDARINFO + " ("
        + "_id integer primary key autoincrement, " + "albaname VARCHAR, "
        + "startYear VARCHAR, " + "startMonth VARCHAR, startDay VARCHAR, endYear VACHAR, endMonth VACHAR, endDay VACHAR, unique(albaname))";
    db.execSQL(createTable3);

    String createTable4 = "create table IF NOT EXISTS " + DBConstant.TABLE_WEEK_INFO + " ("
        + "_id integer primary key autoincrement, " + "albaname VARCHAR, "
        + "sun VARCHAR, " + "mon VARCHAR, thu VARCHAR, wed VACHAR, thur VACHAR, fri VACHAR, sat VACHAR, requestCode integer DEFAULT 0, unique(albaname))";
    db.execSQL(createTable4);

  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.i(TAG, "Upgrading from version " + oldVersion
        + " to " + newVersion);

    String createTable4 = "create table IF NOT EXISTS " + DBConstant.TABLE_WEEK_INFO + " ("
        + "_id integer primary key autoincrement, " + "albaname VARCHAR, "
        + "sun VARCHAR, " + "mon VARCHAR, thu VARCHAR, wed VACHAR, thur VACHAR, fri VACHAR, sat VACHAR, unique(albaname))";
    db.execSQL(createTable4);


    String sql = "select * from " + DBConstant.TABLE_PARTTIMEDATA + ";";
    Cursor results = db.rawQuery(sql, null);
    results.moveToFirst();
    for (int i = 0; i < PARTTIME_DATA_COLUMN.length; i++) {
      if (!addColumn(results, PARTTIME_DATA_COLUMN[i])) {
        try {
          db.beginTransaction();
          if (PARTTIME_DATA_COLUMN[i].equals("workPayNight")) {
            Log.i(TAG, "workPayNight 없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workPayNight" + " VACHAR DEFAULT 0");
          } else {
            Log.i(TAG, "workPayNight 있음");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workPayTotal")) {
            Log.i(TAG, "workPayTotal 없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workPayTotal" + " VACHAR DEFAULT 0");
          } else {
            Log.i(TAG, "workPayTotal 있음");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workRefresh")) {
            Log.i(TAG, "workRefresh 없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workRefresh" + " VACHAR DEFAULT false");
          } else {
            Log.i(TAG, "workRefresh 있음");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workPaygabulVal")) {
            Log.i(TAG, "workPaygabul 없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workPaygabulVal" + " VACHAR DEFAULT 0");
          } else {
            Log.i(TAG, "workPaygabul 있음");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workPayAdd")) {
            Log.i(TAG, "workPayAdd 없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workPayAdd" + " VACHAR DEFAULT false");
          } else {
            Log.i(TAG, "workPayAdd 있음");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workRefreshType")) {
            Log.i(TAG, "workRefreshType 없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workRefreshType" + " VACHAR DEFAULT 0");
          } else {
            Log.i(TAG, "workRefreshType 있음");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workRefreshHour")) {
            Log.i(TAG, "workRefreshHour 없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workRefreshHour" + " VACHAR DEFAULT 0");
          } else {
            Log.i(TAG, "workRefreshHour 있음");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workRefreshMin")) {
            Log.i(TAG, "workRefreshMin 없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workRefreshMin" + " VACHAR DEFAULT 0");
          } else {
            Log.i(TAG, "workRefreshMin 있음");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workPayTotalTime")) {
            Log.i(TAG, "workPayTotalTime 없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workPayTotalTime" + " integer  DEFAULT 0");
          } else {
            Log.i(TAG, "workPayTotalTime 있음");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workPayNightTotalTime")) {
            Log.i(TAG, "workPayNightTotalTime   없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workPayNightTotalTime" + " integer  DEFAULT 0");
          } else {
            Log.i(TAG, "workPayNightTotalTime  있음");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workPayAddTotalTime")) {
            Log.i(TAG, "workPayAddTotalTime  없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workPayAddTotalTime" + " integer  DEFAULT 0");
          } else {
            Log.i(TAG, "workPayAddTotalTime  있음");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workPayRefreshTotalTime")) {
            Log.i(TAG, "workPayRefreshTotalTime  없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workPayRefreshTotalTime" + " integer  DEFAULT 0");
          } else {
            Log.i(TAG, "workPayRefreshTotalTime  있음");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workPayEtcMoney")) {
            Log.i(TAG, "workPayEtcMoney  없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workPayEtcMoney" + " VACHAR  DEFAULT 0");
          } else {
            Log.i(TAG, "workPayEtcMoney  있음");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workPayEtcNum")) {
            Log.i(TAG, "workPayEtcNum  없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workPayEtcNum" + " VACHAR  DEFAULT 1");
          } else {
            Log.i(TAG, "workPayEtcNum  있음");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workPayEtc")) {
            Log.i(TAG, "workPayEtc  없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workPayEtc" + " VACHAR  DEFAULT false");
          } else {
            Log.i(TAG, "workPayEtc  있음");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workPayWeekTime")) {
            Log.i(TAG, "workPayWeekTime  없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workPayWeekTime" + " integer  DEFAULT 0");
            //db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workPayWeekTime" + " Double  DEFAULT 0.0");
          } else {
            Log.i(TAG, "workPayWeekTime  있음");
            //  db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " MODIFY COLUMN " + "workPayWeekTime" + " Double  DEFAULT 0.0");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workPayWeekMoney")) {
            Log.i(TAG, "workPayWeekMoney  없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workPayWeekMoney" + " integer  DEFAULT 0");
          } else {
            Log.i(TAG, "workPayWeekMoney  있음");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workPayWeek")) {
            Log.i(TAG, "workPayWeek  없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workPayWeek" + " VACHAR  DEFAULT false");
          } else {
            Log.i(TAG, "workPayWeek  있음");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workRefreshState")) {
            Log.i(TAG, "workRefreshState  없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workRefreshState" + " integer  DEFAULT 0");
          } else {
            Log.i(TAG, "workRefreshState  있음");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workAddType")) {
            Log.i(TAG, "workAddType  없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workAddType" + " VACHAR  DEFAULT 0");
          } else {
            Log.i(TAG, "workAddType  있음");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workPayAddHour")) {
            Log.i(TAG, "workPayAddHour  없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workPayAddHour" + " VACHAR  DEFAULT 0");
          } else {
            Log.i(TAG, "workPayAddHour  있음");
          }
          if (PARTTIME_DATA_COLUMN[i].equals("workPayAddMin")) {
            Log.i(TAG, "workPayAddMin  없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workPayAddMin" + " VACHAR  DEFAULT 0");
          } else {
            Log.i(TAG, "workPayAddMin  있음");
          }

          db.setTransactionSuccessful();
        } catch (IllegalStateException e) {
          Log.e(TAG, e.getMessage());
        } finally {
          db.endTransaction();
        }
      }
    }


    sql = "select * from " + DBConstant.TABLE_PARTTIMEINFO + ";";
    results = db.rawQuery(sql, null);
    results.moveToFirst();
    for (int i = 0; i < PARTTIME_INFO_COLUMN.length; i++) {
      if (!addColumn(results, PARTTIME_INFO_COLUMN[i])) {
        try {
          db.beginTransaction();
          if (PARTTIME_INFO_COLUMN[i].equals("workPayNight")) {
            Log.i(TAG, "workPayNight 없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEINFO + " ADD COLUMN " + "workPayNight" + " VACHAR DEFAULT 0");
          } else {
            Log.i(TAG, "workPayNight 있음");
          }
          if (PARTTIME_INFO_COLUMN[i].equals("workRefresh")) {
            Log.i(TAG, "workRefresh 없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEINFO + " ADD COLUMN " + "workRefresh" + " VACHAR DEFAULT 0");
          } else {
            Log.i(TAG, "workRefresh 있음");
          }
          if (PARTTIME_INFO_COLUMN[i].equals("workPayAdd")) {
            Log.i(TAG, "workPayAdd 없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEINFO + " ADD COLUMN " + "workPayAdd" + " VACHAR DEFAULT 0");
          } else {
            Log.i(TAG, "workPayAdd 있음");
          }
          if (PARTTIME_INFO_COLUMN[i].equals("workRefreshType")) {
            Log.i(TAG, "workRefreshType 없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEINFO + " ADD COLUMN " + "workRefreshType" + " VACHAR DEFAULT 0");
          } else {
            Log.i(TAG, "workRefreshType 있음");
          }
          if (PARTTIME_INFO_COLUMN[i].equals("workRefreshHour")) {
            Log.i(TAG, "workRefreshHour 없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEINFO + " ADD COLUMN " + "workRefreshHour" + " VACHAR DEFAULT 0");
          } else {
            Log.i(TAG, "workRefreshHour 있음");
          }
          if (PARTTIME_INFO_COLUMN[i].equals("workRefreshMin")) {
            Log.i(TAG, "workRefreshMin 없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEINFO + " ADD COLUMN " + "workRefreshMin" + " VACHAR DEFAULT 0");
          } else {
            Log.i(TAG, "workRefreshMin 있음");
          }
          if (PARTTIME_INFO_COLUMN[i].equals("workPayEtc")) {
            Log.i(TAG, "workPayEtc 없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEINFO + " ADD COLUMN " + "workPayEtc" + " VACHAR DEFAULT false");
          } else {
            Log.i(TAG, "workPayEtc 있음");
          }
          if (PARTTIME_INFO_COLUMN[i].equals("workPayEtcNum")) {
            Log.i(TAG, "workPayEtcNum 없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEINFO + " ADD COLUMN " + "workPayEtcNum" + " VACHAR DEFAULT 1");
          } else {
            Log.i(TAG, "workPayEtcNum 있음");
          }
          if (PARTTIME_INFO_COLUMN[i].equals("workPayEtcMoney")) {
            Log.i(TAG, "workPayEtcMoney 없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEINFO + " ADD COLUMN " + "workPayEtcMoney" + " VACHAR DEFAULT 0");
          } else {
            Log.i(TAG, "workPayEtcMoney 있음");
          }
          if (PARTTIME_INFO_COLUMN[i].equals("workPayWeekTime")) {
            Log.i(TAG, "workPayWeekTime  없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEINFO + " ADD COLUMN " + "workPayWeekTime" + " integer  DEFAULT 0");
            //db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " ADD COLUMN " + "workPayWeekTime" + " Double  DEFAULT 0.0");
          } else {
            Log.i(TAG, "workPayWeekTime  있음");
            // db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEDATA + " MODIFY COLUMN " + "workPayWeekTime" + " Double  DEFAULT 0.0");
          }
          if (PARTTIME_INFO_COLUMN[i].equals("workPayWeekMoney")) {
            Log.i(TAG, "workPayWeekMoney 없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEINFO + " ADD COLUMN " + "workPayWeekMoney" + " integer DEFAULT 0");
          } else {
            Log.i(TAG, "workPayWeekMoney 있음");
          }
          if (PARTTIME_INFO_COLUMN[i].equals("workPayWeek")) {
            Log.i(TAG, "workPayWeek 없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEINFO + " ADD COLUMN " + "workPayWeek" + " VACHAR DEFAULT false");
          } else {
            Log.i(TAG, "workPayWeek 있음");
          }
          if (PARTTIME_INFO_COLUMN[i].equals("workAddType")) {
            Log.i(TAG, "workAddType  없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEINFO + " ADD COLUMN " + "workAddType" + " VACHAR  DEFAULT 0");
          } else {
            Log.i(TAG, "workAddType  있음");
          }
          if (PARTTIME_INFO_COLUMN[i].equals("workPayAddHour")) {
            Log.i(TAG, "workPayAddHour  없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEINFO + " ADD COLUMN " + "workPayAddHour" + " VACHAR  DEFAULT 0");
          } else {
            Log.i(TAG, "workPayAddHour  있음");
          }
          if (PARTTIME_INFO_COLUMN[i].equals("workPayAddMin")) {
            Log.i(TAG, "workPayAddMin  없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEINFO + " ADD COLUMN " + "workPayAddMin" + " VACHAR  DEFAULT 0");
          } else {
            Log.i(TAG, "workPayAddMin  있음");
          }
          if (PARTTIME_INFO_COLUMN[i].equals("workAlarm")) {
            Log.i(TAG, "workAlarm  없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEINFO + " ADD COLUMN " + "workAlarm" + " VACHAR  DEFAULT false");
          } else {
            Log.i(TAG, "workAlarm  있음");
          }
          if (PARTTIME_INFO_COLUMN[i].equals("workMonthDay")) {
            Log.i(TAG, "workMonthDay  없음");
            db.execSQL("ALTER TABLE " + DBConstant.TABLE_PARTTIMEINFO + " ADD COLUMN " + "workMonthDay" + " integer  DEFAULT 1");
          } else {
            Log.i(TAG, "workMonthDay  있음");
          }

          db.setTransactionSuccessful();
        } catch (IllegalStateException e) {
          Log.e(TAG, e.getMessage());
        } finally {
          db.endTransaction();
        }
      }
    }
  }

  public boolean addColumn(Cursor results, String column) {
    for (int i = 0; i < results.getColumnNames().length; i++) {
      if (column.equals(results.getColumnName(i)))
        return true;
    }
    return false;
  }

  // 알바정보 데이터 추가
  public boolean insertPartTimeInfo(PartTimeInfo info) {
    if (INFO)
      Log.i(TAG, "insertData DataBase");
    SQLiteDatabase db = getWritableDatabase();
    try {
      ContentValues values = new ContentValues();
      values.put("albaname", info.getAlbaname());
      values.put("hourMoney", info.getHourMoney());
      values.put("startTimeHour", info.getStartTimeHour());
      values.put("startTimeMin", info.getStartTimeMin());
      values.put("endTimeHour", info.getEndTimeHour());
      values.put("endTimeMin", info.getEndTimeMin());
      values.put("simpleMemo", info.getSimpleMemo());
      values.put("workPayNight", info.getWorkPayNight());
      values.put("workRefresh", info.getWorkRefresh());
      values.put("workPayAdd", info.getWorkPayAdd());
      values.put("workRefreshType", info.getWorkRefreshType());
      values.put("workRefreshHour", info.getWorkRefreshHour());
      values.put("workRefreshMin", info.getWorkRefreshMin());
      values.put("workPayEtc", info.getWorkPayEtc());
      values.put("workPayEtcNum", info.getWorkPayEtcNum());
      values.put("workPayEtcMoney", info.getWorkPayEtcMoney());
      values.put("workPayWeekTime", info.getWorkPayWeekTime());
      values.put("workPayWeekMoney", info.getWorkPayWeekMoney());
      values.put("workPayWeek", info.getWorkPayWeek());
      values.put("workAddType", info.getWorkAddType());
      values.put("workPayAddHour", info.getWorkPayAddHour());
      values.put("workPayAddMin", info.getWorkPayAddMin());
      values.put("workAlarm", Boolean.toString(info.getWorkAlarm()));
      values.put("workMonthDay", info.getWorkMonthDay());

      db.insert(DBConstant.TABLE_PARTTIMEINFO, null, values);
      return true;
    } catch (Exception e) {
      Log.e(TAG, e.toString());
    }
    return false;
  }

  // 캘린더 데이터 추가
  public boolean insertCalendarInfo(CalendarInfo info) {
    if (INFO)
      Log.i(TAG, "insertCalendarInfo DataBase");
    SQLiteDatabase db = getWritableDatabase();

    try {
      String sql = "insert into " + DBConstant.TABLE_CALENDARINFO + " values(NULL, '"
          + info.getAlbaname() + "', '"
          + info.getStartYear() + "', '"
          + info.getStartMonth() + "', '"
          + info.getStartDay() + "', '"
          + info.getEndYear() + "', '"
          + info.getEndMonth() + "', '"
          + info.getEndDay() + "');";

      db.execSQL(sql);

      return true;
    } catch (Exception e) {
      Log.e(TAG, e.toString());
    }
    return false;
  }

  // 일한 데이터 추가
  public boolean insertPartTimeData(PartTimeItem item) {
    if (INFO)
      Log.i(TAG, "insertData DataBase");
    Log.i("tag", item.getWorkPayWeek());
    SQLiteDatabase db = getWritableDatabase();
    try {
           /* String sql = "insert into " + DBConstant.TABLE_PARTTIMEDATA + " values(NULL, '"
                    + item.getAlbaname() + "', '"
                    + item.getDate() + "', '"
                    + item.getHourMoney() + "', '"
                    + item.getStartTimeHour() + "', '"
                    + item.getStartTimeMin() + "', '"
                    + item.getEndTimeHour() + "', '"
                    + item.getEndTimeMin() + "', '"
                    + item.getSimpleMemo() + "', '"
                    + item.getWorkTimeHour() + "', '"
                    + item.getWorkTimeMin() + "', '"
                    + item.getWorkPayGabul() + "', '"
                    + item.getWorkPayNight() + "', '"
                    + item.getWorkPayTotal() + "', '"
                    + item.getWorkRefreshTime() + "', '"
                    + item.getWorkPayGabulVal() + "');";
            db.execSQL(sql);*/
      ContentValues values = new ContentValues();
      values.put("albaname", item.getAlbaname());
      values.put("date", item.getDate());
      values.put("hourMoney", item.getHourMoney());
      values.put("startTimeHour", item.getStartTimeHour());
      values.put("startTimeMin", item.getStartTimeMin());
      values.put("endTimeHour", item.getEndTimeHour());
      values.put("endTimeMin", item.getEndTimeMin());
      values.put("simpleMemo", item.getSimpleMemo());
      values.put("workTimeHour", item.getWorkTimeHour());
      values.put("workTimeMin", item.getWorkTimeMin());
      values.put("workPaygabul", item.getWorkPayGabul());
      values.put("workPayNight", item.getWorkPayNight());
      values.put("workPayTotal", item.getWorkPayTotal());
      values.put("workRefresh", item.getWorkRefreshTime());
      values.put("workPaygabulVal", item.getWorkPayGabulVal());
      values.put("workPayAdd", item.getWorkPayAdd());
      values.put("workRefreshType", item.getWorkRefreshType());
      values.put("workRefreshHour", item.getWorkRefreshHour());
      values.put("workRefreshMin", item.getWorkRefreshMin());
      values.put("workPayTotalTime ", item.getWorkPayTotalTime());
      values.put("workPayNightTotalTime ", item.getWorkPayNightTotalTime());
      values.put("workPayAddTotalTime ", item.getWorkPayAddTotalTime());
      values.put("workPayRefreshTotalTime ", item.getWorkPayRefreshTotalTime());
      values.put("workPayEtcNum ", item.getWorkEtcNum());
      values.put("workPayEtcMoney ", item.getWorkEtcMoeny());
      values.put("workPayEtc ", item.getWorkEtc());
      values.put("workPayWeekTime ", item.getWorkPayWeekTime());
      values.put("workPayWeekMoney ", item.getWorkPayWeekMoney());
      values.put("workPayWeek ", item.getWorkPayWeek());
      values.put("workRefreshState ", item.getWorkRefreshState());
      values.put("workAddType ", item.getWorkAddType());
      values.put("workPayAddHour ", item.getWorkAddHour());
      values.put("workPayAddMin ", item.getWorkAddMin());


      db.insert(DBConstant.TABLE_PARTTIMEDATA, null, values);


      return true;
    } catch (Exception e) {

    }
    return false;
  }


  // 알바정보 갱신
  //"workPayEtc", "workPayEtcNum", "workPayEtcMoney", "workPayWeekTime", "workPayWeekMoney", "workPayWeek"
  public boolean updatePartTimeInfo(PartTimeInfo info, String albaname) {
    if (INFO)
      Log.i(TAG, "updateData DataBase");
    SQLiteDatabase db = getWritableDatabase();
    try {
      String sql = "update " + DBConstant.TABLE_PARTTIMEINFO + " set albaname = '" + info.getAlbaname()
          + "', hourMoney = '" + info.getHourMoney()
          + "', startTimeHour = '" + info.getStartTimeHour()
          + "', startTimeMin = '" + info.getStartTimeMin()
          + "', endTimeHour = '" + info.getEndTimeHour()
          + "', endTimeMin = '" + info.getEndTimeMin()
          + "', simpleMemo = '" + info.getSimpleMemo()
          + "', workPayNight = '" + info.getWorkPayNight()
          + "', workRefresh = '" + info.getWorkRefresh()
          + "', workPayAdd = '" + info.getWorkPayAdd()
          + "', workRefreshType = '" + info.getWorkRefreshType()
          + "', workRefreshHour = '" + info.getWorkRefreshHour()
          + "', workRefreshMin = '" + info.getWorkRefreshMin()
          + "', workPayEtc = '" + info.getWorkPayEtc()
          + "', workPayEtcNum = '" + info.getWorkPayEtcNum()
          + "', workPayEtcMoney = '" + info.getWorkPayEtcMoney()
          + "', workPayWeekTime = '" + info.getWorkPayWeekTime()
          + "', workPayWeekMoney = '" + info.getWorkPayWeekMoney()
          + "', workPayWeek = '" + info.getWorkPayWeek()
          + "', workAddType  = '" + info.getWorkAddType()
          + "', workPayAddHour  = '" + info.getWorkPayAddHour()
          + "', workPayAddMin  = '" + info.getWorkPayAddMin()
          + "', workAlarm  = '" + info.getWorkAlarm()
          + "', workMonthDay  = '" + info.getWorkMonthDay()
          + "' where albaname = '" + albaname
          + "';";
      Log.i(TAG, sql);
      db.execSQL(sql);
      sql = "update " + DBConstant.TABLE_PARTTIMEDATA + " set albaname = '" + info.getAlbaname()
          + "' where albaname = '" + albaname
          + "';";
      Log.i(TAG, sql);
      db.execSQL(sql);

      return true;
    } catch (Exception e) {

    }
    return false;
  }

  public boolean updatePartTimeInfoSwap(PartTimeInfo info, String _id) {
    if (INFO)
      Log.i(TAG, "updateData DataBase");
    SQLiteDatabase db = getWritableDatabase();
    try {
     /* String sql = "replace " + DBConstant.TABLE_PARTTIMEINFO + " set albaname = (" + info.getAlbaname()
          + "';";*/

      ContentValues values = new ContentValues();
      values.put("albaname", info.getAlbaname());
      values.put("hourMoney", info.getHourMoney());
      values.put("startTimeHour", info.getStartTimeHour());
      values.put("startTimeMin", info.getStartTimeMin());
      values.put("endTimeHour", info.getEndTimeHour());
      values.put("endTimeMin", info.getEndTimeMin());
      values.put("simpleMemo", info.getSimpleMemo());
      values.put("workPayNight", info.getWorkPayNight());
      values.put("workRefresh", info.getWorkRefresh());
      values.put("workPayAdd", info.getWorkPayAdd());
      values.put("workRefreshType", info.getWorkRefreshType());
      values.put("workRefreshHour", info.getWorkRefreshHour());
      values.put("workRefreshMin", info.getWorkRefreshMin());
      values.put("workPayEtc", info.getWorkPayEtc());
      values.put("workPayEtcNum", info.getWorkPayEtcNum());
      values.put("workPayEtcMoney", info.getWorkPayEtcMoney());
      values.put("workPayWeekTime", info.getWorkPayWeekTime());
      values.put("workPayWeekMoney", info.getWorkPayWeekMoney());
      values.put("workPayWeek", info.getWorkPayWeek());
      values.put("workAddType", info.getWorkAddType());
      values.put("workPayAddHour", info.getWorkPayAddHour());
      values.put("workPayAddMin", info.getWorkPayAddMin());
      values.put("workAlarm", Boolean.toString(info.getWorkAlarm()));
      values.put("workMonthDay", info.getWorkMonthDay());

      db.insert(DBConstant.TABLE_PARTTIMEINFO, null, values);
      return true;
    } catch (Exception e) {
      Log.i(TAG, e.toString());

    }
    return false;
  }


  // 데이터 갱신
  public boolean updatePartTimeData(PartTimeItem item, String date) {
    if (INFO)
      Log.i(TAG, "updateData DataBase");
    SQLiteDatabase db = getWritableDatabase();
    try {
      String sql = "update " + DBConstant.TABLE_PARTTIMEDATA + " set hourMoney = '" + item.getHourMoney()
          + "', startTimeHour = '" + item.getStartTimeHour()
          + "', startTimeMin = '" + item.getStartTimeMin()
          + "', endTimeHour = '" + item.getEndTimeHour()
          + "', endTimeMin = '" + item.getEndTimeMin()
          + "', simpleMemo = '" + item.getSimpleMemo()
          + "', workTimeHour = '" + item.getWorkTimeHour()
          + "', workTimeMin = '" + item.getWorkTimeMin()
          + "', workPaygabul = '" + item.getWorkPayGabul()
          + "', workPayNight = '" + item.getWorkPayNight()
          + "', workRefresh = '" + item.getWorkRefreshTime()
          + "', workPayTotal = '" + item.getWorkPayTotal()
          + "', workPaygabulVal = '" + item.getWorkPayGabulVal()
          + "', workPayAdd = '" + item.getWorkPayAdd()
          + "', workRefreshType = '" + item.getWorkRefreshType()
          + "', workRefreshHour = '" + item.getWorkRefreshHour()
          + "', workRefreshMin = '" + item.getWorkRefreshMin()
          + "', workPayTotalTime  = '" + item.getWorkPayTotalTime()
          + "', workPayNightTotalTime  = '" + item.getWorkPayNightTotalTime()
          + "', workPayAddTotalTime  = '" + item.getWorkPayAddTotalTime()
          + "', workPayRefreshTotalTime  = '" + item.getWorkPayRefreshTotalTime()
          + "', workPayEtcMoney  = '" + item.getWorkEtcMoeny()
          + "', workPayEtcNum  = '" + item.getWorkEtcNum()
          + "', workPayEtc  = '" + item.getWorkEtc()
          + "', workPayWeekTime  = '" + item.getWorkPayWeekTime()
          + "', workPayWeekMoney  = '" + item.getWorkPayWeekMoney()
          + "', workPayWeek  = '" + item.getWorkPayWeek()
          + "', workRefreshState  = '" + item.getWorkRefreshState()
          + "', workAddType  = '" + item.getWorkAddType()
          + "', workPayAddHour  = '" + item.getWorkAddHour()
          + "', workPayAddMin  = '" + item.getWorkAddMin()
          + "' where date = '" + date
          + "' AND albaname = '" + item.getAlbaname() + "';";
      Log.i(TAG, sql);
      db.execSQL(sql);
      return true;
    } catch (Exception e) {
      Log.i(TAG, "updateData fail :: " + e.toString());

    }
    return false;
  }

  // 캘린더정보 갱신
  public boolean updateCalendarInfo(CalendarInfo Info) {
    if (INFO)
      Log.i(TAG, "updateData DataBase");
    SQLiteDatabase db = getWritableDatabase();
    try {
      String sql = "update " + DBConstant.TABLE_CALENDARINFO + " set startYear = '" + Info.getStartYear()
          + "', startMonth = '" + Info.getStartMonth()
          + "', startDay = '" + Info.getStartDay()
          + "', endYear = '" + Info.getEndYear()
          + "', endMonth = '" + Info.getEndMonth()
          + "', endDay = '" + Info.getEndDay()
          + "' where albaname = '" + Info.getAlbaname() + "';";
      Log.i(TAG, sql);
      db.execSQL(sql);
      return true;
    } catch (Exception e) {
      Log.i(TAG, "updateData fail :: " + e.toString());

    }
    return false;
  }
  // 데이터 전체삭제
  public boolean removeALLData(String table) {
    if (INFO)
      Log.i(TAG, "removeALLData DataBase");
    SQLiteDatabase db = getWritableDatabase();
    try {
      String sql = "Delete from " + table +  ";";
      db.execSQL(sql);
      return true;
    } catch (Exception e) {
      Log.i(TAG, "removeALLData fail :: " + e.toString());
    }
    return false;
  }

  // 데이터 삭제
  public boolean removeData(String date, String albaname) {
    if (INFO)
      Log.i(TAG, "removeData DataBase");


    SQLiteDatabase db = getWritableDatabase();
    try {
      String sql = "delete from " + DBConstant.TABLE_PARTTIMEDATA + " where date = '" + date + "' AND " + "albaname" + " = '" + albaname
          + "';";
      db.execSQL(sql);
      return true;
    } catch (Exception e) {
      Log.i(TAG, "removeData fail :: " + e.toString());
    }
    return false;
  }

  // 알바 삭제
  public boolean removeInfo(String albaname) {
    if (INFO)
      Log.i(TAG, "removeData DataBase");
    SQLiteDatabase db = getWritableDatabase();
    try {
      String sql = "delete from " + DBConstant.TABLE_PARTTIMEINFO + " where albaname = '" + albaname + "';";
      db.execSQL(sql);

      String sql_data = "delete from " + DBConstant.TABLE_PARTTIMEDATA + " where albaname = '" + albaname + "';";
      db.execSQL(sql_data);

      String sql_week = "delete from " + DBConstant.TABLE_WEEK_INFO + " where albaname = '" + albaname + "';";
      db.execSQL(sql_week);
      return true;
    } catch (Exception e) {
      Log.i(TAG, "removeData fail :: " + e.toString());
    }
    return false;
  }


  // 데이터 검색
  public PartTimeInfo selectInfo(String column, String data) {
    if (INFO)
      Log.i(TAG, "selectData DataBase");
    SQLiteDatabase db = getWritableDatabase();
    String sql = "select * from " + DBConstant.TABLE_PARTTIMEINFO + " where " + column + " = '" + data
        + "';";
    Cursor results = db.rawQuery(sql, null);
    // result(Cursor 객체)가 비어 있으면 false 리턴
    if (results.moveToFirst()) {
      PartTimeInfo info = new PartTimeInfo();
      info.setAlbaname(results.getString(results.getColumnIndex("albaname")));
      info.setHourMoney(results.getString(results.getColumnIndex("hourMoney")));
      info.setStartTimeHour(results.getString(results.getColumnIndex("startTimeHour")));
      info.setStartTimeMin(results.getString(results.getColumnIndex("startTimeMin")));
      info.setEndTimeHour(results.getString(results.getColumnIndex("endTimeHour")));
      info.setEndTimeMin(results.getString(results.getColumnIndex("endTimeMin")));
      info.setSimpleMemo(results.getString(results.getColumnIndex("simpleMemo")));
      info.setWorkPayNight(results.getString(results.getColumnIndex("workPayNight")));
      info.setWorkRefresh(results.getString(results.getColumnIndex("workRefresh")));
      info.setWorkPayAdd(results.getString(results.getColumnIndex("workPayAdd")));
      info.setWorkRefreshType(results.getString(results.getColumnIndex("workRefreshType")));
      info.setWorkRefreshHour(results.getString(results.getColumnIndex("workRefreshHour")));
      info.setWorkRefreshMin(results.getString(results.getColumnIndex("workRefreshMin")));
      info.setWorkPayEtc(results.getString(results.getColumnIndex("workPayEtc")));
      info.setWorkPayEtcNum(results.getString(results.getColumnIndex("workPayEtcNum")));
      info.setWorkPayEtcMoney(results.getString(results.getColumnIndex("workPayEtcMoney")));
      info.setWorkPayWeekTime((double) results.getInt(results.getColumnIndex("workPayWeekTime")));
      info.setWorkPayWeekMoney(results.getInt(results.getColumnIndex("workPayWeekMoney")));
      info.setWorkPayWeek(results.getString(results.getColumnIndex("workPayWeek")));
      info.setWorkAddType(results.getString(results.getColumnIndex("workAddType")));
      info.setWorkPayAddHour(results.getString(results.getColumnIndex("workPayAddHour")));
      info.setWorkPayAddMin(results.getString(results.getColumnIndex("workPayAddMin")));
      info.setWorkAlarm(Boolean.parseBoolean(results.getString(results.getColumnIndex("workAlarm"))));
      info.setWorkMonthDay(results.getInt(results.getColumnIndex("workMonthDay")));


      results.close();
      if (INFO)
        Log.i(TAG, "not null");
      return info;
    }
    if (INFO)
      Log.i(TAG, "null");
    results.close();
    return null;
  }

  // 데이터 검색
  public PartTimeItem selectOneData(String column1, String data1, String column2, String data2) {
    if (INFO)
      Log.i(TAG, "selectOneData DataBase");
    SQLiteDatabase db = getWritableDatabase();
    String sql = "select * from " + DBConstant.TABLE_PARTTIMEDATA + " where " + column1 + " = '" + data1 + "' AND " + column2 + " = '" + data2
        + "';";


    Cursor result = db.rawQuery(sql, null);

    // result(Cursor 객체)가 비어 있으면 false 리턴
    if (result.moveToFirst()) {


      PartTimeItem item = new PartTimeItem();
      item.setAlbaname(result.getString(result.getColumnIndex("albaname")));
      item.setDate(result.getString(result.getColumnIndex("date")));
      item.setHourMoney(result.getString(result.getColumnIndex("hourMoney")));
      item.setStartTimeHour(result.getString(result.getColumnIndex("startTimeHour")));
      item.setStartTimeMin(result.getString(result.getColumnIndex("startTimeMin")));
      item.setEndTimeHour(result.getString(result.getColumnIndex("endTimeHour")));
      item.setEndTimeMin(result.getString(result.getColumnIndex("endTimeMin")));
      item.setSimpleMemo(result.getString(result.getColumnIndex("simpleMemo")));
      item.setWorkTimeHour(result.getString(result.getColumnIndex("workTimeHour")));
      item.setWorkTimeMin(result.getString(result.getColumnIndex("workTimeMin")));
      item.setWorkPayGabul(result.getString(result.getColumnIndex("workPaygabul")));
      item.setWorkPayGabulVal(result.getString(result.getColumnIndex("workPaygabulVal")));
      item.setWorkRefreshTime(result.getString(result.getColumnIndex("workRefresh")));
      item.setWorkPayNight(result.getString(result.getColumnIndex("workPayNight")));
      item.setWorkPayTotal(result.getString(result.getColumnIndex("workPayTotal")));
      item.setWorkPayAdd(result.getString(result.getColumnIndex("workPayAdd")));
      item.setWorkRefreshType(result.getString(result.getColumnIndex("workRefreshType")));
      item.setWorkRefreshHour(result.getString(result.getColumnIndex("workRefreshHour")));
      item.setWorkRefreshMin(result.getString(result.getColumnIndex("workRefreshMin")));
      item.setWorkPayTotalTime(result.getInt(result.getColumnIndex("workPayTotalTime")));
      item.setWorkPayNightTotalTime(result.getInt(result.getColumnIndex("workPayNightTotalTime")));
      item.setWorkPayAddTotalTime(result.getInt(result.getColumnIndex("workPayAddTotalTime")));
      item.setWorkPayRefreshTotalTime(result.getInt(result.getColumnIndex("workPayRefreshTotalTime")));
      item.setWorkEtcMoeny(result.getString(result.getColumnIndex("workPayEtcMoney")));
      item.setWorkEtcNum(result.getString(result.getColumnIndex("workPayEtcNum")));

      item.setWorkEtc(result.getString(result.getColumnIndex("workPayEtc")));
      item.setWorkPayWeekTime(result.getDouble(result.getColumnIndex("workPayWeekTime")));
      item.setWorkPayWeekMoney(result.getInt(result.getColumnIndex("workPayWeekMoney")));

      item.setWorkPayWeek(result.getString(result.getColumnIndex("workPayWeek")));
      item.setWorkRefreshState(result.getInt(result.getColumnIndex("workRefreshState")));
      item.setWorkAddType(result.getString(result.getColumnIndex("workAddType")));
      item.setWorkAddHour(result.getString(result.getColumnIndex("workPayAddHour")));
      item.setWorkAddMin(result.getString(result.getColumnIndex("workPayAddMin")));

            /*PartTimeItem item = new PartTimeItem(result.getString(1),
                    result.getString(2), result.getString(3), result.getString(4), result.getString(5), result.getString(6), result.getString(7), result.getString(8), result.getString(9), result.getString(10), result.getString(11), result.getString(12), result.getString(13), result.getString(14), result.getString(15));
           */
      result.close();

      if (INFO)
        Log.i(TAG, "not null");
      result.close();
      return item;
    }
    if (INFO)
      Log.i(TAG, "null");
    result.close();
    return null;
  }

  // 데이터 검색
  public CalendarInfo selectCalendar(String albaname) {
    if (INFO)
      Log.i(TAG, "selectOneData DataBase");
    SQLiteDatabase db = getWritableDatabase();
    String sql = "select * from " + DBConstant.TABLE_CALENDARINFO + " where " + DBConstant.COLUMN_ALBANAME + " = '" + albaname + "';";


    Cursor result = db.rawQuery(sql, null);

    // result(Cursor 객체)가 비어 있으면 false 리턴
    if (result.moveToFirst()) {
      CalendarInfo item = new CalendarInfo(result.getString(1),
          result.getString(2), result.getString(3), result.getString(4), result.getString(5), result.getString(6), result.getString(7));
      result.close();

      if (INFO)
        Log.i(TAG, "not null");
      result.close();
      return item;
    }
    if (INFO)
      Log.i(TAG, "null");
    result.close();
    return null;
  }

  // 데이터 전체 검색
  public Cursor selectAllData(String table, String column, String data) {
    if (INFO)
      Log.i(TAG, "selectAllData DataBase");
    SQLiteDatabase db = getWritableDatabase();
    String sql = "select * from " + table + " where " + column + " = '" + data
        + "';";
    Cursor results = db.rawQuery(sql, null);

    return results;

  }

  // 데이터 전체 검색
  public Cursor selectAll(String table) {
    if (INFO)
      Log.i(TAG, "selectAll DataBase");
    SQLiteDatabase db = getWritableDatabase();
    String sql = "select * from " + table + " ORDER BY _id DESC;";
    Cursor results = db.rawQuery(sql, null);

    return results;
  }

  //알람정보저장
  // 알바정보 데이터 추가
  public boolean insertWeekInfo(CalendarNoti info) {
    if (INFO)
      Log.i(TAG, "insertWeekInfo DataBase");
    SQLiteDatabase db = getWritableDatabase();
    try {
      ContentValues values = new ContentValues();
      values.put("albaname", info.getAlbaName());
      values.put("sun", Boolean.toString(info.isSun()));
      values.put("mon", Boolean.toString(info.isMon()));
      values.put("thu", Boolean.toString(info.isThu()));
      values.put("wed", Boolean.toString(info.isWed()));
      values.put("thur", Boolean.toString(info.isThur()));
      values.put("fri", Boolean.toString(info.isFri()));
      values.put("sat", Boolean.toString(info.isSat()));
      values.put("requestCode", info.getRequestCode());

      db.insert(DBConstant.TABLE_WEEK_INFO, null, values);
      return true;
    } catch (Exception e) {
      Log.e(TAG, e.toString());
    }
    return false;
  }

  // 캘린더정보 갱신
  public boolean updateWeekInfo(CalendarNoti info, String albaname) {
    if (INFO)
      Log.i(TAG, "updateWeekInfo DataBase");
    SQLiteDatabase db = getWritableDatabase();
    try {
      String sql = "update " + DBConstant.TABLE_WEEK_INFO + " set sun = '" + info.isSun()
          + "', mon = '" + info.isMon()
          + "', thu = '" + info.isThu()
          + "', wed = '" + info.isWed()
          + "', thur = '" + info.isThur()
          + "', fri = '" + info.isFri()
          + "', sat = '" + info.isSat()
          + "', albaname = '" + info.getAlbaName()
          + "' where albaname = '" + albaname + "';";
      Log.i(TAG, sql);
      db.execSQL(sql);
      return true;
    } catch (Exception e) {
      Log.i(TAG, "updateData fail :: " + e.toString());

    }
    return false;
  }

  public Cursor selectWEEKSeachData(String column, String data) {
    if (INFO)
      Log.i(TAG, "selectWEEKSeachData DataBase");
    SQLiteDatabase db = getWritableDatabase();
    String sql = "select * from " + DBConstant.TABLE_WEEK_INFO + " where " + column + " = '" + data
        + "';";
    Cursor results = db.rawQuery(sql, null);

    return results;
  }


}
