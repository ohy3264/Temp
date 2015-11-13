package com.hw.oh.utility;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hw.oh.temp.R;

public class HYFont {
  private Typeface mTypeface;
  private HYPreference mPref;

  public HYFont(Context context) {
    // TODO Auto-generated constructor stub
    mPref = new HYPreference(context);
    switch (mPref.getValue(mPref.KEY_FONT, 1)) {
      case 0:
        mTypeface = null;
        break;
      case 1:
        mTypeface = Typeface.createFromAsset(context.getAssets(),
            context.getString(R.string.sangsang));
        break;
      case 2:
        mTypeface = Typeface.createFromAsset(context.getAssets(),
            context.getString(R.string.godo));

        break;
    }
  }

  public void setGlobalFont(ViewGroup root) {
    if (mTypeface instanceof Typeface) {
      for (int i = 0; i < root.getChildCount(); i++) {
        View child = root.getChildAt(i);
        if (child instanceof TextView)
          ((TextView) child).setTypeface(mTypeface);
        else if (child instanceof ViewGroup)
          setGlobalFont((ViewGroup) child);
      }
    }
  }
}
