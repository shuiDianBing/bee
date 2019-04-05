package com.stynet.map.assist;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import androidx.annotation.RequiresPermission;
import android.util.Log;

import com.stynet.map.R;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by xx shuiDianBing, 2018/11/30-16:28:16:28.Refer to the website: nullptr
 **/

public class LocationServer {
    private final String TAG = LocationServer.class.getName();
    private LocationManager locationManager;
    private LocationListener locationListener;
    private float minDistance = 0;//位置更新之间的最小距离，单位为米
    private int rest = 0x3e8;//默认1000毫秒，定位间隔

    public LocationServer(LocationManager locationManager, float minDistance, int rest) {
        this.locationManager = locationManager;
        this.minDistance = minDistance;
        this.rest = rest;
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);//海拔
        criteria.setCostAllowed(true);//允许产生资费
        criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);//设置方位精度参数：Criteria.NO_REQUIREMENT 无, Criteria.ACCURACY_LOW 低, Criteria.ACCURACY_HIGH 高
        criteria.setBearingRequired(true);//指示是否要求方位信息
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);//设置水平方向精准度
        criteria.setVerticalAccuracy(Criteria.ACCURACY_FINE);//设置垂直方向精准度
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);//设置电池消耗要求 参数：Criteria.NO_REQUIREMENT 无, Criteria.POWER_LOW 低, Criteria.POWER_MEDIUM 中, Criteria.POWER_HIGH 高。
        criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);//设置速度精度
        criteria.setSpeedRequired(true);//是否要求速度信息
        locationManager.getBestProvider(criteria,true);
    }
    @SuppressLint("MissingPermission")
    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void gps(){
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, rest, minDistance,locationListener);
        else
            Log.e(TAG,"gps<<未开启gps");
    }
    @SuppressLint("MissingPermission")
    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void worknet(){
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, rest, minDistance, locationListener);
        else
            Log.e(TAG,"worknet<<未开启网络定位");
    }
    @SuppressLint("MissingPermission")
    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void gpsWorknet(){
        gps();
        worknet();
    }

    /**
     *
     * @param context
     * @param intent
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void serviceLocation(Context context,Intent intent){
        Notification.Builder builder = new Notification.Builder(context).setContentIntent(PendingIntent.getService(context,0,intent,0)).
                setContentTitle(context.getResources().getString(R.string.location)).// 设置下拉列表里的标题
                setSmallIcon(R.drawable.icon_location).// 设置状态栏内的小图标
                setContentText(context.getResources().getString(R.string.backgroundPositioning)).//设置上下文内容
                setAutoCancel(true).
                setWhen(System.currentTimeMillis());// 设置该通知发生的时间
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
    }
    public void stopLocation(){
        locationManager.removeUpdates(locationListener);
    }

    public void setLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }
}
