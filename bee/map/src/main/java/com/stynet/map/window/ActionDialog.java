package com.stynet.map.window;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.stynet.map.R;

/**
 * Created by xx shuiDianBing, 2018/11/29-11:44:11:44.Refer to the website: nullptr
 * 带有目的dialog(比如不限于跳转开启权限or设置)
 **/

public class ActionDialog extends Sight{
    /**
     *
     * @param context
     * @param intent
     * @param iconId
     * @param titleId
     * @param message
     */
    public static void displayActionDialog(final Context context, final Intent intent, @DrawableRes int iconId, @StringRes int titleId, @StringRes int message){
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(intent);
            }
        };
        new AlertDialog.Builder(context).setIcon(iconId).setTitle(titleId).setMessage(message).
                setNegativeButton(R.string.cancel,clickListener).setPositiveButton(R.string.confirm,clickListener).create().show();
    }

    /**
     *
     * @param context
     * @param action
     * @param iconId
     * @param titleId
     * @param message
     */
    public static void displayActionDialog(final Context context,final String action,@DrawableRes int iconId, @StringRes int titleId, @StringRes int message){
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(action));
            }
        };
        new AlertDialog.Builder(context).setIcon(R.drawable.icon_ring_down).setTitle(titleId).setMessage(message).
                setNegativeButton(R.string.cancel,clickListener).setPositiveButton(R.string.confirm,clickListener).create().show();
    }

    /**
     *
     * @param context
     * @param uri
     * @param iconId
     * @param titleId
     * @param message
     */
    public static void displayActionDialog(final Context context,Uri uri,@DrawableRes int iconId, @StringRes int titleId, @StringRes int message){
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent());
            }
        };
        new AlertDialog.Builder(context).setIcon(R.drawable.icon_ring_down).setTitle(titleId).setMessage(message).
                setNegativeButton(R.string.cancel,clickListener).setPositiveButton(R.string.confirm,clickListener).create().show();
    }
}
