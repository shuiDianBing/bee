package com.stynet.bee.presenterLayer;

import com.stynet.bee.modelLayer.SigninMode;
import com.stynet.frameset.FrameModel;
import com.stynet.frameset.mvpvm.viewModelLayer.MvpvmViewModel;
import com.stynet.frameset.mvvm.viewModelLayer.MvvmViewModel;
/**
 * Created by shuiDianBing on 2018/5/7.
 */
public class SigninVm extends MvpvmViewModel<SigninMode> {
    public SigninVm(FrameModel model) {
        super(model);
    }
}
