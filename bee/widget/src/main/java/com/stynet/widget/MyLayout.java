package com.stynet.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by xx shuiDianBing, 2018/09/29-10:38:10:38.Refer to the website: nullptr
 * Android 自定义View 实现手势监听，左右滑动，上下滑动 https://blog.csdn.net/oNingNing1/article/details/52607114
 **/
@Deprecated
public class MyLayout extends RelativeLayout implements GestureDetector.OnGestureListener{
    private
    int verticalMinDistance = 20;

    private
    int minVelocity = 0;

    private GestureDetector gestureDetector;

    public MyLayout(Context context) {
        super(context);
        gestureDetector = new GestureDetector(context, this);
    }

    public MyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetector(context, this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d("pingan", "onDown");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d("pingan", "onScroll" + distanceX + "distanceY:" + distanceY);

        if (distanceX < -verticalMinDistance) {
            Log.d("pingan", "向右手势");

        } else if (distanceX > verticalMinDistance){

            Log.d("pingan", "向左手势");

        }else if (distanceY <- verticalMinDistance) {
            Log.d("pingan", "向下手势");

        } else if (distanceY > verticalMinDistance ) {

            Log.d("pingan", "向上手势");

        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("pingan", "onFling");

        if (e1.getX()
                - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
            Log.d("pingan", "向左手势");

        } else if ((e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity)) {

            Log.d("pingan", "向右手势");

        } else if (e1.getY()
                - e2.getY() > verticalMinDistance && Math.abs(velocityY) > minVelocity) {
            Log.d("pingan", "向上手势");

        } else if ((e2.getY() - e1.getY() > verticalMinDistance && Math.abs(velocityY) > minVelocity)) {

            Log.d("pingan", "向下手势");

        }

        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed,l,t,r,b);
    }
}
