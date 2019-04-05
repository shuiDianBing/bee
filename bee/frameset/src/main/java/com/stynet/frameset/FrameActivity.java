package com.stynet.frameset;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by xx on 2018/5/4.
 * view层activity view基类
 * 对应于Activity和xml，只负责view与用户交互操作数据显示等
 */

public abstract class FrameActivity<Presemter extends FramePresenter> extends AppCompatActivity {
    private Presemter presemter;
    protected void setPresemter(Presemter presemter){this.presemter = presemter;}
    protected Presemter getPresemter(){return presemter;}
}
