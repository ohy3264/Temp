package com.hw.oh.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hw.oh.temp.MainActivity;
import com.hw.oh.temp.R;
import com.hw.oh.temp.SplashActivity;
import com.hw.oh.utility.CommonUtil;
import com.hw.oh.utility.ImageLoader;

import org.apache.commons.validator.UrlValidator;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ohyowan on 16. 7. 11..
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private final static AtomicInteger c = new AtomicInteger(0); // 자동증가값(키)

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        Log.d(TAG, "Notification Message title: " + remoteMessage.getNotification().getTitle());

        final String PUSH_TITLE = decode(remoteMessage.getData().get("PUSH_TITLE"));
        final String PUSH_CONTENT = decode(remoteMessage.getData().get("PUSH_CONTENT"));
        final String PUSH_BANNER_IMAGE_URL = decode(remoteMessage.getData().get("PUSH_BANNER_IMAGE_URL"));
        final String PUSH_KIND = decode(remoteMessage.getData().get("PUSH_KIND"));
        final String PUSH_SEQ = decode(remoteMessage.getData().get("PUSH_SEQ"));
        final String PUSH_MOVE = decode(remoteMessage.getData().get("PUSH_MOVE"));

        Log.i(TAG, "PUSH_TITLE : " + PUSH_TITLE);
        Log.i(TAG, "PUSH_CONTENT : " + PUSH_CONTENT);
        Log.i(TAG, "PUSH_BANNER_IMAGE_URL : " + PUSH_BANNER_IMAGE_URL);
        Log.i(TAG, "PUSH_KIND : " + PUSH_KIND);
        Log.i(TAG, "PUSH_SEQ : " + PUSH_SEQ);
        Log.i(TAG, "PUSH_MOVE : " + PUSH_MOVE);


        // 이동페이지 인텐트 생성
        final Intent moveIntent = createIntent(PUSH_KIND, PUSH_SEQ, PUSH_MOVE);

        if ("0".equals(PUSH_BANNER_IMAGE_URL)) {
            notificationWithBigPicture(getApplicationContext(), PUSH_TITLE, PUSH_CONTENT, R.drawable.ic_launcher, null, moveIntent);
        } else {
            Handler mainHandler = new Handler(getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Glide.with(getApplicationContext()) // could be an issue!
                            .load(PUSH_BANNER_IMAGE_URL)
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                                    // do something with the bitmap
                                    // for demonstration purposes, let's just set it to an ImageView
                                    notificationWithBigPicture(getApplicationContext(), PUSH_TITLE, PUSH_CONTENT, R.drawable.ic_launcher, bitmap, moveIntent);
                                }

                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                    super.onLoadFailed(e, errorDrawable);
                                    notificationWithBigPicture(getApplicationContext(), PUSH_TITLE, PUSH_CONTENT, R.drawable.ic_launcher, null, moveIntent);
                                }
                            });
                }
            });


        }

    }
    // [END receive_message]


    /**
     * [GCM] URL디코딩 적용 함수입니다.
     *
     * @param str 변경할 String 객체
     * @return 디코딩된 값
     */

    private String decode(String str) {
        String decode = str;
        try {
            decode = URLDecoder.decode(CommonUtil.nullToString(str, ""), "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return decode;
    }

    /**
     * [GCM] 인텐트 생성 함수 입니다.
     * URL 유효성 체크
     *
     * @param kind 푸시유형
     * @param seq  파라미터 키
     * @param move 이동 activity / URL
     * @return Intent 이동Intent 객체
     */
    public Intent createIntent(String kind, String seq, String move) {
        Intent intent = null;
        if ((kind != null && !kind.equals("")) && (seq != null && !seq.equals("")) && (move != null && !move.equals(""))) {

            String[] schemes = {"http", "https"}; // DEFAULT schemes = "http", "https", "ftp"
            UrlValidator urlValidator = new UrlValidator(schemes);
            boolean checkURL = urlValidator.isValid(move);

            if (checkURL) { // 01. URL
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(move));
            } else { // 02. Activity
                Class<?> activityClass = getLandingClass(kind, move);
                if (activityClass != null) {
                    intent = new Intent(getApplicationContext(), activityClass);
                    intent.putExtra("seq", Integer.parseInt(seq));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            }
        }
        return intent;
    }

    /**
     * [GCM] 랜딩 클래스를 생성합니다.
     *
     * @param kind 푸시유형
     * @param move 이동 activity / URL
     * @return Class 생성된 클래스 객체
     */
    public Class<?> getLandingClass(String kind, String move) {
        Class<?> activityClass = null;
        try {
            if ("0".equals(move)) { // home으로 이동
                activityClass = Class.forName("kr.co.nmp.gamekok.process.MainActivity");
            } else { // 일반 이동
                activityClass = Class.forName(move);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return activityClass;
    }

    /**
     * [GCM] 알림(노티피케이션) 정의 함수입니다.
     *
     * @param context Context 객체
     * @param title   제목
     * @param message 내용
     * @param icon    아이콘
     * @param banner  이미지
     * @param intent  Intent 객체
     */
    public static void notificationWithBigPicture(Context context, String title, String message, int icon, Bitmap banner, Intent intent) {
        int notifyID = getNotifyID();
        PendingIntent pendingIntent = null;
        if (intent != null) {
            intent.putExtra("notifyID", notifyID);
            //Log.e(TAG_WIFI, "notificationWithBigPicture - notifyID : " + notifyID);
            // 두번째 파라미터인 requestCode 가 달라야 함.
            pendingIntent = PendingIntent.getActivity(context, notifyID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(icon)
                .setTicker(title)
                .setContentTitle(Html.fromHtml(title))
                .setContentText(Html.fromHtml(message))
                .setAutoCancel(true);

        NotificationCompat.Style style = null;

        if (banner != null) { // 이미지가 있는 경우
            NotificationCompat.BigPictureStyle bigStyle = new NotificationCompat.BigPictureStyle();
            bigStyle.setBigContentTitle(Html.fromHtml(title));
            bigStyle.setSummaryText(Html.fromHtml(message));
            bigStyle.bigPicture(banner);
            style = bigStyle;
        }

        if (style != null) {
            builder.setStyle(style);
        }
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }

        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notifyID, builder.build());
    }

    /**
     * [GCM] 자동증가값(알림 - 키) 생성 함수 입니다.
     *
     * @return int 키 값
     */
    public static int getNotifyID() {
        return c.incrementAndGet();
    }
}
