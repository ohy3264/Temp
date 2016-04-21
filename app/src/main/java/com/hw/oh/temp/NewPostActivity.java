package com.hw.oh.temp;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hw.oh.network.RestClient;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYNetworkInfo;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.InfoExtra;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by oh on 2015-02-02.
 */
public class NewPostActivity extends BaseActivity implements View.OnClickListener {
  public static final String TAG = "NewPostActivity";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;
  //View
  private Toolbar mToolbar;
  private EditText mEdtNewPost;
  private Button mBtnFloating;
  private ImageView mPhotoImageView;

  //Flag
  private String mTextInsertFlag, mImageInsertFlag;
  private static final int PICK_FROM_CAMERA = 0;
  private static final int PICK_FROM_ALBUM = 1;
  private static final int CROP_FROM_CAMERA = 2;

  //Crouton
  private View mCroutonView;
  private TextView mTxtCrouton;
  private CroutonHelper mCroutonHelper;

  //Utill
  private InfoExtra mInfoExtra;
  private HYPreference mPref;
  private HYFont mFont;
  private HYNetworkInfo mNet;

  //Image
  private String mUri = "";
  private Uri mImageCaptureUri;
  private Bitmap mBitmapOrg;
  private String mCropfilePath = "";

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_newpost);
    //Utill
    mNet = new HYNetworkInfo(this);
    mInfoExtra = new InfoExtra(this);
    mPref = new HYPreference(this);
    mFont = new HYFont(this);
    ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
    mFont.setGlobalFont(root);

    //Crouton
    mCroutonHelper = new CroutonHelper(this);
    mCroutonView = getLayoutInflater().inflate(
        R.layout.crouton_custom_view, null);
    mTxtCrouton = (TextView) mCroutonView.findViewById(R.id.txt_crouton);


    // Floating
    mBtnFloating = (Button) findViewById(R.id.btnFloating);
    mBtnFloating.setOnClickListener(this);

    //ActionBar
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    if (mToolbar != null) {
      mToolbar.setTitle("새글쓰기");
      setSupportActionBar(mToolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      mFont.setGlobalFont((ViewGroup) mToolbar);
    }

    mPhotoImageView = (ImageView) findViewById(R.id.profile_imgview);
    mEdtNewPost = (EditText) findViewById(R.id.edtNewPost);

    // ADmob
    if (Constant.ADMOB) {
      LinearLayout layout = (LinearLayout) findViewById(R.id.mainLayout);
      layout.setVisibility(View.VISIBLE);
      AdView ad = new AdView(this);
      ad.setAdUnitId("ca-app-pub-8578540426651700/9047026070");
      ad.setAdSize(AdSize.BANNER);
      layout.addView(ad);
      AdRequest adRequest = new AdRequest.Builder().build();
      ad.loadAd(adRequest);
    }
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.btnFloating) {
      if (!mNet.networkgetInfo()) {
        if (!mEdtNewPost.getText().toString().isEmpty()) {
          //  requestNewPostSend();
          requetNewImageSend();

          new Thread(new Runnable() {
            @Override
            public void run() {
              //doFileUpload();

            }
          }).start();
        } else {
          mTxtCrouton.setText("아무글이나 써주세요");
          mCroutonHelper.setCustomView(mCroutonView);
          mCroutonHelper.setDuration(1000);
          mCroutonHelper.show();
        }
      } else {

        mTxtCrouton.setText("네트워크를 확인해주세요");
        mCroutonHelper.setCustomView(mCroutonView);
        mCroutonHelper.setDuration(1000);
        mCroutonHelper.show();
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_newpost, menu);

    return true;

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        break;

      case R.id.action_album:
        // 앨범 호출
        doTakeAlbumAction();
        break;

      case R.id.action_camera:

        doTakePhotoAction();

        break;
    }
    return super.onOptionsItemSelected(item);
  }

  private void doTakePhotoAction() {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // 임시로 사용할 파일의 경로를 생성
    mImageCaptureUri = createSaveCropFile();
    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
        mImageCaptureUri); //해당 uri에 사진 저장
    // 특정기기에서 사진을 저장못하는 문제가 있어 다음을 주석처리 합니다.
    // intent.putExtra("return-data", true);
    startActivityForResult(intent, PICK_FROM_CAMERA);
  }

  /**
   * 앨범에서 이미지 가져오기
   */
  private void doTakeAlbumAction() {
    // 앨범 호출
    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
    startActivityForResult(intent, PICK_FROM_ALBUM);
  }

  /**
   * Crop된 이미지가 저장될 파일을 만든다.
   *
   * @return Uri
   */
  private Uri createSaveCropFile() {
    Uri uri;
    mUri = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
    File[] filepath = ContextCompat.getExternalFilesDirs(getApplicationContext(), null);
    uri = Uri.fromFile(new File(filepath[0].getPath(), mUri));
    return uri;
  }

  public void requetNewImageSend() {
    RequestParams params = new RequestParams();
    params.put("MODE", "NewPostSend");
    params.put("ANDROID_ID", mInfoExtra.getAndroidID());
    params.put("GENDER", mPref.getValue(mPref.KEY_GENDER, "0"));
    params.put("NEW_POST", mEdtNewPost.getText().toString());
    try {
      File myFile = new File(mCropfilePath);
      if (!myFile.isFile()) {
        if (INFO)
          Log.i(TAG, "파일 없음 :: " + mCropfilePath);
      } else {
        if (INFO)
          Log.i(TAG, "파일 있음 :: " + mCropfilePath);
        params.put("uploadedfile", myFile);
      }
    } catch (FileNotFoundException e) {
      Log.d(TAG, "FileNotFoundException :: " + e.toString());
    }

    //upload.php
    RestClient.post("newPostSave.php", params, new JsonHttpResponseHandler() {
      @Override
      public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
      }

      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.d("onSuccess - object", response.toString());

        try {
          mTextInsertFlag = (String) response.get("TextResult");
          mImageInsertFlag = (String) response.get("ImgResult");
          if (INFO) {
            Log.i(TAG, "mTextInsertFlag :: " + mTextInsertFlag);
            Log.i(TAG, "mImageInsertFlag :: " + mImageInsertFlag);
          }
        } catch (JSONException e) {
          if (DBUG)
            Log.d(TAG, "JSONException");
        } catch (NullPointerException e) {
          if (DBUG)
            Log.d(TAG, "NullPointException");

        }
        if (mTextInsertFlag.equals("InsertSuccess") && mImageInsertFlag.equals("InsertSuccess")) {
          Message msg = IntentHandler.obtainMessage();
          IntentHandler.sendMessage(msg);
        }

      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        Log.d("OnFailure!", "obj");
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        Log.d("OnFailure!", "arry");
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        Log.d("OnFailure!", responseString);
      }

      @Override
      public void onFinish() {
        super.onFinish();
        Log.i(TAG, "onFinish");
      }
    });
  }

  final Handler IntentHandler = new Handler() {
    public void handleMessage(Message msg) {
      Constant.REFRESH_BOARD_FLAG = Constant.REQUEST_NEWPOST;
      finish();
    }
  };


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != RESULT_OK) {
      if (INFO)
        Log.i(TAG, "RESULT_NO");
      return;
    }

    switch (requestCode) {
      case PICK_FROM_ALBUM: {
        if (INFO)
          Log.i(TAG, "PICK_FROM_ALBUM");
        // 이후의 처리가 카메라와 같으므로 일단 break없이 진행합니다.
        // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.

        mImageCaptureUri = data.getData();
        File original_file = getImageFile(mImageCaptureUri);

        mImageCaptureUri = createSaveCropFile();
        File cpoy_file = new File(mImageCaptureUri.getPath());

        // SD카드에 저장된 파일을 이미지 Crop을 위해 복사한다.
        copyFile(original_file, cpoy_file);
      }

      case PICK_FROM_CAMERA: {
        if (INFO)
          Log.i(TAG, "PICK_FROM_CAMERA");
        // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
        // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(mImageCaptureUri, "image/*");


        // Crop한 이미지를 저장할 Path
        intent.putExtra("output", mImageCaptureUri);

        startActivityForResult(intent, CROP_FROM_CAMERA);

        break;
      }
      case CROP_FROM_CAMERA: {
        if (INFO)
          Log.i(TAG, "CROP_FROM_CAMERA");
        // 크롭이 된 이후의 이미지를 넘겨 받습니다.
        // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
        // 임시 파일을 삭제합니다.
        File[] filepath = ContextCompat.getExternalFilesDirs(getApplicationContext(), null);
        if (DBUG) {
          Log.d(TAG, "filepath[0] :: " + filepath[0].getPath() + mUri);
          Log.d("Storage Max Count : ", String.valueOf(filepath.length));
        }
        mCropfilePath = filepath[0].getPath() + "/" + mUri;
        if (new File(mCropfilePath).isFile()) {
          Log.d(TAG, "파일 존재함 :: " + new File(mCropfilePath));
          BitmapFactory.Options options = new BitmapFactory.Options();
          options.inSampleSize = 4;
          mBitmapOrg = BitmapFactory.decodeFile(new File(mCropfilePath).getPath(), options);
        }
        mPhotoImageView.setImageBitmap(mBitmapOrg);
        storeCropImage(mBitmapOrg, mCropfilePath);


               /* // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if (f.exists()) {
                    Log.d(TAG, "삭제 :: " + mImageCaptureUri.getPath());
                    f.delete();
                }*/

        break;
      }
    }
  }

  /**
   * 파일 복사
   *
   * @param srcFile  : 복사할 File
   * @param destFile : 복사될 File
   */
  public static boolean copyFile(File srcFile, File destFile) {
    boolean result = false;
    try {
      InputStream in = new FileInputStream(srcFile);
      try {
        result = copyToFile(in, destFile);
      } finally {
        in.close();
      }
    } catch (IOException e) {
      result = false;
    }
    return result;
  }

  /**
   * Copy data from a source stream to destFile. Return true if succeed, return false if failed.
   */
  private static boolean copyToFile(InputStream inputStream, File destFile) {
    try {
      OutputStream out = new FileOutputStream(destFile);
      try {
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) >= 0) {
          out.write(buffer, 0, bytesRead);
        }
      } finally {
        out.close();
      }
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  private void storeCropImage(Bitmap bitmap, String filePath) {
    File copyFile = new File(filePath);
    BufferedOutputStream out = null;

    try {
      copyFile.createNewFile();
      out = new BufferedOutputStream(new FileOutputStream(copyFile));
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
      out.flush();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 선택된 uri의 사진 Path를 가져온다. uri 가 null 경우 마지막에 저장된 사진을 가져온다.
   */
  private File getImageFile(Uri uri) {
    String[] projection = {MediaStore.Images.Media.DATA};
    if (uri == null) {
      uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    Cursor mCursor = getContentResolver().query(uri, projection, null, null,
        MediaStore.Images.Media.DATE_MODIFIED + " desc");
    if (mCursor == null || mCursor.getCount() < 1) {
      return null; // no cursor or no record
    }
    int column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    mCursor.moveToFirst();

    String path = mCursor.getString(column_index);

    if (mCursor != null) {
      mCursor.close();
      mCursor = null;
    }

    return new File(path);
  }


  private void doFileUpload() {

    HttpURLConnection conn = null;
    DataOutputStream dos = null;
    DataInputStream inStream = null;

    String existingFileName = Environment.getExternalStorageDirectory() + "/test.jpg";
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1 * 1024 * 1024;
    String responseFromServer = "";
    String urlString = "http://ohy3264.cafe24.com/Anony/api/upload.php";

    Log.d(TAG, existingFileName);
    try {

      //------------------ CLIENT REQUEST
      FileInputStream fileInputStream = new FileInputStream(new File(existingFileName));
      // open a URL connection to the Servlet
      URL url = new URL(urlString);
      // Open a HTTP connection to the URL
      conn = (HttpURLConnection) url.openConnection();
      // Allow Inputs
      conn.setDoInput(true);
      // Allow Outputs
      conn.setDoOutput(true);
      // Don't use a cached copy.
      conn.setUseCaches(false);
      // Use a post method.
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Connection", "Keep-Alive");
      conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
      dos = new DataOutputStream(conn.getOutputStream());
      dos.writeBytes(twoHyphens + boundary + lineEnd);
      dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + existingFileName + "\"" + lineEnd);
      dos.writeBytes(lineEnd);
      // create a buffer of maximum size
      bytesAvailable = fileInputStream.available();
      bufferSize = Math.min(bytesAvailable, maxBufferSize);
      buffer = new byte[bufferSize];
      // read file and write it into form...
      bytesRead = fileInputStream.read(buffer, 0, bufferSize);

      while (bytesRead > 0) {
        dos.write(buffer, 0, bufferSize);
        bytesAvailable = fileInputStream.available();
        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

      }

      // send multipart form data necesssary after file data...
      dos.writeBytes(lineEnd);
      dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
      // close streams
      Log.e("Debug", "File is written");
      fileInputStream.close();
      dos.flush();
      dos.close();

    } catch (MalformedURLException ex) {
      Log.e("Debug", "error: " + ex.getMessage(), ex);
    } catch (IOException ioe) {
      Log.e("Debug", "error: " + ioe.getMessage(), ioe);
    }

    //------------------ read the SERVER RESPONSE
    try {

      inStream = new DataInputStream(conn.getInputStream());
      String str;

      while ((str = inStream.readLine()) != null) {

        Log.e("Debug", "Server Response " + str);

      }

      inStream.close();

    } catch (IOException ioex) {
      Log.e("Debug", "error: " + ioex.getMessage(), ioex);
    }
  }
}
