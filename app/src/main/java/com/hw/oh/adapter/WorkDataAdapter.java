package com.hw.oh.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hw.oh.model.PartTimeItem;
import com.hw.oh.model.WorkItem;
import com.hw.oh.temp.R;
import com.hw.oh.utility.HYFont;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class WorkDataAdapter extends ArrayAdapter<WorkItem> implements UndoAdapter {
  public static final String TAG = "WorkDataAdapter";
  public static final boolean DBUG = true;
  public static final boolean INFO = true;


  private Context mContext;
  private List<WorkItem> mAlbaInfoList;
  private HYFont mFont;
  private Calendar mCalStart, mCalEnd;
  private String day = "";
  private NumberFormat mNumFomat = new DecimalFormat("###,###,###");

  public WorkDataAdapter(Context context, ArrayList<WorkItem> objects) {
    // TODO Auto-generated constructor stub
    super(context, R.layout.row_work_list, objects);
    mContext = context;
    mAlbaInfoList = objects;
    mFont = new HYFont(mContext);
    mCalStart = Calendar.getInstance();
    mCalEnd = Calendar.getInstance();
  }


  @NonNull
  @Override
  public View getUndoView(final int position, final View convertView, @NonNull final ViewGroup parent) {
    View view = convertView;
    if (view == null) {
      view = LayoutInflater.from(mContext).inflate(R.layout.undo_row, parent, false);
    }
    return view;
  }

  @NonNull
  @Override
  public View getUndoClickView(@NonNull final View view) {
    return view.findViewById(R.id.undo_row_undobutton);
  }
  @Override
  public long getItemId(final int position) {
    return getItem(position).hashCode();
  }

  @Override
  public boolean hasStableIds() {
    return true;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    // TODO Auto-generated method stub
    View ret;
    ViewHolder holder;
    if (convertView == null) {
      ret = LayoutInflater.from(mContext).inflate(
          R.layout.row_work_list, null);
      holder = new ViewHolder();
      holder.mTxtMonth = (TextView) ret.findViewById(R.id.txtMonth);
      holder.mTxtDay = (TextView) ret.findViewById(R.id.txtDay);
      holder.mTxtTotalMoney = (TextView) ret.findViewById(R.id.txtTotalMoney);
      holder.mTxtStartTime = (TextView) ret.findViewById(R.id.txtStartTime);
      holder.mTxtEndTime = (TextView) ret.findViewById(R.id.txtEndTime);
      holder.mTxtMemo = (TextView) ret.findViewById(R.id.txtMemo);
      holder.mTxtGabulMoney = (TextView) ret.findViewById(R.id.txtGabulMoney);
      holder.mTxtEtc = (TextView) ret.findViewById(R.id.txtEtc);
      //holder.mLinGabulCheck = (LinearLayout) ret.findViewById(R.id.linGabulCheck);
      holder.mLinNightView = (LinearLayout) ret.findViewById(R.id.linNightView);
      holder.mLinRefreshTime = (LinearLayout) ret.findViewById(R.id.linRefreshTime);
      holder.mLinGabulMoney = (LinearLayout) ret.findViewById(R.id.linGabulView);
      holder.mLinAddMoney = (LinearLayout) ret.findViewById(R.id.linAddView);
      holder.mLinEtc = (LinearLayout) ret.findViewById(R.id.linEtc);
      holder.mLinWeek = (LinearLayout) ret.findViewById(R.id.linWeek);
      ret.setTag(holder);
    } else {
      ret = convertView;
      holder = (ViewHolder) ret.getTag();
    }
    try {
      mCalStart.set(Integer.parseInt(mAlbaInfoList.get(position).getYear()), Integer.parseInt(mAlbaInfoList.get(position).getMonth()), Integer.parseInt(mAlbaInfoList.get(position).getDay()), Integer.parseInt(mAlbaInfoList.get(position).getStartTimeHour()), Integer.parseInt(mAlbaInfoList.get(position).getStartTimeMin()));
      mCalEnd.set(Integer.parseInt(mAlbaInfoList.get(position).getYear()), Integer.parseInt(mAlbaInfoList.get(position).getMonth()), Integer.parseInt(mAlbaInfoList.get(position).getDay()), Integer.parseInt(mAlbaInfoList.get(position).getEndTimeHour()), Integer.parseInt(mAlbaInfoList.get(position).getEndTimeMin()));

      if (mCalEnd.compareTo(mCalStart) == 0 || mCalEnd.compareTo(mCalStart) == 1) {
        day = " (" + mCalEnd.get(Calendar.DATE) + "일)";
      } else {
        mCalEnd.add(Calendar.DATE, 1);
        day = " (" + mCalEnd.get(Calendar.DATE) + "일)";
      }

      holder.mTxtMonth.setText(mAlbaInfoList.get(position).getMonth());
      holder.mTxtDay.setText(mAlbaInfoList.get(position).getDay());
      holder.mTxtTotalMoney.setText(mNumFomat.format(mAlbaInfoList.get(position).getTotalMoney()));
      holder.mTxtStartTime.setText(mAlbaInfoList.get(position).getStartTimeHour() + " 시 " + mAlbaInfoList.get(position).getStartTimeMin() + " 분" + " (" + mCalStart.get(Calendar.DATE) + "일)");
      holder.mTxtEndTime.setText(mAlbaInfoList.get(position).getEndTimeHour() + " 시 " + mAlbaInfoList.get(position).getEndTimeMin() + " 분" + day);
      holder.mTxtMemo.setText(mAlbaInfoList.get(position).getSimpleMemo());
      //holder.mTxtEtc.setText(mAlbaInfoList.get(position).getWorkEtcNum()+"x "+mAlbaInfoList.get(position).getWorkEtcMoney());
      if (Boolean.parseBoolean(mAlbaInfoList.get(position).getWorkPayGabul())) {
      //  holder.mLinGabulCheck.setBackgroundResource(R.drawable.circleview_gabul);
        //holder.mTxtGabulMoney.setText("-"+mAlbaInfoList.get(position).getWorkPayGabulValue());
        holder.mLinGabulMoney.setVisibility(View.VISIBLE);

      } else {
       // holder.mLinGabulCheck.setBackgroundResource(R.drawable.circleview_red);
        holder.mLinGabulMoney.setVisibility(View.GONE);
      }
      if (Boolean.parseBoolean(mAlbaInfoList.get(position).getWorkNight())) {
        holder.mLinNightView.setVisibility(View.VISIBLE);
      } else {
        // holder.mLinNightView.setVisibility(View.INVISIBLE);
        holder.mLinNightView.setVisibility(View.GONE);
      }
      if (Boolean.parseBoolean(mAlbaInfoList.get(position).getWorkRefresh())) {
        holder.mLinRefreshTime.setVisibility(View.VISIBLE);
      } else {
        holder.mLinRefreshTime.setVisibility(View.GONE);
        // holder.mLinRefreshTime.setVisibility(View.GONE);
      }
      if (Boolean.parseBoolean(mAlbaInfoList.get(position).getWorkAdd())) {
        holder.mLinAddMoney.setVisibility(View.VISIBLE);
      } else {
        // holder.mLinAddMoney.setVisibility(View.INVISIBLE);
        holder.mLinAddMoney.setVisibility(View.GONE);
      }
      if (Boolean.parseBoolean(mAlbaInfoList.get(position).getWorkEtc())) {
        holder.mLinEtc.setVisibility(View.VISIBLE);
      } else {
        holder.mLinEtc.setVisibility(View.GONE);
      }
      if (Boolean.parseBoolean(mAlbaInfoList.get(position).getWorkWeek())) {
        holder.mLinWeek.setVisibility(View.VISIBLE);
      } else {
        holder.mLinWeek.setVisibility(View.GONE);
      }
    } catch (Exception e) {
      Log.e(TAG, e.toString());
    }


    mFont.setGlobalFont((ViewGroup) ret);
    return ret;
  }

  public class ViewHolder {
    private TextView mTxtTotalMoney, mTxtStartTime, mTxtEndTime, mTxtMemo, mTxtMonth, mTxtDay, mTxtGabulMoney, mTxtEtc;
    private LinearLayout  mLinNightView, mLinRefreshTime, mLinGabulMoney, mLinAddMoney, mLinEtc, mLinWeek;

  }
}
