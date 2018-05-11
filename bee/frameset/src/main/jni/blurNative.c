#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>
#include "stackBlur.h"
#include "com_stynet_frameset_photoProcess_StackBlurNative.h"

#define TAG "com_stynet_frameset_photoProcess_StackBlurNative"
/*
 * JNI编程-- undefined reference to `__android_log_print' 的解决办法 https://blog.csdn.net/forandever/article/details/50393499
 * 使用__android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)要在Android.mk添加LOCAL_LDLIBS    := -lm -llog -ljnigraphics
 * */
#define ANDROID_LOGeRROR(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__);
JNIEXPORT void JNICALL Java_com_stynet_frameset_photoProcess_StackBlurNative_blurPixels
        (JNIEnv *env, jclass obj, jintArray array, jint width, jint height, jint radius){
    jint *pixels = (*env)->GetIntArrayElements(env,array,0);
    if(NULL != pixels) {
        pixels = blur_ARGB_8888(pixels,width,height,radius);
        (*env)->ReleaseIntArrayElements(env,array,pixels,0);
    }else
        ANDROID_LOGeRROR("input pixels isn't null");
}
JNIEXPORT void JNICALL Java_com_stynet_frameset_photoProcess_StackBlurNative_blurBitmap
        (JNIEnv *env, jclass obj, jobject bitmap, jint radius) {
    AndroidBitmapInfo bitmapInfo;
    void *pixels;
    if (ANDROID_BITMAP_RESUT_SUCCESS != AndroidBitmap_getInfo(env, bitmap, &bitmapInfo)) {
        ANDROID_LOGeRROR("AndroidBitmap_getInfo failed!");
        return;
    }
    if(ANDROID_BITMAP_FORMAT_RGBA_8888 != bitmapInfo.format && ANDROID_BITMAP_FORMAT_RGB_565 != bitmapInfo.format) {
        ANDROID_LOGeRROR("Only support ANDROID_BITMAP_FORMAT_RGBA_8888 && ANDROID_BITMAP_FORMAT_RGB_565");
        return;
    }
    if(ANDROID_BITMAP_RESUT_SUCCESS != AndroidBitmap_lockPixels(env,bitmap,&pixels)) {
        ANDROID_LOGeRROR("AndroidBitmap_lockPixels failed!");
        return;
    }
    int width = bitmapInfo.width;
    int height = bitmapInfo.height;
    if(ANDROID_BITMAP_FORMAT_RGBA_8888 == bitmapInfo.format)
        pixels = blur_ARGB_8888((int*)pixels,width,height,radius);
    else if(ANDROID_BITMAP_FORMAT_RGB_565 == bitmapInfo.format)
        pixels = blur_RGB_565((short *)pixels,width,height,radius);
    AndroidBitmap_unlockPixels(env,bitmap);
}