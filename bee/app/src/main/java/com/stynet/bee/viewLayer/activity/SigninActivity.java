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

import butterknife.ButterKnife;
import butterknife.OnClick;

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
        ButterKnife.bind(this);
//        signinBinding.textSignin.setOnClickListener(getClickListener());
//        signinBinding.textRegister.setOnClickListener(getClickListener());
//        signinBinding.textForgotPassword.setOnClickListener(getClickListener());
        new StackBlur().blurNativelyPixels(getWindow().getDecorView().findViewById(android.R.id.content),((BitmapDrawable)getResources().getDrawable(R.mipmap.bg_sign)).getBitmap(),64,true);
    }
    @OnClick({R.id.textSignin,R.id.textRegister,R.id.textForgotPassword})
    public void clickListener(View view) {
        switch (view.getId()) {
            case R.id.textSignin:
                startActivity(new Intent(SigninActivity.this, HomeActivity.class));
                break;
            case R.id.textRegister:
                startActivity(new Intent(SigninActivity.this, EphemerisActivity.class));
                break;
            case R.id.textForgotPassword:
                startActivity(new Intent(SigninActivity.this, EphemerisActivity.class));
                break;
            default:
                break;
        }
    }
}
