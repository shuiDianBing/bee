package com.stynet.bee.viewLayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.stynet.bee.R;
import com.stynet.bee.presenterLayer.HomeVm;
import com.stynet.bee.HomeBinding;
import com.stynet.frameset.Printf;
import com.stynet.frameset.mvpvm.viewLayer.activity.MvpvmActivity;
import com.stynet.shuidianbing.opticalcharacterrecognition.activity.ScanCodeActivity;

/**
 * Created by shuiDianBing on 2018/5/7.
 */

public class HomeActivity extends MvpvmActivity<HomeVm,HomeBinding> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        startActivityForResult(new Intent(this, ScanCodeActivity.class),0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if(null!= data)
                    Printf.outInfo("HomeActivity",data.getStringExtra(ScanCodeActivity.SCANrESULT));
                break;
        }
    }
}
