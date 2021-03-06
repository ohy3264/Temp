package com.hw.oh.temp.talk;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hw.oh.temp.BaseActivity;
import com.hw.oh.temp.R;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by oh on 2015-02-20.
 */
public class ImageClickActivity extends BaseActivity implements View.OnTouchListener {
    private static final String TAG = "ImageClickActivity";
    private static final boolean DBUG = true;
    private static final boolean INFO = true;
    @BindView(R.id.imgDetail)
    ImageView mImgDetail;
    private ImageLoader mImageClickImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageclick);
        ButterKnife.bind(this);
        setToolbar("");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            mImgDetail.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mImageClickImageLoader = new ImageLoader(this);
        mImageClickImageLoader.loadImage(Constant.IMG_UPLOAD_URL + getIntent().getStringExtra("id") + ".jpg", mImgDetail);
        mImgDetail.setOnTouchListener(this);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            Log.i(TAG, "손가락이 눌렸습니다.");

        } else if (action == MotionEvent.ACTION_MOVE) {
            Log.i(TAG, "손가락이 움직였습니다..");

        } else if (action == MotionEvent.ACTION_UP) {
            Log.i(TAG, "손가락이 떼졌습니다.");

        }


        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
