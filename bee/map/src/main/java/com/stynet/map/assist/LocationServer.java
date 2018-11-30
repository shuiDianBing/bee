package com.stynet.map.assist;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import com.stynet.map.R;
import com.stynet.map.service.LocationService;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by xx shuiDianBing, 2018/11/30-16:28:16:28.Refer to the website: nullptr
 **/

public class LocationServer {
    private final String TAG = LocationServer.class.getName();
    private LocationManager locationManager;
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
        locationManager.getBestProvider(criteria,true);
    }
    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void gps(LocationListener listener){
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, rest, minDistance,listener);
        else
            Log.e(TAG,"gps<<未开启gps");
    }
    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void worknet(LocationListener listener){
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, rest, minDistance, listener);
        else
            Log.e(TAG,"worknet<<未开启网络定位");
    }
    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void gpsWorknet(LocationListener listener){
        gps(listener);
        worknet(listener);
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
}
