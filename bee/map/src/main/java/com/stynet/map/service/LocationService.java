package com.stynet.map.service;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.stynet.map.R;
import com.stynet.map.assist.Geocoding;

/**
 * Created by xx shuiDianBing, 2018/08/27-14:59:14:59.Refer to the website: nullptr
 * Android Service两种启动方式详解（总结版） https://www.jianshu.com/p/4c798c91a613
 * Android学习之Activity与Service进行通信的三种方式 https://blog.csdn.net/qq_28468727/article/details/52144155
 * Android 之 Service（一）启动，绑定服务 https://www.jianshu.com/p/5d73389f3ab2
 **/

public class LocationService extends Service {
    public static final String ACTION = LocationService.class.getName();
    public static final String LOCATION = "location";
    private LocationListener locationListener;//与activity绑定监听
    private LocationManager locationManager;
    private Location place;
    private float minDistance = 0;//位置更新之间的最小距离，单位为米
    private int rest = 0x3e8;//默认1000，定位间隔
    private  android.location.LocationListener gpsListener = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(Geocoding.isBetterLocation(location,place))
                place = location;
            if(null != place)
                pushout(place);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };
    private android.location.LocationListener worknetListener = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(Geocoding.isBetterLocation(location,place))
                place = location;
            if(null != place)
                pushout(place);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };
    public class LocationBinder extends Binder {
        public LocationService getService(LocationListener locationListener,int locationRest,float distance){
            LocationService.this.locationListener = locationListener;
            rest = locationRest;
            minDistance = distance;
            return LocationService.this;
        }
    }
    public interface LocationListener{
        void pushout(Location location);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocationBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = ((LocationManager) getSystemService(LOCATION_SERVICE));
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);//海拔
        criteria.setCostAllowed(true);//允许产生资费
        locationManager.getBestProvider(criteria,true);
        //String provider =  DeviceExplore.autoProvider(this);
        //if(null != provider) {
            if (checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, Binder.getCallingPid(), Binder.getCallingUid()) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, rest, minDistance,gpsListener);
                else
                    Log.e(ACTION,"未开启gps");
                if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, rest, minDistance, worknetListener);
                else
                    Log.e(ACTION,"未开启网络定位");
                //if(null!= locationManager.getLastKnownLocation(provider))//获取的不是最新的位置信息
                //    pushout(locationManager.getLastKnownLocation(provider));
            }else
                Log.e(ACTION,"To obtain location permissions");
        //}else
        //    Log.e(ACTION,"Please turn on GPS or network location");
    }

    /**
     * 推出location
     * @param location
     */
    private void pushout(Location location){
        if(null == locationListener){
            Intent intent = new Intent(ACTION);
            intent.putExtra(LOCATION, location);
            //LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            sendBroadcast(intent);
        }else
            locationListener.pushout(location);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void serviceLocation(){
        Intent intent = new Intent(this,LocationService.class);
        Notification.Builder builder = new Notification.Builder(this).setContentIntent(PendingIntent.getService(this,0,intent,0)).
                setContentTitle(getResources().getString(R.string.location)).// 设置下拉列表里的标题
                setSmallIcon(R.drawable.icon_location).// 设置状态栏内的小图标
                setContentText(getResources().getString(R.string.backgroundPositioning)).//设置上下文内容
                setAutoCancel(true).
                setWhen(System.currentTimeMillis());// 设置该通知发生的时间
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
    }

    /**
     * --------START_NOT_STICKY
     * 如果返回START_NOT_STICKY，表示当Service运行的进程被Android系统强制杀掉之后，不会重新创建该Service
     * ---------START_STICKY
     * 如果返回START_STICKY，表示Service运行的进程被Android系统强制杀掉之后，Android系统会将该Service依然设置为started状态（即运行状态），
     * 但是不再保存onStartCommand方法传入的intent对象，然后Android系统会尝试再次重新创建该Service，并执行onStartCommand回调方法，但是onStartCommand回调方法
     * 的Intent参数为null，也就是onStartCommand方法虽然会执行但是获取不到intent信息。如果你的Service可以在任意时刻运行或结束都没什么问题，而且不需要intent信息，
     * 那么就可以在onStartCommand方法中返回START_STICKY，比如一个用来播放背景音乐功能的Service就适合返回该值。
     * ---------START_REDELIVER_INTENT
     * 如果返回START_REDELIVER_INTENT，表示Service运行的进程被Android系统强制杀掉之后，与返回START_STICKY的情况类似，Android系统会将再次重新创建该Service，
     * 并执行onStartCommand回调方法，但是不同的是，Android系统会再次将Service在被杀掉之前最后一次传入onStartCommand方法中的Intent再次保留下来并再次传入到重新创建后的
     * Service的onStartCommand方法中，这样我们就能读取到intent参数。只要返回START_REDELIVER_INTENT，那么onStartCommand重的intent一定不是null。如果我们的Service
     * 需要依赖具体的Intent才能运行（需要从Intent中读取相关数据信息等），并且在强制销毁后有必要重新创建运行，那么这样的Service就适合返回START_REDELIVER_INTENT。
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null != locationManager){
           locationManager.removeUpdates(gpsListener);
           locationManager.removeUpdates(worknetListener);
        }
    }

    public void setLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }
}
