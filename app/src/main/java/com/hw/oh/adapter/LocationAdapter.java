package com.hw.oh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;

import java.util.ArrayList;


public class LocationAdapter extends BaseAdapter {

  private Context mContext;
  private ArrayList<String> mKmaItemList = new ArrayList<String>();
  private HYFont mFont;

  public LocationAdapter(Context context, ArrayList<String> objects) {
    // TODO Auto-generated constructor stub
    mContext = context;
    mKmaItemList = objects;
    mFont = new HYFont(mContext);
  }

  public int getCount() {
    // TODO Auto-generated method stub
    return mKmaItemList.size();
  }

  public Object getItem(int position) {
    // TODO Auto-generated method stub
    return mKmaItemList.get(position);
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
          R.layout.row_location, null);
      holder = new ViewHolder();
      holder.txtLocation = (TextView) ret.findViewById(R.id.txtLocation);

      ret.setTag(holder);
    } else {
      ret = convertView;
      holder = (ViewHolder) ret.getTag();
    }
    try {
      try {
        holder.txtLocation.setText(mKmaItemList.get(position).toString());

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
    public TextView txtLocation;
  }
}
