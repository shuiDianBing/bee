package com.stynet.shuidianbing.opticalcharacterrecognition.permission;

import android.support.annotation.NonNull;

/**
 * Created by shuiDianBing on 2018/5/7.
 */
public interface Request<T extends Request> {

    /**
     * Here to fill in all of this to apply for permission, can be a, can be more.
     *
     * @param permissions one or more permissions.
     * @return {@link Request}.
     */
    @NonNull
    T permission(String... permissions);

    /**
     * Request code.
     *
     * @param requestCode int, the first parameter in callback {@code onRequestPermissionsResult(int, String[],
     *                    int[])}}.
     * @return {@link Request}.
     */
    @NonNull
    T requestCode(int requestCode);

    /**
     * Set the callback object.
     *
     * @return {@link Request}.
     */
    T callback(Object callback);

    /**
     * Request permission.
     *
     * @deprecated use {@link #start()} instead.
     */
    @Deprecated
    void send();

    /**
     * Request permission.
     */
    void start();

}

