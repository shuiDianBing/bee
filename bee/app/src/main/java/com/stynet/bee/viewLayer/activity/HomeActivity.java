package com.stynet.bee.viewLayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;

import com.stynet.bee.R;
import com.stynet.bee.presenterLayer.HomeVm;
import com.stynet.bee.HomeBinding;
import com.stynet.frameset.Printf;
import com.stynet.frameset.mvpvm.viewLayer.activity.MvpvmActivity;
import com.stynet.shuidianbing.opticalcharacterrecognition.activity.ScanCodeActivity;
import com.stynet.widget.KindView;

/**
 * Created by shuiDianBing on 2018/5/7.
 */

public class HomeActivity extends MvpvmActivity<HomeVm,HomeBinding> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //startActivityForResult(new Intent(this, ScanCodeActivity.class),0);
        KindView kind = findViewById(R.id.king);
        kind.setKinds(new CharSequence[]{"詹姆斯·T·柯克","斯波克","莱昂纳德·麦考伊","蒙哥马利·史考特","苏鲁","帕维尔·安德烈维奇·契科夫","尼欧塔·乌胡拉","让-卢克·皮卡德","威廉·T·瑞克","data"});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if(null!= data)
                    Printf.outInfo("HomeActivity",""+data.getStringExtra(ScanCodeActivity.SCANrESULT));
                break;
        }
    }
}
