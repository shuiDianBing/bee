package com.stynet.shuidianbing.opticalcharacterrecognition;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.stynet.shuidianbing.opticalcharacterrecognition.zbar.Config;
import com.stynet.shuidianbing.opticalcharacterrecognition.zbar.Image;
import com.stynet.shuidianbing.opticalcharacterrecognition.zbar.ImageScanner;
import com.stynet.shuidianbing.opticalcharacterrecognition.zbar.Symbol;
import com.stynet.shuidianbing.opticalcharacterrecognition.zbar.SymbolSet;

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
    private Image barcode;
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
            barcode = new Image(size.width, size.height, "Y800");
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
}