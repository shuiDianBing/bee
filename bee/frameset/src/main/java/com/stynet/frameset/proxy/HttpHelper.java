package com.stynet.frameset.proxy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Administrator<<shuiDianBing on  2019/3/2 Refer to the website: nullptr
 */
public class HttpHelper implements HttpProcessor{
    private static HttpProcessor httpProcessor;//持有网络访问的对象引用
    private static HttpHelper interest;
    public static void init(HttpProcessor processor){
        httpProcessor = processor;
    }
    private HttpHelper(){}
    public static HttpHelper interest(){
        synchronized (HttpHelper.class){
            if(null == interest)
                interest = new HttpHelper();
        }
        return interest;
    }
    @Override
    public void get(String url, Map<String, Object> params, Callback callback) {
        httpProcessor.get(url,params,callback);
    }

    @Override
    public void post(String url, Map<String, Object> params, Callback callback) {
        httpProcessor.post(url,params,callback);
    }

    public static String appendParems(String url,Map<String,Object>params,Callback callback){
        if(null == params || params.isEmpty())
            return url;
        StringBuilder urlBuilder = new StringBuilder(url);
        if(0>= urlBuilder.indexOf("?"))
            urlBuilder.append("?");
        else if(!urlBuilder.toString().endsWith("?"))
            urlBuilder.append("&");
        for(Map.Entry<String,Object> entry : params.entrySet())
            urlBuilder.append("&"+entry.getKey()).append("=").append(encode(entry.getValue().toString()));
        return urlBuilder.toString();
    }
    private static final String encode(String string){
        try {
            return URLEncoder.encode(string,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
