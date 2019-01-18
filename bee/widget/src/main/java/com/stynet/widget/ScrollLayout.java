package com.stynet.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.OverScroller;
import android.widget.Scroller;

/**
 * Created by xx shuiDianBing, 2018/09/28-17:13:17:13.Refer to the website: nullptr
 * 自定义ViewGroup实现水平滑动 https://blog.csdn.net/deng0zhaotai/article/details/21404589
 * Android view体系简析及自定义滑动ViewGroup的优化 https://www.jianshu.com/p/c1d04960cfa1
 * 自定义ViewGroup实现弹性滑动效果 https://blog.csdn.net/y874961524/article/details/52752169
 * [android]ScrollTo、ScrollBy、Scroller,都给我滚！https://www.jianshu.com/p/8fe96aeff3ee
 * Android 浅谈scrollTo和scrollBy源码 https://blog.csdn.net/jsonChumpKlutz/article/details/78544385
 * android ScrollTo()和ScrollBy()解析送给初学者 https://www.jianshu.com/p/2e60448ac44c
 * Android 自定义ScrollView 支持惯性滑动，惯性回弹效果。支持上拉加载更多 https://blog.csdn.net/xufeifandj/article/details/48415889
 * ScrollView实现以惯性滑动的形式滑动到任意位置/禁止惯性滑动/监听惯性滑动 https://www.jianshu.com/p/0c99fb893b35
 * ScrollView源码分析 https://www.jianshu.com/p/c3ed4253f87e
 * Android  View视图系统分析和Scroller和OverScroller分析 https://www.cnblogs.com/zsychanpin/p/6945371.html
 * 自定义控件的惯性滑动 https://www.jianshu.com/p/57ce979b23e8
 **/

public class ScrollLayout extends ViewGroup {
    private static final String TAG = ScrollLayout.class.getName();
    private OverScroller overScroller;//弹性滑动对象，用于实现View的弹性滑动
    private VelocityTracker velocityTracker;// 速度轨迹追踪
    private int width,height;
    private float x,y;
    private float speed = 0x200;//滑动视图的速率
    private int scrollPointerId =-1;
    private int mtouchslop;
    private int minFlingVelocity,maxFlingVelocity;
    private int overscrollDistance;
    private int overflingDistance;

    public ScrollLayout(Context context) {
        super(context,null);
    }

