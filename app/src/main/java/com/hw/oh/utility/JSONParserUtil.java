package com.hw.oh.utility;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Platform Development Center - NMP Corp.
 *
 * @author David KIM
 * @since 1.0
 */
public class JSONParserUtil {

    /**
    *
    * get some object
    *
    * @param jsonStr
    * @param searchWord
    * @return
    */
   public static Object getObject(final Object jsonStr, final String searchWord) {

       Object res = null;

       try {
           JSONObject obj = new JSONObject((String)jsonStr);
           res = obj.get(searchWord);
       } catch (Exception e) {
           e.printStackTrace();
       }
       return res;
   }

//    /**
//     *
//     * MemberSimple Domain 객체 반환
//     *
//     * @param jsonStr
//     * @param searchWord
//     * @return
//     */
//    public static MemberSimple getMemberSimpleDomainObject(final Object jsonStr, final String searchWord) {
//
//        MemberSimple memberSimple = null;
//
//        try {
//            ObjectMapper mObjMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//            JSONObject obj = new JSONObject((String)jsonStr);
//            Object res = obj.get(searchWord);
//            memberSimple = mObjMapper.readValue(res.toString(), MemberSimple.class);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return memberSimple;
//    }

//        /**
//         *
//         * Member Domain 객체 반환
//         *
//         * @param jsonStr
//         * @param searchWord
//         * @return
//         */
//        public static Member getMemberDomainObject(final Object jsonStr, final String searchWord) {
//
//        	Member member = null;
//
//        	try {
//                ObjectMapper mObjMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//                JSONObject obj = new JSONObject((String)jsonStr);
//                Object res = obj.get(searchWord);
//                member = mObjMapper.readValue(res.toString(), Member.class);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return member;
//        }

//        /**
//        *
//        * Member List Domain 객체 반환
//        *
//        * @param jsonStr
//        * @param searchWord
//        * @return
//        */
//        public static ArrayList<Member> getMemberParser(final Object jsonStr, final String searchWord){
//
//        	JSONObject obj;
//        	Member member;
//        	ArrayList<Member> list = new ArrayList<Member>();
//
//        	try {
//        		ObjectMapper mObjMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        		JSONArray ja = new JSONObject((String)jsonStr).getJSONArray(searchWord);
//
//        		for(int i=0; i<ja.length(); i++){
//        			obj = (JSONObject) ja.get(i);
//        			member = mObjMapper.readValue(obj.toString(), Member.class);
//        			list.add(member);
//        		}
//
//    		} catch (Exception e) {
//    			e.printStackTrace();
//    		}
//
//        	return list;
//        }

//        /**
//        *
//        * App List Domain 객체 반환
//        *
//        * @param jsonStr
//        * @param searchWord
//        * @return
//        */
//        public static ArrayList<App> appParser(final Object jsonStr, final String searchWord){
//
//        	JSONObject obj;
//        	App app;
//        	ArrayList<App> list = new ArrayList<App>();
//
//        	try {
//        		ObjectMapper mObjMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        		JSONArray ja = new JSONObject((String)jsonStr).getJSONArray(searchWord);
//
//        		for(int i=0; i<ja.length(); i++){
//        			obj = (JSONObject) ja.get(i);
//        			app = mObjMapper.readValue(obj.toString(), App.class);
//        			list.add(app);
//        		}
//
//    		} catch (Exception e) {
//    			e.printStackTrace();
//    		}
//
//        	return list;
//        }

//        /**
//        *
//        * Friend Domain 객체 반환
//        *
//        * @param jsonStr
//        * @param searchWord
//        * @return
//        */
//       public static Friend friendDomainObject(final Object jsonStr, final String searchWord) {
//
//       	Friend friend = null;
//
//       	try {
//               ObjectMapper mObjMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//               JSONObject obj = new JSONObject((String)jsonStr);
//               Object res = obj.get(searchWord);
//               friend = mObjMapper.readValue(res.toString(), Friend.class);
//
//           } catch (Exception e) {
//               e.printStackTrace();
//           }
//
//           return friend;
//       }

//        /**
//        *
//        * Friend List Domain 객체 반환
//        *
//        * @param jsonStr
//        * @param searchWord
//        * @return
//        */
//        public static ArrayList<Friend> friendParser(final Object jsonStr, final String searchWord){
//
//        	JSONObject obj;
//        	Friend friend;
//        	ArrayList<Friend> list = new ArrayList<Friend>();
//
//        	try {
//        		ObjectMapper mObjMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        		JSONArray ja = new JSONObject((String)jsonStr).getJSONArray(searchWord);
//
//        		for(int i=0; i<ja.length(); i++){
//        			obj = (JSONObject) ja.get(i);
//        			friend = mObjMapper.readValue(obj.toString(), Friend.class);
//        			list.add(friend);
//        		}
//
//    		} catch (Exception e) {
//    			e.printStackTrace();
//    		}
//
//        	return list;
//        }


//        /**
//        *
//        * Kok List Domain 객체 반환
//        *
//        * @param jsonStr
//        * @param searchWord
//        * @return
//        */
//        public static ArrayList<Kok> KokParser(final Object jsonStr, final String searchWord) {
//
//        	JSONObject obj;
//    	    Kok kok;
//    	    ArrayList<Kok> list = new ArrayList<Kok>();
//
//    	    try {
//    	    	ObjectMapper mObjMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//    		    JSONArray ja = new JSONObject((String)jsonStr).getJSONArray(searchWord);
//
//    		    for(int i=0; i<ja.length(); i++){
//    		    	obj = (JSONObject) ja.get(i);
//       			    kok = mObjMapper.readValue(obj.toString(), Kok.class);
//       			    list.add(kok);
//    		    }
//
//    	    } catch (Exception e) {
//    	    	e.printStackTrace();
//    	    }
//
//    	    return list;
//        }

//        /**
//         *
//         * Auction Domain 객체 반환
//         *
//         * @param jsonStr
//         * @param searchWord
//         * @return
//         */
//        public static Auction getAuctionDomainObject(final Object jsonStr, final String searchWord) {
//
//        	Auction auction = null;
//
//        	try {
//                ObjectMapper mObjMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//                JSONObject obj = new JSONObject((String)jsonStr);
//                Object res = obj.get(searchWord);
//                auction = mObjMapper.readValue(res.toString(), Auction.class);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return auction;
//        }
//
//        /**
//        *
//        * Auction Domain 객체 반환
//        *
//        * @param jsonStr
//        * @param searchWord
//        * @return
//        */
//        public static ArrayList<Auction> auctionParser(final Object jsonStr, final String searchWord){
//
//        	JSONObject obj;
//        	Auction auction;
//        	ArrayList<Auction> list = new ArrayList<Auction>();
//
//        	try {
//        		ObjectMapper mObjMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        		JSONArray ja = new JSONObject((String)jsonStr).getJSONArray(searchWord);
//
//        		for(int i=0; i<ja.length(); i++){
//        			obj = (JSONObject) ja.get(i);
//        			auction = mObjMapper.readValue(obj.toString(), Auction.class);
//        			list.add(auction);
//        		}
//
//    		} catch (Exception e) {
//    			e.printStackTrace();
//    		}
//
//        	return list;
//        }
//
//        /**
//        *
//        * AuctionApply Domain 객체 반환
//        *
//        * @param jsonStr
//        * @param searchWord
//        * @return
//        */
//        public static ArrayList<AuctionApply> auctionApplyParser(final Object jsonStr, final String searchWord){
//
//        	JSONObject obj;
//        	AuctionApply auctionApply;
//        	ArrayList<AuctionApply> list = new ArrayList<AuctionApply>();
//
//        	try {
//        		ObjectMapper mObjMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        		JSONArray ja = new JSONObject((String)jsonStr).getJSONArray(searchWord);
//
//        		for(int i=0; i<ja.length(); i++){
//        			obj = (JSONObject) ja.get(i);
//        			auctionApply = mObjMapper.readValue(obj.toString(), AuctionApply.class);
//        			list.add(auctionApply);
//        		}
//
//    		} catch (Exception e) {
//    			e.printStackTrace();
//    		}
//
//        	return list;
//        }
//
//        /**
//        *
//        * Store Domain 객체 반환
//        *
//        * @param jsonStr
//        * @param searchWord
//        * @return ArrayList<Store>
//        */
//        public static ArrayList<Store> storeParser(final Object jsonStr, final String searchWord){
//
//        	JSONObject obj;
//        	Store store;
//        	ArrayList<Store> list = new ArrayList<Store>();
//
//        	try {
//        		ObjectMapper mObjMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        		JSONArray ja = new JSONObject((String)jsonStr).getJSONArray(searchWord);
//
//        		for(int i=0; i<ja.length(); i++){
//        			obj = (JSONObject) ja.get(i);
//        			store = mObjMapper.readValue(obj.toString(), Store.class);
//        			list.add(store);
//        		}
//
//    		} catch (Exception e) {
//    			e.printStackTrace();
//    		}
//
//        	return list;
//        }
//
//        /**
//        *
//        * MyWalletCoupon Domain 객체 반환
//        *
//        * @param jsonStr
//        * @param searchWord
//        * @return ArrayList<MyWalletCoupon>
//        */
//        public static ArrayList<MyWalletCoupon> myWalletCouponParser(final Object jsonStr, final String searchWord){
//
//        	JSONObject obj;
//        	MyWalletCoupon myWalletCoupon;
//        	ArrayList<MyWalletCoupon> list = new ArrayList<MyWalletCoupon>();
//
//        	try {
//        		ObjectMapper mObjMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        		JSONArray ja = new JSONObject((String)jsonStr).getJSONArray(searchWord);
//
//        		for(int i=0; i<ja.length(); i++){
//        			obj = (JSONObject) ja.get(i);
//        			myWalletCoupon = mObjMapper.readValue(obj.toString(), MyWalletCoupon.class);
//        			list.add(myWalletCoupon);
//        		}
//
//    		} catch (Exception e) {
//    			e.printStackTrace();
//    		}
//
//        	return list;
//        }
//
//        /**
//         *
//         * MyWalletCouponDetail Domain 객체 반환
//         *
//         * @param jsonStr
//         * @param searchWord
//         * @return MyWalletCouponDetail 도메인
//         */
//        public static MyWalletCouponDetail getMyWalletCouponDetailDomainObject(final Object jsonStr, final String searchWord) {
//
//        	MyWalletCouponDetail myWalletCouponDetail = null;
//
//        	try {
//                ObjectMapper mObjMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//                JSONObject obj = new JSONObject((String)jsonStr);
//                Object res = obj.get(searchWord);
//                myWalletCouponDetail = mObjMapper.readValue(res.toString(), MyWalletCouponDetail.class);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return myWalletCouponDetail;
//        }
//
//        /**
//        *
//        * Campaign List Domain 객체 반환 (애드팝콘)
//        *
//        * @param jsonStr
//        * @param searchWord
//        * @return
//        */
//        public static ArrayList<Campaign> campaignParser(final Object jsonStr, final String searchWord){
//
//        	JSONObject obj;
//        	Campaign campaign;
//        	ArrayList<Campaign> list = new ArrayList<Campaign>();
//
//        	try {
//        		ObjectMapper mObjMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        		JSONArray ja = new JSONObject((String)jsonStr).getJSONArray(searchWord);
//
//        		for(int i=0; i<ja.length(); i++){
//        			obj = (JSONObject) ja.get(i);
//        			campaign = mObjMapper.readValue(obj.toString(), Campaign.class);
//        			list.add(campaign);
//        		}
//
//    		} catch (Exception e) {
//    			e.printStackTrace();
//    		}
//
//        	return list;
//        }

