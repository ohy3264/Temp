package com.hw.oh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hw.oh.model.WeekWeatherItem;
import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;
import com.hw.oh.utility.HYStringUtil;

import java.util.ArrayList;
import java.util.List;


public class WeekWeatherAdapter extends BaseAdapter {

  private Context mContext;
  private List<WeekWeatherItem> mCategoryList;
  private HYFont mFont;

  public WeekWeatherAdapter(Context context, ArrayList<WeekWeatherItem> objects) {
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
          R.layout.row_weekweather, null);
      holder = new ViewHolder();
      holder.txtDay = (TextView) ret.findViewById(R.id.txtDay);
      holder.txtDate = (TextView) ret.findViewById(R.id.txtDate);
      holder.txtHigh = (TextView) ret.findViewById(R.id.txtHigh);
      holder.txtLow = (TextView) ret.findViewById(R.id.txtLow);
      holder.imageTemp = (ImageView) ret.findViewById(R.id.img_current_temp);

      ret.setTag(holder);
    } else {
      ret = convertView;
      holder = (ViewHolder) ret.getTag();
    }
    try {
      try {
        holder.txtDay.setText(HYStringUtil.yoilFunction(mCategoryList.get(position).getDay()));
        holder.txtDate.setText(HYStringUtil.monthFunction(HYStringUtil.spaceFunction(mCategoryList.get(position).getDate())[1]) + " " +
            HYStringUtil.spaceFunction(mCategoryList.get(position).getDate())[0]);
        holder.txtHigh.setText(mCategoryList.get(position).getHighTemp());
        holder.txtLow.setText(mCategoryList.get(position).getLowTemp());
        holder.imageTemp.setImageBitmap(mCategoryList.get(position).getTempImg());
           /* //성별
            if (mCategoryList.get(position).getGender().equals("0")) {
                holder.imageGender.setImageResource(R.drawable.icon_man);
            } else {
                holder.imageGender.setImageResource(R.drawable.icon_woman);
            }*/
      } catch (Exception e) {
        e.toString();
      }


    } catch (Exception e) {
      // TODO: handle exception

    }

    mFont.setGlobalFont((ViewGroup) ret);
    return ret;
  }

  public class ViewHolder {
    public TextView txtDay;
    public TextView txtDate;
    public ImageView imageTemp;
    public TextView txtHigh;
    public TextView txtLow;
  }

}
