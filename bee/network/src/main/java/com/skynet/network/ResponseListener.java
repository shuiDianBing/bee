package com.skynet.network;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator<<shuiDianBing on  2019/3/3 Refer to the website: nullptr
 * 内部的回调接口
 */
interface ResponseListener {
    void onSuccess(InputStream inputStream);
    void onFailure(IOException exception);
}
