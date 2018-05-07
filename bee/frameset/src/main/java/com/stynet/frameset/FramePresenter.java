package com.stynet.frameset;

/**
 * Created by xx on 2018/5/4.
 * mvp的presenter层:调用具体的逻辑实现（比如 请求网络等）
 * mvvm的ViewModel层：以databinding为基础，对外提供控制xml界面的方法
 */

public abstract class FramePresenter<Model extends FrameModel> {
    private Model model;

    public FramePresenter(Model model) {
        this.model = model;
    }
}
