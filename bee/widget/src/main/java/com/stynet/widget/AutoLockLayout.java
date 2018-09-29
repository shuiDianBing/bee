package com.stynet.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.Scroller;

/**
 * Created by xx shuiDianBing, 2018/09/28-19:19:19:19.Refer to the website: nullptr
 * 类似于可滑动的ViewPager
 * ??? 暂时只能水平滑动,竖向滑动用空添加
 * ??? 隐藏掉的view还在,后面用空在来实现
 *
 * Android-自定义ViewGroup（一） 水平滑动 https://www.jianshu.com/p/a2ff778eade2
 * 自定义ViewGroup实现垂直滚动 https://www.cnblogs.com/oversea201405/p/3752038.html
 **/

public class AutoLockLayout extends ScrollLayout {
    private Scroller scroller;//弹性滑动对象，用于实现View的弹性滑动
    private VelocityTracker velocityTracker;//速度追踪
    private int childCount;//子View数量
    private int childIndex;//子View索引
    private int measuredHeight;//子View的高度
    private int measuredWidth;//子View的宽度
    private float lastX,lastY;//当前显示的坐标点
    private float lastXintercept, lastYintercept;//阻断的坐标点

    public AutoLockLayout(Context context) {
        super(context);
        scroller = new Scroller(getContext());
        velocityTracker = VelocityTracker.obtain();
    }

    public AutoLockLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(getContext());
        velocityTracker = VelocityTracker.obtain();
    }

    public AutoLockLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new Scroller(getContext());
        velocityTracker = VelocityTracker.obtain();
    }

    public AutoLockLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        scroller = new Scroller(getContext());
        velocityTracker = VelocityTracker.obtain();
    }

    private void initAttribute(@Nullable TypedArray typedArray){ }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        // 获得它的父容器为它设置的测量模式和大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        // 1.计算自定义的ViewGroup中所有子控件的大小
        // measureChildren(widthMeasureSpec, heightMeasureSpec);
        int height = 0,width = 0;
        for (int index = 0; index < getChildCount(); index++)//统计childView宽高
            if(GONE != getChildAt(index).getVisibility()) {
                measureChild(getChildAt(index), widthMeasureSpec, heightMeasureSpec);// 为ScrollerLayout中的每一个子控件测量大小
                measuredHeight = getChildAt(index).getMeasuredHeight();
                measuredWidth = getChildAt(index).getMeasuredWidth();
                height = Math.max(height, measuredHeight);
                width += measuredWidth;
                height += measuredHeight;
            }
        // 设置自定义的控件MyViewGroup的大小，如果是MeasureSpec.EXACTLY则直接使用父ViewGroup传入的宽和高，否则设置为自己计算的宽和高
        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width, heightMode == MeasureSpec.EXACTLY ? heightSize : height);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = 0,height = 0;
        for(int index = 0 ;index < getChildCount();index++) {
            //getChildAt(index).layout(width, height, width += getChildAt(i).getMeasuredWidth(),height +=  getChildAt(i).getMeasuredHeight());
            //getChildAt(index).layout(width, 0, width += getChildAt(index).getMeasuredWidth(),getChildAt(index).getMeasuredHeight());
            if (GONE != getChildAt(index).getVisibility()) {//跳过隐藏的
                measuredHeight = getMeasuredHeight();
                measuredWidth = getMeasuredWidth();
                height = Math.max(height, measuredHeight);
                getChildAt(index).layout(width, 0, width += measuredWidth, height);
                childCount++;
            }
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //如果动画还没有结束，再次点击时结束上次动画，即开启这次新的ACTION_DOWN的动画
                if(!scroller.isFinished())
                    scroller.abortAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                scrollBy(-(int)(event.getX()- lastX),0);
                break;
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();//View的左边缘 - View内容的左边缘 位置的像素点
                velocityTracker.computeCurrentVelocity(0x200);
                float xVelocity = velocityTracker.getXVelocity();//获取X方向手指滑动的速度，之前必须调用computeCurrentVelocity（）方法
                if(0xff < Math.abs(xVelocity))//当滑动速度>256Px/S时
                    childIndex = 0< xVelocity ? childIndex -1: childIndex +1;
                else
                    childIndex = (scrollX + (measuredWidth >>1))/ measuredWidth;
                childIndex = Math.max(0,Math.min(childIndex,childCount -1));//限定childIndex在0到childCount之间
                scroller.startScroll(getScrollX(),0,childIndex * measuredWidth - scrollX,0,0x200);//up 时自动滚动到
                invalidate();
                break;
        }
        lastX = event.getX();
        lastY = event.getY();
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
                if(scroller.isFinished())
                    scroller.abortAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(ev.getX() - lastXintercept) > Math.abs(ev.getY()- lastYintercept))
                    return true;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        lastXintercept = lastX = ev.getX();
        lastYintercept = lastY = ev.getY();
        return super.onInterceptTouchEvent(ev);
    }
}
