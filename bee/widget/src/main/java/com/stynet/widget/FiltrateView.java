package com.stynet.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

/**
 * Created by shuiDianBing on 2018/5/7.
 * 不设置监听没有提示
 */
public class FiltrateView extends View {
    private static final String TAG = "FiltrateView";
    private float widthOffset = 32.0f;// 向右偏移多少画字符， default 32
    private int minFontSize = 0x18,maxFontSize = 0x30;//字体大小
    private int hintFontSize = 0x40;//提示字体大小
    private float hintOffset = 32.0f;// 提示字符的额外偏移
    private float maxBezierHeight = 150.0f;//贝塞尔曲线控制的高度
    private float maxBezierWidth = 240.0f;// 贝塞尔曲线单侧宽度
    private int maxBezierLines = 0x20;//贝塞尔曲线单侧模拟线量
    private int fontColor = 0xff00ff00;// 列表字符颜色
    private int hintFontColor = 0xff0000ff;//提示字符颜色
    private String[] constChar = {"#","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","*"};
    private ClickListener clickListener;
    private int chooseIndex = -1;
    private PointF pointF;
    private PointF[] bezier1,bezier2;
    PointF lastFucusPostion;
    private Scroller scroller;
    private boolean animating,hideAnimation;
    private float animationOffset;
    private int alpha = 0xff;
    private Handler hideWaitingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    hideAnimation = true;
                    animating = false;
                    invalidate();
                    break;
            }
        }
    };
    public FiltrateView(Context context) {
        super(context);
    }

    public FiltrateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getAttribute(getContext().obtainStyledAttributes(attrs, R.styleable.filtrateView));
    }

    public FiltrateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttribute(getContext().obtainStyledAttributes(attrs, R.styleable.filtrateView,defStyleAttr,0));
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FiltrateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getAttribute(getContext().obtainStyledAttributes(attrs, R.styleable.filtrateView,defStyleAttr,defStyleRes));
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(scroller.computeScrollOffset()){
            if(animating)
                animationOffset = scroller.getCurrX();
            else if(hideAnimation)
                alpha = 0xff- scroller.getCurrX();
            invalidate();
        }else if(scroller.isFinished())
            if(hideAnimation){
                hideAnimation = false;
                chooseIndex = -1;
                pointF.x = pointF.y = -10000;
            }else if(animating)
                hideWaitingHandler.sendEmptyMessage(0);
    }
    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setConstChar(String[] constChar) {
        this.constChar = constChar;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int c = (int)(event.getY()/ getHeight()* constChar.length);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(getWidth() > widthOffset && event.getX()< getWidth()- widthOffset)
                    return false;
                hideWaitingHandler.removeMessages(0);
                scroller.abortAnimation();
                animating = hideAnimation = false;
                alpha = 0xff;
                pointF.x = event.getX();
                pointF.y = event.getY();
                if(null!= clickListener && chooseIndex != c && 0< c && c < constChar.length)
                    clickListener.onClick(constChar[chooseIndex = c]);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                pointF.x = event.getX();
                pointF.y = event.getY();
                invalidate();
                if(null!= clickListener && chooseIndex != c && 0<= c && c < constChar.length)
                    clickListener.onClick(constChar[chooseIndex = c]);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                pointF.x = event.getX();
                pointF.y = event.getY();
                scroller.startScroll(0,0,(int)maxBezierHeight,0,2000);
                animating = true;
                postInvalidate();
                break;
        }
        return true;//super.dispatchTouchEvent(event);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(0== alpha)
            return;
        Paint paint = new Paint();
        paint.reset();
        int saveCount = 0;
        if(hideAnimation){
            saveCount = canvas.save();
            canvas.saveLayerAlpha(0,0,getWidth(),getHeight(),alpha,Canvas.ALL_SAVE_FLAG);
        }
        int workHeight = 0;
        for(int index = 0; index < constChar.length; index++) {
            paint.setColor(fontColor);
            paint.setAntiAlias(true);
            float yPos = workHeight + getHeight()/(float)constChar.length /2;
            //float adjustX = adjustXPos( yPos, i == mChooseIndex );
            // 根据当前字母y的位置计算得到字体大小
            paint.setTextSize((maxFontSize - minFontSize)* Math.abs(ajustXPosAnimation(index, yPos)) / maxBezierHeight + minFontSize);
            workHeight += getHeight()/(float)constChar.length;
            drawTextInCenter(canvas, paint, constChar[index], getWidth() - widthOffset + ajustXPosAnimation(index, yPos), yPos);
            if (index == chooseIndex) {// 绘制的字母和当前触摸到的一致, 绘制红色被选中字母
                paint.setColor(hintFontColor);
                paint.setFakeBoldText(true);
                paint.setTextSize(hintFontSize);
                float pos;
                yPos = pointF.y;
                if (animating || hideAnimation) {
                    pos = lastFucusPostion.x;
                    yPos = lastFucusPostion.y;
                } else {
                    lastFucusPostion.y = yPos;
                    lastFucusPostion.x = pos = getWidth() - widthOffset + ajustXPosAnimation(index, lastFucusPostion.y) - hintOffset;
                }
                drawTextInCenter(canvas, paint, constChar[index], pos, yPos);
            }
            paint.reset();
        }
        if(hideAnimation)
            canvas.restoreToCount(saveCount);
    }
    /**
     * @param canvas 画板
     * @param string 被绘制的字母
     * @param centerX 字母的中心x方向位置
     * @param centerY 字母的中心y方向位置
     */
    private void drawTextInCenter(Canvas canvas,Paint paint,String string,float centerX,float centerY){
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float drawY = centerY + paint.getFontSpacing()/2- fontMetrics.descent;
        if(drawY <- fontMetrics.ascent - fontMetrics.descent)
            drawY = -fontMetrics.ascent - fontMetrics.descent;
        if(getHeight()< drawY)
            drawY = getHeight();
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(string,centerX,drawY,paint);
    }
    /**
     * x 方向的向左偏移量
     * @param index	当前字母的索引
     * @param yPos y方向的初始位置
     * @return
     */
    private float ajustXPosAnimation(int index,float yPos){
        float[] lastOffset = new float[constChar.length];
        if(animating || hideAnimation){
            // 正在动画中或在做隐藏动画
            float offset = lastOffset[index];
            if(0.0f!= offset){
                offset += animationOffset;
                if(0< offset)
                    offset = 0;
            }
            return offset;
        }else{
            float offset = adjustXpos(yPos);//根据当前字母y方向位置, 计算水平方向偏移量
            // 字母绘制时向左偏移量 进行修正, offset需要是<=0的值
            if(0.0f != offset && getWidth()-60 - widthOffset < pointF.x)
                offset += pointF.x - getWidth()+60+ widthOffset;
            if(0< offset)
                offset = 0;
            return lastOffset[index] = offset;
        }
    }
    /**
     * x 方向的向左偏移量
     * @param yPos y方向的初始位置
     * @return
     */
    private float adjustXpos(float yPos){
        float dis = yPos - pointF.y;// 字母y方向位置和触摸时y值坐标的差值, 距离越小, 得到的水平方向偏差越大
        if(-maxBezierWidth < dis && dis < maxBezierWidth) {// 在2个贝赛尔曲线宽度范围以内 (一个贝赛尔曲线宽度是指一个山峰的一边)
            if (maxBezierWidth /4< dis) {// 第一段 曲线
                for (int index = maxBezierLines -1; index >0; index--) {// 从下到上, 逐个计算
                    if (-bezier1[index].y == dis)//落在点上
                        return bezier1[index].x;
                    if (-bezier1[index].y < dis && -bezier1[index -1].y > dis)// 如果距离dis落在两个贝塞尔曲线模拟点之间, 通过三角函数计算得到当前dis对应的x方向偏移量
                        return (dis + bezier1[index].y)*(bezier1[index -1].x - bezier1[index].x)/(-bezier1[index -1].y + bezier1[index].y)+ bezier1[index].x;
                    }
                return bezier1[0].x;
            } else if (-maxBezierWidth /4> dis) {// 第三段 曲线, 和第一段曲线对称
                for (int index = 0; index < maxBezierLines -1; index++) {// 从上到下
                    if (bezier1[index].y == dis)// 落在点上
                        return bezier1[index].x;
                    if (bezier1[index].y < dis && bezier1[index +1].y > dis)// 如果距离dis落在两个贝塞尔曲线模拟点之间, 通过三角函数计算得到当前dis对应的x方向偏移量
                        return (dis - bezier1[index].y)*(bezier1[index +1].x - bezier1[index].x)/(bezier1[index +1].y - bezier1[index].y)+ bezier1[index].x;
                }
                return bezier1[maxBezierLines -1].x;
            }
            // 第二段 峰顶曲线
            for (int index = 0; index < maxBezierLines -1; index++) {
                if (dis == bezier2[index].y)
                    return bezier2[index].x;
                if (dis > bezier2[index].y && dis < bezier2[index +1].y)// 如果距离dis落在两个贝塞尔曲线模拟点之间, 通过三角函数计算得到当前dis对应的x方向偏移量
                    return (dis - bezier2[index].y)*(bezier2[index +1].x - bezier2[index].x)/(bezier2[index +1].y - bezier2[index].y) + bezier2[index].x;
            }
            return bezier2[maxBezierLines -1].x;
        }
        return 0.0f;
    }
    /**
     * 获取xml属性设置
     * @param typedArray {@link TypedArray}
     */
    private void getAttribute(TypedArray typedArray){
        widthOffset = typedArray.getDimension(R.styleable.filtrateView_widthOffset, widthOffset);
        minFontSize = typedArray.getInteger(R.styleable.filtrateView_minFontSize, minFontSize);
        maxFontSize = typedArray.getInteger(R.styleable.filtrateView_maxBezierHeight, maxFontSize);
        hintFontSize = typedArray.getInteger(R.styleable.filtrateView_hintFontSize, hintFontSize);
        maxBezierHeight = typedArray.getDimension(R.styleable.filtrateView_maxBezierHeight, maxBezierHeight);
        maxBezierWidth = typedArray.getDimension(R.styleable.filtrateView_maxBezierWidth, maxBezierWidth);
        maxBezierLines = typedArray.getInteger(R.styleable.filtrateView_maxBezierLines, maxBezierLines);
        hintOffset = typedArray.getDimension(R.styleable.filtrateView_hintOffset, hintOffset);
        fontColor = typedArray.getColor(R.styleable.filtrateView_fontColor, fontColor);
        hintFontColor = typedArray.getColor(R.styleable.filtrateView_hintFontColor, hintFontColor);
        typedArray.recycle();
        scroller = new Scroller(getContext());
        pointF = new PointF(0,-10* maxBezierWidth);
        bezier1 = new PointF[maxBezierLines];
        bezier2 = new PointF[maxBezierLines];
        lastFucusPostion = new PointF();
        calculateBezierPoints();
    }
    /**
     * 计算出所有贝塞尔曲线上的点
     * 个数为 maxBezierLines * 2 = 64
     * http://www.see-source.com/androidwidget/detail.html?wid=536
     */
    private void calculateBezierPoints(){
        PointF start = new PointF();//开始点
        PointF end = new PointF();//结束点
        PointF control = new PointF();//控制点
        //计算第一段红色部分,贝塞尔曲线的点
        //开始点
        start.x = 0.0f;
        start.y =- maxBezierWidth;
        // 控制点
        control.x = 0.0f;
        control.y =- maxBezierWidth /2;
        // 结束点
        end.x =- maxBezierHeight /2;
        end.y =- maxBezierWidth /4;
        bezier1[0] = new PointF();
        bezier1[maxBezierLines -1] = new PointF();
        bezier1[0].set(start);
        bezier1[maxBezierLines -1].set(end);
        for(int index = 1;index < maxBezierLines -1;index++){
            bezier1[index] = new PointF();
            bezier1[index].x = calculateBezier(start.x,end.x,control.x,index /(float)maxBezierLines);
            bezier1[index].y = calculateBezier(start.y,end.y,control.y,index /(float)maxBezierLines);
        }
        // 计算第二段蓝色部分 贝赛尔曲线的点
        start.y =- maxBezierWidth /4;
        start.x =- maxBezierHeight /2;
        control.y = 0.0f;
        control.x =- maxBezierHeight;
        end.y = maxBezierWidth /4;
        end.x =- maxBezierHeight /2;
        bezier2[0] = new PointF();
        bezier2[maxBezierLines -1] = new PointF();
        bezier2[0].set(start);
        bezier2[maxBezierLines -1].set(end);
        for(int index = 1;index < maxBezierLines -1;index++){
            bezier2[index] = new PointF();
            bezier2[index].x = calculateBezier(start.x,end.x,control.x,index /(float)maxBezierLines);
            bezier2[index].y = calculateBezier(start.y,end.y,control.y,index /(float)maxBezierLines);
        }
    }
    /**
     * 贝塞尔曲线核心算法
     * @param start
     * @param end
     * @param control
     * @param val
     * @return
     * 公式及动图, 维基百科: https://en.wikipedia.org/wiki/B%C3%A9zier_curve
     * 中文可参考此网站: http://blog.csdn.net/likendsl/article/details/7852658
     * 贝塞尔曲线原理(简单阐述) https://www.cnblogs.com/hnfxs/p/3148483.html
     * 德卡斯特里奥算法——找到Bezier曲线上的一个点 https://blog.csdn.net/venshine/article/details/51750906
     */
    private float calculateBezier(float start, float end, float control, float val) {
        float s = 1- val;
        return start * s * s + 2 * control * s * val + end * val * val;
    }
    public interface ClickListener {
        public void onClick(String s);
    }
}
