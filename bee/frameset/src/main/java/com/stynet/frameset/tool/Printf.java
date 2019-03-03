package com.stynet.frameset.tool;

import android.util.Log;

/**
 * Created by Administrator<<shuiDianBing on  2019/3/2 Refer to the website: nullptr
 */
public final class Printf {
    private static final Boolean isOut = true;
    public static final void debug(String tag,String message){
        if(isOut)
            Log.d(tag,message);
    }
    public static final void info(String tag,String message){
        if(isOut)
            Log.i(tag,message);
    }
    public static final void warning(String tag,String message){
        if(isOut)
            Log.w(tag,message);
    }
    public static final void error(String tag,String message){
        if(isOut)
            Log.e(tag,message);
    }
}
