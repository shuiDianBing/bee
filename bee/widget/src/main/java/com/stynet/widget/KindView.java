package com.stynet.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by shuiDianBing on 2018/5/7.
 * 绘制种类view
 * 深入理解Android之View的绘制流程 https://www.jianshu.com/p/060b5f68da79
 * Android自定义View（三、深入解析控件测量onMeasure） https://blog.csdn.net/xmxkf/article/details/51490283
 * 自定义View学习笔记之详解onMeasure https://www.jianshu.com/p/1695988095a5
 */
public class KindView extends View {
    private CharSequence[] kinds;
    private float interval,lineWidth,radius,padding,textSize;
    private @ColorInt int defaultColor,selectedColor,banColor;
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
        interval = array.getDimension(R.styleable.kind_interval,4);
        lineWidth = array.getDimension(R.styleable.kind_lineWidth,4);
        radius = array.getDimension(R.styleable.kind_radius,4);
        padding = array.getDimension(R.styleable.kind_padding,4);
        defaultColor = array.getColor(R.styleable.kind_defaultColor, 0Xff0000);
        selectedColor = array.getColor(R.styleable.kind_selectedColor,0xffE3360E);
        banColor = array.getColor(R.styleable.kind_banColor,0xff999999);
        textSize = array.getDimensionPixelSize(R.styleable.kind_textSize,16);
        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(null== kinds)
            return;
        float ordinate[] = new float[]{0.0f,0.0f};
        for(int index = 0;index < kinds.length;index++)
            startDraw(canvas, kinds[index], ordinate);
        super.onDraw(canvas);
    }

    /**
     * Android 自定义View学习(三)——Paint 绘制文字属性 https://www.jianshu.com/p/1728b725b4a6
     * Android中关于字体宽高的获取 https://blog.csdn.net/u010661782/article/details/52805939
     * Android绘制文本之获取宽高笔记 https://blog.csdn.net/eyishion/article/details/51824520
     * Android Canvas 方法总结 https://www.jianshu.com/p/f69873371763
     * 一起看画布Android Canvas https://www.jianshu.com/p/afa06f716ca6
     * Android Canvas之Path操作 https://www.jianshu.com/p/9ad3aaae0c63
     * @param canvas {@link Canvas}
     * @param content {@link CharSequence}
     */
    private void startDraw(Canvas canvas,CharSequence content,float[] ordinate){
        Paint textPaint = new Paint();
        textPaint.setColor(defaultColor);
        textPaint.setStrokeCap(Paint.Cap.ROUND);//结束的画笔为圆心
        textPaint.setStrokeJoin(Paint.Join.ROUND);//连接处元
        textPaint.setAlpha(0xff);
        textPaint.setAntiAlias(true);
        textPaint.setStrokeMiter(1.0f);
        textPaint.setTextSize(textSize);
        textPaint.setStyle(Paint.Style.FILL);
        //textPaint.setStrokeWidth(textSize);//线宽
        //获取与设置字体类型。Android默认有四种字体样式：BOLD(加粗)、BOLD_ITALIC(加粗并倾斜)、ITALIC(倾斜)、NORMAL(正常)，我们也可以通过Typeface类来自定义个性化字体
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
        textPaint.setTextSkewX(-0.25f);//设置文字倾斜，参数没有具体范围，官方推荐值为-0.25，值为负则右倾，为正则左倾，默认值为0
        textPaint.setTextAlign(Paint.Align.LEFT);//获取与设置文本对齐方式，取值为CENTER、LEFT、RIGHT，也就是文字绘制是左边对齐、右边还是局中的
        Rect stringRect = new Rect();
        textPaint.getTextBounds(content.toString(),0,content.length(),stringRect);//获得文字宽高
        canvas.drawText(content,0,content.length(),ordinate[0]+ lineWidth,32+ lineWidth,textPaint);
        Paint linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(defaultColor);
        linePaint.setStrokeWidth(lineWidth);//线宽
        linePaint.setStrokeCap(Paint.Cap.ROUND);//结束的画笔为圆心
        linePaint.setStrokeJoin(Paint.Join.ROUND);//连接处元
        linePaint.setAlpha(0xff);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeMiter(1.0f);
        RectF frame = new RectF(lineWidth,lineWidth,stringRect.width()+ lineWidth,stringRect.height()+ lineWidth);
        canvas.drawRoundRect(frame,radius,radius,linePaint);
        ordinate[0] = frame.width()+ interval;
        ordinate[1] = frame.height()+ interval;
    }

    public void setKinds(CharSequence[] kinds) {
        this.kinds = kinds;
        //invalidate();
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
}
