package com.stynet.frameset.mvpvm.presenterLayer;

import com.stynet.frameset.FrameModel;
import com.stynet.frameset.FramePresenter;
import com.stynet.frameset.mvp.modelLayer.MvpModel;

/**
 * Created by xx on 2018/5/4.
 * Presenter层：实现Presenter Interface，处理业务逻辑
 */

public abstract class MvpvmPresenter<Model extends MvpModel> extends FramePresenter {
    public MvpvmPresenter(FrameModel model) {
        super(model);
    }
}
