package com.stynet.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by xx shuiDianBing, 2018/09/28-17:13:17:13.Refer to the website: nullptr
 * 自定义ViewGroup实现水平滑动 https://blog.csdn.net/deng0zhaotai/article/details/21404589
 * Android view体系简析及自定义滑动ViewGroup的优化 https://www.jianshu.com/p/c1d04960cfa1
 **/

public class ScrollLayout extends ViewGroup {
    private Scroller scroller;//弹性滑动对象，用于实现View的弹性滑动
    private VelocityTracker velocityTracker;// 速度轨迹追踪
    private int childCount;//子View数量
    private int childIndex;//子View索引
    private int measuredHeight;//子View的高度
    private int measuredWidth;//子View的宽度
    private float upX,upY;//放开的坐标点
    private float downX, downY;//按下的坐标点
    public ScrollLayout(Context context) {
        super(context);
        scroller = new Scroller(context);
        velocityTracker = VelocityTracker.obtain();
    }

    public ScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(context);
        velocityTracker = VelocityTracker.obtain();
    }

    public ScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new Scroller(context);
        velocityTracker = VelocityTracker.obtain();
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScrollLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        scroller = new Scroller(context);
        velocityTracker = VelocityTracker.obtain();
    }
    private void initAttribute(@Nullable TypedArray typedArray){ }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(),attrs);
    }

    @Override//自定义View学习笔记之详解onMeasure https://www.jianshu.com/p/1695988095a5
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        // 1.计算自定义的ViewGroup中所有子控件的大小
        // measureChildren(widthMeasureSpec, heightMeasureSpec);
        int width = getPaddingLeft(),height = getPaddingTop();
        for (int index = 0; index < getChildCount(); index++)//测量childView宽高
            if(GONE != getChildAt(index).getVisibility()) {
                measureChild(getChildAt(index), widthMeasureSpec, heightMeasureSpec);// 为ScrollerLayout中的每一个子控件测量大小
                measuredWidth = getChildAt(index).getMeasuredWidth();
                measuredHeight = getChildAt(index).getMeasuredHeight();
                height = Math.max(height, measuredHeight);
                width += measuredWidth;
                height += measuredHeight;
            }
        // 设置自定义的控件的大小，如果是MeasureSpec.EXACTLY则直接使用父ViewGroup传入的宽和高，否则设置为自己计算的宽和高
        setMeasuredDimension(MeasureSpec.EXACTLY == MeasureSpec.getMode(widthMeasureSpec) ? MeasureSpec.getSize(widthMeasureSpec) : width,
                MeasureSpec.EXACTLY == MeasureSpec.getMode(heightMeasureSpec)? MeasureSpec.getSize(heightMeasureSpec) : height);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getPaddingLeft(),height = getPaddingTop();
        for(int index = 0 ;index < getChildCount();index++) {
            //getChildAt(index).layout(width, height, width += getChildAt(i).getMeasuredWidth(),height +=  getChildAt(i).getMeasuredHeight());
            //getChildAt(index).layout(width, 0, width += getChildAt(index).getMeasuredWidth(),getChildAt(index).getMeasuredHeight());
            if (GONE != getChildAt(index).getVisibility()) {//跳过隐藏的
                measuredWidth = getChildAt(index).getMeasuredWidth();
                measuredHeight = getChildAt(index).getMeasuredHeight();
                //height = Math.max(height, measuredHeight);
                getChildAt(index).layout(((MarginLayoutParams)getChildAt(index).getLayoutParams()).leftMargin + width,
                        ((MarginLayoutParams)getChildAt(index).getLayoutParams()).topMargin + height,
                        width += measuredWidth -((MarginLayoutParams)getChildAt(index).getLayoutParams()).rightMargin,
                        measuredHeight + getPaddingBottom()- ((MarginLayoutParams)getChildAt(index).getLayoutParams()).bottomMargin);
                childCount++;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                scroller.startScroll((int)downX,(int)downY,(int)event.getX(),(int)event.getY(),0);//up 时自动滚动到
                break;
            case MotionEvent.ACTION_UP:
                upX = event.getX();
                upY = event.getY();
                invalidate();
                break;
        }
        return true;//super.onInterceptTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        //super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