   /**
    *
    * get some String
    *
    * @param jsonStr
    * @param searchWord
    * @return
    */
   public static String getString(final Object jsonStr, final String searchWord) {

       Object res = null;

       try {
           JSONObject obj = new JSONObject((String)jsonStr);
           res = obj.get(searchWord);

           if(CommonUtil.isNull(res))
               return "";

       } catch (Exception e) {
           e.printStackTrace();
       }
       return String.valueOf(res);
   }

   /**
    *
    * get some String[]
    *
    * @param jsonStr
    * @param searchWord
    * @return
    */
   public static String[] getArrayString(final Object jsonStr, final String searchWord) {

      Object res[] = null;

      try {
          JSONObject obj = new JSONObject((String)jsonStr);
          res = (String[])obj.get(searchWord);

          if(CommonUtil.isNull(res))
              return null;

      } catch (Exception e) {
          e.printStackTrace();
      }
      return (String[]) res;
  }

   /**
    *
    * get some int
    *
    * @param jsonStr
    * @param searchWord
    * @return
    */
   public static int getInt(final Object jsonStr, final String searchWord) {

       Object res = null;

       try {
           JSONObject obj = new JSONObject((String)jsonStr);
           res = obj.get(searchWord);

           if(CommonUtil.isNull(res))
               return 0;

       } catch (Exception e) {
           e.printStackTrace();
       }
       return (Integer) res;
   }

