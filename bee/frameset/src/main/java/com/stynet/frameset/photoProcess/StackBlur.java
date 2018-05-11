package com.stynet.frameset.photoProcess;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/5/7.
 * This is blur image class
 * Use {@link StackBlurNative} fast blur bitmap
 * Blur arithmetic is StackBlur
 * 背景模糊
 * Android 图片高斯模糊解决方案 https://www.jianshu.com/p/02da487a2f43
 */
public class StackBlur extends StackBlurNative {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)//api>16设置背景
    public void blurApi(View view,final Bitmap sentBitmap,int radius,final boolean canReuseInBitmap){
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), canReuseInBitmap);
        final RenderScript rs = RenderScript.create(view.getContext());
        final Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius /* e.g. 3.f */);//Caused by: android.renderscript.RSIllegalArgumentException: Radius out of range (0 < r <= 25).
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);
        display(view,new BitmapDrawable(view.getResources(),bitmap));
    }
    /**
     * 优化main即ui主线程读取文件比较耗时造成ui卡顿用户体验不好,让读取文件在在新线程中执行
     * 这种方式要无法返回bitmap,只能通过回掉方式设置,但是这样要添加接口了
     */
    public void blurJava(final View view, final Bitmap bitmap, final int radius, final boolean canReuseInBitmap){
        //final WeakReference<Activity> weakReference = new WeakReference<>(activity);
        final Handler handler = new Handler(){
            @SuppressWarnings("deprecation")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        //Activity activity = weakReference.get();
                        //获取activity的rootView方式1.activity.getWindow().getDecorView().findViewById(android.R.id.content)
                        //获取activity的rootView方式2.((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0)
                        display(view,new BitmapDrawable(view.getResources(), (Bitmap) msg.obj));
                        break;
                }
            }
        };
        final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {//线程池优化
                //singleThreadExecutor.shutdown();//关闭线程池，不影响已经提交的任务
                Message message = new Message();
                message.what = 0;
                message.obj = blur(bitmap,90,radius,canReuseInBitmap);
                handler.sendMessage(message);
                singleThreadExecutor.shutdownNow();//关闭线程池，并尝试去终止正在执行的线程
            }
        });
    }
    private void display(View view,Drawable drawable){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            view.setBackground(drawable);
        else
            view.setBackgroundDrawable(drawable);
    }
    private Bitmap blur(Bitmap bgBitmap,int scale,int radius,boolean canReuseInBitmap){
        bgBitmap = ThumbnailUtils.extractThumbnail(bgBitmap,scale,scale);
        Bitmap overlay = Bitmap.createBitmap(scale,scale,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bgBitmap,0,0,paint);
        overlay = fastblur(overlay,radius,canReuseInBitmap);
        canvas.save();
        canvas.drawARGB(96,0,0,0);
        canvas.restore();
        return overlay;
    }
    /**
     * StackBlur By Java Bitmap
     *
     * @param original         Original Image
     * @param radius           Blur radius
     * @param canReuseInBitmap Can reuse In original Bitmap
     * @return Image Bitmap
     */
    @SuppressLint("NewApi")
    private Bitmap fastblur(Bitmap original, int radius,boolean canReuseInBitmap) {
        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at quasimondo.com>
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at kayenko.com>
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>
        if (radius < 1)
            return null;
        Bitmap bitmap = original.copy(original.getConfig(), canReuseInBitmap);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pix = new int[width * height];
//        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, width, 0, 0, width, height);
        int wm = width - 1;
        int hm = height - 1;
        int wh = width * height;
        int div = radius + radius + 1;
        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(width, height)];
        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int temp = 256 * divsum;
        int dv[] = new int[temp];
        for (i = 0; i < temp; i++)
            dv[i] = (i / divsum);
        yw = yi = 0;
        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;
        for (y = 0; y < height; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;
            for (x = 0; x < width; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (y == 0)
                    vmin[x] = Math.min(x + radius + 1, wm);
                p = pix[yw + vmin[x]];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi++;
            }
            yw += width;
        }
        for (x = 0; x < width; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * width;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                sir = stack[i + radius];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                rbs = r1 - Math.abs(i);
                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
                if (i < hm)
                    yp += width;
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < height; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (x == 0)
                    vmin[y] = Math.min(y + r1, hm) * width;
                p = x + vmin[y];
                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi += width;
            }
        }
//        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, width, 0, 0, width, height);
        return (bitmap);
    }

    private Bitmap buildBitmap(Bitmap original,boolean canReuseInBitmap){
        if(null== original)
            throw new NullPointerException("Blur bitmap original isn't null");
        Bitmap.Config config = original.getConfig();
        if(Bitmap.Config.ARGB_8888 != config && Bitmap.Config.RGB_565 != config)
            throw new RuntimeException("Blur bitmap only supported Bitmap.Config.ARGB_8888 && Bitmap.Config.RGB_565.");
        //下面这种方式original不可绘制用到setPixels会抛异常 Caused by: java.lang.IllegalStateException
        //return canReuseInBitmap ? original : original.copy(config,true);// If can reuse in bitmap return this or copy
        return original.copy(config,true);
    }
    /**
     * StackBlur By Jni Bitmap
     *
     * @param original         Original Image
     * @param radius           Blur radius
     * @param canReuseInBitmap Can reuse In original Bitmap
     * @return Image Bitmap
     */
    public void blurNatively(View view,Bitmap original, int radius, boolean canReuseInBitmap){
        if(1 > radius)
            return ;
        Bitmap bitmap = buildBitmap(original,canReuseInBitmap);
        if(1 != radius)
            blurBitmap(bitmap,radius);
        display(view,new BitmapDrawable(view.getResources(),bitmap));
    }
    /**
     * StackBlur By Jni Pixels
     *
     * @param original         Original Image
     * @param radius           Blur radius
     * @param canReuseInBitmap Can reuse In original Bitmap
     * @return Image Bitmap
     */
    public void blurNativelyPixels(View view,Bitmap original, int radius, boolean canReuseInBitmap){
        if(1> radius)
            return ;
        Bitmap bitmap = buildBitmap(original, canReuseInBitmap);
        if(1!= radius){
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pix = new int[width * height];
            bitmap.getPixels(pix,0,width,0,0,width,height);
            blurPixels(pix,width,height,radius);//Jni Pixels Blur
            bitmap.setPixels(pix,0,width,0,0,width,height);
        }
        display(view,new BitmapDrawable(view.getResources(),bitmap));
    }
}
