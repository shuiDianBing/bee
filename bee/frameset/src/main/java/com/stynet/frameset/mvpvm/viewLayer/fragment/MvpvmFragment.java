package com.stynet.frameset.mvpvm.viewLayer.fragment;

import androidx.databinding.ViewDataBinding;

import com.stynet.frameset.FrameFragment;
import com.stynet.frameset.mvpvm.viewModelLayer.MvpvmViewModel;

/**
 * Created by xx on 2018/5/4.
 * view层:只负责view与用户交互操作数据显示等
 */

public abstract class MvpvmFragment<Presemter extends MvpvmViewModel,DataBinding extends ViewDataBinding>  extends FrameFragment {
}
