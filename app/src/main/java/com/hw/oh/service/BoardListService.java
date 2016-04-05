package com.hw.oh.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.hw.oh.model.BoardItem;
import com.hw.oh.utility.Constant;
import com.hw.oh.utility.HYTime_Maximum;
import com.hw.oh.utility.ServerRequestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by oh on 2015-07-10.
 */
public class BoardListService extends Service {
  private static final String TAG = "BoardListService";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;


  public static final int MSG_REGISTER_CLIENT = 1; // 메시지 클라이언트 등록
  public static final int MSG_UNREGISTER_CLIENT = 2; // 메시지 클라이언트 해제
  public static final int MSG_REQ_REFRESH_CLIENT = 3; // 메시지 리프레쉬
  public static final int MSG_SET_REQUEST_VALUE = 4;

  Messenger mClients;
  //Flag
  private boolean mResponeEmpty = false;
  private ArrayList<BoardItem> mBoardDataList = new ArrayList<BoardItem>();
  private ArrayList<BoardItem> mBoardPrevList = new ArrayList<BoardItem>();

  //Utill
  private CountDownLatch mCountDownLatch_BoardList;


  //클라이언트로 부터 메시지 수신하는 핸들러
  class IncomingHandler extends Handler { // Handler of incoming messages from clients.

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case MSG_REGISTER_CLIENT:
          Log.d(TAG, "mHandler.case MSG_REGISTER_CLIENT");
          mClients = msg.replyTo;
        //  asyncTask_BoardList_Call();
          requestCallRest_BoardList();

          break;

        case MSG_UNREGISTER_CLIENT:
          Log.d(TAG, "mHandler.case MSG_UNREGISTER_CLIENT");


          break;

        case MSG_REQ_REFRESH_CLIENT:
          Log.d(TAG, "mHandler.case MSG_REFRESH_CLIENT");
        //  asyncTask_BoardList_Call();
          requestCallRest_BoardList();

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

  private void sendMessageToUI() {
    new Handler().post(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "sendMessageToUI");

        try {
          // Send data as an Integer

          //Send data as a String
          Bundle b = new Bundle();
          b.putParcelableArrayList("Board_Data", mBoardDataList);
          Message msg = Message.obtain(null, MSG_SET_REQUEST_VALUE);
          msg.setData(b);
          mClients.send(msg);

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
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  // Intent Handler
  final Handler listCallHandler = new android.os.Handler() {
    public void handleMessage(Message msg) {
      switch(msg.what){
        case -1:
          break;
        case 0:
          Log.d(TAG, "onResponse :: " + msg.obj);
          String response = msg.obj.toString();
          Constant.RESPON_EMPTY = false;
          if (response.trim().equals("null")) {
            Log.d(TAG, "null");
            Constant.RESPON_EMPTY = true;
            mBoardDataList = mBoardPrevList;
          } else {
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
                t.setRegDate(HYTime_Maximum.formatTimeString(m.getRegDate()));
                t.setUniqueID(m.getUniqueID());
                t.setHitCNT(m.getHitCNT());
                t.setCommCNT(m.getCommCNT());
                t.setLikeCNT(m.getLikeCNT());
                t.setHateCNT(m.getHateCNT());
                t.setImgState(m.getImgState());
                mBoardDataList.add(t);
                Log.d(TAG, m.getStrText());
              }

            } catch (Exception e) {
              Log.d(TAG, "allsync.Response : Gson Exception");
            }
          }
          break;
      }//switch
      sendMessageToUI();
    }
  };
  public void requestCallRest_BoardList() {
    switch (Constant.REFRESH_BOARD_FLAG) {
      case 0:
        Constant.LIMIT_START = 0;
        break;
      case 1:
        Constant.LIMIT_START = 0;
        break;
      case 2:
        Constant.LIMIT_START = 0;
        break;
      case 3:
        Constant.LIMIT_START += Constant.LIMIT_ADD;

        break;
      case 4:
        Constant.LIMIT_START = 0;
        break;
    }

    mBoardPrevList = mBoardDataList;
    //  mBoardDataList.clear();
    mBoardDataList = new ArrayList<BoardItem>();
    try {
      HashMap<String, String> params = new HashMap<String, String>();
      params.put("MODE", "BoardList");
      params.put("LIMIT", Integer.toString(Constant.LIMIT_START));
      ServerRequestUtils.request(this, "http://ohy3264.cafe24.com", "/Anony/api/boardListAll.php", params, listCallHandler);




    } catch (Exception e) {
      Log.d(TAG, "Splash:handler2: " + e.getMessage());
    }
  }

