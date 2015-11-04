package com.hw.oh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hw.oh.model.SampleItem;
import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oh on 2015-02-01.
 */
public class DrawerMenuAdapter extends BaseAdapter {
  private Context mContext;
  private List<SampleItem> mDrawMenuList;
  private HYFont mFont;

  public DrawerMenuAdapter(Context context, ArrayList<SampleItem> temp) {
    mContext = context;
    mDrawMenuList = temp;
    mFont = new HYFont(context);
  }

  @Override
  public int getCount() {
    // TODO Auto-generated method stub
    return mDrawMenuList.size();
  }

  @Override
  public Object getItem(int position) {
    // TODO Auto-generated method stub
    return mDrawMenuList.get(position);
  }

  @Override
  public long getItemId(int position) {
    // TODO Auto-generated method stub
    return mDrawMenuList.get(position).hashCode();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    // TODO Auto-generated method stub
    View ret;
    ViewHolder holder;

    if (convertView == null) {
      ret = LayoutInflater.from(mContext).inflate(
          R.layout.row_menu, null);
      holder = new ViewHolder();
      holder.imgRow = (ImageView) ret
          .findViewById(R.id.row_icon);
      holder.txtRow = (TextView) ret
          .findViewById(R.id.row_title);
      ret.setTag(holder);
    } else {
      ret = convertView;
      holder = (ViewHolder) ret.getTag();
    }
    holder.imgRow
        .setImageResource(mDrawMenuList.get(position).getIconRes());

    holder.txtRow.setText(mDrawMenuList.get(position).getTag());

    mFont.setGlobalFont((ViewGroup) ret);
    return ret;
  }

  public class ViewHolder {
    public TextView txtRow;
    public ImageView imgRow;
  }

}
