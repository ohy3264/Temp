package com.hw.oh.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hw.oh.model.PartTimeInfo;
import com.hw.oh.temp.NewAlbaActivity;
import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


public class WorkAlbaInfoAdapter_Array extends ArrayAdapter<PartTimeInfo> {
  public static final String TAG = "WorkAlbaInfoAdapter_Array";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;

  private Context mContext;
  private List<PartTimeInfo> mAlbaInfoList;
  private HYFont mFont;
  private NumberFormat mNumFomat = new DecimalFormat("###,###,###");


  public WorkAlbaInfoAdapter_Array(Context context, ArrayList<PartTimeInfo> objects) {
    super(context, R.layout.row_alba_list, objects);
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
          R.layout.row_alba_list, null);
      holder = new ViewHolder();
      holder.mTxtMoney = (TextView) ret.findViewById(R.id.txtMoney);
      holder.mTxtStartTime = (TextView) ret.findViewById(R.id.txtStartTime);
      holder.mTxtEndTime = (TextView) ret.findViewById(R.id.txtEndTime);
      holder.mTxtMemo = (TextView) ret.findViewById(R.id.txtMemo);
      holder.mTxtAlbaName = (TextView) ret.findViewById(R.id.txtAlbaName);
      holder.mBtnUpdate = (ImageButton) ret.findViewById(R.id.btnUdate);
      holder.mLinNightView = (LinearLayout) ret.findViewById(R.id.linNightView);
      holder.mLinRefreshTime = (LinearLayout) ret.findViewById(R.id.linRefreshTime);
      holder.mLinAddMoney = (LinearLayout) ret.findViewById(R.id.linAddView);
      holder.mLinEtc = (LinearLayout) ret.findViewById(R.id.linEtc);
      holder.mLinWeek = (LinearLayout) ret.findViewById(R.id.linWeek);
      ret.setTag(holder);
    } else {
      ret = convertView;
      holder = (ViewHolder) ret.getTag();
    }
    try {
      holder.mTxtAlbaName.setText(mAlbaInfoList.get(position).getAlbaname());
      holder.mTxtMoney.setText(mNumFomat.format(Double.parseDouble(mAlbaInfoList.get(position).getHourMoney())));
      holder.mTxtStartTime.setText(mAlbaInfoList.get(position).getStartTimeHour() + " 시 " + mAlbaInfoList.get(position).getStartTimeMin() + " 분");
      holder.mTxtEndTime.setText(mAlbaInfoList.get(position).getEndTimeHour() + " 시 " + mAlbaInfoList.get(position).getEndTimeMin() + " 분");
      holder.mTxtMemo.setText(mAlbaInfoList.get(position).getSimpleMemo());
      holder.mBtnUpdate.setFocusable(false);
      holder.mBtnUpdate.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intnet_UpdateAlba = new Intent(mContext, NewAlbaActivity.class);
          intnet_UpdateAlba.putExtra("Flag", "UPDATE");
          intnet_UpdateAlba.putExtra("ObjectData", mAlbaInfoList.get(position));
          mContext.startActivity(intnet_UpdateAlba);
        }
      });


      if (Boolean.parseBoolean(mAlbaInfoList.get(position).getWorkPayNight())) {
        holder.mLinNightView.setVisibility(View.VISIBLE);
      } else {
        //holder.mLinNightView.setVisibility(View.INVISIBLE);
        holder.mLinNightView.setVisibility(View.GONE);
      }
      if (Boolean.parseBoolean(mAlbaInfoList.get(position).getWorkRefresh())) {
        holder.mLinRefreshTime.setVisibility(View.VISIBLE);
      } else {
        holder.mLinRefreshTime.setVisibility(View.GONE);
        //holder.mLinRefreshTime.setVisibility(View.GONE);
      }
      if (Boolean.parseBoolean(mAlbaInfoList.get(position).getWorkPayAdd())) {
        holder.mLinAddMoney.setVisibility(View.VISIBLE);
      } else {
        //holder.mLinAddMoney.setVisibility(View.INVISIBLE);
        holder.mLinAddMoney.setVisibility(View.GONE);
      }

      if (Boolean.parseBoolean(mAlbaInfoList.get(position).getWorkPayEtc())) {
        holder.mLinEtc.setVisibility(View.VISIBLE);
      } else {
        //holder.mLinAddMoney.setVisibility(View.INVISIBLE);
        holder.mLinEtc.setVisibility(View.GONE);
      }

      if (Boolean.parseBoolean(mAlbaInfoList.get(position).getWorkPayWeek())) {
        holder.mLinWeek.setVisibility(View.VISIBLE);
      } else {
        //holder.mLinAddMoney.setVisibility(View.INVISIBLE);
        holder.mLinWeek.setVisibility(View.GONE);
      }


    } catch (Exception e) {
      Log.e(TAG, e.toString());
    }

    mFont.setGlobalFont((ViewGroup) ret);
    return ret;
  }

  public class ViewHolder {
    private TextView mTxtMoney, mTxtStartTime, mTxtEndTime, mTxtMemo, mTxtAlbaName;
    private ImageButton mBtnUpdate;
    private LinearLayout mLinNightView, mLinRefreshTime, mLinAddMoney, mLinEtc, mLinWeek;
  }

}
