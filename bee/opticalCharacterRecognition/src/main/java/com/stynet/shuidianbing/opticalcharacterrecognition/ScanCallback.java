package com.stynet.shuidianbing.opticalcharacterrecognition;

/**
 * <p>Scan results callback.</p>
 * Created by shuiDianBing on 2017/5/10.
 */
public interface ScanCallback {

    /**
     * Content is not empty when the callback.
     *
     * @param content qr code content, is not null.
     */
    void onScanResult(String content);

}
