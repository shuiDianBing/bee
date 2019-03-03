package com.stynet.frameset.proxy;

import java.util.Map;

/**
 * Created by Administrator<<shuiDianBing on  2019/3/2 Refer to the website: nullptr
 * 网络抽象层接口
 */
public interface HttpProcessor {
    void get(String url, Map<String,Object> params,Callback callback);
    void post(String url, Map<String,Object> params,Callback callback);
}
