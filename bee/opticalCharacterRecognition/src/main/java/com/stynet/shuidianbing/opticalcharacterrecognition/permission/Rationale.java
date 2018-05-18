package com.stynet.shuidianbing.opticalcharacterrecognition.permission;
/**
 * Created by shuiDianBing on 2018/5/7.
 */
public interface Rationale extends Cancelable{
    /**
     * Go request permission.
     */
    void resume();
}
