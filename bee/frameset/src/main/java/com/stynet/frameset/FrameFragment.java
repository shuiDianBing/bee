package com.stynet.frameset;

import android.support.v4.app.Fragment;

/**
 * Created by xx on 2018/5/4.
 * 视图层fragment view基类
 * 对应于Activity和xml，只负责view与用户交互操作数据显示等
 */

public abstract class FrameFragment<Presemter extends FramePresenter> extends Fragment {
    private Presemter presemter;
    protected void setPresemter(Presemter presemter){this.presemter = presemter;}
    protected Presemter getPresemter(){return presemter;}
}
