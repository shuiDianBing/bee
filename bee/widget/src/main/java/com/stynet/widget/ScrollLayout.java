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
 * 自定义ViewGroup实现弹性滑动效果 https://blog.csdn.net/y874961524/article/details/52752169
 * [android]ScrollTo、ScrollBy、Scroller,都给我滚！https://www.jianshu.com/p/8fe96aeff3ee
 * Android 浅谈scrollTo和scrollBy源码 https://blog.csdn.net/jsonChumpKlutz/article/details/78544385
 * android ScrollTo()和ScrollBy()解析送给初学者 https://www.jianshu.com/p/2e60448ac44c
 **/

public class ScrollLayout extends ViewGroup {
    private Scroller scroller;//弹性滑动对象，用于实现View的弹性滑动
    private VelocityTracker velocityTracker;// 速度轨迹追踪
    private int width,height;//子View的高度
    private float x,y;
    private float speed = 0x200;//滑动视图的速率
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

    @Override//自定义View学习笔记之详解onMeasure https://www.jianshu.com/p/1695988095a5
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        // 1.计算自定义的ViewGroup中所有子控件的大小
        // measureChildren(widthMeasureSpec, heightMeasureSpec);
        int width = getPaddingLeft(),height = getPaddingTop();
        for (int index = 0; index < getChildCount(); index++)//测量childView宽高
            if(GONE != getChildAt(index).getVisibility()) {
                measureChild(getChildAt(index), widthMeasureSpec, heightMeasureSpec);// 为ScrollerLayout中的每一个子控件测量大小
                int measuredWidth = getChildAt(index).getMeasuredWidth();
                int measuredHeight = getChildAt(index).getMeasuredHeight();
                height = Math.max(height, measuredHeight);
                width += measuredWidth;
                height += measuredHeight;
            }
        // 设置自定义的控件的大小，如果是MeasureSpec.EXACTLY则直接使用父ViewGroup传入的宽和高，否则设置为自己计算的宽和高
        setMeasuredDimension(MeasureSpec.EXACTLY == MeasureSpec.getMode(widthMeasureSpec) ? MeasureSpec.getSize(widthMeasureSpec) : width,
                MeasureSpec.EXACTLY == MeasureSpec.getMode(heightMeasureSpec)? MeasureSpec.getSize(heightMeasureSpec) : height);
    }
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(),attrs);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        width = getPaddingLeft();
        height = getPaddingTop();
        for(int index = 0 ;index < getChildCount();index++) {
            //getChildAt(index).layout(width, height, width += getChildAt(i).getMeasuredWidth(),height +=  getChildAt(i).getMeasuredHeight());
            //getChildAt(index).layout(width, 0, width += getChildAt(index).getMeasuredWidth(),getChildAt(index).getMeasuredHeight());
            if (GONE != getChildAt(index).getVisibility()) {//跳过隐藏的
                //height = Math.max(height, measuredHeight);
                //TODO 这里根据角度排列childView,此处插入根据角度算位置code
                getChildAt(index).layout(((MarginLayoutParams)getChildAt(index).getLayoutParams()).leftMargin + width,
                        ((MarginLayoutParams)getChildAt(index).getLayoutParams()).topMargin + height,
                        width += getChildAt(index).getMeasuredWidth() -((MarginLayoutParams)getChildAt(index).getLayoutParams()).rightMargin,
                        getChildAt(index).getMeasuredHeight() + getPaddingBottom()- ((MarginLayoutParams)getChildAt(index).getLayoutParams()).bottomMargin);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(!scroller.isFinished())
                    scroller.abortAnimation();//终止滑动
                break;
            case MotionEvent.ACTION_MOVE:
                float monevX = event.getX()- x;
                float monevY = event.getY()- y;
                //scrollTo(-(int)(event.getX()- x),-(int)(event.getY()- y));//每次从头开始不累计偏移量
                scrollBy(0< getScrollX()- monevX ? width > getScrollX()- monevX ? -(int)monevX : width :0,
                        0< getScrollY()-(int)(event.getY()- y)? -(int)(event.getY()- y):0);
                //方式1
                //scroller.startScroll(getScrollX(),getScrollY(),-(int)(event.getX()- x),-(int)(event.getY()- y),0x0);
                //invalidate();
                break;
            case MotionEvent.ACTION_UP:
                velocityTracker.addMovement(event);
//                velocityTracker.computeCurrentVelocity(0x200);
//                float xVelocity = velocityTracker.getXVelocity();//获取X方向手指滑动的速度，之前必须调用computeCurrentVelocity（）方法
//                scroller.startScroll(getScrollX(),0,getScrollX()+(int)xVelocity,0,0x200);//up 时自动滚动到
//                invalidate();
                break;
        }
        x = event.getX();
        y = event.getY();
        return true;//super.onInterceptTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
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
