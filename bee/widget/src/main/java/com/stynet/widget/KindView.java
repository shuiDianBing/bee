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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by shuiDianBing on 2018/5/7.
 * 绘制种类view
 * 深入理解Android之View的绘制流程 https://www.jianshu.com/p/060b5f68da79
 * Android View的绘制流程 https://www.jianshu.com/p/5a71014e7b1b
 * 源码解析 - View的绘制流程 https://www.jianshu.com/p/1075d7d521ec
 * Android自定义View（三、深入解析控件测量onMeasure） https://blog.csdn.net/xmxkf/article/details/51490283
 * 【Android开发—电商系列】（二）：仿淘宝商品属性标签页 https://blog.csdn.net/u010924834/article/details/50353955
 * 自定义View学习笔记之详解onMeasure https://www.jianshu.com/p/1695988095a5
 */
public class KindView extends View {
    private static final String TAG = "KindView";
    private CharSequence[] kinds,bans;
    private float[][]points;
    private int selectedIndex =-1;
    private float interval,lineWidth,radius,padding,textSize;
    private @ColorInt int defaultColor,textColor,selectedTextColor,selectedBackgroundColor,banColor;
    private ClickListener clickListener;

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
        defaultColor = array.getColor(R.styleable.kind_defaultColor, 0Xff000000);//默认颜色
        selectedBackgroundColor = array.getColor(R.styleable.kind_selectedBackgroundColor,0xffc77502);//选中背景颜色
        banColor = array.getColor(R.styleable.kind_banColor,0xff999999);//禁用颜色
        textSize = array.getDimensionPixelSize(R.styleable.kind_textSize,16);//字体大小
        textColor = array.getColor(R.styleable.kind_textColor,0xff000000);//字体默认颜色
        selectedTextColor = array.getColor(R.styleable.kind_selectedTextColor,0xffffffff);//字体选中颜色
        array.recycle();
    }
    private Paint[] initPaint(){
        Paint[] paints = new Paint[2];
        paints[0] = new Paint();
        paints[0].setStrokeCap(Paint.Cap.ROUND);//结束的画笔为圆心
        paints[0].setStrokeJoin(Paint.Join.ROUND);//连接处元
        paints[0].setAlpha(0xff);
        paints[0].setAntiAlias(true);
        paints[0].setStrokeMiter(1.0f);
        paints[0].setTextSize(textSize);
        paints[0].setStyle(Paint.Style.FILL);
        //textPaint.setStrokeWidth(textSize);//线宽
        //Android默认有四种字体样式：BOLD(加粗)、BOLD_ITALIC(加粗并倾斜)、ITALIC(倾斜)、NORMAL(正常)，我们也可以通过Typeface类来自定义个性化字体
        paints[0].setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
        paints[0].setTextSkewX(0);//(-0.25f);//设置文字倾斜，参数没有具体范围，官方推荐值为-0.25，值为负则右倾，为正则左倾，默认值为0
        paints[0].setTextScaleX(1.0f);//设置文字拉长
        paints[0].setTextAlign(Paint.Align.LEFT);//获取与设置文本对齐方式，取值为CENTER、LEFT、RIGHT，也就是文字绘制是左边对齐、右边还是局中的
        paints[1] = new Paint();
        paints[1].setStrokeWidth(lineWidth);//线宽
        paints[1].setStrokeCap(Paint.Cap.ROUND);//结束的画笔为圆心
        paints[1].setStrokeJoin(Paint.Join.ROUND);//连接处元
        paints[1].setAlpha(0xff);
        paints[1].setAntiAlias(true);
        paints[1].setStrokeMiter(1.0f);
        return paints;
    }
    /**
     * Android自定义View的测量过程详解 https://www.jianshu.com/p/1db39d6ee4be
     * Android自定义控件系列七：详解onMeasure()方法中如何测量一个控件尺寸(一) https://blog.csdn.net/cyp331203/article/details/45027641
     * Android 自定义View (一) https://blog.csdn.net/lmj623565791/article/details/24252901/
     * Android如何绘制视图，解释了为何onMeasure有时要调用多次 https://blog.csdn.net/jewleo/article/details/39547631
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = 0;
        int heightSize = 0;
        if(null != points &&(MeasureSpec.EXACTLY !=  MeasureSpec.getMode(widthMeasureSpec)|| MeasureSpec.EXACTLY != MeasureSpec.getMode(heightMeasureSpec))){
            float ordinate[] = new float[]{0.0f,0.0f};
            Paint[] paints = initPaint();
            Rect rect = new Rect();
            for (int index = 0; index < kinds.length; index++) {
                paints[0].getTextBounds(kinds[index].toString(), 0, kinds[index].length(), rect);//获得文字宽高
                points[index] = measure(rect,paints[0].getFontMetricsInt() ,ordinate, MeasureSpec.EXACTLY != MeasureSpec.getMode(widthMeasureSpec) ? MeasureSpec.getSize(widthMeasureSpec) : MeasureSpec.getSize(widthMeasureSpec));
                if (MeasureSpec.EXACTLY != MeasureSpec.getMode(widthMeasureSpec)) {
                    int width = (int) Math.ceil(points[index][2]);
                    widthSize = widthSize <  width ? width : widthSize;//获取x最大值
                }
                if (MeasureSpec.EXACTLY != MeasureSpec.getMode(heightMeasureSpec)) {
                    int height = (int) Math.ceil(points[index][3]);
                    heightSize = heightSize <  height ? height : heightSize;//获取y最大值
                }
                //Log.d("KindView","onMeasure:point["+index+"]=("+points[index][2]+","+points[index][3]+")");
            }
        }
        setMeasuredDimension(0== widthSize ? MeasureSpec.getSize(widthMeasureSpec): widthSize,0== heightSize ? MeasureSpec.getSize(heightMeasureSpec): heightSize);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(null== kinds)
            return;
        Paint[] paints = initPaint();
        Rect stringRect = new Rect();
        float ordinate[] = new float[]{0.0f,0.0f};
        for(int index = 0;index < kinds.length;index++){
            boolean isBan = false;
            if(null != bans)
                for(CharSequence ban : bans)
                    if(kinds[index].equals(ban)){
                        isBan = true;
                        break;
                    }
            //points[index] = startDraw(canvas,kinds[index], ordinate,index == selectedIndex,isBan);
            paints[0].getTextBounds(kinds[index].toString(),0,kinds[index].length(),stringRect);//获得文字宽高
            //points[index] = measure(stringRect,ordinate,canvas.getWidth());//onMeasure中调用
            paints[0].setColor(isBan ? banColor : index == selectedIndex ? selectedTextColor : textColor);
            paints[1].setColor(isBan ? banColor :  index == selectedIndex ? selectedBackgroundColor : defaultColor);
            paints[1].setStyle(index == selectedIndex ? Paint.Style.FILL : Paint.Style.STROKE);
            RectF frame = new RectF(points[index][0]+ lineWidth/2,points[index][1]+ lineWidth/2,points[index][2]- lineWidth /2,points[index][3]- lineWidth /2);
            canvas.drawRoundRect(frame,radius,radius,paints[1]);
            Paint.FontMetricsInt fontMetricsInt = paints[0].getFontMetricsInt();
            canvas.drawText(kinds[index],0,kinds[index].length(),points[index][0]+ lineWidth + padding,points[index][3]- lineWidth - padding +2*(fontMetricsInt.top - fontMetricsInt.ascent),paints[0]);
            //canvas.drawCircle(points[index][0]+(points[index][2]- points[index][0])/2,(points[index][3]-points[index][1])/2,(points[index][3]-points[index][1])/2-lineWidth -padding,paints[0]);
            //canvas.drawLine(points[index][0],points[index][1],points[index][2],points[index][3],paints[1]);
            //canvas.drawLine(points[index][2],points[index][1],points[index][0],points[index][3],paints[1]);
            //canvas.drawLine(points[index][0]+(points[index][2]- points[index][0])/2,points[index][1],points[index][0]+(points[index][2]- points[index][0])/2,points[index][3],paints[1]);
            //canvas.drawLine(points[index][0],points[index][1]+(points[index][3]- points[index][1])/2,points[index][2],points[index][1]+(points[index][3]- points[index][1])/2,paints[1]);
//            paints[1].setColor(0xff0000ff);
//            canvas.drawLine(points[index][0],40+points[index][3]+fontMetricsInt.bottom-Math.abs(fontMetricsInt.top),points[index][2],40+points[index][3]+fontMetricsInt.bottom-Math.abs(fontMetricsInt.top),paints[1]);
//            paints[1].setColor(0xff00ff00);
//            canvas.drawLine(points[index][0],40+points[index][3]+fontMetricsInt.bottom-Math.abs(fontMetricsInt.ascent),points[index][2],40+points[index][3]+fontMetricsInt.bottom-Math.abs(fontMetricsInt.ascent),paints[1]);
//            paints[1].setColor(0xffff0000);
//            canvas.drawLine(points[index][0],40+points[index][3]+fontMetricsInt.bottom-fontMetricsInt.descent,points[index][2],40+points[index][3]+fontMetricsInt.bottom-fontMetricsInt.descent,paints[1]);
//            paints[1].setColor(0xffff0000);
//            canvas.drawLine(points[index][0],40+points[index][3]+fontMetricsInt.bottom-fontMetricsInt.bottom,points[index][2],40+points[index][3]+fontMetricsInt.bottom-fontMetricsInt.bottom,paints[1]);
//            paints[1].setColor(0xff00ff00);
//            canvas.drawLine(points[index][0],40+points[index][3]+fontMetricsInt.bottom-fontMetricsInt.leading,points[index][2],40+points[index][3]+fontMetricsInt.bottom-fontMetricsInt.leading,paints[1]);
            ordinate[0] += frame.width()+ interval;
        }
    }
    /**
     * @param stringRect {@link Rect} 测量string宽高
     * @param ordinate 根据传递进来的坐标计算位置,然后记录xy坐标点
     * @param width
     */
    private float[] measure(Rect stringRect,Paint.FontMetricsInt fontMetricsInt,float[] ordinate,int width){
        if(width < ordinate[0]+ stringRect.width()+2* padding + lineWidth *2+ interval){//判断是否超出屏宽了
            ordinate[0] = 0;
            ordinate[1] += stringRect.height()+2* padding + lineWidth *2+ interval;//超出屏宽累加坐标y值
        }
        float[] location = new float[4];
        location[0] = ordinate[0];
        location[1] = ordinate[1];
        location[2] = ordinate[0]+ stringRect.width()+2*(padding + lineWidth + stringRect.left);
        location[3] = ordinate[1]+ stringRect.height()+2*(padding + lineWidth -(fontMetricsInt.top - fontMetricsInt.ascent));//添加字符top和ascent差值
        ordinate[0] += lineWidth *2+ padding *2+ stringRect.width()+ interval;
        return location;
    }
    /**
     * 效率问题:每次循环都创建画笔设置画笔属性,性能效率低不能充分利用相同属性画笔.留此方法经供参考,不建议使用
     * 优化方案:先初始化设置画笔属性然后循环使用{@link KindView#measure(Rect stringRect,float[] ordinate,int width)}
     *
     * Android 自定义View学习(三)——Paint 绘制文字属性 https://www.jianshu.com/p/1728b725b4a6
     * Android中关于字体宽高的获取 https://blog.csdn.net/u010661782/article/details/52805939
     * Android绘制文本之获取宽高笔记 https://blog.csdn.net/eyishion/article/details/51824520
     * Android Canvas 方法总结 https://www.jianshu.com/p/f69873371763
     * 一起看画布Android Canvas https://www.jianshu.com/p/afa06f716ca6
     * Android Canvas之Path操作 https://www.jianshu.com/p/9ad3aaae0c63
     * android canvas drawText()文字居中 https://blog.csdn.net/zly921112/article/details/50401976
     * Android自定义View五（绘制文本大小、多行多列居中）https://www.jianshu.com/p/2eb8ae713c1f
     *
     * @param canvas {@link Canvas}
     * @param content {@link CharSequence}
     * @param ordinate 根据传递进来的坐标计算位置,然后记录xy坐标点
     * @param selected 选中
     * @param isBan 不能被选择的
     */
    private float[] startDraw(Canvas canvas,CharSequence content,float[] ordinate,boolean selected,boolean isBan){
        Paint textPaint = new Paint();
        textPaint.setColor(isBan ? banColor : selected ? selectedTextColor : textColor);
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
        Paint borderPaint = new Paint();
        borderPaint.setStyle(selected ? Paint.Style.FILL : Paint.Style.STROKE);
        borderPaint.setColor(isBan ? banColor : selected ? selectedBackgroundColor : defaultColor);
        borderPaint.setStrokeWidth(lineWidth);//线宽
        borderPaint.setStrokeCap(Paint.Cap.ROUND);//结束的画笔为圆心
        borderPaint.setStrokeJoin(Paint.Join.ROUND);//连接处元
        borderPaint.setAlpha(0xff);
        borderPaint.setAntiAlias(true);
        borderPaint.setStrokeMiter(1.0f);
        if(canvas.getWidth()- lineWidth - padding < ordinate[0]+ stringRect.width()+2* padding + lineWidth /2*3+ interval){//判断是否超出屏宽了
            ordinate[0] = 0;
            ordinate[1] += stringRect.height()+2* padding + lineWidth /2*3+ interval;//超出屏宽累加坐标y值
        }
        float location[] = new float[4];
        location[0] = ordinate[0]+ lineWidth /2;
        location[1] = ordinate[1]+ lineWidth /2;
        location[2] = ordinate[0]+ stringRect.width()+2* padding + lineWidth /2*3;
        location[3] = ordinate[1]+ stringRect.height()+2* padding + lineWidth /2*3;
        RectF frame = new RectF(location[0],location[1],location[2],location[3]);
        canvas.drawRoundRect(frame,radius,radius,borderPaint);
        canvas.drawText(content,0,content.length(),ordinate[0]+ lineWidth + padding,ordinate[1]+ stringRect.height()+ lineWidth + padding - fontMetricsInt.bottom /2,textPaint);
        ordinate[0] += frame.width()+ interval;
        return location;
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
                for(int index = 0;index < points.length;index++) {
                    boolean isClick = false;
                    for(CharSequence ban : bans)
                        if(ban.equals(kinds[index])){
                            isClick = true;
                            break;
                        }
                        if(isClick)
                            continue;
                    if (points[index][0]<= x && points[index][1]<= y && points[index][2]>= x && points[index][3]>= y) {
                        //Log.d(TAG,"onTouchEvent:kinds[index]="+kinds[index]);
                        selectedIndex = selectedIndex == index ? -1 : index;
                        if (null != clickListener)
                            clickListener.onClick(-1 == selectedIndex ? null : kinds[selectedIndex]);
                        invalidate();//更新view
                        break;
                    }
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

    public void setPoints(float[][] points) {
        this.points = points;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public void setSelectedTextColor(int selectedTextColor) {
        this.selectedTextColor = selectedTextColor;
    }

    public void setSelectedBackgroundColor(int selectedBackgroundColor) {
        this.selectedBackgroundColor = selectedBackgroundColor;
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

    public void setBans(CharSequence[] bans) {
        this.bans = bans;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener{
        public void onClick(Object  key);
    }
}
