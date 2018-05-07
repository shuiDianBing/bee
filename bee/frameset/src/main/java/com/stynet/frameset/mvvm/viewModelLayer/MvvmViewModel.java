package com.stynet.frameset.mvvm.viewModelLayer;

import com.stynet.frameset.FrameModel;
import com.stynet.frameset.FramePresenter;
import com.stynet.frameset.mvvm.modelLayer.MvvmModel;

/**
 * Created by xx on 2018/5/4.
 * Presenter层：实现Presenter Interface，处理业务逻辑
 */

public class MvvmViewModel<Model extends MvvmModel> extends FramePresenter {
    public MvvmViewModel(FrameModel model) {
        super(model);
    }
}
