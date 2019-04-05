package com.stynet.bee.viewLayer.activity;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.stynet.bee.R;
import com.stynet.bee.presenterLayer.HomeVm;
import com.stynet.bee.HomeBinding;
import com.stynet.frameset.Printf;
import com.stynet.frameset.mvpvm.viewLayer.activity.MvpvmActivity;
import com.stynet.shuidianbing.opticalcharacterrecognition.activity.ScanCodeActivity;
import com.stynet.widget.FiltrateView;
import com.stynet.widget.KindView;

import java.io.IOException;

/**
 * Created by shuiDianBing on 2018/5/7.
 */

public class HomeActivity extends MvpvmActivity<HomeVm,HomeBinding> {
    private SurfaceView surface;
    private Camera camera;
    private SurfaceHolder holder;
    private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        startActivityForResult(new Intent(this, ScanCodeActivity.class),0);
        KindView kind = findViewById(R.id.king);
        kind.setKinds(new CharSequence[]{"龍靐龘龘","abcdefghijklmnopqrstuvwxyz","詹姆斯·T·柯克","斯波克","莱昂纳德·麦考伊","蒙哥马利·史考特","苏鲁","帕维尔·安德烈维奇·契科夫","尼欧塔·乌胡拉","让-卢克·皮卡德","威廉·T·瑞克","data"});
        kind.setBans(new CharSequence[]{"苏鲁","data"});
        kind.setClickListener(new KindView.ClickListener() {
            @Override
            public void onClick(Object key) {
                Log.d("HomeActivity","onClick:key="+ key);
            }
        });
        final TextView textView = findViewById(R.id.textView);
        //simulate(textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this,textView.getText().toString().trim(),Toast.LENGTH_SHORT).show();
            }
        });
        surface = findViewById(R.id.surface);
        holder = surface.getHolder();
        holder.addCallback(getCallback());
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//surfaceview不维护自己的缓冲区，等待屏幕渲染引擎将内容推送到用户面前
        ((FiltrateView)findViewById(R.id.filtrate)).setClickListener(new FiltrateView.ClickListener() {
            @Override
            public void onClick(String s) {

            }
        });
        //PhoneUtils.getPhoneInfo(this);
    }
    private SurfaceHolder.Callback getCallback(){
        return new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if(camera == null) {
                    camera = Camera.open();
                    try {
                        camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                        camera.startPreview();//开始预览
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                camera.stopPreview();
                camera.release();
                camera = null;
                holder = null;
                surface = null;
            }
        };
    }
    private void simulate(TextView textView){
        //Android代码模拟物理、屏幕点击事件 https://blog.csdn.net/hp910315/article/details/52106834
        //Android 根据坐标获取控件方法 https://blog.csdn.net/chenlove1/article/details/42709127
        //android根据坐标获取相应控件，判断点是否在控件上 https://blog.csdn.net/liufang1991/article/details/44303113
        //Android 根据坐标获取控件方法 http://www.bkjia.com/Androidjc/943474.html
        Long downTime = SystemClock.uptimeMillis();
        MotionEvent downEvent = MotionEvent.obtain(downTime,downTime,MotionEvent.ACTION_DOWN,textView.getX(),textView.getY(),0);
        MotionEvent upEvent = MotionEvent.obtain(downTime,downTime,MotionEvent.ACTION_UP,textView.getX(),textView.getY(),0);
        textView.onTouchEvent(downEvent);
        textView.onTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();
    }
    private void change(){
        //切换前后摄像头
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数

        for(int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if(cameraPosition == 1) {
                //现在是后置，变更为前置
                if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    camera.stopPreview();//停掉原来摄像头的预览
                    camera.release();//释放资源
                    camera = null;//取消原来摄像头
                    camera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    camera.startPreview();//开始预览
                    cameraPosition = 0;
                    break;
                }
            } else {
                //现在是前置， 变更为后置
                if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    camera.stopPreview();//停掉原来摄像头的预览
                    camera.release();//释放资源
                    camera = null;//取消原来摄像头
                    camera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    camera.startPreview();//开始预览
                    cameraPosition = 1;
                    break;
                }
            }
        }
        camera.setDisplayOrientation(90);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        switch (itemId){
            case android.R.id.home:
                break;
        }
        return true;
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
