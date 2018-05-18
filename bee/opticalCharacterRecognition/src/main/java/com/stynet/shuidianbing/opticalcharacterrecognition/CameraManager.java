package com.stynet.shuidianbing.opticalcharacterrecognition;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * <p>Camera manager.</p>
 * Created by shuiDianBing on 2017/5/10.
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
        if (null== camera)
            throw new IOException("The camera is occupied.");
        cameraConfiguration.initFromCameraParameters(context,camera);
        Camera.Parameters parameters = camera.getParameters();
        String parametersFlattened = null== parameters ?null: parameters.flatten();
        try {
            cameraConfiguration.setDesiredCameraParameters(camera, false);
        } catch (RuntimeException re) {
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
