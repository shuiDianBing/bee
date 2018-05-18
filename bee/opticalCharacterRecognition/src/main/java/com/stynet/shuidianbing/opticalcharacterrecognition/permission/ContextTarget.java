package com.stynet.shuidianbing.opticalcharacterrecognition.permission;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
/**
 * Created by shuiDianBing on 2018/5/7.
 */
public class ContextTarget implements Target {

    private Context mContext;

    public ContextTarget(Context context) {
        this.mContext = context;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public boolean shouldShowRationalePermissions(@NonNull String... permissions) {
        return false;
    }

    @Override
    public void startActivity(Intent intent) {
        mContext.startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        mContext.startActivity(intent);
    }
}
