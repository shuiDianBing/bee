package com.skynet.network;
/**
 * Created by Administrator<<shuiDianBing on  2019/3/3 Refer to the website: nullptr
 */
abstract class Request {
    protected String url;
    protected byte[] requestBytes;
    protected ResponseListener responseListener;

    public Request(String url, byte[] requestBytes, ResponseListener responseListener) {
        this.url = url;
        this.requestBytes = requestBytes;
        this.responseListener = responseListener;
    }
    protected abstract void execute();
}
