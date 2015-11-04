package com.hw.oh.utility;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by oh on 2015-02-17.
 */
public class HYFileUtil {
  public static final String TAG = "HYFileUtil";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;

  /* external 저장공간에 읽기/쓰기 가 가능한지 확인한다 */
  public boolean isExternalStorageWritable() {
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)) {
      return true;
    } else {
      if (INFO)
        Log.i(TAG, "not read");
    }
    return false;
  }

  /* external 저장공간에서 최소한 읽기라도 할 수 있는지 확인한다 */
  public boolean isExternalStorageReadable() {
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state) ||
        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
      return true;
    } else {
      if (INFO)
        Log.i(TAG, "not read");
    }
    return false;
  }

  /*  유저의 공개 사진 디렉토리를 얻어온다 */
  public File getAlbumStorageDir(String albumName) {
    // Get the directory for the user's public pictures directory.
    File file = new File(Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES), albumName);
    if (!file.mkdirs()) {
      if (INFO)
        Log.i(TAG, "Directory not created");
    }
    return file;
  }

  /*  앱의 private 사진 디렉토리를 얻어온다. */
  public File getAlbumStorageDir(Context context, String albumName) {

    File file = new File(context.getExternalFilesDir(
        Environment.DIRECTORY_PICTURES), albumName);
    if (!file.mkdirs()) {
      if (INFO)
        Log.i(TAG, "Directory not created");
    }
    return file;
  }
}
