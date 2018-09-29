package com.stynet.bee.viewLayer.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.stynet.bee.R;
import com.stynet.bee.presenterLayer.SigninVm;
import com.stynet.frameset.mvpvm.viewLayer.activity.MvpvmActivity;
import com.stynet.bee.SigninBinding;
import com.stynet.frameset.photoProcess.StackBlur;
import com.stynet.widget.activity.BubbleApiActivity;
import com.stynet.widget.activity.EphemerisActivity;

/**
 * Created by shuiDianBing on 2018/5/7.
 */

public class SigninActivity extends MvpvmActivity<SigninVm,SigninBinding> {
    public static final String SIGNIN = "signin",ACCOUNT = "account",PASSWOD = "passwod";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_signin);
        SigninBinding signinBinding = DataBindingUtil.setContentView(this,R.layout.activity_signin);
        signinBinding.textSignin.setOnClickListener(getClickListener());
        new StackBlur().blurNativelyPixels(getWindow().getDecorView().findViewById(android.R.id.content),((BitmapDrawable)getResources().getDrawable(R.mipmap.bg_sign)).getBitmap(),64,true);
    }
    private View.OnClickListener getClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.text_signin:
                        startActivity(new Intent(SigninActivity.this,EphemerisActivity.class));
                        break;
                        default:break;
                }
            }
        };
    }
}
