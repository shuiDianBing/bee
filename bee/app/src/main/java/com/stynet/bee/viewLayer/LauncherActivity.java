package com.stynet.bee.viewLayer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.stynet.bee.R;
import com.stynet.bee.viewLayer.activity.HomeActivity;
import com.stynet.bee.viewLayer.activity.SigninActivity;
import com.stynet.frameset.Printf;
import com.stynet.frameset.storage.SharedXml;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

public class LauncherActivity extends AppCompatActivity {
    private static final String TAG = "LauncherActivity";
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        //Android 隐藏虚拟按键及状态栏 https://blog.csdn.net/liuyuejinqiu/article/details/70230963
        // Example of a call to a native method
        //Androidstudio编译c/c++jni方法 https://blog.csdn.net/zrf1335348191/article/details/73914631
        //提示更改了包要修改native函数对应报名
        ((TextView) findViewById(R.id.sample_text)).setText(stringFromJNI());
        //Glide最新版V4使用指南 https://blog.csdn.net/u013005791/article/details/74532091
        //https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1525689178097&di=2cbc2939ba4c96ef11c983a3d7c0a3e3&imgtype=0&src=http%3A%2F%2Fimg3.duitang.com%2Fuploads%2Fitem%2F201511%2F29%2F20151129194142_hZzMP.jpeg
        //https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E6%89%8B%E6%9C%BA%E5%A3%81%E7%BA%B8&step_word=&hs=2&pn=13&spn=0&di=24753857150&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=2910742445%2C2594002876&os=2169060262%2C2016180231&simid=4088208774%2C514443929&adpicid=0&lpn=0&ln=1978&fr=&fmq=1525679085392_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=http%3A%2F%2Fimg4.duitang.com%2Fuploads%2Fitem%2F201403%2F02%2F20140302235502_AxFyM.jpeg&fromurl=ippr_z2C%24qAzdH3FAzdH3F4_z%26e3B17tpwg2_z%26e3Bv54AzdH3Frj5rsjAzdH3F4ks52AzdH3F8d900ladmAzdH3F1jpwtsAzdH3F&gsm=0&rpstart=0&rpnum=0&islist=&querylist=
        //https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1525689277288&di=6957a78d9b6e65ca393f863b59f09744&imgtype=0&src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fitem%2F201510%2F09%2F20151009144400_cGrN5.jpeg
        //"https://tu.ttt669.com/girl/TuiGirl/137/08.jpg"
        Glide.with(this).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1525689277288&di=6957a78d9b6e65ca393f863b59f09744&imgtype=0&src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fitem%2F201510%2F09%2F20151009144400_cGrN5.jpeg")
                .into((ImageView) findViewById(R.id.image_ad));
        startTime(3);
    }
    private void startTime(final int second){
        Observable.interval(0,1, TimeUnit.SECONDS).take(second).map(new Func1<Long, Long>() {
            @Override
            public Long call(Long aLong) {
                return second - aLong;
            }
        }).//subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        subscribe(new Observer<Long>() {
            @Override
            public void onCompleted() {
                startActivity(new Intent(LauncherActivity.this, null != getSharedPreferences(SigninActivity.SIGNIN, Context.MODE_PRIVATE).getString(SigninActivity.ACCOUNT,null)? HomeActivity.class : SigninActivity.class));
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Long aLong) {
                Printf.outDebug(TAG,"onNext:aLong="+aLong);
            }
        });
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
