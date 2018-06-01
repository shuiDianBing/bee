package com.stynet.shuidianbing.opticalcharacterrecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.IOException;

/**
 * <p>QRCode Camera preview, include QRCode recognition.</p>
 * Created by shuiDianBing on 2017/5/10.
 */
public class CameraPreview extends FrameLayout implements SurfaceHolder.Callback {
    private CameraManager cameraManager;
    private CameraScanAnalysis cameraScanAnalysis;
    private SurfaceView surfaceView;
    private float[] point = {0.0f,0.0f,0.0f,0.0f};

    public CameraPreview(Context context) {
        this(context, null);
    }
    public CameraPreview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        cameraManager = new CameraManager();
        cameraScanAnalysis = new CameraScanAnalysis();
    }
    /**
     * @param bitmap 扫描图片
     * @return result 解析的结果
     */
    public String scanPhoto(Bitmap bitmap) {
        return cameraScanAnalysis.scanPhoto(bitmap);
    }

    /**
     * Set Scan results callback.
     *
     * @param callback {@link ScanCallback}.
     */
    public void setScanCallback(ScanCallback callback) {
        cameraScanAnalysis.setScanCallback(callback);
    }
    /**
     * 开关手电筒
     */
    public void openLighting(){
        cameraManager.openLighting(getContext());
    }
    /**
     * 切换摄像头
     */
    public void changeCamera(){
        try {
            cameraManager.changeCamera(surfaceView.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void alterCamera(){
        cameraManager.alterCamera(surfaceView.getHolder());
    }
    /**
     * Camera start preview.
     */
    public boolean start() {
        try {
            cameraManager.openDriver(getContext(),CameraManager.BACK);
        } catch (Exception e) {
            return false;
        }
        cameraScanAnalysis.onStart();
        if (null== surfaceView) {
            surfaceView = new SurfaceView(getContext());
            addView(surfaceView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            SurfaceHolder holder = surfaceView.getHolder();
            holder.addCallback(this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        startCameraPreview(surfaceView.getHolder());
        return true;
    }

    /**
     * Camera stop preview.
     */
    public void stop() {
        removeCallbacks(mAutoFocusTask);
        cameraScanAnalysis.onStop();
        cameraManager.stopPreview();
        cameraManager.closeDriver();
    }

    private void startCameraPreview(SurfaceHolder holder) {
        try {
            cameraManager.startPreview(holder, cameraScanAnalysis);
            cameraManager.autoFocus(mFocusCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) { }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (null== holder.getSurface())
            return;
        cameraManager.stopPreview();
        startCameraPreview(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { }

    private Camera.AutoFocusCallback mFocusCallback = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            postDelayed(mAutoFocusTask, 1000);
        }
    };

    private Runnable mAutoFocusTask = new Runnable() {
        public void run() {
            cameraManager.autoFocus(mFocusCallback);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        stop();
        super.onDetachedFromWindow();
    }
    /**
     * Android自定义View（三、深入解析控件测量onMeasure）https://blog.csdn.net/xmxkf/article/details/51490283
     * android 自定义View之View的测量（onMeasure()方法）https://blog.csdn.net/gongzhiyao3739124/article/details/52540888
     * Android 自定义View学习(十)——View的测量方法onMeasure()学习 https://www.jianshu.com/p/12666343ead6
     * Android自定义控件系列七：详解onMeasure()方法中如何测量一个控件尺寸(一) https://blog.csdn.net/cyp331203/article/details/45027641
     * Android自定义View的测量过程详解 https://www.jianshu.com/p/1db39d6ee4be
     * Android中获取View宽高方法 https://www.jianshu.com/p/f56c92e29dea
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int actionMasked = event.getActionMasked();
        switch (actionMasked){
            case MotionEvent.ACTION_DOWN://记录多点的坐标
                point[0] = event.getX();
                point[1] = event.getY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                point[2] = event.getX(1);
                point[3] = event.getY(1);
                break;
            case MotionEvent.ACTION_MOVE://主、辅点移动
                touchZoomScale(event.getPointerCount(),event);
                break;
                default:break;
        }
        return true;
    }
    /**
     * 安卓自定义View进阶《十六》——多点触控详解 https://www.jianshu.com/p/cafedd319512
     * Android自定义View的多点触控 https://blog.csdn.net/u014582176/article/details/78934641
     * Android多点触控技术实战，自由地对图片进行缩放和移动 https://blog.csdn.net/guolin_blog/article/details/11100327
     * 曼哈顿距离 https://baike.baidu.com/item/%E6%9B%BC%E5%93%88%E9%A1%BF%E8%B7%9D%E7%A6%BB/743092?fr=aladdin
     * 几种常见的优化算法 https://www.cnblogs.com/xinbaby829/p/7289431.html
     * 优化算法——坐标上升法 https://blog.csdn.net/google19890102/article/details/51065297
     * @param touchPoint 触摸点数量
     * @param event {@link MotionEvent}
     */
    private void touchZoomScale(int touchPoint,MotionEvent event){
        int width = getWidth();
        int height = getHeight();
        switch (touchPoint){
            case 1://单点触摸
                float moveLevel = point[0]- event.getX();
                float moveVertical = point[1]- event.getY();
                float zoom = (float) (Math.sqrt(moveLevel * moveLevel + moveVertical * moveVertical)/ Math.sqrt(width * width + height * height));
                cameraManager.setZoomScale(0 < moveVertical ? -zoom : zoom);//静止添加横向,手势滑动容易偏差
                break;
            case 2://多点触摸
                //触摸点移动是否达到缩放要求,使用Math.sqrt过多效率不高
                //if(Math.sqrt((point[0]-event.getX(0))*(point[1]-event.getY(0)))<8&&8> Math.sqrt((point[2]-event.getX(1))*(point[3]-event.getY(1))))
                //    break;
                float levelInterval = event.getX(0)- event.getX(1);
                float verticalInterval = event.getY(0)- event.getY(1);
                float currentLength = (float)Math.sqrt(levelInterval * levelInterval + verticalInterval * verticalInterval);
                float originalDistance = (float) Math.sqrt((point[0]- point[2])*(point[0]- point[2])+(point[1]- point[3])*(point[1]- point[3]));
                if(8< Math.abs(currentLength - originalDistance)) {//触摸点移动是否达标优化方式
                    float scale = (float)(currentLength /  Math.sqrt(width * width + height * height));
                    cameraManager.setZoomScale(currentLength < originalDistance ? -scale : scale);
                }
                break;
                default:break;
        }
    }
}
