package com.hw.oh.temp.talk;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hw.oh.temp.ApplicationClass;
import com.hw.oh.temp.BaseActivity;
import com.hw.oh.temp.R;
import com.hw.oh.utility.CommonUtil;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.JSONParserUtil;
import com.hw.oh.utility.NetworkUtil;
import com.hw.oh.utility.OkHttpUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by oh on 2015-02-02.
 */
public class NewPostActivity extends BaseActivity {
    public static final String TAG = "NewPostActivity";
    public static final boolean DBUG = true;
    public static final boolean INFO = true;

    private final int MY_PERMISSION_REQUEST_STORAGE = 100;
    private final int MY_PERMISSION_REQUEST_CAMERA = 200;

    //View
    @BindView(R.id.profile_imgview)
    ImageView mPhotoImageView;
    @BindView(R.id.edtNewPost)
    EditText mEdtNewPost;

    //Flag
    private String mTextInsertFlag, mImageInsertFlag;
    private static final int REQUEST_IMAGE_CAPTURE = 0;  //카메라 촬영
    private static final int PICK_FROM_ALBUM = 1;    //앨범 선택
    private static final String TEMP_FILE_NAME = "tempFile.jpg";
    private String mfilePath = "";


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpost);
        ButterKnife.bind(this);

        mfilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";

        // 구글 통계
        Tracker mTracker = ((ApplicationClass) getApplication()).getDefaultTracker();
        mTracker.setScreenName("새글등록화면_(NewPostActivity)");
        mTracker.send(new HitBuilders.AppViewBuilder().build());
        setToolbar("새글쓰기");

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

    /**
     * Permission check.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission_Externalstorage(Bitmap bitmap) {
        int permissionCheck_WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck_READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck_WRITE_EXTERNAL_STORAGE == PackageManager.PERMISSION_DENIED
                || permissionCheck_READ_EXTERNAL_STORAGE == PackageManager.PERMISSION_DENIED) {

            // 권한이 필요한 이유 설명
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
            }

            // 외부저장소 읽기/쓰기 권한 없음
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST_STORAGE);

        } else {
            // 외부저장소 읽기/쓰기 권한 있음
            mPhotoImageView.setImageBitmap(bitmap);
            saveBitmaptoJpeg(bitmap, mfilePath + TEMP_FILE_NAME);
        }
    }

    /**
     * Permission check.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission_Camera() {
        int permissionCheck_CAMERA = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCheck_CAMERA != PackageManager.PERMISSION_GRANTED) {            // 카메라 권한 없음

            // 권한이 필요한 이유 설명
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Toast.makeText(this, "CAMERA", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSION_REQUEST_CAMERA);
        } else {
            capturePhoto();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission pass");

                } else {
                    Log.d(TAG, "Permission always deny");

                }
                break;
            case MY_PERMISSION_REQUEST_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission pass");
                } else {
                    Log.d(TAG, "Permission always deny");
                }
                break;
        }
    }


    @OnClick(R.id.btnFloating)
    public void onClick(View v) {
        if (v.getId() == R.id.btnFloating) {
            if (NetworkUtil.isConnect(this)) {
                if (!mEdtNewPost.getText().toString().isEmpty()) {
                    uploadFileRequest();
                    //requetNewImageSend();
                } else {
                    showCrountonText("아무글이나 써주세요");
                }
            } else {
                showCrountonText("네트워크를 확인해주세요");
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
        //퍼미션체크
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_album:
                picPhoto();
                break;

            case R.id.action_camera:
                checkPermission_Camera();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //카메라 촬영
    public void capturePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    //갤러리에서 이미지 선택
    private void picPhoto() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, PICK_FROM_ALBUM);
    }


    //Activity Results
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    onCaptureImageResult(data);
                    break;
                case PICK_FROM_ALBUM:
                    onSelectFromGalleryResult(data);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //앨범에서 이미지선택결과 파일로 저장
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                checkPermission_Externalstorage(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //카메라 촬영결과 파일로 저장
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        checkPermission_Externalstorage(thumbnail);
    }

    //비트맵 파일로 저장
    private void saveBitmaptoJpeg(Bitmap bitmap, String filePath) {
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

    public void uploadFileRequest() {
        if (INFO)
            Log.i(TAG, "requestCallRest_Pass()");
        MultipartBody.Builder multiBody = new MultipartBody.Builder();
        multiBody.setType(MultipartBody.FORM).addFormDataPart("MODE", "NewPostSend")
                .addFormDataPart("ANDROID_ID", CommonUtil.getAndroidID(this))
                .addFormDataPart("GENDER", mPref.getValue(mPref.KEY_GENDER, "0"))
                .addFormDataPart("NEW_POST", mEdtNewPost.getText().toString());
        File f = new File(mfilePath + TEMP_FILE_NAME);
        if (f.isFile()) {
            multiBody.addFormDataPart("uploadedfile", f.getName(),
                    RequestBody.create(MediaType.parse("text/plain"), f))
                    .addFormDataPart("other_field", "other_field_value");
        }

        try {
            OkHttpUtils.post(this, Constant.SERVER_URL, "/Anony/api/newPostSave.php", multiBody.build(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "uploadFileRequest - onFailure :: " + e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i("Response", " " + response.code());
                    final String results = response.body().string();
                    Log.i("OkHTTP Results: ", results);
                    mTextInsertFlag = JSONParserUtil.getString(results, "TextResult");
                    mImageInsertFlag = JSONParserUtil.getString(results, "ImgResult");

                    if (mTextInsertFlag.equals("InsertSuccess") && mImageInsertFlag.equals("InsertSuccess")) {
                        File removeFile = new File(mfilePath + TEMP_FILE_NAME);
                        if (removeFile.isFile()) {
                            removeFile.delete();
                        }

                        Constant.REFRESH_BOARD_FLAG = Constant.REQUEST_NEWPOST;
                        finish();
                    }

                }
            });
        } catch (Exception e) {
            Log.d(TAG, "uploadFileRequest - exception :: " + e.toString());

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }
}
