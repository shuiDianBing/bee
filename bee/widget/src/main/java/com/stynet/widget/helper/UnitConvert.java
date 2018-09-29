package com.stynet.widget.helper;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.DimenRes;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

/**
 * Created by xx shuiDianBing, 2018/08/03-19:02:19:02.Refer to the website: nullptr
 * Android 的几个单位dp 、sp、px的转换 https://blog.csdn.net/lvbo23/article/details/52597574
 **/

public class UnitConvert {
    public static float sp2px(Context context, float spValue) {
        return spValue * context.getResources().getDisplayMetrics().scaledDensity + 0.5f;
    }
    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param context
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        return (int) (pxValue / context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @param context
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        return (int) (dipValue * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @param context
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        return (int) (pxValue / context.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * Android开发中将dp和px转化成对应的px数值的方法 https://blog.csdn.net/iwalking11/article/details/52194680
     * @param context
     * @param dimenId
     * @return
     */
    public static float dpTurnPx(Context context, @DimenRes int dimenId){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, context.getResources().getDimension(dimenId), context.getResources().getDisplayMetrics());
    }
    /**
     * 获取状态栏的高度
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getApplicationContext().getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }
    /**
     * 获取屏幕宽高
     */
    public static int[] getScreenWH(Context context) {
        WindowManager manager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels, outMetrics.heightPixels};
    }

    /**
     * 隐藏软键盘
     */
    public static void hide(Dialog dialog) {
        View view = dialog.getCurrentFocus();
        if (view instanceof TextView) {
            InputMethodManager mInputMethodManager = (InputMethodManager) dialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }
}
