package com.stynet.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
/**
 * 气泡布局
 * Created by shuiDianBing on 2018-06-11
 */
public class BubbleLayout extends FrameLayout {
    private Look look;
    public BubbleLayout(@NonNull Context context) {
        this(context,null);
    }

    public BubbleLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BubbleLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttribute(getContext().obtainStyledAttributes(attrs, R.styleable.FiltrateView, defStyleAttr,0));
    }

    public BubbleLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getAttribute(getContext().obtainStyledAttributes(attrs, R.styleable.FiltrateView, defStyleAttr,defStyleRes));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void getAttribute(@Nullable TypedArray typedArray){
        widthOffset = typedArray.getDimension(R.styleable.FiltrateView_widthOffset, widthOffset);
        minFontSize = typedArray.getInteger(R.styleable.FiltrateView_minFontSize, minFontSize);
        maxFontSize = typedArray.getInteger(R.styleable.FiltrateView_maxBezierHeight, maxFontSize);
        hintFontSize = typedArray.getInteger(R.styleable.FiltrateView_hintFontSize, hintFontSize);
        maxBezierHeight = typedArray.getDimension(R.styleable.FiltrateView_maxBezierHeight, maxBezierHeight);
        maxBezierWidth = typedArray.getDimension(R.styleable.FiltrateView_maxBezierWidth, maxBezierWidth);
        maxBezierLines = typedArray.getInteger(R.styleable.FiltrateView_maxBezierLines, maxBezierLines);
        hintOffset = typedArray.getDimension(R.styleable.FiltrateView_hintOffset, hintOffset);
        fontColor = typedArray.getColor(R.styleable.FiltrateView_fontColor, fontColor);
        hintFontColor = typedArray.getColor(R.styleable.FiltrateView_hintFontColor, hintFontColor);
        typedArray.recycle();
    }
    /**
     * 箭头指向
     */
    public enum Look{
        /**
         * 左上右下
         */
        LEFT(0),TOP(1),RIGHT(2),BOTTOM(3);
        int value;
        Look(int value){this.value = value;}
        public static Look getType(int value){
            switch (value){
                case 0:
                    return  Look.LEFT;
                case 1:
                    return  Look.TOP;
                case 2:
                    return  Look.RIGHT;
                case 3:
                    return  Look.BOTTOM;
                    default:
                        return  Look.LEFT;
            }
        }
    }
}
