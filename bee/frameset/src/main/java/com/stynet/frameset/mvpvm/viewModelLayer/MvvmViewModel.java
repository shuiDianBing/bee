package com.stynet.frameset.mvpvm.viewModelLayer;

import com.stynet.frameset.FrameModel;
import com.stynet.frameset.FramePresenter;
import com.stynet.frameset.mvvm.modelLayer.MvvmModel;

/**
 * Created by xx on 2018/5/4.
 * ViewModel层：以databinding为基础，对外提供控制xml界面的方法
 */

public abstract class MvvmViewModel<Model extends MvvmModel> extends FramePresenter {
    public MvvmViewModel(FrameModel model) {
        super(model);
    }
}
