package com.stynet.frameset.mvp.presenterLayer;

import com.stynet.frameset.FramePresenter;
import com.stynet.frameset.mvp.modelLayer.MvpModel;

/**
 * Created by xx on 2018/5/4.
 * presenter层:调用具体的逻辑实现（比如 请求网络等）
 */

public abstract class MvpPresenter<Model extends MvpModel> extends FramePresenter {
    public MvpPresenter(MvpModel model) {
        super(model);
    }
}
