package com.hw.oh.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;

import java.util.ArrayList;
import java.util.List;


public class AlbaInfoAdapter extends BaseAdapter {
  public static final String TAG = "AlbaInfoAdapter";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;


  private Context mContext;
  private List<String> mAlbaInfoList;
  private HYFont mFont;

  public AlbaInfoAdapter(Context context, ArrayList<String> objects) {
    // TODO Auto-generated constructor stub
    mContext = context;
    mAlbaInfoList = objects;
    mFont = new HYFont(mContext);
  }

  public int getCount() {
    // TODO Auto-generated method stub
    return mAlbaInfoList.size();
  }

  public Object getItem(int position) {
    // TODO Auto-generated method stub
    return mAlbaInfoList.get(position);
  }

  @Override
  public long getItemId(int position) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    // TODO Auto-generated method stub
    View ret;
    ViewHolder holder;
    if (convertView == null) {
      ret = LayoutInflater.from(mContext).inflate(
          R.layout.row_alba_info1, null);

      holder = new ViewHolder();
      holder.mTxtInfo = (TextView) ret.findViewById(R.id.txtInfo);


      ret.setTag(holder);
    } else {
      ret = convertView;
      holder = (ViewHolder) ret.getTag();
    }
    try {
      holder.mTxtInfo.setText(mAlbaInfoList.get(position).toString());

    } catch (Exception e) {
      Log.e(TAG, e.toString());
    }


    mFont.setGlobalFont((ViewGroup) ret);
    return ret;
  }

  public class ViewHolder {
    private TextView mTxtInfo;

  }

}
