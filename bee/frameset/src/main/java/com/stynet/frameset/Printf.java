package com.stynet.frameset;

import android.util.Log;

/**
 * Created by zhouda on 2018/5/3.
 * 日子输出
 */

public final class Printf {
    private static final boolean isOut = true;//日志控制

    public Printf(boolean isOut) {
        //this.isOut = isOut;
    }
    public static final void outInfo(String tag, String out){
        if(isOut)
            Log.i(tag,out);
    }
    public static final void outInfo(Class<?> tag, String out){
        if(isOut)
            Log.i(tag.getName(),out);
    }
    public static final void outInfo(Object tag, String out){
        if(isOut)
            Log.i(tag.getClass().getName(),out);
    }

    public static final void outDebug(String tag,String out){
        if(isOut)
            Log.d(tag,out);
    }
    public static final void outDebug(Class<?> tag, String out){
        if(isOut)
            Log.d(tag.getName(),out);
    }
    public static final void outDebug(Object tag, String out){
        if(isOut)
            Log.d(tag.getClass().getName(),out);
    }

    public static final void outWarnning(String tag,String out){
        if(isOut)
            Log.w(tag,out);
    }
    public static final void outWarnning(Class<?> tag, String out){
        if(isOut)
            Log.w(tag.getName(),out);
    }
    public static final void outWarnning(Object tag, String out){
        if(isOut)
            Log.w(tag.getClass().getName(),out);
    }

    public static final void outError(String tag,String out){
        if(isOut)
            Log.e(tag,out);
    }
    public static final void outError(Class<?> tag, String out){
        if(isOut)
            Log.e(tag.getName(),out);
    }
    public static final void outError(Object tag, String out){
        if(isOut)
            Log.e(tag.getClass().getName(),out);
    }

    public static final void outAssert(String tag,String out){
        if(isOut)
            Log.wtf(tag,out);
    }
    public static final void outAssert(Class<?> tag, String out){
        if(isOut)
            Log.wtf(tag.getName(),out);
    }
    public static final void outAssert(Object tag, String out){
        if(isOut)
            Log.wtf(tag.getClass().getName(),out);
    }

    public static final void outVerbose(String tag,String out){
        if(isOut)
            Log.v(tag,out);
    }
    public static final void outVerbose(Class<?> tag, String out){
        if(isOut)
            Log.v(tag.getName(),out);
    }
    public static final void outVerbose(Object tag, String out){
        if(isOut)
            Log.v(tag.getClass().getName(),out);
    }
}
