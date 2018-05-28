package com.stynet.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by shuiDianBing on 2018/5/7.
 * 绘制种类view
 * 深入理解Android之View的绘制流程 https://www.jianshu.com/p/060b5f68da79
 * Android自定义View（三、深入解析控件测量onMeasure） https://blog.csdn.net/xmxkf/article/details/51490283
 * 自定义View学习笔记之详解onMeasure https://www.jianshu.com/p/1695988095a5
 */
public class KindView extends View {
    private static final String TAG = "KindView";
    private CharSequence[] kinds;
    private float[][]points;
    private int selectedIndex =-1;
    private float interval,lineWidth,radius,padding,textSize;
    private @ColorInt int defaultColor,textColor,selectedColor,banColor;
    private KindClickListener clickListener;
    public KindView(Context context) {
        super(context);
    }

    public KindView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttribute(attrs);
    }

    public KindView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttribute(attrs);
    }

    public KindView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getAttribute(attrs);
    }
    private void getAttribute(AttributeSet attrs){
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.kind);
        interval = array.getDimension(R.styleable.kind_interval,0);//间距
        lineWidth = array.getDimension(R.styleable.kind_lineWidth,0);//线宽
        radius = array.getDimension(R.styleable.kind_radius,0);//圆角半径
        padding = array.getDimension(R.styleable.kind_padding,0);//内边距
        defaultColor = array.getColor(R.styleable.kind_defaultColor, 0Xff0000);//默认颜色
        selectedColor = array.getColor(R.styleable.kind_selectedColor,0xffE3360E);//选中颜色
        banColor = array.getColor(R.styleable.kind_banColor,0xff999999);//禁用颜色
        textSize = array.getDimensionPixelSize(R.styleable.kind_textSize,16);//字体大小
        textColor = array.getColor(R.styleable.kind_textColor,0xff000000);//字体颜色
        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(null== kinds)
            return;
        float ordinate[] = new float[]{0.0f,0.0f};
        for(int index = 0;index < kinds.length;index++)
            points[index] = startDraw(canvas,kinds[index], ordinate,index == selectedIndex);
    }

    /**
     * Android 自定义View学习(三)——Paint 绘制文字属性 https://www.jianshu.com/p/1728b725b4a6
     * Android中关于字体宽高的获取 https://blog.csdn.net/u010661782/article/details/52805939
     * Android绘制文本之获取宽高笔记 https://blog.csdn.net/eyishion/article/details/51824520
     * Android Canvas 方法总结 https://www.jianshu.com/p/f69873371763
     * 一起看画布Android Canvas https://www.jianshu.com/p/afa06f716ca6
     * Android Canvas之Path操作 https://www.jianshu.com/p/9ad3aaae0c63
     * android canvas drawText()文字居中 https://blog.csdn.net/zly921112/article/details/50401976
     * @param canvas {@link Canvas}
     * @param content {@link CharSequence}
     */
    private float[] startDraw(Canvas canvas,CharSequence content,float[] ordinate,boolean selected){
        Paint textPaint = new Paint();
        textPaint.setColor(selected ? selectedColor : textColor);
        textPaint.setStrokeCap(Paint.Cap.ROUND);//结束的画笔为圆心
        textPaint.setStrokeJoin(Paint.Join.ROUND);//连接处元
        textPaint.setAlpha(0xff);
        textPaint.setAntiAlias(true);
        textPaint.setStrokeMiter(1.0f);
        textPaint.setTextSize(textSize);
        textPaint.setStyle(Paint.Style.FILL);
        //textPaint.setStrokeWidth(textSize);//线宽
        //Android默认有四种字体样式：BOLD(加粗)、BOLD_ITALIC(加粗并倾斜)、ITALIC(倾斜)、NORMAL(正常)，我们也可以通过Typeface类来自定义个性化字体
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
        textPaint.setTextSkewX(0);//(-0.25f);//设置文字倾斜，参数没有具体范围，官方推荐值为-0.25，值为负则右倾，为正则左倾，默认值为0
        textPaint.setTextScaleX(1.0f);//设置文字拉长
        textPaint.setTextAlign(Paint.Align.LEFT);//获取与设置文本对齐方式，取值为CENTER、LEFT、RIGHT，也就是文字绘制是左边对齐、右边还是局中的
        Rect stringRect = new Rect();
        //textPaint.measureText(content,0,content.length());//粗略获取文本的宽度，和下面的getTextBounds比较类似，返回浮点数。
        //textPaint.getTextWidths(content,0,content.length(),new float[]{});//精确计算文字宽度
        textPaint.getTextBounds(content.toString(),0,content.length(),stringRect);//获得文字宽高
        Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        Paint linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(selected ? selectedColor : defaultColor);
        linePaint.setStrokeWidth(lineWidth);//线宽
        linePaint.setStrokeCap(Paint.Cap.ROUND);//结束的画笔为圆心
        linePaint.setStrokeJoin(Paint.Join.ROUND);//连接处元
        linePaint.setAlpha(0xff);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeMiter(1.0f);
        if(canvas.getWidth()- lineWidth - padding < ordinate[0]+ stringRect.width()+2* padding + lineWidth /2*3+ interval){//判断是否超出屏宽了
            ordinate[0] = 0;
            ordinate[1] += stringRect.height()+2* padding + lineWidth /2*3+ interval;//超出屏宽累加坐标y值
        }
        canvas.drawText(content,0,content.length(),ordinate[0]+ lineWidth + padding,ordinate[1]+ stringRect.height()+ lineWidth + padding - fontMetricsInt.bottom /2,textPaint);
        float location[] = new float[4];
        location[0] = ordinate[0]+ lineWidth /2;
        location[1] = ordinate[1]+ lineWidth /2;
        location[2] = ordinate[0]+ stringRect.width()+2* padding + lineWidth /2*3;
        location[3] = ordinate[1]+ stringRect.height()+2* padding + lineWidth /2*3;
        RectF frame = new RectF(location[0],location[1],location[2],location[3]);
        canvas.drawRoundRect(frame,radius,radius,linePaint);
        ordinate[0] += frame.width()+ interval;
        return location;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
    /**
     * 自定义VIEW中区域点击事件 https://blog.csdn.net/chopinyychopinyy/article/details/54754935
     * Android自定义View中的常用方法(距离、位置、点击事件) https://www.jianshu.com/p/336f90f0026f?nomobile=yes
     * android之View坐标系（view获取自身坐标的方法和点击事件中坐标的获取）https://blog.csdn.net/jason0539/article/details/42743531
     * 安卓通过设置View的OnTouchListener超简单实现点击动画效果 https://www.jianshu.com/p/a6b9b909d4de
     * 安卓自定义View进阶-MotionEvent详解 http://www.gcssloop.com/customview/motionevent
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:break;
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                for(int index = 0;index < points.length;index++)
                    if(points[index][0]<= x && points[index][1]<= y && points[index][2]>= x && points[index][3]>=y){
                        //Log.d(TAG,"onTouchEvent:kinds[index]="+kinds[index]);
                        selectedIndex = index;
                        if(null != clickListener)
                            clickListener.onClick(kinds[selectedIndex]);
                        invalidate();//更新view
                        break;
                    }
                break;
        }
        //Android onTouchEvent的MotionEvent.ACTION_UP不响应 https://blog.csdn.net/wangdong20/article/details/50339621
        //原来在onTouchEvent的Motion.ACTION_DOWN分支如果返回的是false，那么onTouch事件将不会往下面传递下去，如果返回true，那么onTouch事件将会传递下去，从而可以响应Motion.ACTION_UP分支
        return super.onTouchEvent(event);
    }

    public void setKinds(CharSequence[] kinds) {
        this.kinds = kinds;
        if(null != kinds)
            points = new float[kinds.length][4];
    }

    public void setInterval(float interval) {
        this.interval = interval;
    }

    public void setPadding(float padding) {
        this.padding = padding;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }

    public void setBanColor(int banColor) {
        this.banColor = banColor;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setClickListener(KindClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface KindClickListener{
        public void onClick(Object  key);
    }
}
