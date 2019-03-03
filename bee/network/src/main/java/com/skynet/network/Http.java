package com.skynet.network;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator<<shuiDianBing on  2019/3/3 Refer to the website: nullptr
 */
class Http extends Request {
    public Http(String url, byte[] requestBytes, ResponseListener responseListener) {
        super(url, requestBytes, responseListener);
    }

    @Override
    public void execute() {
        connection();
    }
    public void connection(){
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(this.url);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setConnectTimeout(0x600);
            urlConnection.setUseCaches(false);//不使用缓存
            urlConnection.setInstanceFollowRedirects(true);
            urlConnection.setReadTimeout(0x300);//响应超时
            urlConnection.setDoInput(true);//设置写入
            urlConnection.setDoOutput(true);//设置输出
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/json.charset-UTF-8");
            urlConnection.connect();
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            outputStream.write(requestBytes);
            outputStream.flush();//刷新缓冲区发送数据
            outputStream.close();
            bufferedOutputStream.close();
            //------写入----
            if(HttpURLConnection.HTTP_OK == urlConnection.getResponseCode())
                responseListener.onSuccess(urlConnection.getInputStream());//提高灵活度，具体实现根据传进来的类型决定
            else
                responseListener.onFailure(new IOException(urlConnection.getResponseMessage()));
        } catch (IOException e) {
            e.printStackTrace();
            responseListener.onFailure(e);
        }finally {
            if(null != urlConnection)
                urlConnection.disconnect();//使用完关闭TCP连接释放资源
        }
    }
}
