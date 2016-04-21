package com.hw.oh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hw.oh.model.BoardItem;
import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.InfoExtra;

import java.util.ArrayList;
import java.util.List;

public class BoardListAdapter extends BaseAdapter {
  private Context mContext;
  private List<BoardItem> mBoardItem;
  private HYFont mFont;
  private InfoExtra mInfoExtra;
  private ViewHolder mHolder;
  private final int max_cache_size = 1000000;
  private final String mImgURI = "http://ohy3264.cafe24.com/Anony/api/uploads/Img";

  public BoardListAdapter(Context context, ArrayList<BoardItem> temp) {
    mContext = context;
    mBoardItem = temp;
    mFont = new HYFont(context);
    mInfoExtra = new InfoExtra(context);
  }

  @Override
  public int getCount() {
    // TODO Auto-generated method stub
    return mBoardItem.size();
  }

  @Override
  public Object getItem(int position) {
    // TODO Auto-generated method stub
    return mBoardItem.get(position);
  }

  @Override
  public long getItemId(int position) {
    // TODO Auto-generated method stub
    return mBoardItem.get(position).hashCode();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    // TODO Auto-generated method stub
    View ret;

    if (convertView == null) {
      ret = LayoutInflater.from(mContext).inflate(
          R.layout.row_talk_list, null);
      mHolder = new ViewHolder();
      mHolder.txtPost = (TextView) ret
          .findViewById(R.id.txtPost);
      mHolder.imgGender = (ImageView) ret
          .findViewById(R.id.imgGender);
      mHolder.networkimgProfile = (ImageView) ret
          .findViewById(R.id.networkimageview);
      mHolder.txtPostType = (TextView) ret
          .findViewById(R.id.txtPostType);
      mHolder.txtRagDate = (TextView) ret
          .findViewById(R.id.txtRagDate);
      mHolder.txtLikeCNT = (TextView) ret
          .findViewById(R.id.txtLikeCNT);
      mHolder.txtHateCNT = (TextView) ret
          .findViewById(R.id.txtHateCNT);
      mHolder.txtCommCNT = (TextView) ret
          .findViewById(R.id.txtCommCNT);

      ret.setTag(mHolder);
    } else {
      ret = convertView;
      mHolder = (ViewHolder) ret.getTag();
    }
        /* 데이터 셋 */
    if (Integer.parseInt(mBoardItem.get(position).getHateCNT()) > 5) {
      mHolder.txtPost
          .setText("다수의 신고로 블라인드 처리된 글입니다.");
    } else {
      mHolder.txtPost
          .setText(mBoardItem.get(position).getStrText());
    }
    mHolder.txtRagDate
        .setText(mBoardItem.get(position).getRegDate());
    mHolder.txtLikeCNT
        .setText(mBoardItem.get(position).getLikeCNT());
    mHolder.txtHateCNT
        .setText(mBoardItem.get(position).getHateCNT());
    mHolder.txtCommCNT
        .setText(mBoardItem.get(position).getCommCNT());
    //성별
    if (mBoardItem.get(position).getGender().equals("0")) {
      mHolder.imgGender.setImageResource(R.drawable.icon_man);
    } else {
      mHolder.imgGender.setImageResource(R.drawable.icon_woman);
    }
    //수신, 발신
    if (mBoardItem.get(position).getUniqueID().equals(mInfoExtra.getAndroidID())) {
      mHolder.txtPostType.setText("Send");
    } else {
      mHolder.txtPostType.setText("From");
    }
    // 이미지 유무
    if (mBoardItem.get(position).getImgState().equals("1")) {
      if (Integer.parseInt(mBoardItem.get(position).getHateCNT()) < 5) {
        mHolder.networkimgProfile.setVisibility(View.VISIBLE);
        Glide.with(mContext).load(mImgURI + mBoardItem.get(position).get_id() + ".jpg").into(mHolder.networkimgProfile);
      }
    } else {
      mHolder.networkimgProfile.setVisibility(View.INVISIBLE);
    }

    mFont.setGlobalFont((ViewGroup) ret);
    return ret;
  }

  public class ViewHolder {
    public TextView txtPost;
    public ImageView imgGender;
    public ImageView networkimgProfile;
    public TextView txtPostType;
    public TextView txtLikeCNT;
    public TextView txtHateCNT;
    public TextView txtCommCNT;
    public TextView txtRagDate;

  }


}
