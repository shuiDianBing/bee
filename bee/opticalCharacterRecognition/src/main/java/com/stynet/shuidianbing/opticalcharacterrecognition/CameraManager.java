package com.stynet.shuidianbing.opticalcharacterrecognition;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.os.Build;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

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
 * 使用surfaceview预览camera，预览的图像有时会模糊，拍出来的照片也是模糊的。https://ask.csdn.net/questions/381074
 * 解决方案;配置这个特性试试<uses-feature android:name="android.hardware.camera.autofocus"/>若是还是得不到解决，建议你使用Camera2.0的API，Camera已经过期了，你的设备应该是5.0以上的
 */
public final class CameraManager {
    public static final byte FRONT = 0,BACK = 1;
    private final CameraConfiguration cameraConfiguration;
    private Camera camera;
    private byte currentCameraType;
    private boolean openLight;
    public CameraManager() {
        cameraConfiguration = new CameraConfiguration();
        openLight = true;
    }
    /**
     * Opens the mCamera driver and initializes the hardware parameters.
     *
     * @throws Exception ICamera open failed, occupied or abnormal.
     */
    public synchronized void openDriver(Context context,byte type) throws Exception {
        if (null!= camera)
            return;
        openCamera(currentCameraType = type);//camera = Camera.open();
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
     * 切换相机->方式1
     * android实现前置后置摄像头相互切换 https://www.cnblogs.com/hithlb/p/3543760.html
     * Android拍照、摄像方向旋转的问题 代码详解 https://blog.csdn.net/zhjali123/article/details/46986467
     * Android自定义照相机 预览拍照 切换前后置摄像头 https://www.jianshu.com/p/9b40e903b4a0
     * Android自定义照相机 预览拍照 切换前后置摄像头 https://blog.csdn.net/csh86277516/article/details/53325006?locationNum=3&fps=1
     * Android 切换前后置摄像头并录制 http://www.360doc.com/content/17/0520/13/9008018_655555740.shtml
     * @param holder {@link SurfaceHolder}
     */
    public void changeCamera(SurfaceHolder holder) throws IOException{
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        openCamera(currentCameraType = FRONT == currentCameraType ? BACK : FRONT);
        camera.setDisplayOrientation(90);
        camera.setPreviewDisplay(holder);
        camera.startPreview();
    }
    /**
     * 切换相机->方式2
     * Android自定义照相机 预览拍照 切换前后置摄像头 https://www.jianshu.com/p/9b40e903b4a0
     * Android 自定义视频录制翻转问题终极解决方案 https://blog.csdn.net/u014665060/article/details/53037864
     * @param holder {@link SurfaceHolder}
     */
    public void alterCamera(SurfaceHolder holder){
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        for(int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if(BACK == currentCameraType) {//现在是后置，变更为前置
                if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    camera.setPreviewCallback(null);
                    camera.stopPreview();//停掉原来摄像头的预览
                    camera.release();//释放资源
                    camera = null;//取消原来摄像头
                    camera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    currentCameraType = FRONT;
                    camera.setDisplayOrientation(90);
                    camera.startPreview();//开始预览
                    break;
                }
            } else //现在是前置， 变更为后置
                if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    camera.setPreviewCallback(null);
                    camera.stopPreview();//停掉原来摄像头的预览
                    camera.release();//释放资源
                    camera = null;//取消原来摄像头
                    camera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    currentCameraType = BACK;
                    camera.setDisplayOrientation(90);
                    camera.startPreview();//开始预览
                    break;
                }
        }
    }
    /**
     * 前摄像头parameters.getSupportedFlashModes()返回null无法开启手电筒
     * 安卓手机通过代码打开手机的手电筒。https://blog.csdn.net/cainiaobukeyi/article/details/68922106
     * Android开启手电筒功能（完美适配Android4x,5x,6x）https://blog.csdn.net/mynameishuangshuai/article/details/53214763
     */
    private void openLighting(){
        final Camera.Parameters parameters = camera.getParameters();
        List<String>flashModes = parameters.getSupportedFlashModes();
        if(null == flashModes)
            return;
        if(!parameters.getFlashMode().contains(Camera.Parameters.FLASH_MODE_TORCH))
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        else if(!parameters.getFlashMode().contains(Camera.Parameters.FLASH_MODE_OFF))
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameters);
    }
    /**
     * 前摄像头parameters.getSupportedFlashModes()返回null无法开启手电筒
     * @param context {@link Context}
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void openLighting(Context context){
        android.hardware.camera2.CameraManager cameraManager = (android.hardware.camera2.CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            final FeatureInfo[] features = context.getPackageManager().getSystemAvailableFeatures();
            for(final FeatureInfo info : features)
                if (PackageManager.FEATURE_CAMERA_FLASH.equals(info.name)) //判断设备否支持闪光灯
                    openLighting();
        }else
            try {
                cameraManager.setTorchMode("0",openLight = !openLight);
            } catch (CameraAccessException e) {
                e.printStackTrace();
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
    /**
     * 切换相机
     *
     * @param type {@link CameraManager#FRONT,CameraManager#BACK}.
     */
    @SuppressLint("NewApi")
    private void openCamera(int type){
        int frontIndex =-1;
        int backIndex = -1;
        int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for(int cameraIndex = 0; cameraIndex<cameraCount; cameraIndex++){
            Camera.getCameraInfo(cameraIndex, info);
            if(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                frontIndex = cameraIndex;
            }else if(info.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
                backIndex = cameraIndex;
            }
        }
        if(-1 != frontIndex)
            camera = Camera.open(type == FRONT ? frontIndex : backIndex);
    }

}
