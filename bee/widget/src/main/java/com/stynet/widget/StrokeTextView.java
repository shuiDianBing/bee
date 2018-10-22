package com.stynet.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by xx shuiDianBing, 2018/09/10-18:17:18:17.Refer to the website: nullptr
 * 字体描边阴影效果颜色
 * Android TextView字体描边 https://www.jianshu.com/p/b5a082604075
 * android之View坐标系（view获取自身坐标的方法和点击事件中坐标的获取）https://blog.csdn.net/jason0539/article/details/42743531
 **/

public class StrokeTextView extends TextView {
    private @ColorInt int strokeColor;
    private float strokeWidth;

    public StrokeTextView(Context context) {
        super(context);
        initPaint();
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context,attrs);
        initAttribute(getContext().obtainStyledAttributes(attrs, R.styleable.strokeTextView));
        initPaint();
    }

    public StrokeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
        initAttribute(getContext().obtainStyledAttributes(attrs, R.styleable.strokeTextView,defStyle,0));
        initPaint();
    }
    private void initAttribute(@Nullable TypedArray typedArray){
        strokeColor = typedArray.getColor(R.styleable.strokeTextView_strokeColor,0x0);//描边颜色
        strokeWidth = typedArray.getDimension(R.styleable.strokeTextView_strokeWidth,0x0);//描边宽度
        typedArray.recycle();
    }
    private void initPaint() {
        TextPaint paint = getPaint();
        paint.setStrokeWidth(strokeWidth);//描边宽度
        paint.setStyle(Paint.Style.STROKE);
        setTextColor(strokeColor);//(Color.parseColor("#ffffff"));//描边颜色
        setGravity(getGravity());
    }
}