    public ScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScrollLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        overScroller = new OverScroller(context);
        setFocusable(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setWillNotDraw(false);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mtouchslop = configuration.getScaledTouchSlop();//被认为是滑动操作的最小距离
        minFlingVelocity = configuration.getScaledMinimumFlingVelocity();//最小加速度
        maxFlingVelocity = configuration.getScaledMaximumFlingVelocity();//最大加速度
        overscrollDistance = configuration.getScaledOverscrollDistance();//用手指拖动超过边缘的最大距离
        overflingDistance = configuration.getScaledOverflingDistance();//滑动超过边缘的最大距离
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
                        ((MarginLayoutParams)getChildAt(index).getLayoutParams()).topMargin + getPaddingTop(),
                        width += getChildAt(index).getMeasuredWidth() -((MarginLayoutParams)getChildAt(index).getLayoutParams()).rightMargin,
                        getChildAt(index).getMeasuredHeight() + getPaddingBottom()- ((MarginLayoutParams)getChildAt(index).getLayoutParams()).bottomMargin);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(null == velocityTracker)
            velocityTracker = VelocityTracker.obtain();
        velocityTracker.addMovement(event);
        boolean eventAddedToVelocityTracker = false;
        final MotionEvent cloneEvent = MotionEvent.obtain(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                scrollPointerId = event.getPointerId(0);
                velocityTracker.clear();
                velocityTracker.addMovement(event);
                if(!overScroller.isFinished())
                    overScroller.abortAnimation();//终止滑动
                break;
            case MotionEvent.ACTION_MOVE:
                float monevX = event.getX()- x;
                float monevY = event.getY()- y;
                //scrollTo(-(int)(event.getX()- x),-(int)(event.getY()- y));//每次从头开始不累计偏移量
                //手势拖动 << 方式1
                scrollBy(0< getScrollX()- monevX ? width - getMeasuredWidth()+ getPaddingRight()> getScrollX()- monevX ? -(int)monevX :0:0,0);
                        //0< getScrollY()- monevY ? height > getScrollY()- monevY ? -(int)monevY : getPaddingBottom()- getHeight() :0);
                //手势拖动 << 方式2
//                overScroller.startScroll(getScrollX(),getScrollY(),0< getScrollX()- monevX ? width - getMeasuredWidth()+getPaddingRight()> getScrollX()- monevX ? -(int)monevX :0:0,
//                        -(int)(event.getY()- y),0x0);
//                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL://惯性滑动
                velocityTracker.addMovement(cloneEvent);
                velocityTracker.computeCurrentVelocity(0x400,maxFlingVelocity);
                float xVelocity = -velocityTracker.getXVelocity(scrollPointerId);//获取X方向手指滑动的速度，之前必须调用computeCurrentVelocity（）方法
                float yVelocity = velocityTracker.getXVelocity(scrollPointerId);//获取X方向手指滑动的速度，之前必须调用computeCurrentVelocity（）方法
                Log.i(TAG, "onTouchEvent<<速度取值<<xVelocity=" + xVelocity+"<<yVelocity="+yVelocity);
//                //TODO 惯性滑动
//                int inertiaX = (int)(event.getX()- x);
//                int inertiaY = getScrollY()-(int)yVelocity;
//                overScroller.startScroll(getScrollX(),0,0< inertiaX ? width > inertiaX ? inertiaX : width - getMeasuredWidth()+ getPaddingRight() : 0,0,0x200);//up 时自动滚动到
//                //invalidate();//postInvalidateOnAnimation();
//                velocityTracker.recycle();
//                velocityTracker = null;
                overScroller.fling(0, 0, Math.abs(xVelocity)> minFlingVelocity ?(int)Math.max(-maxFlingVelocity,Math.min(xVelocity, maxFlingVelocity)):0,
                        0, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
                velocityTracker.clear();
            break;
            case MotionEvent.ACTION_POINTER_UP:
                int pointerIndexLeave = event.getActionIndex();
                if (scrollPointerId == event.getPointerId(pointerIndexLeave))// 离开屏幕的正是目前的有效手指，此处需要重新调整，并且需要重置VelocityTracker
                    scrollPointerId = event.getPointerId(pointerIndexLeave == 0 ? 1 : 0);
                break;
        }
        // 调整触摸位置，防止出现跳动
        x = event.getX();
        y = event.getY();
        cloneEvent.recycle();
        return true;//super.onInterceptTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (overScroller.computeScrollOffset()) {
            scrollTo(overScroller.getCurrX(), overScroller.getCurrY());
            postInvalidate();
        }
    }
    /**
     * onInterceptTouchEvent()用来询问是否要拦截处理。 onTouchEvent()是用来进行处理。
     * <p/>
     * 例如：parentLayout----childLayout----childView 事件的分发流程：
     * parentLayout::onInterceptTouchEvent()---false?--->
     * childLayout::onInterceptTouchEvent()---false?--->
     * childView::onTouchEvent()---false?--->
     * childLayout::onTouchEvent()---false?---> parentLayout::onTouchEvent()
     * <p/>
     * <p/>
     * <p/>
     * 如果onInterceptTouchEvent()返回false，且分发的子View的onTouchEvent()中返回true，
     * 那么onInterceptTouchEvent()将收到所有的后续事件。
     * <p/>
     * 如果onInterceptTouchEvent()返回true，原本的target将收到ACTION_CANCEL，该事件
     * 将会发送给我们自己的onTouchEvent()。
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
