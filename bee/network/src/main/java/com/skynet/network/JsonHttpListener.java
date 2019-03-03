package com.skynet.network;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

final class JsonHttpListener<Result>implements ResponseListener {
    private Handler handle;
    private Callback callback;

    JsonHttpListener(Callback callback) {
        this.handle = new Handler(Looper.myLooper());
        this.callback = callback;
    }

    @Override
    public void onSuccess(InputStream inputStream) {
        final Result result = (Result) JSON.parseObject(getConent(inputStream),analysisClassInfo());
        handle.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(result);
            }
        });
    }

    @Override
    public void onFailure(IOException exception) {
        callback.onFailure(exception);
    }
    private String getConent(InputStream inputStream){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while (null != (line = reader.readLine()))
                stringBuilder.append(line + "\n");
            return stringBuilder.toString();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 反射获取泛型class
     * @return
     */
    private Class<?>analysisClassInfo(){
        Type type = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType)type).getActualTypeArguments();
        return (Class<?>) params[0];
    }
}
