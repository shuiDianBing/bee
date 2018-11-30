package com.stynet.map.view;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.stynet.map.R;
import com.stynet.map.service.LocationService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xx shuiDianBing, 2018/11/27-10:37:10:37.Refer to the website: nullptr
 *
 **/

public class ActivityMap extends AppCompatActivity {
    private BroadcastReceiver receiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //applyPermission(0x0,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION);
//        registerReceiver(receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Location location = intent.getParcelableExtra(LocationService.LOCATION);
//                Log.d("ActivityGoogleMap","onReceive<<location:"+ location);
//            }
//        }, new IntentFilter(LocationService.ACTION));
//        startService(new Intent(this, LocationService.class));
    }

    /**
     * 未获得权限提出申请
     * @param permissions
     */
    protected void applyPermission(int requestCode,String... permissions){
        if(Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            List<String> permissionList = new ArrayList<>();
            for (String permission : permissions)
                if(PackageManager.PERMISSION_DENIED == checkPermission(permission, Binder.getCallingPid(), Binder.getCallingUid()))//or ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    permissionList.add(permission);
            if(0< permissionList.size())
                requestPermissions(permissionList.toArray(new String[permissionList.size()]), requestCode);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int index = 0; index < grantResults.length; index++)
            if (0 < grantResults.length && PackageManager.PERMISSION_DENIED == grantResults[index])
                Log.d("ActivityMap", "onRequestPermissionsResult.The permission was not obtained:" + permissions[index]);
        switch (requestCode) {
            case 0x0:
                //if(!((LocationManager) getSystemService(LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER))//检测gps是否开启
                //    displayActionDialog(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),R.drawable.icon_ring_down,R.string.warmPrompt,R.string.whetherTheJumpSettingTurnsOnGPS);//提示打开gps
            break;
        }
    }
    /**
     * 带有目的dialog(比如不限于跳转开启权限or设置)
     * @param intent
     * @param requestCode
     * @param iconId
     * @param titleId
     * @param message
     */
    protected void displayActionDialog(final Intent intent,final int requestCode, @DrawableRes int iconId, @StringRes int titleId, @StringRes int message){
            DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            startActivityForResult(intent, requestCode);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE: break;
                    }
                }
            };
        new AlertDialog.Builder(this).setIcon(iconId).setTitle(titleId).setMessage(message).setNegativeButton(R.string.cancel,clickListener).
                setPositiveButton(R.string.confirm,clickListener).create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Intent intent = new Intent(LocationService.class.getName());
//        intent.setPackage(getPackageName());
//        stopService(intent);
//        if(null != receiver)
//            unregisterReceiver(receiver);
    }
}
