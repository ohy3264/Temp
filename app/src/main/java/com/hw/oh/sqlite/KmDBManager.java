package com.hw.oh.sqlite;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by oh on 2015-02-21.
 */
public class KmDBManager {
  private static final String TAG = "KmDBManager";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;

  private Context mContext;

  public KmDBManager(Context context) {
    mContext = context;

    initialize(context);
  }


  public static void initialize(Context ctx) {
    String PACKAGE_DIR;
    PACKAGE_DIR = DBConstant.PACKAGE_DIR + ctx.getPackageName() + "/databases";
    File folder = new File(PACKAGE_DIR);
    if (!folder.isDirectory())
      folder.mkdirs();

    //File outfile = new File(PACKAGE_DIR + "/" + DBConstant.KM_DATABASE_NAME);
    File outfile = ctx.getDatabasePath(DBConstant.KM_DATABASE_NAME);
    if (outfile.length() <= 0) {
      AssetManager assetManager = ctx.getResources().getAssets();
      try {
        InputStream is = assetManager.open(DBConstant.KM_DATABASE_NAME, AssetManager.ACCESS_BUFFER);
        long filesize = is.available();
        byte[] tempdata = new byte[(int) filesize];
        is.read(tempdata);
        is.close();
        outfile.createNewFile();
        FileOutputStream fo = new FileOutputStream(outfile);
        fo.write(tempdata);
        fo.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


  // 데이터 전체 검색
  public Cursor selectKMAllData() {
    if (INFO)
      Log.i(TAG, "selectAllData DataBase");
    SQLiteDatabase db = mContext.openOrCreateDatabase(DBConstant.KM_DATABASE_NAME, mContext.MODE_PRIVATE, null);
    String sql = "select * from " + DBConstant.TABLE_KMLOCATION_INFO + ";";
    Cursor results = db.rawQuery(sql, null);

    return results;

  }


  public Cursor selectKMSeachData(String column, String data) {
    if (INFO)
      Log.i(TAG, "selectSeachData DataBase");
    SQLiteDatabase db = mContext.openOrCreateDatabase(DBConstant.KM_DATABASE_NAME, mContext.MODE_PRIVATE, null);
    String sql = "select * from " + DBConstant.TABLE_KMLOCATION_INFO + " where " + column + " = '" + data
        + "';";
    Cursor results = db.rawQuery(sql, null);

    return results;

  }

}
