package com.hw.oh.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import com.hw.oh.model.BoardItem;
import com.hw.oh.utility.HYPreference;
import com.hw.oh.utility.HYTime_Maximum;
import com.hw.oh.utility.InfoExtra;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by oh on 2015-07-10.
 */
public class BoardDetailService extends Service {
  private static final String TAG = "BoardDetailService";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;


  public static final int MSG_REGISTER_CLIENT = 0; // 메시지 등록
  public static final int MSG_REGISTER_COMMENT = 1; // 코멘트 등록 요청
  public static final int MSG_DELETE_COMMENT = 2; // 코멘트 삭제 요청
  public static final int MSG_UNREGISTER_CLIENT = 3; // 메시지 클라이언트 해제
  public static final int MSG_SET_DETAIL_HEADER_VALUE = 4; //헤더, 코멘트 전체 응답
  public static final int MSG_SET_DETAIL_COMMENT_ALL = 5; // 코멘트 전체 응답

  Messenger mClients;
  //Flag
  private boolean mResponeEmpty = false;

  //Data
  private String mEdtCommnet;
  private String mSelectBoardID;
  private String mSelectCommentID;
  private BoardItem mHeaderData = new BoardItem();
  private ArrayList<BoardItem> mCommentList = new ArrayList<BoardItem>();

  //Utill
  private InfoExtra mInfoExtra;
  private HYPreference mPref;

  // Latch
  private CountDownLatch mCDL_HeaderSet;
  private CountDownLatch mCDL_CommentInsert;
  private CountDownLatch mCDL_CommentList;
  private CountDownLatch mCDL_BoardPostDel;
  private CountDownLatch mCDL_CommentDel;


  //클라이언트로 부터 메시지 수신하는 핸들러
  class IncomingHandler extends Handler { // Handler of incoming messages from clients.

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case MSG_REGISTER_CLIENT:
          Log.d(TAG, "mHandler.case MSG_REGISTER_CLIENT");
          mClients = msg.replyTo;
          mSelectBoardID = msg.getData().getString("BSEQ");
          asyncTask_CommentList_Call();
          break;

        case MSG_REGISTER_COMMENT:
          Log.d(TAG, "mHandler.case MSG_REGISTER_COMMENT");
          mClients = msg.replyTo;
          mEdtCommnet = msg.getData().getString("COMMENT");
          requestNewCommentSend();
          break;

        case MSG_DELETE_COMMENT:
          Log.d(TAG, "mHandler.case MSG_DELETE_COMMENT");
          mClients = msg.replyTo;
          mSelectCommentID = msg.getData().getString("COMMENT_ID");
          requestCall_CommentDel();
          break;

