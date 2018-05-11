package com.stynet.bee.viewLayer.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.stynet.bee.R;
import com.stynet.bee.presenterLayer.SigninVm;
import com.stynet.frameset.mvpvm.viewLayer.activity.MvpvmActivity;
import com.stynet.bee.SigninBinding;
import com.stynet.frameset.photoProcess.StackBlur;

/**
 * Created by Administrator on 2018/5/7.
 */

public class SigninActivity extends MvpvmActivity<SigninVm,SigninBinding> {
    public static final String SIGNIN = "signin",ACCOUNT = "account",PASSWOD = "passwod";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_signin);
        DataBindingUtil.setContentView(this,R.layout.activity_signin);
        new StackBlur().blurNativelyPixels(getWindow().getDecorView().findViewById(android.R.id.content),((BitmapDrawable)getResources().getDrawable(R.mipmap.bg_sign)).getBitmap(),64,true);
    }
}
