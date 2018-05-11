package com.stynet.frameset.mvpvm.viewModelLayer;

import com.stynet.frameset.FrameModel;
import com.stynet.frameset.FramePresenter;
import com.stynet.frameset.mvpvm.modelLayer.MvpvmModel;

/**
 * Created by xx on 2018/5/4.
 * ViewModel层：以databinding为基础，对外提供控制xml界面的方法
 */

public abstract class MvpvmViewModel<Model extends MvpvmModel> extends FramePresenter {
    public MvpvmViewModel(FrameModel model) {
        super(model);
    }
}
