package com.stynet.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * Created by shuiDianBing on 2018/5/24.
 * Android自定义控件 -- 带边框的TextView https://blog.csdn.net/zhyh1986/article/details/48415353
 * Android自定义TextView边框颜色（动态改变边框颜色以及字体颜色）https://blog.csdn.net/lplj717/article/details/52776524
 * Android绘图基础--Canvas和Drawable https://www.jianshu.com/p/ebca5649a51d
 * Android 图像绘制之 Drawable https://www.jianshu.com/p/4cd87e3c43d6
 * 缺点不能调节某个边款角度,只能4个一样角度
 */
public class BorderTextView extends TextView {
    private static final int DEFAULTsTROKErADIUS = 4;
    private float lineWidth,strokeRadius,leftTop,rightTop,rightBottom,leftBottom;
    public BorderTextView(Context context) {
        super(context);
    }

    public BorderTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getAttribute(attrs);
    }

    public BorderTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttribute(attrs);
    }

    public BorderTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getAttribute(attrs);
    }
    private void getAttribute(AttributeSet attrs){
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.border);
        lineWidth = array.getDimension(R.styleable.border_lineWidth,DEFAULTsTROKErADIUS);
        strokeRadius = array.getDimension(R.styleable.border_strokeRadius,DEFAULTsTROKErADIUS);
        leftTop = array.getDimension(R.styleable.border_leftTop,DEFAULTsTROKErADIUS);
        rightTop = array.getDimension(R.styleable.border_rightTop,DEFAULTsTROKErADIUS);
        rightBottom = array.getDimension(R.styleable.border_rightBottom,DEFAULTsTROKErADIUS);
        leftBottom = array.getDimension(R.styleable.border_leftBottom,DEFAULTsTROKErADIUS);
        array.recycle();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if (0== getText().toString().length())
            return;
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(lineWidth);
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(getTextColors().getDefaultColor());//(getCurrentTextColor());
        canvas.drawRoundRect(new RectF(lineWidth, lineWidth, getMeasuredWidth()- lineWidth, getMeasuredHeight()- lineWidth),strokeRadius, strokeRadius, borderPaint);//(leftTop,rightTop,rightBottom,leftBottom, strokeRadius, strokeRadius, borderPaint);
        super.onDraw(canvas);
    }
}
