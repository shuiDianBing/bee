package com.stynet.frameset.proxy;

/**
 * Created by Administrator<<shuiDianBing on  2019/3/2 Refer to the website: nullptr
 * 回调接口
 */
public interface Callback {
    void onSuccess(String result);
    void onFailure(String error);
}
