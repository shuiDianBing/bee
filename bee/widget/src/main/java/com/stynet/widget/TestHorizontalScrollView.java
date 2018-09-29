package com.stynet.widget;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.stynet.widget.helper.HorizontalScroller;

/**
 * Created by xx shuiDianBing, 2018/09/29-10:22:10:22.Refer to the website: nullptr
 **/

public class TestHorizontalScrollView extends ViewGroup {
    //手势
    private GestureDetector mGestureDetector;
    private int startX;
    private int startY;
    private int distanceX;
    private int distanceY;

    private int currentX;
    private int currentY;

    private long startTime;
    private long duration = 1000L;

    private boolean isFinish;

    /**
     *
     * @param scrollX
     *            x坐标
     * @param scrollY
     *            y坐标
     * @param distanceX
     *            X方向移动的距离
     * @param distanceY
     *            y方向移动的距离
     */
    public void startScroll(int scrollX, int scrollY, int distanceX, int distanceY) {
        startX = scrollX;
        startY = scrollY;
        this.distanceX = distanceX;
        this.distanceY = distanceY;
        isFinish = false;
        startTime = SystemClock.uptimeMillis();
    }

    /**
     * 计算偏移量，
     *
     * @return true 还在移动 false：移动已经停止
     */
    public boolean computeScrollOffset() {
        if (isFinish) {
            return false;
        }

        long timePassed = SystemClock.uptimeMillis() - startTime;

        if (timePassed < duration) {

            currentX = (int) (startX + distanceX * timePassed / duration);
            currentY = (int) (startY + distanceY * timePassed / duration);

            System.out.println("currentX:::" + currentX);
        } else if (timePassed >= duration) {
            currentX = startX + distanceX;
            currentY = startY + distanceY;
            isFinish = true;
        }

        return true;
    }

    public int getCurrX() {
        return currentX;
    }

    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    private HorizontalScroller mScroller;
    private int curID;
    //快速滑动
    private boolean isFlying;

    //--回调函数-------------------------------------
    private OnChangeListener mListener;
    public void setOnChangeListener(OnChangeListener listener) {
        if (listener != null) {
            mListener = listener;
        }
    }
    public interface OnChangeListener{
        void move2dest(int curID);
    }

    public TestHorizontalScrollView(Context context) {
        this(context, null);
    }

    public TestHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new HorizontalScroller();
        isFlying = false;
        initGesture(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 模向移动，
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            //给水平方向的每个view定位
            view.layout(i * getWidth(), 0, getWidth() + i * getWidth(), getHeight());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.measure(widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isFlying) {
                    move2dest();
                }
                isFlying = false;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return true;
    }

    public void move2dest() {
        //
        int destID = (getScrollX() + getWidth() / 2) / getWidth();
        move2dest(destID);
    }

    public void move2dest(int destID) {
        curID = destID;

        if (destID > getChildCount() - 1) {
            destID = getChildCount() - 1;
        }

        if (mListener != null) {
            mListener.move2dest(curID);
        }

        int distance = (int) (destID * getWidth() - getScrollX());
        // scrollBy(distance, 0);
        mScroller.startScroll(getScrollX(), getScrollY(), distance, 0);
        invalidate();
    }

    /**
     * invalidate()此方法会触发下面的方法
     */
    @Override
    public void computeScroll() {
        // 如果存在偏移，就不断刷新
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
        super.computeScroll();
    }

    private void initGesture(Context context) {
        mGestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                scrollBy((int) distanceX, 0);
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
            }

            @Override
            /**
             * 快速滑动时
             */
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                isFlying = true;
                if (curID > 0 && velocityX > 0) {// 表示向左移
                    move2dest(curID - 1);
                } else if (curID < getChildCount() && velocityX < 0) {
                    move2dest(curID + 1);// 向右
                } else {
                    move2dest();// 移到原位
                }
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }
        });
    }
}
