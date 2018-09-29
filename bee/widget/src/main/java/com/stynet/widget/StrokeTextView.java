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
 * Android TextView字体描边 https://www.jianshu.com/p/b5a082604075
 * android之View坐标系（view获取自身坐标的方法和点击事件中坐标的获取）https://blog.csdn.net/jason0539/article/details/42743531
 **/

public class StrokeTextView extends TextView {
    private @ColorInt
    int strokeColor;
    private TextView outlineTextView = null;

    public StrokeTextView(Context context) {
        super(context);
        outlineTextView = new TextView(context);
        initPaint();
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context,attrs);
        outlineTextView = new TextView(context,attrs);
        initAttribute(getContext().obtainStyledAttributes(attrs, R.styleable.strokeTextView));
        initPaint();
    }

    public StrokeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
        outlineTextView = new TextView(context,attrs,defStyle);
        initAttribute(getContext().obtainStyledAttributes(attrs, R.styleable.strokeTextView,defStyle,0));
        initPaint();
    }
    private void initAttribute(@Nullable TypedArray typedArray){
        strokeColor = typedArray.getColor(R.styleable.strokeTextView_strokeColor,0x00000000);//元素数量
        typedArray.recycle();
    }
    private void initPaint() {
        TextPaint paint = outlineTextView.getPaint();
        paint.setStrokeWidth(2);//描边宽度
        paint.setStyle(Paint.Style.STROKE);
        outlineTextView.setTextColor(strokeColor);//(Color.parseColor("#ffffff"));//描边颜色
        outlineTextView.setGravity(getGravity());
    }

    @Override

    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        outlineTextView.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
//设置轮廓文字
        CharSequence outlineText = outlineTextView.getText();
        if(outlineText==null|| !outlineText.equals(this.getText())) {
            outlineTextView.setText(getText());
            postInvalidate();
        }
        outlineTextView.measure(widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed,int left,int top,int right,int bottom) {
        super.onLayout(changed,left,top,right,bottom);
        outlineTextView.layout(left,top,right,bottom);
    }

    @Override

    protected void onDraw(Canvas canvas) {
        outlineTextView.draw(canvas);
        super.onDraw(canvas);
    }
}
