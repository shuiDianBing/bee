package com.stynet.shuidianbing.opticalcharacterrecognition.permission;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
/**
 * Created by shuiDianBing on 2018/5/7.
 */
public interface Target {
    Context getContext();

    boolean shouldShowRationalePermissions(@NonNull String... permissions);

    void startActivity(Intent intent);

    void startActivityForResult(Intent intent, int requestCode);
}
