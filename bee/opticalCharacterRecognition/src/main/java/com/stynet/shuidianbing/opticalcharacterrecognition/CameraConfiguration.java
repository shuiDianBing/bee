/*
 * Copyright © Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stynet.shuidianbing.opticalcharacterrecognition;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Camera config.</p>
 * Created by shuiDianBing on 2018/5/7.
 */
public final class CameraConfiguration {

    private static final String TAG = "CameraConfiguration";

    private static final int MIN_PREVIEW_PIXELS = 480 * 320;
    private static final double MAX_ASPECT_DISTORTION = 0.15;
    private Point screenResolution;
    private Point cameraResolution;

    @SuppressWarnings("SuspiciousNameCombination")
    public void initFromCameraParameters(Context context,Camera.Parameters parameters) {
        screenResolution = getDisplaySize(((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay());
        Point screenResolutionForCamera = new Point();
        screenResolutionForCamera.x = screenResolution.x;
        screenResolutionForCamera.y = screenResolution.y;
        // Convert to vertical screen.
        if (screenResolution.x < screenResolution.y) {
            screenResolutionForCamera.x = screenResolution.y;
            screenResolutionForCamera.y = screenResolution.x;
        }
        cameraResolution = findBestPreviewSizeValue(parameters, screenResolutionForCamera);
    }
    /**
     * 根据api版本设置point的x,y
     * @param display
     * @return point
     */
    private Point getDisplaySize(final Display display) {
        final Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
            display.getSize(point);
        else
            point.set(display.getWidth(), display.getHeight());
        return point;
    }
    /**
     * 设置相机参数
     * @param camera
     * @return safeMode
     */
    public void setDesiredCameraParameters(Camera camera, boolean safeMode) {
        Camera.Parameters parameters = camera.getParameters();
        if (null== parameters) {
            Log.w(TAG, "Device error: no camera parameters are available. Proceeding without configuration.");
            return;
        }
        //parameters.setPictureFormat(ImageFormat.JPEG);//过时(PixelFormat.JPEG);
        parameters.setPreviewFpsRange(2,4);//setPreviewFrameRate(4);
        //parameters.set("jpeg-quality", 85);//设置照片质量
        parameters.setFlashMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);//(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
        //List<Camera.Area> areas = new ArrayList<>();
        //areas.add(new Camera.Area(new Rect(),100));
        //parameters.setFocusAreas(areas);
        parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
        parameters.setPictureSize(cameraResolution.x,cameraResolution.y);
        parameters.setRotation(90);
        camera.setParameters(parameters);
        Camera.Size afterSize = camera.getParameters().getPreviewSize();
        if (null!= afterSize &&(cameraResolution.x != afterSize.width || cameraResolution.y != afterSize.height)) {
            cameraResolution.x = afterSize.width;
            cameraResolution.y = afterSize.height;
        }
        camera.setDisplayOrientation(90);//旋转捕获的画面,主要解决捕获到的画面和手机方向不一致
    }
    /**
     * Camera resolution.
     * @param camera
     * @param scale 调整比例
     * @return {@link Camera}.
     */
    public void setZoomScale(Camera camera,float scale){
        Camera.Parameters parameters = camera.getParameters();
        int zoom = parameters.getZoom();
        int maxZoom = parameters.getMaxZoom();
        int displayZoom = Math.round((0== zoom ? maxZoom : zoom)* scale)+ zoom;
        parameters.setZoom(maxZoom < displayZoom ? maxZoom : 0> displayZoom ?0: displayZoom);//控制displayZoom限制在0和maxZoom内
        camera.setParameters(parameters);
    }
    /**
     * Camera resolution.
     *
     * @return {@link Point}.
     */
    public Point getCameraResolution() {
        return cameraResolution;
    }

    /**
     * Screen resolution.
     *
     * @return {@link Point}.
     */
    public Point getScreenResolution() {
        return screenResolution;
    }

    /**
     * Calculate the preview interface size.
     *
     * @param parameters       camera params.
     * @param screenResolution screen resolution.
     * @return {@link Point}.
     */
    private Point findBestPreviewSizeValue(Camera.Parameters parameters, Point screenResolution) {
        List<Camera.Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
        if (rawSupportedSizes == null) {
            Log.w(TAG, "Device returned no supported preview sizes; using default");
            Camera.Size defaultSize = parameters.getPreviewSize();
            return new Point(defaultSize.width, defaultSize.height);
        }
        // Sort by size, descending
        List<Camera.Size> supportedPreviewSizes = new ArrayList<>(rawSupportedSizes);
        Collections.sort(supportedPreviewSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
//                if (bPixels < aPixels)
//                    return -1;
//                if (bPixels > aPixels)
//                    return 1;
//                return 0;
                return bPixels < aPixels ?-1: bPixels > aPixels ?1:0;//实现排序规则
            }
        });
        if (Log.isLoggable(TAG, Log.INFO)) {
            StringBuilder previewSizesString = new StringBuilder();
            for (Camera.Size supportedPreviewSize : supportedPreviewSizes)
                previewSizesString.append(supportedPreviewSize.width).append('x').append(supportedPreviewSize.height).append(' ');
            Log.i(TAG, "Supported preview sizes: " + previewSizesString);
        }
        double screenAspectRatio = (double) screenResolution.x / (double) screenResolution.y;
        // Remove sizes that are unsuitable
        Iterator<Camera.Size> it = supportedPreviewSizes.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewSize = it.next();
            int realWidth = supportedPreviewSize.width;
            int realHeight = supportedPreviewSize.height;
            if (realWidth * realHeight < MIN_PREVIEW_PIXELS) {
                it.remove();
                continue;
            }
            boolean isCandidatePortrait = realWidth < realHeight;
            int maybeFlippedWidth = isCandidatePortrait ? realHeight : realWidth;
            int maybeFlippedHeight = isCandidatePortrait ? realWidth : realHeight;
            double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove();
                continue;
            }
            if (maybeFlippedWidth == screenResolution.x && maybeFlippedHeight == screenResolution.y) {
                Point exactPoint = new Point(realWidth, realHeight);
                Log.i(TAG, "Found preview size exactly matching screen size: " + exactPoint);
                return exactPoint;
            }
        }
        // If no exact match, use largest preview size. This was not a great
        // idea on older devices because
        // of the additional computation needed. We're likely to get here on
        // newer Android 4+ devices, where
        // the CPU is much more powerful.
        if (!supportedPreviewSizes.isEmpty()) {
            Camera.Size largestPreview = supportedPreviewSizes.get(0);
            Point largestSize = new Point(largestPreview.width, largestPreview.height);
            Log.i(TAG, "Using largest suitable preview size: " + largestSize);
            return largestSize;
        }
        // If there is nothing at all suitable, return current preview size
        Camera.Size defaultPreview = parameters.getPreviewSize();
        Point defaultSize = new Point(defaultPreview.width, defaultPreview.height);
        Log.i(TAG, "No suitable preview sizes, using default: " + defaultSize);
        return defaultSize;
    }
}
