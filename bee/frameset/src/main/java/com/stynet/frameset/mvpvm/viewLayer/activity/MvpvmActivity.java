package com.stynet.frameset.mvpvm.viewLayer.activity;

import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.stynet.frameset.FrameActivity;
import com.stynet.frameset.mvpvm.viewModelLayer.MvpvmViewModel;

/**
 * Created by xx on 2018/5/4.
 * view层:只负责view与用户交互操作数据显示等
 * Android MVPVM架构实践 https://blog.csdn.net/myterabithia/article/details/53783600
 */

public abstract class MvpvmActivity<Presemter extends MvpvmViewModel,DataBinding extends ViewDataBinding> extends FrameActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
