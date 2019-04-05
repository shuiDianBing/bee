package com.stynet.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.stynet.widget.helper.UnitConvert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 气泡布局
 * Created by shuiDianBing on 2018-06-11
 */
public class BubbleLayout extends FrameLayout {
    private Look look;
    private Paint paint;
    private Path path;
    private Region region = new Region();
    private ClickEdgeListener clickEdgeListener;
    private int bubblePadding;
    private int width, height;
    private int left, top, right, bottom;
    private int lookPosition, lookWidth, lookLength;
    private int shadowColor, shadowRadius, shadowX, shadowY;
    private int bubbleRadius, bubbleColor;
    /**
     * 箭头指向
     */
    public enum Look{
        /**
         * 左上右下
         */
        LEFT(1),TOP(2),RIGHT(3),BOTTOM(4);
        int value;
        Look(int value){this.value = value;}
        public static Look getType(int value){
            switch (value){
                case 1:
                    return LEFT;
                case 2:
                    return TOP;
                case 3:
                    return RIGHT;
                case 4:
                    return BOTTOM;
                default:
                    return BOTTOM;
            }
        }
    }
    public interface ClickEdgeListener{
        void onEdge();
    }
    public BubbleLayout(@NonNull Context context) {
        this(context,null);
    }

    public BubbleLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BubbleLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE, null);//close关闭硬件加速
        /**
         * setWillNotDraw（）;方法的使用 https://blog.csdn.net/jijiaxin1989/article/details/42237401
         * ViewGroup默认情况下，出于性能考虑，会被设置成WILL_NOT_DROW，这样，ondraw就不会被执行了。
         * 如果我们想重写一个viewgroup的ondraw方法，有两种方法：
         * 1，构造函数中，给viewgroup设置一个颜色。
         * 2，构造函数中，调用setWillNotDraw（false），去掉其WILL_NOT_DRAW flag。
         * 在viewgroup初始化的时候，它调用了一个私有方法：initViewGroup，它里面会有一句setFlags（WILLL_NOT_DRAW,DRAW_MASK）;相当于调用了setWillNotDraw（true），
         * 所以说，对于ViewGroup，他就认为是透明的了，如果我们想要重写onDraw，就要调用setWillNotDraw（false）
         */
        setWillNotDraw(false);//去掉其WILL_NOT_DRAW flag
        initAttribute(getContext().obtainStyledAttributes(attrs, R.styleable.bubbleLayout, defStyleAttr,0));
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setStyle(Paint.Style.FILL);
        path = new Path();
        initPadding();
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BubbleLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttribute(getContext().obtainStyledAttributes(attrs, R.styleable.bubbleLayout, defStyleAttr,defStyleRes));
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setStyle(Paint.Style.FILL);
        path = new Path();
        initPadding();
    }
    private void initAttribute(@Nullable TypedArray typedArray){
        look = Look.getType(typedArray.getInt(R.styleable.bubbleLayout_lookAt, Look.BOTTOM.value));
        lookPosition = typedArray.getDimensionPixelOffset(R.styleable.bubbleLayout_lookPosition, 0);//箭头指向
        lookWidth = typedArray.getDimensionPixelOffset(R.styleable.bubbleLayout_lookWidth, UnitConvert.dip2px(getContext(), 17F));//箭头宽度
        lookLength = typedArray.getDimensionPixelOffset(R.styleable.bubbleLayout_lookLength, UnitConvert.dip2px(getContext(), 17F));//箭头长度
        shadowRadius = typedArray.getDimensionPixelOffset(R.styleable.bubbleLayout_shadowRadius,UnitConvert.dip2px(getContext(), 3.3F));//阴影圆弧半径
        shadowX = typedArray.getDimensionPixelOffset(R.styleable.bubbleLayout_shadowX, UnitConvert.dip2px(getContext(), 1F));//阴影x轴偏移
        shadowY = typedArray.getDimensionPixelOffset(R.styleable.bubbleLayout_shadowY,UnitConvert.dip2px(getContext(), 1F));//阴影y轴偏移
        bubbleRadius = typedArray.getDimensionPixelOffset(R.styleable.bubbleLayout_bubbleRadius,UnitConvert.dip2px(getContext(), 7F));//气泡圆弧
        bubblePadding = typedArray.getDimensionPixelOffset(R.styleable.bubbleLayout_bubblePadding, UnitConvert.dip2px(getContext(), 8));//气泡内边距
        shadowColor = typedArray.getColor(R.styleable.bubbleLayout_shadowColor, Color.GRAY);//阴影颜色
        bubbleColor = typedArray.getColor(R.styleable.bubbleLayout_bubbleColor, Color.WHITE);//气泡颜色
        typedArray.recycle();
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        initPaint();
    }

    @Override
    public void invalidate() {
        initPaint();
        super.invalidate();
    }

    @Override
    public void postInvalidate() {
        initPaint();
        super.postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path,paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            RectF r = new RectF();
            path.computeBounds(r, true);
            region.setPath(path, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
            if (!region.contains((int) event.getX(), (int) event.getY())&& null != clickEdgeListener)
                clickEdgeListener.onEdge();
        }
        return super.onTouchEvent(event);
    }
    public void initPadding() {
        int p = bubblePadding <<1;
        switch (look) {
            case BOTTOM:
                setPadding(p, p, p, lookLength + p);
                break;
            case TOP:
                setPadding(p, p + lookLength, p, p);
                break;
            case LEFT:
                setPadding(p + lookLength, p, p, p);
                break;
            case RIGHT:
                setPadding(p, p, p + lookLength, p);
                break;
        }
    }
    private void initPaint() {
        paint.setPathEffect(new CornerPathEffect(bubbleRadius));//设置角度圆润
        paint.setShadowLayer(shadowRadius, shadowX, shadowY, shadowColor);//阴影

        left = bubblePadding + (look == Look.LEFT ? lookLength : 0);
        top = bubblePadding + (look == Look.TOP ? lookLength : 0);
        right = width - bubblePadding - (look == Look.RIGHT ? lookLength : 0);
        bottom = height - bubblePadding - (look == Look.BOTTOM ? lookLength : 0);
        paint.setColor(bubbleColor);

        path.reset();

        int topOffset = (topOffset = lookPosition) + lookLength > bottom ? bottom - lookWidth : topOffset;
        topOffset = topOffset > bubblePadding ? topOffset : bubblePadding;
        int leftOffset = (leftOffset = lookPosition) + lookLength > right ? right - lookWidth : leftOffset;
        leftOffset = leftOffset > bubblePadding ? leftOffset : bubblePadding;
        switch (look) {
            case LEFT:
                path.moveTo(left, topOffset);
                path.rLineTo(-lookLength, lookWidth >>1);
                path.rLineTo(lookLength, lookWidth >>1);
                path.lineTo(left, bottom);
                path.lineTo(right, bottom);
                path.lineTo(right, top);
                path.lineTo(left, top);
                break;
            case TOP:
                path.moveTo(leftOffset, top);
                path.rLineTo(lookWidth >>1, -lookLength);
                path.rLineTo(lookWidth >>1, lookLength);
                path.lineTo(right, top);
                path.lineTo(right, bottom);
                path.lineTo(left, bottom);
                path.lineTo(left, top);
                break;
            case RIGHT:
                path.moveTo(right, topOffset);
                path.rLineTo(lookLength, lookWidth >>1);
                path.rLineTo(-lookLength, lookWidth >>1);
                path.lineTo(right, bottom);
                path.lineTo(left, bottom);
                path.lineTo(left, top);
                path.lineTo(right, top);
                break;
            case BOTTOM:
                path.moveTo(leftOffset, bottom);
                path.rLineTo(lookWidth >>1, lookLength);
                path.rLineTo(lookWidth >>1, -lookLength);
                path.lineTo(right, bottom);
                path.lineTo(right, top);
                path.lineTo(left, top);
                path.lineTo(left, bottom);
                break;
        }
        path.close();
    }
    @Override
    public Parcelable onSaveInstanceState(){
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("lookPosition",lookPosition);
        bundle.putInt("lookWidth", lookWidth);
        bundle.putInt("lookLength", lookLength);
        bundle.putInt("shadowColor", shadowColor);
        bundle.putInt("shadowRadius", shadowRadius);
        bundle.putInt("shadowX", shadowX);
        bundle.putInt("shadowY", shadowY);
        bundle.putInt("bubbleRadius", bubbleRadius);
        bundle.putInt("width", width);
        bundle.putInt("height", height);
        bundle.putInt("left", left);
        bundle.putInt("top", top);
        bundle.putInt("right", right);
        bundle.putInt("bottom",bottom);
        return bundle;
    }
    @Override
    public void onRestoreInstanceState(Parcelable parcelable){
        if(parcelable instanceof Bundle){
            lookPosition = ((Bundle)parcelable).getInt("lookPosition");
            lookWidth = ((Bundle)parcelable).getInt("lookWidth");
            lookLength = ((Bundle)parcelable).getInt("lookLength");
            shadowColor = ((Bundle)parcelable).getInt("shadowColor");
            shadowRadius = ((Bundle)parcelable).getInt("shadowRadius");
            shadowX = ((Bundle)parcelable).getInt("shadowX");
            shadowY = ((Bundle)parcelable).getInt("shadowY");
            bubbleRadius = ((Bundle)parcelable).getInt("bubbleRadius");
            width = ((Bundle)parcelable).getInt("width");
            height = ((Bundle)parcelable).getInt("height");
            left = ((Bundle)parcelable).getInt("left");
            top = ((Bundle)parcelable).getInt("top");
            right = ((Bundle)parcelable).getInt("right");
            bottom = ((Bundle)parcelable).getInt("bottom");
            onRestoreInstanceState(((Bundle)parcelable).getParcelable("instanceState"));
        }
        super.onRestoreInstanceState(parcelable);
    }
    public Look getLook() {
        return look;
    }

    public void setLook(Look look) {
        this.look = look;
        initPadding();
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getBubblePadding() {
        return bubblePadding;
    }

    public void setBubblePadding(int bubblePadding) {
        this.bubblePadding = bubblePadding;
    }

    public int getLookWidth() {
        return width;
    }

    public void setLookWidth(int width) {
        this.lookWidth = width;
    }

    public int getLookHeight() {
        return height;
    }

    public void setLookHeight(int height) {
        this.height = height;
    }

    public int getLookPosition() {
        return lookPosition;
    }

    public void setLookPosition(int lookPosition) {
        this.lookPosition = lookPosition;
    }

    public int getLookLength() {
        return lookLength;
    }

    public void setLookLength(int lookLength) {
        this.lookLength = lookLength;
        initPadding();
    }

    public int getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
    }

    public int getShadowRadius() {
        return shadowRadius;
    }

    public void setShadowRadius(int shadowRadius) {
        this.shadowRadius = shadowRadius;
    }

    public int getShadowX() {
        return shadowX;
    }

    public void setShadowX(int shadowX) {
        this.shadowX = shadowX;
    }

    public int getShadowY() {
        return shadowY;
    }

    public void setShadowY(int shadowY) {
        this.shadowY = shadowY;
    }

    public int getBubbleRadius() {
        return bubbleRadius;
    }

    public void setBubbleRadius(int bubbleRadius) {
        this.bubbleRadius = bubbleRadius;
    }

    public int getBubbleColor() {
        return bubbleColor;
    }

    public void setBubbleColor(int bubbleColor) {
        this.bubbleColor = bubbleColor;
    }

    public ClickEdgeListener getClickEdgeListener() {
        return clickEdgeListener;
    }

    public void setClickEdgeListener(ClickEdgeListener clickEdgeListener) {
        this.clickEdgeListener = clickEdgeListener;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
