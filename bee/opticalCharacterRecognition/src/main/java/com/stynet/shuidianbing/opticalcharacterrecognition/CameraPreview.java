package com.stynet.shuidianbing.opticalcharacterrecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
/**
 * <p>QRCode Camera preview, include QRCode recognition.</p>
 * Created by shuiDianBing on 2017/5/10.
 */
public class CameraPreview extends FrameLayout implements SurfaceHolder.Callback {
    private CameraManager cameraManager;
    private CameraScanAnalysis cameraScanAnalysis;
    private SurfaceView surfaceView;

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
     * Camera start preview.
     */
    public boolean start() {
        try {
            cameraManager.openDriver(getContext());
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
}
