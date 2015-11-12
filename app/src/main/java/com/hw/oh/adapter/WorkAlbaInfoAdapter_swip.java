package com.hw.oh.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hw.oh.model.PartTimeInfo;
import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


public class WorkAlbaInfoAdapter_swip extends ArrayAdapter<PartTimeInfo> {
  public static final String TAG = "WorkAlbaInfoAdapter_swip";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;

  private Context mContext;
  private List<PartTimeInfo> mAlbaInfoList;
  private HYFont mFont;
  private NumberFormat mNumFomat = new DecimalFormat("###,###,###");


  public WorkAlbaInfoAdapter_swip(Context context, ArrayList<PartTimeInfo> objects) {
    super(context, R.layout.row_alba_swip, objects);
    mContext = context;
    mAlbaInfoList = objects;
    mFont = new HYFont(mContext);
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    // TODO Auto-generated method stub
    View ret;
    ViewHolder holder;
    if (convertView == null) {
      ret = LayoutInflater.from(mContext).inflate(
          R.layout.row_alba_swip, null);
      holder = new ViewHolder();
      holder.mTxtAlbaName = (TextView) ret.findViewById(R.id.txtAlbaName);
      holder.mImageHandle = (ImageView) ret.findViewById(R.id.handle);
      ret.setTag(holder);
    } else {
      ret = convertView;
      holder = (ViewHolder) ret.getTag();
    }
    try {
      holder.mTxtAlbaName.setText(mAlbaInfoList.get(position).getAlbaname());



    } catch (Exception e) {
      Log.e(TAG, e.toString());
    }

    mFont.setGlobalFont((ViewGroup) ret);
    return ret;
  }

  public class ViewHolder {
    private TextView mTxtAlbaName;
    private ImageView mImageHandle;
  }

}
