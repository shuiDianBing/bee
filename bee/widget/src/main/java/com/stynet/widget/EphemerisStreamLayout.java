package com.stynet.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.stynet.widget.helper.UnitConvert;

/**
 * Created by xx shuiDianBing, 2018/09/06-18:14:18:14.Refer to the website: nullptr
 * 星历流                         B
 *                              *
 *                           *  *
 *                       *      *
 *              c     *         *   a
 *                *             *
 *           *                  *
 *       *                      *
 *  A  **************************   C
 *                  b
 *   sin A = a/c    cos A = b/c     tan A = a/b     cot A = b/a     sec A = c/b     csc A = c/a
 *   sin * sin A + cos * cos A = 1      tan A = sin A / cos A
 *   Android-自定义ViewGroup（一） 水平滑动 https://www.jianshu.com/p/a2ff778eade2
 **/

public class EphemerisStreamLayout extends ScrollLayout {
    private int[] confines;
    private float elementRadius,angle,particleRadius,shadowRadius;
    private int element,elementColor,particleColor,knownColor,exploreColor,unKnownColor,shadowX,shadowY,shadowColor;
    public EphemerisStreamLayout(Context context) {
        super(context);
    }

    public EphemerisStreamLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EphemerisStreamLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setWillNotDraw(false);
        initAttribute(getContext().obtainStyledAttributes(attrs, R.styleable.ephemerisStreamLayout, defStyleAttr,0));
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EphemerisStreamLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setWillNotDraw(false);
        initAttribute(getContext().obtainStyledAttributes(attrs, R.styleable.ephemerisStreamLayout, defStyleAttr,defStyleRes));
    }
    private void initAttribute(@Nullable TypedArray typedArray){
        element = typedArray.getInt(R.styleable.ephemerisStreamLayout_element,1);//元素数量
        elementRadius = typedArray.getFloat(R.styleable.ephemerisStreamLayout_elementRadius, 0.0f);//元素半径
        elementColor = typedArray.getColor(R.styleable.ephemerisStreamLayout_elementColor, 0x00000000);//元素颜色
        angle = typedArray.getColor(R.styleable.ephemerisStreamLayout_angle, 0x0);//流向角度
        particleRadius = typedArray.getFloat(R.styleable.ephemerisStreamLayout_particleRadius, 0.0f);//粒子半径(元素外圆)
        particleColor = typedArray.getColor(R.styleable.ephemerisStreamLayout_particleColor,0x00000000);//粒子颜色(元素外圆颜色)
        knownColor = typedArray.getColor(R.styleable.ephemerisStreamLayout_knownColor,0x00000000);//已知领域流颜色
        exploreColor = typedArray.getColor(R.styleable.ephemerisStreamLayout_exploreColor,0x00000000);//探索域流颜色
        unKnownColor = typedArray.getColor(R.styleable.ephemerisStreamLayout_unKnownColor,0x00000000);//未知域流颜色
        shadowX = typedArray.getDimensionPixelOffset(R.styleable.ephemerisStreamLayout_shadowX, UnitConvert.dip2px(getContext(), 1F));//阴影x轴偏移
        shadowY = typedArray.getDimensionPixelOffset(R.styleable.ephemerisStreamLayout_shadowY,UnitConvert.dip2px(getContext(), 1F));//阴影y轴偏移
        shadowRadius = typedArray.getFloat(R.styleable.ephemerisStreamLayout_shadowRadius,UnitConvert.dip2px(getContext(), 7F));//阴影半径
        shadowColor = typedArray.getColor(R.styleable.ephemerisStreamLayout_shadowColor, Color.GRAY);//阴影颜色
        typedArray.recycle();
        confines = new int[element];
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed,l,t,r,b);
        int width = 0,height = 0;
        for(int i = 0 ;i < getChildCount();i++)
            //getChildAt(i).layout(width, height, width += getChildAt(i).getMeasuredWidth(),height +=  getChildAt(i).getMeasuredHeight());
            getChildAt(i).layout(width, 0, width += getChildAt(i).getMeasuredWidth(),getChildAt(i).getMeasuredHeight());
    }

    @Override
    public Parcelable onSaveInstanceState(){
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("element",element);
        bundle.putFloat("elementRadius", elementRadius);
        bundle.putInt("elementColor", elementColor);
        bundle.putIntArray("confines", confines);
        bundle.putFloat("particleRadius", particleRadius);
        bundle.putInt("particleColor", particleColor);
        bundle.putInt("knownColor", knownColor);
        bundle.putInt("exploreColor", exploreColor);
        bundle.putInt("unKnownColor", unKnownColor);
        bundle.putInt("shadowX", shadowX);
        bundle.putInt("shadowY", shadowY);
        bundle.putFloat("shadowRadius", shadowRadius);
        bundle.putInt("shadowColor", shadowColor);
        return bundle;
    }
    @Override
    public void onRestoreInstanceState(Parcelable parcelable){
        if(parcelable instanceof Bundle){
            element = ((Bundle)parcelable).getInt("element");
            elementRadius = ((Bundle)parcelable).getInt("elementRadius");
            elementColor = ((Bundle)parcelable).getInt("elementColor");
            confines = ((Bundle)parcelable).getIntArray("confines");
            particleRadius = ((Bundle)parcelable).getInt("particleRadius");
            particleColor = ((Bundle)parcelable).getInt("particleColor");
            knownColor = ((Bundle)parcelable).getInt("knownColor");
            exploreColor = ((Bundle)parcelable).getInt("exploreColor");
            unKnownColor = ((Bundle)parcelable).getInt("unKnownColor");
            shadowX = ((Bundle)parcelable).getInt("shadowX");
            shadowY = ((Bundle)parcelable).getInt("shadowY");
            shadowRadius = ((Bundle)parcelable).getInt("shadowRadius");
            shadowColor = ((Bundle)parcelable).getInt("shadowColor");
            onRestoreInstanceState(((Bundle)parcelable).getParcelable("instanceState"));
        }
        super.onRestoreInstanceState(parcelable);
    }
}
