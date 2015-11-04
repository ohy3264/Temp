package com.hw.oh.popupWindow;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hw.oh.temp.R;

import java.util.ArrayList;
import java.util.List;


public class QuickAction extends PopupWindows implements OnDismissListener {
  private View mRootView;
  private LayoutInflater mInflater;
  private ViewGroup mTrack;
  private ScrollView mScroller;
  private OnActionItemClickListener mItemClickListener;
  private OnDismissListener mDismissListener;

  private RelativeLayout.LayoutParams mVerticalSeparatorParam =
      new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 2);

  private List<ActionItem> mActionItems = new ArrayList<ActionItem>();

  private DisplayMetrics mDisplayMetrics;

  private boolean mDidAction;

  private int mChildPos;
  private int mInsertPos;
  private int mAnimStyle;
  private int mOrientation;
  private int mRootWidth = 0;

  public static final int HORIZONTAL = 0;
  public static final int VERTICAL = 1;

  public static final int ANIM_GROW_FROM_LEFT = 1;
  public static final int ANIM_GROW_FROM_RIGHT = 2;
  public static final int ANIM_GROW_FROM_CENTER = 3;
  public static final int ANIM_REFLECT = 4;
  public static final int ANIM_AUTO = 5;

  /**
   * Constructor for default vertical layout
   *
   * @param context Context
   */
  public QuickAction(Context context) {
    this(context, VERTICAL);
  }

  /**
   * Constructor allowing orientation override
   *
   * @param context     Context
   * @param orientation Layout orientation, can be vartical or horizontal
   */
  public QuickAction(Context context, int orientation) {
    super(context);

    mOrientation = orientation;

    mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    mDisplayMetrics = context.getResources().getDisplayMetrics();

    if (mOrientation == HORIZONTAL) {
      setRootViewId(R.layout.popup_horizontal);
    } else {
      setRootViewId(R.layout.popup_vertical);
    }

    mAnimStyle = ANIM_AUTO;
    mChildPos = 0;
  }

  /**
   * Get action item at an index
   *
   * @param index Index of item (position from callback)
   * @return Action Item at the position
   */
  public ActionItem getActionItem(int index) {
    return mActionItems.get(index);
  }

  /**
   * Set root view.
   *
   * @param id Layout resource id
   */
  public void setRootViewId(int id) {
    mRootView = (ViewGroup) mInflater.inflate(id, null);
    mTrack = (ViewGroup) mRootView.findViewById(R.id.tracks);

    mScroller = (ScrollView) mRootView.findViewById(R.id.scroller);

    //This was previously defined on show() method, moved here to prevent force close that occured
    //when tapping fastly on a view to show quickaction dialog.
    //Thanx to zammbi (github.com/zammbi)
    mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

    setContentView(mRootView);
  }

  /**
   * Set animation style
   *
   * @param mAnimStyle animation style, default is set to ANIM_AUTO
   */
  public void setAnimStyle(int mAnimStyle) {
    this.mAnimStyle = mAnimStyle;
  }

  /**
   * Set listener for action item clicked.
   *
   * @param listener Listener
   */
  public void setOnActionItemClickListener(OnActionItemClickListener listener) {
    mItemClickListener = listener;
  }

  /**
   * Add action item
   *
   * @param action {@link ActionItem}
   */
  public void addActionItem(ActionItem action) {
    mActionItems.add(action);

    String title = action.getTitle();
    Drawable icon = action.getIcon();

    View container;

    if (mOrientation == HORIZONTAL) {
      container = mInflater.inflate(R.layout.action_item_horizontal, null);
    } else {
      container = mInflater.inflate(R.layout.action_item_vertical, null);
    }

    ImageView img = (ImageView) container.findViewById(R.id.iv_icon);
    TextView text = (TextView) container.findViewById(R.id.tv_title);

    if (icon != null) {
      img.setImageDrawable(icon);
    } else {
      img.setVisibility(View.GONE);
    }

    if (title != null) {
      text.setText(title);
    } else {
      text.setVisibility(View.GONE);
    }

    final int pos = mChildPos;
    final int actionId = action.getActionId();

    container.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mItemClickListener != null) {
          mItemClickListener.onItemClick(QuickAction.this, pos, actionId);
        }

        if (!getActionItem(pos).isSticky()) {
          mDidAction = true;

          dismiss();
        }
      }
    });

    container.setFocusable(true);
    container.setClickable(true);

    if (mOrientation == HORIZONTAL && mChildPos != 0) {
      View separator = mInflater.inflate(R.layout.horiz_separator, null);

      RelativeLayout.LayoutParams params =
          new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
      separator.setPadding(5, 0, 5, 0);

      mTrack.addView(separator, mInsertPos, params);

      mInsertPos++;
    }

