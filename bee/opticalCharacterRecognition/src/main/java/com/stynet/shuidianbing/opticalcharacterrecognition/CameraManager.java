package com.stynet.shuidianbing.opticalcharacterrecognition;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * <p>Camera manager.</p>
 * Created by shuiDianBing on 2017/5/10.
 * Android相机实时自动对焦的完美实现 https://blog.csdn.net/huweigoodboy/article/details/51378751
 * Android摄像头开发完美demo---(循环聚焦,缩放大小,旋转picture,查询支持的picturesize, ImageButton按键效果）https://blog.csdn.net/jdsjlzx/article/details/9103747
 * Android自动聚焦、摄像头拍照、缩放至标准大小的完整实现 https://blog.csdn.net/yanzi1225627/article/details/7926994
 * 如何实现android手机摄像头的的自动对焦 https://blog.csdn.net/geekstart/article/details/13630009
 * android仿微信录制短视频,拍照,自动聚焦,手动聚焦,滑动缩放功能(Camera+TextureView+rxjava实现) https://blog.csdn.net/u012216274/article/details/68059637
 * android 视频播放器 android videoView 按不同比例缩放 .https://blog.csdn.net/sqk1988/article/details/7741050
 * Android 相机开发 闪光灯，前后摄像头切换，调整缩放比例 https://blog.csdn.net/a284266978/article/details/44856199
 * Android相机使用(系统相机、自定义相机、大图片处理) https://www.cnblogs.com/gao-chun/p/4863825.html
 */
public final class CameraManager {
    private final CameraConfiguration cameraConfiguration;
    private Camera camera;
    public CameraManager() {
        cameraConfiguration = new CameraConfiguration();
    }
    /**
     * Opens the mCamera driver and initializes the hardware parameters.
     *
     * @throws Exception ICamera open failed, occupied or abnormal.
     */
    public synchronized void openDriver(Context context) throws Exception {
        if (null!= camera)
            return;
        camera = Camera.open();
        //camera.autoFocus(getAutoFocusCallback());
        if (null== camera)
            throw new IOException("The camera is occupied.");
        cameraConfiguration.initFromCameraParameters(context,camera.getParameters());
        try {
            cameraConfiguration.setDesiredCameraParameters(camera, false);
        } catch (RuntimeException re) {
            Camera.Parameters parameters = camera.getParameters();
            String parametersFlattened = null== parameters ?null: parameters.flatten();
            if (null!= parametersFlattened) {
                parameters = camera.getParameters();
                parameters.unflatten(parametersFlattened);
                try {
                    camera.setParameters(parameters);
                    cameraConfiguration.setDesiredCameraParameters(camera, true);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * Closes the camera driver if still in use.
     */
    public synchronized void closeDriver() {
        if (null!= camera) {
            camera.cancelAutoFocus();//停止对焦
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    /**
     * Camera is opened.
     *
     * @return true, other wise false.
     */
    public boolean isOpen() {
        return null!= camera;
    }

    /**
     * Get camera configuration.
     *
     * @return {@link CameraConfiguration}.
     */
    public CameraConfiguration getConfiguration() {
        return cameraConfiguration;
    }
    /**
     * Camera resolution.
     * @param scale
     */
    public void setZoomScale(float scale){
        cameraConfiguration.setZoomScale(camera,scale);
    }

    /**
     * Camera start preview.
     *
     * @param holder          {@link SurfaceHolder}.
     * @param previewCallback {@link Camera.PreviewCallback}.
     * @throws IOException if the method fails (for example, if the surface is unavailable or unsuitable).
     */
    public void startPreview(SurfaceHolder holder, Camera.PreviewCallback previewCallback) throws IOException {
        if (null!= camera) {
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(previewCallback);
            camera.startPreview();
            camera.cancelAutoFocus();
        }
    }

    /**
     * Camera stop preview.
     */
    public void stopPreview() {
        if (null!= camera)
            try {
                camera.stopPreview();
                camera.setPreviewDisplay(null);
            } catch (Exception ignored) {
                // nothing.
            }
    }

    /**
     * Focus on, make a scan action.
     *
     * @param callback {@link Camera.AutoFocusCallback}.
     */
    public void autoFocus(Camera.AutoFocusCallback callback) {
        if (null!= camera)
            try {
                camera.autoFocus(callback);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
