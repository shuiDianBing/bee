package com.skynet.network;


/**
 * Created by Administrator<<shuiDianBing on  2019/3/3 Refer to the website: nullptr
 * 策略模式
 */
public class Concurrent {
    public static<Request,Response> void sendJsonRequest(final Request request,final String url,final Callback callback){
        ThreadPoolManager.getOutInstance().execute(new HttpTask<Request,Response>(url,request,callback));
    }
}