        case MSG_UNREGISTER_CLIENT:
          Log.d(TAG, "mHandler.case MSG_UNREGISTER_CLIENT");
          break;
      }
    }
  }

  //Message
  final Messenger mMessenger = new Messenger(new IncomingHandler()); // Target we publish for clients to send messages to IncomingHandler.

  @Override
  public IBinder onBind(Intent intent) {
    return mMessenger.getBinder(); //클라이언트에서 바인딩을 시도할때 IBinder 객체를 클라이언트로 전달
  }

  private void sendMessageToUI(final int flag) {
    new Handler().post(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "sendMessageToUI");
        Bundle b = new Bundle();
        Message msg;

        if (DBUG) {
          Log.d(TAG, "ID : " + mHeaderData.get_id());
          Log.d(TAG, "Unique : " + mHeaderData.getUniqueID());
          Log.d(TAG, "Gender : " + mHeaderData.getGender());
          Log.d(TAG, "StrText : " + mHeaderData.getStrText());
          Log.d(TAG, "LIKE : " + mHeaderData.getLikeCNT());
          Log.d(TAG, "COMMENT : " + mHeaderData.getCommCNT());
          Log.d(TAG, "HATE : " + mHeaderData.getHateCNT());
        }

        for (int i = 0; i < mCommentList.size(); i++) {
          Log.i(TAG, mCommentList.get(i).getStrText());
        }
        try {
          switch (flag) {
            case MSG_REGISTER_CLIENT:
              b.putSerializable("HEADER_DATA", mHeaderData);
              b.putSerializable("COMMENT_LIST", mCommentList);
              msg = Message.obtain(null, MSG_SET_DETAIL_HEADER_VALUE);
              msg.setData(b);
              mClients.send(msg);
              break;

            case MSG_REGISTER_COMMENT:

              b.putSerializable("COMMENT_LIST", mCommentList);
              msg = Message.obtain(null, MSG_SET_DETAIL_COMMENT_ALL);
              msg.setData(b);
              mClients.send(msg);
              break;

            case MSG_DELETE_COMMENT:
              b.putSerializable("COMMENT_LIST", mCommentList);
              msg = Message.obtain(null, MSG_SET_DETAIL_COMMENT_ALL);
              msg.setData(b);
              mClients.send(msg);
              break;
          }
        } catch (RemoteException e) {
          Log.d(TAG, e.toString());
          // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
        }
      }
    });
  }


  @Override
  public void onCreate() {
    super.onCreate();
    mInfoExtra = new InfoExtra(this);
    mPref = new HYPreference(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  /* REQUEST METHOD */
  // Request HeaderData
  public void requestCall_Header_Info() {
    mCDL_HeaderSet = new CountDownLatch(1);
    if (INFO)
      Log.i(TAG, "requestCall_Header_Info");
    String url = "http://ohy3264.cafe24.com/Anony/api/boardDetail.php";
    try {
      OkHttpClient client = new OkHttpClient();
      RequestBody formBody = new FormBody.Builder()
              .add("MODE", "HeaderIfo")
              .add("BSEQ", mSelectBoardID)
              .build();

      Request request = new Request.Builder()
              .url(url)
              .post(formBody)
              .build();

      Response response = client.newCall(request).execute();

      if (!response.isSuccessful()){
        throw new IOException("Unexpected code " + response);
      }else{
          BoardItem value = new Gson().fromJson(response.body().toString(), BoardItem.class);
          Log.d(TAG, "value - valueid :: " + value.get_id());
          mHeaderData.set_id(value.get_id());
          mHeaderData.setUniqueID(value.getUniqueID());
          mHeaderData.setGender(value.getGender());
          mHeaderData.setStrText(value.getStrText());
          mHeaderData.setHitCNT(value.getHitCNT());
          mHeaderData.setHateCNT(value.getHateCNT());
          mHeaderData.setLikeCNT(value.getLikeCNT());
          mHeaderData.setCommCNT(value.getCommCNT());
          mHeaderData.setImgState(value.getImgState());
          mHeaderData.setRegDate(HYTime_Maximum.formatTimeString(value.getRegDate()));
          mCDL_HeaderSet.countDown();

      }
    } catch (Exception e) { mCDL_HeaderSet.countDown();

      Log.i(TAG, e.toString());
    }
  }

  public void requestCall_CommentDel() {
    if (INFO)
      Log.i(TAG, "requestCall_CommentDel");
    mCommentList.clear();
    mCDL_CommentDel = new CountDownLatch(1);
    String url = "http://ohy3264.cafe24.com/Anony/api/commentDeleteTest.php";
    try {
      OkHttpClient client = new OkHttpClient();
      RequestBody formBody = new FormBody.Builder()
              .add("ID", mSelectCommentID)
              .add("BSEQ", mSelectBoardID)
              .build();

      Request request = new Request.Builder()
              .url(url)
              .post(formBody)
              .build();

      Response response = client.newCall(request).execute();

      if (!response.isSuccessful()){
        throw new IOException("Unexpected code " + response);
      }else{
        java.lang.reflect.Type type = new TypeToken<List<BoardItem>>() {
        }.getType();

        ArrayList<BoardItem> value = new Gson()
                .fromJson(response.body().toString(), type);

        for (BoardItem m : value) {
          BoardItem t = new BoardItem();

          t.set_id(m.get_id());
          t.setStrText(m.getStrText());
          t.setGender(m.getGender());
          t.setHateCNT(m.getHateCNT());
          t.setRegDate(HYTime_Maximum.formatTimeString(m.getRegDate()));
          t.setUniqueID(m.getUniqueID());
          mCommentList.add(t);
        }
        mCDL_CommentDel.countDown();

      }
    } catch (Exception e) {
      mCDL_CommentDel.countDown();

      Log.i(TAG, e.toString());
    }
  }


  // Request CommentData
  public void requestCall_CommentList() {
    if (INFO)
      Log.i(TAG, "requestCall_CommentList");
    mCommentList.clear();
    mCDL_CommentList = new CountDownLatch(1);
    String url = "http://ohy3264.cafe24.com/Anony/api/commentListAll.php";
    try {
      OkHttpClient client = new OkHttpClient();
      RequestBody formBody = new FormBody.Builder()
              .add("MODE", "CommentList")
              .add("BSEQ", mSelectBoardID)
              .build();

      Request request = new Request.Builder()
              .url(url)
              .post(formBody)
              .build();

      Response response = client.newCall(request).execute();

      if (!response.isSuccessful()){
        throw new IOException("Unexpected code " + response);
      }else{
        java.lang.reflect.Type type = new TypeToken<List<BoardItem>>() {
        }.getType();

        ArrayList<BoardItem> value = new Gson()
                .fromJson(response.body().toString(), type);

        for (BoardItem m : value) {
          BoardItem t = new BoardItem();

          t.set_id(m.get_id());
          t.setStrText(m.getStrText());
          t.setGender(m.getGender());
          t.setHateCNT(m.getHateCNT());
          t.setRegDate(HYTime_Maximum.formatTimeString(m.getRegDate()));
          t.setUniqueID(m.getUniqueID());


          mCommentList.add(t);
        }
        mCDL_CommentList.countDown();

      }
    } catch (Exception e) {
      mCDL_CommentList.countDown();

      Log.i(TAG, e.toString());
    }
    }

  public void requestNewCommentSend() {
    if (INFO)
      Log.i(TAG, "requestNewPostSend()");
    mCommentList.clear();
    mCDL_CommentInsert = new CountDownLatch(1);
    String url = "http://ohy3264.cafe24.com/Anony/api/newCommentSaveTest.php";
    StringRequest request = new StringRequest(Request.Method.POST, url,
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {
            if (DBUG)
              Log.d(TAG, "requestCall_CommentList - onResponse :: " + response.toString());
            try {
              java.lang.reflect.Type type = new TypeToken<List<BoardItem>>() {
              }.getType();

              ArrayList<BoardItem> value = new Gson()
                  .fromJson(response, type);

              for (BoardItem m : value) {
                BoardItem t = new BoardItem();

                t.set_id(m.get_id());
                t.setStrText(m.getStrText());
                t.setGender(m.getGender());
                t.setHateCNT(m.getHateCNT());
                t.setRegDate(HYTime_Maximum.formatTimeString(m.getRegDate()));
                t.setUniqueID(m.getUniqueID());
                mCommentList.add(t);
              }
            } catch (Exception e) {
              Log.d(TAG, "allsync.Response : Gson Exception");
            }
            sendMessageToUI(MSG_REGISTER_COMMENT);
            mCDL_CommentInsert.countDown();
          }
        }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError e) {
        Log.d(TAG,
            "onErrorResponse :: " + e.getLocalizedMessage());
        e.printStackTrace();
        mCDL_CommentInsert.countDown();
      }
    }) {
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        // TODO Auto-generated method stub
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("MODE", "NewPostSend");
        params.put("BSEQ", mHeaderData.get_id());
        params.put("B_ANDROID_ID", mHeaderData.getUniqueID());
        params.put("ANDROID_ID", mInfoExtra.getAndroidID());
        params.put("GENDER", mPref.getValue(mPref.KEY_GENDER, "0"));
        params.put("NEW_COMMENT", mEdtCommnet);
        return params;
      }

    };
    mRequestQueue.add(request);
  }


  // asyncTask Call
  public void asyncTask_CommentList_Call() {
    if (INFO)
      Log.i(TAG, "asyncTask_CommentList_Call");
    new asyncTask_BoardList().execute();

  }

  // asyncTask :: HeaderData, CommentData 요청
  private class asyncTask_BoardList extends AsyncTask<Void, Integer, Void> {
    private ProgressDialog mLoadingDialog;

    @Override
    protected void onPreExecute() {
      // TODO Auto-generated method stub
      super.onPreExecute();

      mCommentList.clear();
    }

    @Override
    protected Void doInBackground(Void... params) {
      // TODO Auto-generated method stub
      requestCall_Header_Info();
      requestCall_CommentList();
      try {
        mCDL_HeaderSet.await();
        mCDL_CommentList.await();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      // TODO Auto-generated method stub
      super.onPostExecute(result);
      sendMessageToUI(MSG_REGISTER_CLIENT);

    }

  }
}
