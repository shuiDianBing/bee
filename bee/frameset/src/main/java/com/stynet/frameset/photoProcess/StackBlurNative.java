package com.stynet.frameset.photoProcess;

import android.graphics.Bitmap;

/**
 * This use jni blur bitmap and pixels
 * Blur arithmetic is StackBlur
 */
public class StackBlurNative {

    /**
     * Load genius jni file
     */
    static {
        System.loadLibrary("photoBlur");
    }

    /**
     * Blur Image By Pixels
     *
     * @param pix Img pixel array
     * @param width   Img width
     * @param height   Img height
     * @param radius   Blur radius
     */
    protected static native void blurPixels(int[] pix, int width, int height, int radius);

    /**
     * Blur Image By Bitmap
     *
     * @param bitmap Img Bitmap
     * @param radius      Blur radius
     */
    protected static native void blurBitmap(Bitmap bitmap, int radius);
}