//		if(mOrientation == VERTICAL && mChildPos != 0){
//			View separator = mInflater.inflate(R.layout.vertical_separator, null);
//
//			mTrack.addView(separator, mInsertPos, mVerticalSeparatorParam);
//
//			mInsertPos++;
//		}

    mTrack.addView(container, mInsertPos);

    mChildPos++;
    mInsertPos++;
  }

  /**
   * Show quickaction popup. Popup is automatically positioned, on top or bottom of anchor view.
   */
  public void show(View anchor) {
    preShow();

    int xPos, yPos;

    mDidAction = false;

    int[] location = new int[2];

    anchor.getLocationOnScreen(location);

    Rect anchorRect = new Rect(location[0], location[1],
        location[0] + anchor.getWidth(), location[1] + anchor.getHeight());

    //mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

    mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

    int rootHeight = mRootView.getMeasuredHeight();

    if (mRootWidth == 0) {
      mRootWidth = mRootView.getMeasuredWidth();
      Log.d("doring", "dd : " + mRootWidth);
    }

    int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
    int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

    //automatically get X coord of popup (top left)
    if ((anchorRect.left + mRootWidth) > screenWidth) {
      xPos = anchorRect.left - mRootWidth + anchorRect.width();
      xPos = (xPos < 0) ? 0 : xPos;
    } else {
      if (anchor.getWidth() > mRootWidth) {
        xPos = anchorRect.centerX() - (mRootWidth / 2);
      } else {
        xPos = anchorRect.left;
      }
    }

    int dyTop = anchorRect.top;
    int dyBottom = screenHeight - anchorRect.bottom;

    boolean onTop = (dyTop > dyBottom) ? true : false;

    int gab = (int) (mDisplayMetrics.density * 15);
    if (onTop) {
      if (rootHeight > dyTop) {
        yPos = gab;
        LayoutParams l = mScroller.getLayoutParams();
        l.height = dyTop - anchor.getHeight();
      } else {
        yPos = anchorRect.top - rootHeight;
      }
    } else {
      yPos = anchorRect.bottom - gab;

      if (rootHeight > dyBottom) {
        LayoutParams l = mScroller.getLayoutParams();
        l.height = dyBottom;
      }
    }

    setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);

    mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
  }

  /**
   * Set animation style
   *
   * @param screenWidth screen width
   * @param requestedX  distance from left edge
   * @param onTop       flag to indicate where the popup should be displayed. Set TRUE if displayed
   *                    on top of anchor view and vice versa
   */
  private void setAnimationStyle(int screenWidth, int requestedX, boolean onTop) {
    switch (mAnimStyle) {
      case ANIM_GROW_FROM_LEFT:
        mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
        break;

      case ANIM_GROW_FROM_RIGHT:
        mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
        break;

      case ANIM_GROW_FROM_CENTER:
        mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
        break;

      case ANIM_REFLECT:
        mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Reflect : R.style.Animations_PopDownMenu_Reflect);
        break;

      case ANIM_AUTO:
        if (requestedX <= screenWidth / 4) {
          mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
        } else if (requestedX > screenWidth / 4 && requestedX < 3 * (screenWidth / 4)) {
          mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
        } else {
          mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
        }

        break;
    }
  }


  /**
   * Set listener for window dismissed. This listener will only be fired if the quicakction dialog
   * is dismissed by clicking outside the dialog or clicking on sticky item.
   */
  public void setOnDismissListener(OnDismissListener listener) {
    setOnDismissListener(this);

    mDismissListener = listener;
  }

  @Override
  public void onDismiss() {
    if (!mDidAction && mDismissListener != null) {
      mDismissListener.onDismiss();
    }
  }

  /**
   * Listener for item click
   */
  public interface OnActionItemClickListener {
    public abstract void onItemClick(QuickAction source, int pos, int actionId);
  }

  /**
   * Listener for window dismiss
   */
  public interface OnDismissListener {
    public abstract void onDismiss();
  }
}