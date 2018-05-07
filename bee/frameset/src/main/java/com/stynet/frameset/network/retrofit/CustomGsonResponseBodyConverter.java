package com.stynet.frameset.network.retrofit;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import static okhttp3.internal.Util.UTF_8;

/**
 * Created by zhouda on 2018/4/27.
 */

final class CustomGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    CustomGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = null;
        try {
            response = value.string();
//            Bean<String> bean = gson.fromJson(response, Bean.class);
//            if (null== bean.result || "".equals(bean.result)) {
//                value.close();
//                throw new ApiException(0, bean.msg);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        MediaType contentType = value.contentType();
        Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
        InputStream inputStream = new ByteArrayInputStream(response.getBytes());
        Reader reader = new InputStreamReader(inputStream, charset);
        try {
            return adapter.read(gson.newJsonReader(reader));
        } finally {
            value.close();
        }
    }
}