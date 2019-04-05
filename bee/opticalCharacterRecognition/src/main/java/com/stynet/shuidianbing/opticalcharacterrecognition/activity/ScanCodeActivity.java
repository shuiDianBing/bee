package com.stynet.shuidianbing.opticalcharacterrecognition.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.stynet.shuidianbing.opticalcharacterrecognition.CameraPreview;
import com.stynet.shuidianbing.opticalcharacterrecognition.R;
import com.stynet.shuidianbing.opticalcharacterrecognition.ScanCallback;
import com.stynet.shuidianbing.opticalcharacterrecognition.permission.AndroidPermission;
import com.stynet.shuidianbing.opticalcharacterrecognition.permission.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shuiDianBing on 2018/5/7.
 */

public class ScanCodeActivity extends AppCompatActivity {
    private static final String TAG = "ScanCodeActivity";
    public static final String TITLE = "title";
    public static final String SCANrESULT ="scanResult";
    private static final int PHOTO = 0;
    //private PowerManager.WakeLock wakeLock;
    private ValueAnimator scanAnimator;
    private CameraPreview previewView;
    private ImageView scanLine;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Android 禁止屏幕休眠和锁屏的方法 https://blog.csdn.net/chenyafei617/article/details/6575621
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止休眠.方式1
        //android.provider.Settings.System.putInt(getContentResolver(),android.provider.Settings.System.LOCK_PATTERN_ENABLED, enabled ?1:0);//不建议使用
        //这是因为android.permission.WRITE_SECURE_SETTINGS是系统限制的权限。所以apk需要是系统的apk （/system/app下才可以）。可以把apk 通过 adb push “你的apk” system/app  或者 修改mk文件将其编入system镜像
        //android.provider.Settings.Secure.putInt(getContentResolver(),android.provider.Settings.Secure.LOCK_PATTERN_ENABLED,1);//禁止休眠.方式3(系统权限)
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//Android开发(33) 透明漂浮的Actionbar https://www.jianshu.com/p/d705befeaeb5
        setContentView(R.layout.activity_scan_code);
        String title = getIntent().getStringExtra(TITLE);
        setTitle(null== title ? getString(R.string.scanQrCode): title);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        actionBar.setSplitBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        previewView = findViewById(R.id.cameraPreview);
        previewView.setScanCallback(new ScanCallback() {
            @Override
            public void onScanResult(String result) {
                scanAnimator.cancel();
                previewView.stop();
                Intent intent = new Intent();
                intent.putExtra(SCANrESULT,result);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        scanLine = findViewById(R.id.iamge_scanLine);
        RecyclerView recycler = findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL));
        //recycler.setAdapter(new ScanCodeSelectAdapter(initScanCodeSelect(R.array.scanCodeIcons,R.array.scanText),R.layout.item_up_image_below_text, ContextCompat.getColor(this,android.R.color.transparent)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(RESULT_OK == resultCode)
            switch (requestCode){
                case PHOTO:
                    if(null!= data &&null!= data.getData())
                        try {
                            Intent intent = new Intent();
                            intent.putExtra(SCANrESULT,previewView.scanPhoto(MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData())));
                            setResult(RESULT_OK,intent);
                            finish();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    break;
                    default:break;
            }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);
        //wakeLock.acquire();//禁止休眠第1步(添加权限android.permission.WAKE_LOCK)
        if(null!= scanAnimator)
            startScanUnKnowPermission();
    }
    @Override
    public void onPause() {
        scanAnimator.cancel();
        previewView.stop();
        super.onPause();
        //if(null != wakeLock)//禁止休眠第2步(添加权限android.permission.WAKE_LOCK)
        //    wakeLock.release();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (null== scanAnimator) {
            scanAnimator = ObjectAnimator.ofFloat(scanLine, "translationY", 0F, getWindow().getDecorView().getHeight()-64.0f).setDuration(4000);//ObjectAnimator详解 https://blog.csdn.net/xiaochuanding/article/details/73290917
            scanAnimator.setInterpolator(new LinearInterpolator());
            scanAnimator.setRepeatCount(ValueAnimator.INFINITE);
            scanAnimator.setRepeatMode(ValueAnimator.REVERSE);
            startScanUnKnowPermission();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        switch (itemId){
            case android.R.id.home:
                finish();
                break;
            default:
                if(itemId == R.id.lighting)
                    previewView.openLighting();
                else if(itemId == R.id.changeCamera)
                    previewView.alterCamera();//.changeCamera();
                if(itemId == R.id.photoLibrary)
                    skipPhotoLibrary();
                break;
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan_code,menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void skipPhotoLibrary(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,PHOTO);
    }
    private void startScanUnKnowPermission(){
        AndroidPermission.with(this).permission(Manifest.permission.CAMERA).callback(new PermissionListener() {
            @Override
            public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                if(previewView.start())
                    scanAnimator.start();
                else
                    new AlertDialog.Builder(ScanCodeActivity.this).setTitle(R.string.camera_failure).setMessage(R.string.camera_hint).setCancelable(false).setPositiveButton
                            (R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
            }

            @Override
            public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                AndroidPermission.defaultSettingDialog(ScanCodeActivity.this).show();
            }
        }).start();
    }
    private List<CharSequence[]> initScanCodeSelect(@ArrayRes int iconIds, @ArrayRes int keyIds){
        String resource = "android.resource://"+getPackageName()+"/";
        List<CharSequence[]> list = new ArrayList<>();
        TypedArray icons = getResources().obtainTypedArray(iconIds);
        TypedArray titles = getResources().obtainTypedArray(keyIds);
        if(null != icons && null != titles) {
            int iconsLen = icons.length();
            int titlesLen = titles.length();
            int lenght =  iconsLen <= titlesLen ? iconsLen : titlesLen;
            for (int index = 0; index < lenght; index++)
                list.add(new String[]{resource +icons.getResourceId(index, 0), getString(titles.getResourceId(index, 0))});
        }
        return list;
    }
}
