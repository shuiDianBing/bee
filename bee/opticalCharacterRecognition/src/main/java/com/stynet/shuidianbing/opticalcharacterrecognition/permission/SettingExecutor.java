package com.stynet.shuidianbing.opticalcharacterrecognition.permission;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import androidx.annotation.NonNull;

/**
 * Created by shuiDianBing on 2018/5/7.
 */
public class SettingExecutor implements SettingService {
    private Target target;
    private int mRequestCode;

    SettingExecutor(@NonNull Target target, int requestCode) {
        this.target = target;
        this.mRequestCode = requestCode;
    }

    @Override
    public void execute() {
        Context context = target.getContext();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        target.startActivityForResult(intent, mRequestCode);
    }

    @Override
    public void cancel() {
    }
}
