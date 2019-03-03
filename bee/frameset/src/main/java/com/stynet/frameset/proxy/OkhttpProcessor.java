package com.stynet.frameset.proxy;

import android.os.Handler;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator<<shuiDianBing on  2019/3/2 Refer to the website: nullptr
 * okhttp加载数据量过大全部堆放在内存中
 * Okhttp3基本使用 https://www.jianshu.com/p/da4a806e599b
 */
public class OkhttpProcessor implements  HttpProcessor{
    private OkHttpClient okHttpClient;
    private Handler handler;
    public OkhttpProcessor(){
        okHttpClient = new OkHttpClient();
        handler = new Handler();
    }
    @Override
    public void get(String url, Map<String, Object> params, Callback callback) {

    }

    @Override
    public void post(String url, Map<String, Object> params, final Callback callback) {
        okHttpClient.newCall(new Request.Builder().url(url).post(appendBody(params)).build()).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e.toString());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if(response.isSuccessful()) {
                    final String result = response.body().string();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(result);
                        }
                    });
                }
            }
        });
    }
    private RequestBody appendBody(Map<String,Object>params){
        FormBody.Builder body = new FormBody.Builder();
        if(null == params || params.isEmpty())
            return body.build();
        for(Map.Entry<String,Object> entry : params.entrySet())
            body.add(entry.getKey(),entry.getValue().toString());
        return body.build();
    }
}
