package com.hw.oh.utility;

/**
 * Created by oh on 2015-02-04.
 */
public class Constant {
  public static final String LOG = "HWOH";
  public static String IMG_UPLOAD_URL = "http://ohy3264.cafe24.com/Anony/api/uploads/Img";
  public static String SERVER_URL = "http://ohy3264.cafe24.com";
  public static String GUIDE_MSG1 = "";
  public static String GUIDE_MSG2 = "";
  public static String GUIDE_MSG3 = "";
  //Fragment_Main flag - 모든카테고리 갱신 플래그
  public static int REQUEST_NONE = 0;
  public static int REQUEST_NEWPOST = 1;
  public static int REQUEST_TOP_REFRESH = 2;
  public static int REQUEST_BOTTOM_REFRESH = 3;
  public static int REQUEST_DELPOST = 4;
  public static int REFRESH_BOARD_FLAG = REQUEST_NONE;

  public static boolean RESPON_EMPTY = false;
  public static int LIMIT_START = 0;
  public static int LIMIT_ADD = 15;

  // 서버 통신 디폴트 시도 횟수
  public static final int MAX_ATTEMPTS = 5;
  // 네트워크 에러코드
  public static final int ERROR_NETWORK = 1;
  public static final int ERROR_SERVER = 2;
  public static final int ERROR_UNKNOWN = 3;

  public static final String SUCCESS = "SUCCESS";
  public static final String WRONG = "WRONG";
  public static final String FAIL = "FAIL";
  public static final String DRAW = "DRAW";

  public static String ANI_STATE_UP = "UP";
  public static String ANI_STATE_DOWN = "DOWN";
  public static String ANI_STATE = ANI_STATE_UP; // 하단메뉴 애니메이션 상태

  //Detail Activity flag - 상세화면에서 변경이 있을 때만 갱신 플래그
  public static int DETAIL_REQUEST_NONE = 0;
  public static int DETAIL_REQUEST_FIRST = 1;
  public static int DETAIL_REQUEST_NEW_COMMENT = 2;
  public static int DETAIL_REQUEST_IMAGE_CLICK = 3;
  public static int REFRESH_DETAIL_FLAG = DETAIL_REQUEST_NONE;

  //Fragment_Calendar - 달력
    /*public static String YEAR;
    public static String MONTH;
    public static String DAY;*/

  public static int NEWWORK_INTENT_NEW = 0;
  public static int NEWWORK_INTENT_UPDATE = 1;
  public static int NEWWORK_INTENT_FLAG = NEWWORK_INTENT_NEW;


  //Serach Activity flag
  public static int SERACH_RESULT_NONE = 0;
  public static int SERACH_RESULT_SELECT = 1;
  public static int SERACH_MAIN_FLAG = SERACH_RESULT_NONE;

  //Select Alba Info
  //public static PartTimeInfo SEL_INFO;

  public static final String PROJECT_ID = "828106356184"; // GCM


  /* admob  on/off */
  public static boolean ADMOB = true;

  public static String FONT_NAME = "fonts/SangSangTitle.ttf";


  // Location
  public static Double LAT; //위도
  public static Double LNG; //경도

  // In APP ITEM
  public static final String ADDMOB_REMOVE = "admob_remove";
}
