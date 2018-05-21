package com.stynet.shuidianbing.opticalcharacterrecognition;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.stynet.shuidianbing.opticalcharacterrecognition.zbar.Config;
import com.stynet.shuidianbing.opticalcharacterrecognition.zbar.Image;
import com.stynet.shuidianbing.opticalcharacterrecognition.zbar.ImageScanner;
import com.stynet.shuidianbing.opticalcharacterrecognition.zbar.Symbol;
import com.stynet.shuidianbing.opticalcharacterrecognition.zbar.SymbolSet;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Created by shuiDianBing on 2017/5/10.
 */
class CameraScanAnalysis implements Camera.PreviewCallback {
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ImageScanner imageScanner;
    private Handler handler;
    private ScanCallback scanCallback;
    private boolean allowAnalysis = true;
    CameraScanAnalysis() {
        imageScanner = new ImageScanner();
        imageScanner.setConfig(0, Config.X_DENSITY, 3);
        imageScanner.setConfig(0, Config.Y_DENSITY, 3);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (null!= scanCallback)
                    scanCallback.onScanResult((String) msg.obj);//扫描结果
            }
        };
    }

    void setScanCallback(ScanCallback callback) {
        this.scanCallback = callback;
    }

    void onStop() {
        this.allowAnalysis = false;
    }

    void onStart() {
        this.allowAnalysis = true;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (allowAnalysis) {
            allowAnalysis = false;
            Camera.Size size = camera.getParameters().getPreviewSize();
            final Image barcode = new Image(size.width, size.height, "Y800");//Y800咱不知干啥用的
            barcode.setData(data);
            // barcode.setCrop(startX, startY, width, height);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    int result = imageScanner.scanImage(barcode);
                    String resultStr = null;
                    if (0!= result) {
                        SymbolSet symSet = imageScanner.getResults();
                        for (Symbol sym : symSet)
                            resultStr = sym.getData();
                    }
                    if (!TextUtils.isEmpty(resultStr)) {
                        Message message = handler.obtainMessage();
                        message.obj = resultStr;
                        message.sendToTarget();
                    } else
                        allowAnalysis = true;
                }
            });
        }
    }
    /**
     * @param width
     * @param height
     * @return result 解析的结果
     * 二维码扫描的坑：本地图片二维码扫描的优化（zxing和zbar）https://www.imooc.com/article/22919?block_id=tuijian_wz
     * 将bitmap里得到的argb数据转成yuv420sp格式
     * */
    private String scanResult(int width,int height,byte[]data) {
        final Image barcode = new Image(width, height, "Y800");
        barcode.setData(data);
        imageScanner.scanImage(barcode);
        SymbolSet symbols = barcode.getSymbols();
        StringBuilder sb = new StringBuilder();
        Iterator<Symbol> iterator = symbols.iterator();
        while(iterator.hasNext())
            sb.append(iterator.next().getData());
        Log.d("CameraScanAnalysis","scanResult:"+sb);
        return sb.toString();
    }
    /**
     * @param bitmap 扫描图片
     * @return result 解析的结果
     */
    public String scanPhoto(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        return scanResult(bitmap.getWidth(),bitmap.getHeight(),byteArrayOutputStream.toByteArray());
        //解析二维码图片
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        int[] argb = new int[width * height];
//        bitmap.getPixels(argb,0,width,0,0,width,height);
//        byte[] yuv = new byte[width * height + ((0== width %2? width : width + 1) * (0== height %2? height : height +1))/2];
//        encodeYUV420SP(yuv,argb,width,height);
//        bitmap.recycle();
//        scanResult(width,height,yuv);
    }
    /**
     * 将bitmap里得到的argb数据转成yuv420sp格式
     * 这个yuv420sp数据就可以直接传给MediaCodec, 通过AvcEncoder间接进行编码
     * * @param yuv420sp 用来存放yuv429sp数据 * @param argb 传入argb数据
     * @param width bmpWidth * @param height bmpHeight
     * */
    private void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        final int frameSize = width * height;// 帧图片的像素大小
        int yIndex = 0; // Y的index从0开始
        int uvIndex = frameSize;// UV的index从frameSize开始
        int argbIndex = 0;
        int Y, U, V, a, R, G, B;
        // ---循环所有像素点，RGB转YUV---
        for (int j = 0; j < height; j++)
            for (int i = 0; i < width; i++) {
                // a is not used obviously
                a = (argb[argbIndex] & 0xff000000) >> 24;
                R = (argb[argbIndex] & 0xff0000) >> 16;
                G = (argb[argbIndex] & 0xff00) >> 8;
                B = (argb[argbIndex] & 0xff);
                argbIndex++;
                // well known RGB to YUV algorithm
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;
                Y = Math.max(0, Math.min(Y, 255));
                U = Math.max(0, Math.min(U, 255));
                V = Math.max(0, Math.min(V, 255));
                // NV21 has a plane of Y and interleaved planes of VU each
                // sampled by a factor of 2
                // meaning for every 4 Y pixels there are 1 V and 1 U. Note the
                // sampling is every other // pixel AND every other scanline.
                // ---Y---
                yuv420sp[yIndex++] = (byte) Y;
                // ---UV---
                if ((j % 2 == 0) && (i % 2 == 0)) {
                    yuv420sp[uvIndex++] = (byte) V;
                    yuv420sp[uvIndex++] = (byte) U;
                }
            }
    }
}