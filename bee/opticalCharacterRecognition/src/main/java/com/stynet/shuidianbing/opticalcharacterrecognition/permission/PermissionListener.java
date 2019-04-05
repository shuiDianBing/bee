package com.stynet.shuidianbing.opticalcharacterrecognition.permission;

import androidx.annotation.NonNull;

import java.util.List;

public interface PermissionListener {
    void onSucceed(int requestCode, @NonNull List<String> grantPermissions);
    void onFailed(int requestCode, @NonNull List<String> deniedPermissions);
}
