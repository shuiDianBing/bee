package com.stynet.shuidianbing.opticalcharacterrecognition.permission;

import android.support.annotation.NonNull;
/**
 * Created by shuiDianBing on 2018/5/7.
 */
public interface RationaleRequest extends Request<RationaleRequest> {
    /**
     * With user privilege refused many times, the Listener will be called back, you can prompt the user
     * permissions role in this method.
     *
     * @param listener {@link RationaleListener}.
     * @return {@link RationaleRequest}.
     */
    @NonNull
    RationaleRequest rationale(RationaleListener listener);
}
