package com.stynet.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.stynet.widget.helper.UnitConvert;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by xx shuiDianBing, 2018/08/30-17:03:17:03.Refer to the website: nullptr
 **/

public class BubbleDialog extends Dialog {
    /**
     * 气泡位置
     */
    public enum Position {
        /**
         * 左边
         */
        LEFT,
        /**
         * 上边
         */
        TOP,
        /**
         * 右边
         */
        RIGHT,
        /**
         * 下边
         */
        BOTTOM
    }
    /**
     * 自动确定位置
     */
    public enum Auto
    {
        /**
         * 四周
         */
        AROUND,
        /**
         * 上下显示
         */
        UP_AND_DOWN,
        /**
         * 左右显示
         */
        LEFT_AND_RIGHT
    }
    private Activity activity;
    private BubbleLayout bubbleLayout;
    private int width, height, margin;
    private View addView;//需要添加的view
    private View clickedView;//点击的View
    private boolean calBar;//计算中是否包含状态栏
    private int offsetX, offsetY;//x和y方向的偏移
    private int relativeOffset;//相对与被点击view的偏移
    private boolean softShowUp;//当软件盘弹出时Dialog上移
    private Position position = Position.TOP;//气泡位置，默认上位
    private boolean isAutoPosition = false;//是否自动决定显示的位置
    private Auto auto;//记录自动确定位置的方案
    private boolean isThroughEvent = false;//是否穿透Dialog事件交互
    private boolean cancelable;//是否能够取消
    private int[] clickedViewLocation = new int[2];
    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;
    public BubbleDialog(Activity activity) {
        super(activity, R.style.bubbleDialog);
        setCancelable(true);
        this.activity = activity;
        Window window = getWindow();
        if (window == null) return;
        final WindowManager.LayoutParams params = window.getAttributes();
        final int screenW = UnitConvert.getScreenWH(getContext())[0];
        final int statusBar =  UnitConvert.getStatusHeight(getContext());
        getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isThroughEvent) {
                    float x = params.x < 0 ? 0 : params.x;//如果小于0则等于0
                    x = x + v.getWidth() > screenW ? screenW - v.getWidth() : x;

                    x += event.getX();
                    float y = params.y + event.getY() + (calBar ? statusBar : 0);

//                LogUtil.e2(String.format("(%s, %s) > (%s, %s)", event.getX(), event.getY(), x, y));
                    event.setLocation(x, y);
                    BubbleDialog.this.activity.getWindow().getDecorView().dispatchTouchEvent(event);
                    return true;
                } else
                    return false;
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isThroughEvent && keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            dismiss();
            activity.onBackPressed();
            activity = null;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (bubbleLayout == null)
            bubbleLayout = new BubbleLayout(getContext());
        if (addView != null)
            bubbleLayout.addView(addView);
        setContentView(bubbleLayout);

        final Window window = getWindow();
        if (window == null) return;
        if (softShowUp)
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        onAutoPosition();

        setLook();
//        mBubbleLayout.post(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                dialogPosition();
//            }
//        });

        mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            int lastWidth, lastHeight;
            @Override
            public void onGlobalLayout() {
                if (lastWidth == bubbleLayout.getWidth() && lastHeight == bubbleLayout.getHeight()) return;
                dialogPosition();
                lastWidth = bubbleLayout.getWidth();
                lastHeight = bubbleLayout.getHeight();
            }
        };

        bubbleLayout.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);


        bubbleLayout.setClickEdgeListener(new BubbleLayout.ClickEdgeListener() {
            @Override
            public void onEdge() {
                if (BubbleDialog.this.cancelable)
                    dismiss();
            }
        });
    }

    /**
     * 处理自动位置
     */
    private void onAutoPosition() {
        if (!isAutoPosition && auto == null || clickedView == null) return;
        final int[] spaces = new int[4];//被点击View左上右下分别的距离边缘的间隔距离
        spaces[0] = clickedViewLocation[0];//左距离
        spaces[1] = clickedViewLocation[1];//上距离
        spaces[2] = UnitConvert.getScreenWH(getContext())[0] - clickedViewLocation[0] - clickedView.getWidth();//右距离
        spaces[3] = UnitConvert.getScreenWH(getContext())[1] - clickedViewLocation[1] - clickedView.getHeight() - (calBar ? UnitConvert.getStatusHeight(getContext()) : 0);//下距离

        if (auto != null) {
            switch (auto) {
                case AROUND:
                    break;
                case UP_AND_DOWN:
                    position = spaces[1] > spaces[3] ? Position.TOP : Position.BOTTOM;
                    return;
                case LEFT_AND_RIGHT:
                    position = spaces[0] > spaces[2] ? Position.LEFT : Position.RIGHT;
                    return;
                default:
            }
        }

        int max = 0;
        for (int value : spaces)
            if (value > max)
                max = value;

        if (max == spaces[0])
            position = Position.LEFT;
        else if (max == spaces[1])
            position = Position.TOP;
        else if (max == spaces[2])
            position = Position.RIGHT;
        else if (max == spaces[3])
            position = Position.BOTTOM;
    }

    private void setLook() {
        switch (position) {
            case LEFT:
                bubbleLayout.setLook(BubbleLayout.Look.RIGHT);
                break;
            case TOP:
                bubbleLayout.setLook(BubbleLayout.Look.BOTTOM);
                break;
            case RIGHT:
                bubbleLayout.setLook(BubbleLayout.Look.LEFT);
                break;
            case BOTTOM:
                bubbleLayout.setLook(BubbleLayout.Look.TOP);
                break;
        }
        bubbleLayout.initPadding();
    }

    @Override
    public void dismiss() {
        if (softShowUp)
            UnitConvert.hide(BubbleDialog.this);
        if (bubbleLayout != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            bubbleLayout.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
        super.dismiss();
    }

    private void dialogPosition() {
        if (clickedView == null)
            return;

        Window window = getWindow();
        if (window == null) return;
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        FrameLayout.LayoutParams bubbleParams = null;

//        if (mWidth != 0 || mHeight != 0)
//        {
//            ViewGroup.LayoutParams bubbleParams = mBubbleLayout.getLayoutParams();
//            bubbleParams.width = MATCH_PARENT;
//            bubbleParams.height = MATCH_PARENT;
//            mBubbleLayout.setLayoutParams(bubbleParams);
//        }

        if (width != 0)
            params.width = width;

        if (height != 0)
            params.height = height;

        if (margin != 0) {
            bubbleParams = (FrameLayout.LayoutParams) bubbleLayout.getLayoutParams();
            if (position == Position.TOP || position == Position.BOTTOM) {
                bubbleParams.leftMargin = margin;
                bubbleParams.rightMargin = margin;
            } else {
                bubbleParams.topMargin = margin;
                bubbleParams.bottomMargin = margin;
            }
            bubbleLayout.setLayoutParams(bubbleParams);
        }

        switch (position) {
            case TOP:
            case BOTTOM:
                params.x = clickedViewLocation[0] + clickedView.getWidth() / 2 - bubbleLayout.getWidth() / 2 + offsetX;
                if (margin != 0 && width == MATCH_PARENT)
                    bubbleLayout.setLookPosition(clickedViewLocation[0] - margin + clickedView.getWidth() / 2 - bubbleLayout.getLookWidth() / 2);
                else if (params.x <= 0)
                    bubbleLayout.setLookPosition(clickedViewLocation[0] + clickedView.getWidth() / 2 - bubbleLayout.getLookWidth() / 2);
                else if (params.x + bubbleLayout.getWidth() > UnitConvert.getScreenWH(getContext())[0])
                    bubbleLayout.setLookPosition(clickedViewLocation[0] - (UnitConvert.getScreenWH(getContext())[0] - bubbleLayout.getWidth()) + clickedView.getWidth() / 2 - bubbleLayout.getLookWidth() / 2);
                else
                    bubbleLayout.setLookPosition(clickedViewLocation[0] - params.x + clickedView.getWidth() / 2 - bubbleLayout.getLookWidth() / 2);
                if (position == Position.BOTTOM) {
                    if (relativeOffset != 0) offsetY = relativeOffset;
                    params.y = clickedViewLocation[1] - (calBar ? UnitConvert.getStatusHeight(getContext()) : 0) + clickedView.getHeight() + offsetY;

                } else {
                    if (relativeOffset != 0) offsetY = -relativeOffset;
                    params.y = clickedViewLocation[1] - (calBar ? UnitConvert.getStatusHeight(getContext()) : 0) - bubbleLayout.getHeight() + offsetY;
                }
                break;
            case LEFT:
            case RIGHT:
                params.y = clickedViewLocation[1] - (calBar ? UnitConvert.getStatusHeight(getContext()) : 0) + offsetY + clickedView.getHeight() / 2 - bubbleLayout.getHeight() / 2;
                if (margin != 0 && height == MATCH_PARENT)
                    bubbleLayout.setLookPosition(clickedViewLocation[1] - margin + clickedView.getHeight() / 2 - bubbleLayout.getLookWidth() / 2 - (calBar ? UnitConvert.getStatusHeight(getContext()) : 0));
                else if (params.y <= 0)
                    bubbleLayout.setLookPosition(clickedViewLocation[1] + clickedView.getHeight() / 2 - bubbleLayout.getLookWidth() / 2 - (calBar ? UnitConvert.getStatusHeight(getContext()) : 0));
                else if (params.y + bubbleLayout.getHeight() > UnitConvert.getScreenWH(getContext())[1])
                    bubbleLayout.setLookPosition(clickedViewLocation[1] - (UnitConvert.getScreenWH(getContext())[1] - bubbleLayout.getHeight()) + clickedView.getHeight() / 2 - bubbleLayout.getLookWidth() / 2);
                else
                    bubbleLayout.setLookPosition(clickedViewLocation[1] - params.y + clickedView.getHeight() / 2 - bubbleLayout.getLookWidth()/ 2 - (calBar ? UnitConvert.getStatusHeight(getContext()) : 0));
                if (position == Position.RIGHT) {
                    if (relativeOffset != 0) offsetX = relativeOffset;
                    params.x = clickedViewLocation[0] + clickedView.getWidth() + offsetX;
                } else {
                    if (relativeOffset != 0) offsetX = -relativeOffset;
                    params.x = clickedViewLocation[0] -  bubbleLayout.getWidth() + offsetX;
                }
                break;
        }


        bubbleLayout.invalidate();
        window.setAttributes(params);
    }

    public boolean onTouchEvent(MotionEvent event) {
        Window window = getWindow();

        if (window == null) return false;
        final View decorView = window.getDecorView();
        if (this.cancelable && isShowing() && shouldCloseOnTouch(event, decorView)) {
            cancel();
            return true;
        }
        return false;
    }

    public boolean shouldCloseOnTouch(MotionEvent event, View decorView) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        return (x <= 0) || (y <= 0)
                || (x > (decorView.getWidth()))
                || (y > (decorView.getHeight()));
    }

    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
        cancelable = flag;
    }


    /**
     * @param width 设置气泡的宽
     * @param height 设置气泡的高
     * @param margin 设置距离屏幕边缘的间距,只有当设置 width 或 height 为 MATCH_PARENT 才有效
     */
    public <T extends BubbleDialog> T setLayout(int width, int height, int margin) {
        this.width = width;
        this.height = height;
        this.margin = margin;
        return (T) this;
    }

    /**
     * 计算时是否包含状态栏(如果有状态栏目，而没有设置为true将会出现上下的偏差)
     */
    public <T extends BubbleDialog> T calBar(boolean cal) {
        this.calBar = cal;
        return (T) this;
    }

    /**
     * 设置被点击的view来设置弹出dialog的位置
     */
    public <T extends BubbleDialog> T setClickedView(View view) {
        this.clickedView = view;
        clickedView.getLocationOnScreen(clickedViewLocation);
        if (mOnGlobalLayoutListener != null) {
            onAutoPosition();
            setLook();
            dialogPosition();
        }
        return (T) this;
    }

    /**
     * 当软件键盘弹出时，dialog根据条件上移
     */
    public <T extends BubbleDialog> T softShowUp() {
        this.softShowUp = true;
        return (T) this;
    }

    /**
     * 设置dialog内容view
     */
    public <T extends BubbleDialog> T addContentView(View view) {
        this.addView = view;
        return (T) this;
    }

    /**
     * 设置气泡位置
     */
    public <T extends BubbleDialog> T setPosition(Position position) {
        this.position = position;
        return (T) this;
    }

    /**
     * 设置是否自动设置Dialog位置
     * @deprecated 弃用，改用新方法{@link #autoPosition(Auto)}
     */
    @Deprecated
    public <T extends BubbleDialog> T autoPosition(boolean isAutoPosition) {
        this.isAutoPosition = isAutoPosition;
        return (T) this;
    }

    /**
     * 设置是否自动设置Dialog的位置
     * @param auto 自动设置位置的方案
     * @see Auto#AROUND 位置可在相对于被点击控件的四周
     * @see Auto#LEFT_AND_RIGHT 位置只可在相对于被点击控件左右
     * @see Auto#UP_AND_DOWN 位置只可在相对于被点击控件的上下
     */
    public <T extends BubbleDialog> T autoPosition(Auto auto) {
        this.auto = auto;
        return (T) this;
    }

    /**
     * 设置是否穿透Dialog手势交互
     * @param cancelable 点击空白是否能取消Dialog，只有当"isThroughEvent = false"时才有效
     */
    public <T extends BubbleDialog> T setThroughEvent(boolean isThroughEvent, boolean cancelable) {
        this.isThroughEvent = isThroughEvent;
        if (isThroughEvent)
            setCancelable(false);
        else
            setCancelable(cancelable);
        return (T) this;
    }

    /**
     * 设置x方向偏移量
     */
    public <T extends BubbleDialog> T setOffsetX(int offsetX) {
        this.offsetX = UnitConvert.dip2px(getContext(), offsetX);
        return (T) this;
    }

    /**
     * 设置y方向偏移量
     */
    public <T extends BubbleDialog> T setOffsetY(int offsetY) {
        this.offsetY = UnitConvert.dip2px(getContext(), offsetY);
        return (T) this;
    }

    /**
     * 设置dialog相对与被点击View的偏移
     */
    public <T extends BubbleDialog> T setRelativeOffset(int relativeOffset) {
        this.relativeOffset = UnitConvert.dip2px(getContext(), relativeOffset);
        return (T) this;
    }

    /**
     * 自定义气泡布局
     */
    public <T extends BubbleDialog> T setBubbleLayout(BubbleLayout bl) {
        this.bubbleLayout = bl;
        return (T) this;
    }

    /**
     * 背景全透明
     */
    public <T extends BubbleDialog> T setTransParentBackground() {
        Window window = getWindow();
        if (window == null) return (T) this;
        window.setDimAmount(0);
        return (T) this;
    }
}
