package com.stynet.bee.viewLayer.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.stynet.bee.R;
import com.stynet.frameset.mvpvm.viewLayer.activity.MvpvmActivity;

/**
 * Created by Administrator on 2018/5/7.
 */

public class HomeActivity extends MvpvmActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}
