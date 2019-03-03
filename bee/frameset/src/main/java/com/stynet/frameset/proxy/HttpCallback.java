package com.stynet.frameset.proxy;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Administrator<<shuiDianBing on  2019/3/2 Refer to the website: nullptr
 * Gson << https://github.com/google/gson
 * 回调接口实现,把返回的string转换成泛型Result
 */
public abstract class HttpCallback<Result>implements Callback {
    @Override
    public void onSuccess(String result) {
        Class<?> clz = analysisClassInfo(this);//clz传入类型的字节码
        Result objectResult = (Result) new Gson().fromJson(result,clz);
        onSuccess(objectResult);
    }

    @Override
    public void onFailure(String error) {

    }
    public abstract void onSuccess(Result result);
    /**
     * 反射获取泛型的class
     * @param object
     * @return
     */
    private static Class<?> analysisClassInfo(Object object){
        Type genType = object.getClass().getGenericSuperclass();//获得包含原始类型,参数化类型，数组类型，类型变量，类型变量，基本类型
        Type[] params = ((ParameterizedType)genType).getActualTypeArguments();//获取参数化类型
        return (Class<?>)params[0];
    }
}
