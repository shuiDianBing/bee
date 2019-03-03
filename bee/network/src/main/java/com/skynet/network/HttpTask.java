package com.skynet.network;

import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator<<shuiDianBing on  2019/3/3 Refer to the website: nullptr
 */
class HttpTask<Request,Response> implements Runnable{
    private Http http;

    /**
     *
     * @param url
     */
    public HttpTask(String url,Request request,Callback callback){
        try {
            http = new Http(url, JSON.toJSONString(request).getBytes("utf-8"), new JsonHttpListener<Response>(callback));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        http.execute();
    }
}
