package com.hw.oh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;

public class SpinnerAdapter extends ArrayAdapter<String> {
  private Context mContext;
  private HYFont mFont;
  private String[] mItems;

  public SpinnerAdapter(Context context, int textViewResourceId, String[] object) {
    super(context, textViewResourceId, object);
    mContext = context;
    mItems = object;
    mFont = new HYFont(mContext);
  }


  @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    return getCustomView(position, convertView, parent);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    return getCustomView(position, convertView, parent);
  }

  public View getCustomView(int position, View convertView, ViewGroup parent) {
    View ret;
    ViewHolder holder;

    if (convertView == null) {
      ret = LayoutInflater.from(mContext).inflate(R.layout.spinner_row_default, parent, false);

      holder = new ViewHolder();
      holder.label = (TextView) ret.findViewById(R.id.company);

      ret.setTag(holder);
    } else {
      ret = convertView;
      holder = (ViewHolder) ret.getTag();
    }
    holder.label.setText(mItems[position]);

    mFont.setGlobalFont((ViewGroup) ret);

    return ret;
  }

  public class ViewHolder {
    public TextView label;
  }

}