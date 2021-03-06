package com.hw.oh.utility;

import android.content.Context;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.hw.oh.sqlite.DBConstant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by oh on 2015-03-30.
 */
public class HYDatabaseBackup {
  public static final String TAG = "HYDatabaseBackup";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;

  public static void importDB(Context context) {
    try {
      File sd = Environment.getExternalStorageDirectory();
      File data = Environment.getDataDirectory();

      Log.i(TAG, sd.getAbsolutePath());
      Log.i(TAG, data.getAbsolutePath());

      String state = Environment.getExternalStorageState();
      if (Environment.MEDIA_MOUNTED.equals(state)) {
        Log.d("Test", "sdcard mounted and writable");
        File[] filepath = ContextCompat.getExternalFilesDirs(context, null);
        String currentDBPath = context.getDatabasePath(DBConstant.DATABASE_NAME).toString();
        //String backupDBPath = filepath[0].getPath(); // From SD directory.
        String backupDBPath = context.getFilesDir().toString(); // From Internal Storage
        File currentDB = new File(backupDBPath + "/" + DBConstant.DATABASE_NAME);
        File backupDB = new File(currentDBPath);
        Log.i(TAG, currentDBPath);
        Log.i(TAG, backupDBPath);

        FileChannel src = new FileInputStream(currentDB).getChannel();
        FileChannel dst = new FileOutputStream(backupDB).getChannel();
        dst.transferFrom(src, 0, src.size());
        src.close();
        dst.close();
        Toast.makeText(context, "Import Successful!",
                Toast.LENGTH_SHORT).show();

      }
      else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
        Log.d("Test", "sdcard mounted readonly");

      }
      else {
        Log.d("Test", "sdcard state: " + state);

      }


    } catch (Exception e) {
      Log.e(TAG, e.toString());

      Toast.makeText(context, "Import Failed!", Toast.LENGTH_SHORT)
          .show();

    }


    try {
      File sd = Environment.getExternalStorageDirectory();
      File data = Environment.getDataDirectory();

      if (sd.canWrite()) {
        String currentDBPath = "//data//package name//databases//database_name";
        String backupDBPath = DBConstant.DATABASE_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);

        if (currentDB.exists()) {
          FileChannel src = new FileInputStream(backupDB).getChannel();
          FileChannel dst = new FileOutputStream(currentDB).getChannel();
          dst.transferFrom(src, 0, src.size());
          src.close();
          dst.close();
          Toast.makeText(context, "Database Restored successfully", Toast.LENGTH_SHORT).show();
        }
      }
    } catch (Exception e) {
    }

  }

  public static void exportDB(Context context) {
    try {
      File sd = Environment.getExternalStorageDirectory();
      File data = Environment.getDataDirectory();

      Log.i(TAG, sd.getAbsolutePath());
      Log.i(TAG, data.getAbsolutePath());
      if (sd.canWrite()) {
        File[] filepath = ContextCompat.getExternalFilesDirs(context, null);
        String currentDBPath = context.getDatabasePath(DBConstant.DATABASE_NAME).toString();
        String backupDBPath = filepath[0].getPath();
        Log.i(TAG, currentDBPath);
        Log.i(TAG, backupDBPath);


        File currentDB = new File(currentDBPath);
        File backupDB = new File(backupDBPath + DBConstant.DATABASE_NAME);
        FileChannel src = new FileInputStream(currentDB).getChannel();
        FileChannel dst = new FileOutputStream(backupDB).getChannel();
        dst.transferFrom(src, 0, src.size());
        src.close();
        dst.close();
        Toast.makeText(context, "Backup Successful!",
            Toast.LENGTH_SHORT).show();

      }
    } catch (Exception e) {
      Log.e(TAG, e.toString());

      Toast.makeText(context, "Backup Failed!", Toast.LENGTH_SHORT)
          .show();

    }
  }
}
