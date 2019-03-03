package com.skynet.network;

import java.io.IOException;

/**
 * Created by Administrator<<shuiDianBing on  2019/3/3 Refer to the website: nullptr
 * 公开的回调接口
 */
public interface Callback<Result> {
    void onSuccess(Result result);
    void onFailure(IOException error);
}