  /*public void requestCallRest_BoardList() {
    mCountDownLatch_BoardList = new CountDownLatch(1);
    mBoardPrevList = mBoardDataList;
    //  mBoardDataList.clear();
    mBoardDataList = new ArrayList<BoardItem>();

    String url = "http://ohy3264.cafe24.com/Anony/api/boardListAll_Test.php";
    StringRequest request = new StringRequest(Request.Method.POST, url,
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {
            Log.d(TAG, "onResponse :: " + response.toString());
            Constant.RESPON_EMPTY = false;
            if (response.trim().equals("null")) {
              Log.d(TAG, "null");
              Constant.RESPON_EMPTY = true;
              mBoardDataList = mBoardPrevList;
              mCountDownLatch_BoardList.countDown();
            } else {
              try {
                JSONObject jsonObj = new JSONObject(response);
                Boolean more = jsonObj.getBoolean("more");
                String data = jsonObj.getString("data");

                java.lang.reflect.Type type = new TypeToken<List<BoardItem>>() {
                }.getType();

                ArrayList<BoardItem> value = new Gson()
                    .fromJson(data, type);

                for (BoardItem m : value) {
                  BoardItem t = new BoardItem();
                  t.set_id(m.get_id());
                  t.setStrText(m.getStrText());
                  t.setGender(m.getGender());
                  t.setRegDate(HYTime_Maximum.formatTimeString(m.getRegDate()));
                  t.setUniqueID(m.getUniqueID());
                  t.setHitCNT(m.getHitCNT());
                  t.setCommCNT(m.getCommCNT());
                  t.setLikeCNT(m.getLikeCNT());
                  t.setHateCNT(m.getHateCNT());
                  t.setImgState(m.getImgState());
                  mBoardDataList.add(t);
                }
                mCountDownLatch_BoardList.countDown();

              } catch (Exception e) {
                Log.d(TAG, "allsync.Response : Gson Exception");
                mCountDownLatch_BoardList.countDown();
              }
            }
          }
        }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError e) {
        Log.d(TAG,
            "onErrorResponse :: " + e.getLocalizedMessage());
        e.printStackTrace();
      }
    }) {
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        Log.i(TAG, Integer.toString(Constant.LIMIT_START));
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("MODE", "BoardList");
        params.put("LIMIT", Integer.toString(Constant.LIMIT_START));
        return params;
      }
    };
    mRequestQueue.add(request);
  }
*/

 /* public void asyncTask_BoardList_Call() {
    if (INFO)
      Log.i(TAG, "asyncTask_BoardList_Call");
    new asyncTask_BoardList().execute();
  }

  private class asyncTask_BoardList extends AsyncTask<Void, Integer, Void> {

    @Override
    protected void onPreExecute() {
      // TODO Auto-generated method stub
      super.onPreExecute();

      switch (Constant.REFRESH_BOARD_FLAG) {
        case 0:
          Constant.LIMIT_START = 0;
          break;
        case 1:
          Constant.LIMIT_START = 0;
          break;
        case 2:
          Constant.LIMIT_START = 0;
          break;
        case 3:
          Constant.LIMIT_START += Constant.LIMIT_ADD;

          break;
        case 4:
          Constant.LIMIT_START = 0;
          break;
      }
    }

    @Override
    protected Void doInBackground(Void... params) {
      // TODO Auto-generated method stub

     *//* requestCallRest_BoardList();
      try {
        mCountDownLatch_BoardList.await();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }*//*
      requestCallRest_BoardListT();



      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      // TODO Auto-generated method stub
      super.onPostExecute(result);
      sendMessageToUI();

    }
  }*/
}
