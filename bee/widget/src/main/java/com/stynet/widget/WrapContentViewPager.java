package com.stynet.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xx shuiDianBing, 2018/07/23-14:25:14:25.Refer to the website: nullptr
 * 解决Viewpager设置高度为wrap_content无效的方法 https://www.jianshu.com/p/f99052da26df
 * NestedScrollView嵌套ViewPager https://www.jianshu.com/p/28e4346a41cf
 * ViewPager特性设置width或height属性为match_parent或wrap_content时ViewPager中的View会自动填充满.设置margin或padding的话
 * 有时候看起来没有效果（其实ViewPager子View遮住了看不出来）其实是有效的只是相对屏幕宽高,要调整margin或padding在ViewPager的子View调节
 * 或者重写ViewPager实现
 **/

public class WrapContentViewPager extends ViewPager {
    private boolean slide;
    private int lastX = -1;
    private int lastY = -1;
    private List<Integer> heights = new ArrayList<>();
    /**
     * Constructor
     *
     * @param context the context
     */
    public WrapContentViewPager(Context context) {
        super(context);
    }

    /**
     * Constructor
     *
     * @param context the context
     * @param attrs the attribute set
     * @param attrs the attribute set
     */
    public WrapContentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WrapContentViewPager);
        slide = typedArray.getBoolean(R.styleable.WrapContentViewPager_slide,true);
    }

    /**
     * Android自定义View的测量过程详解 https://www.jianshu.com/p/1db39d6ee4be
     * UNSPECIFIED模式：本质就是不限制模式，父视图不对子View进行任何约束，View想要多大要多大，想要多长要多长，这个在我们写自定义View中的时候非常少见，一般都是系统内部在设置ListView或者是ScrollView的时候才会用到。
     * EXACTLY模式：该模式其实对应的场景就是match_parent或者是一个具体的数据(50dp或80px)，父视图为子View指定一个确切的大小，无论子View的值设置多大，都不能超出父视图的范围。
     * AT_MOST模式：这个模式对应的场景就是wrap_content，其内容就是父视图给子View设置一个最大尺寸，子View只要不超过这个尺寸即可。
     * 测量出来的height默认以最大的height设置{@link WrapContentViewPager}的height
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(MeasureSpec.EXACTLY != MeasureSpec.getMode(heightMeasureSpec)) {
            int height = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                if (h > height)
                    height = h;
                heights.add(h);
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return slide ? super.onTouchEvent(ev) : true;
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return slide ? super.onInterceptHoverEvent(event): true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return slide ? super.onInterceptTouchEvent(ev): false;
    }
    /**
     * 在切换tab的时候,重置viewPager的高度
     * @param position
     */
    public void resetHeight(int position) {
        if (heights.size() > position) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (layoutParams == null)
                layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heights.get(position));
            else
                layoutParams.height = heights.get(position);
            setLayoutParams(layoutParams);
        }
    }
    public boolean isScrool() {
        return slide;
    }

    public void setScrool(boolean scrool) {
        slide = scrool;
    }
}
