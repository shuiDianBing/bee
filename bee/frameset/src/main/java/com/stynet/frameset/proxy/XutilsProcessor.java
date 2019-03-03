package com.stynet.frameset.proxy;

import android.app.Application;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Map;

/**
 * Created by Administrator<<shuiDianBing on  2019/3/2 Refer to the website: nullptr
 * xUtils3 << https://github.com/wyouflf/xUtils3
 * Android实践：xUtils3探究 << https://blog.csdn.net/p106786860/article/details/53564605
 */
public class XutilsProcessor implements HttpProcessor{
    public XutilsProcessor(Application application,boolean isLog){
        x.Ext.init(application);
        x.Ext.setDebug(isLog);
    }
    @Override
    public void get(String url, Map<String, Object> params, Callback callback) {

    }

    @Override
    public void post(String url, Map<String, Object> params, final Callback callback) {
        RequestParams requestParams = new RequestParams(url);
        x.http().post(requestParams, new org.xutils.common.Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                callback.onFailure(ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {}

            @Override
            public void onFinished() {}
        });
    }
}