   /**
    *
    * get some int Array
    *
    * @param jsonStr
    * @param searchWord
    * @return
    */
   public static Integer[] getArrayInt(final Object jsonStr, final String searchWord) {

       Object res[] = null;

       try {
           JSONObject obj = new JSONObject((String)jsonStr);
           res = (Integer[]) obj.get(searchWord);

           if(CommonUtil.isNull(res))
               return null;

       } catch (Exception e) {
           e.printStackTrace();
       }
       return (Integer[]) res;
   }

   /**
    *
    * get some boolean
    *
    * @param jsonStr
    * @param searchWord
    * @return
    */
   public static boolean getBoolean(final Object jsonStr, final String searchWord) {

       Object res = null;

       try {
           JSONObject obj = new JSONObject((String)jsonStr);
           res = obj.get(searchWord);

           if(CommonUtil.isNull(res))
               return false;

       } catch (Exception e) {
           e.printStackTrace();
       }
       return (Boolean) res;
   }

   //    /**
   //     *
   //     * profile paser
   //     *
   //     * @param jsonStr
   //     * @param searchWord
   //     * @return
   //     */
   //    public static ArrayList<Profile> profileParser(final Object jsonStr, final String searchWord) {
   //
   //        JSONObject obj;
   //        Profile profile;
   //        ArrayList<Profile> list = new ArrayList<Profile>();
   //
   //        try {
   //            ObjectMapper mObjMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
   //            JSONArray ja  = new JSONObject((String)jsonStr).getJSONArray(searchWord);
   //
   //            for(int i=0; i<ja.length(); i++){
   //                //profile = new Profile();
   //                obj = (JSONObject) ja.get(i);
   //                profile = mObjMapper.readValue(obj.toString(), Profile.class);
   //                list.add(profile);
   //            }
   //
   //        } catch (Exception e) {
   //            e.printStackTrace();
   //        }
   //
   //        return list;
   //    }

    /**
     *
     * get some JSONArray
     *
     * @param jsonStr
     * @param searchWord
     * @return
     */
    public static JSONArray getJSONArray(final Object jsonStr, final String searchWord) {

        Object res = null;

        try {
            JSONObject obj = new JSONObject((String)jsonStr);
            res = obj.get(searchWord);

            if(CommonUtil.isNull(res))
                return null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return (JSONArray)res;
    }
}