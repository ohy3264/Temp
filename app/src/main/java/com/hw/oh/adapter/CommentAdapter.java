package com.hw.oh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hw.oh.model.BoardItem;
import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;

import java.util.ArrayList;
import java.util.List;


public class CommentAdapter extends BaseAdapter {

  private Context mContext;
  private List<BoardItem> mCategoryList;
  private HYFont mFont;

  public CommentAdapter(Context context, ArrayList<BoardItem> objects) {
    // TODO Auto-generated constructor stub
    mContext = context;
    mCategoryList = objects;
    mFont = new HYFont(mContext);
  }

  public int getCount() {
    // TODO Auto-generated method stub
    return mCategoryList.size();
  }

  public Object getItem(int position) {
    // TODO Auto-generated method stub
    return mCategoryList.get(position);
  }

  @Override
  public long getItemId(int position) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    // TODO Auto-generated method stub
    View ret;
    ViewHolder holder;
    if (convertView == null) {
      ret = LayoutInflater.from(mContext).inflate(
          R.layout.row_detail_list_comment, null);
      holder = new ViewHolder();
      holder.txtText = (TextView) ret.findViewById(R.id.txtStrText);
      holder.txtUnique = (TextView) ret.findViewById(R.id.txtUnique);
      holder.txtRegDate = (TextView) ret.findViewById(R.id.txtRagDate);
      holder.imageGender = (ImageView) ret.findViewById(R.id.imgGender);

      ret.setTag(holder);
    } else {
      ret = convertView;
      holder = (ViewHolder) ret.getTag();
    }
    try {

      if (Integer.parseInt(mCategoryList.get(position).getHateCNT()) > 10) {
        holder.txtText.setText("다수의 신고로 블라인드 처리된 글입니다.");
      } else {
        holder.txtText.setText(mCategoryList.get(position).getStrText());
      }
      holder.txtUnique.setText(mCategoryList.get(position).getUniqueID());

      holder.txtRegDate.setText(mCategoryList.get(position).getRegDate());

      //성별
      if (mCategoryList.get(position).getGender().equals("0")) {
        holder.imageGender.setImageResource(R.drawable.icon_man);
      } else {
        holder.imageGender.setImageResource(R.drawable.icon_woman);
      }


    } catch (Exception e) {
      // TODO: handle exception

    }

    mFont.setGlobalFont((ViewGroup) ret);
    return ret;
  }

  public class ViewHolder {
    public TextView txtText;
    public ImageView imageGender;
    public TextView txtRegDate;
    public TextView txtUnique;
  }

}
