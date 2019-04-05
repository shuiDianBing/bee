package com.stynet.map.assist;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import androidx.core.content.ContextCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by xx shuiDianBing, 2018/11/30-09:47:09:47.Refer to the website: nullptr
 *
 **/

public final class DeviceExplore {
    private static final String TAG = DeviceExplore.class.getName();
    /**
     * check if it has any gps provider检测是否有gps模块
     * @param context
     * @return
     */
    public boolean isHasGpsModule(Context context){
        LocationManager locationManager = (LocationManager)context.getSystemService(context.LOCATION_SERVICE);
        return null != locationManager ? null != locationManager.getAllProviders()&&
                locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)?true:false:false;
    }

    /**
     * Automatic priority acquisition gps > worknet
     * 自动优先获取
     * @param context
     * @return
     */
    public static final String autoProvider(Context context){
        List<String> providers =((LocationManager)context.getSystemService(context.LOCATION_SERVICE)).getProviders(true);
        return providers.contains(LocationManager.GPS_PROVIDER)? LocationManager.GPS_PROVIDER ://gps
            providers.contains(LocationManager.NETWORK_PROVIDER)? LocationManager.NETWORK_PROVIDER://worknet
            null;
    }
    /**
     * check if gps opened 检测是否打开gps
     * @param context
     * @return
     */
    public boolean checkGpsOpen(Context context){
        return ((LocationManager)context.getSystemService(context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 手动打开gps
     */
    public static void openGPSsettings(final Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)||//通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){//通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
            Log.e(TAG,"GPS未开启");
            activity.startActivityForResult(new Intent(Settings.ACTION_SECURITY_SETTINGS), activity.RESULT_CANCELED); // 此为设置完成后返回到获取界面
        }
        Log.e(TAG,"GPS模块正常");
    }
    public static final void openGps(final Context context){
        ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE)).setTestProviderEnabled(android.location.LocationManager.GPS_PROVIDER,true);
    }
    /**
     * 强制帮用户打开GPS--->方式1
     * Android中 跳转到系统设置界面方法总结 https://blog.csdn.net/da_caoyuan/article/details/72829106
     * Android中跳转到系统一些设置界面的方法 https://blog.csdn.net/fengyulinde/article/details/78726473
     * 安卓情景模式开发（一）－控制GPS/WIFI/蓝牙/飞行模式 https://www.cnblogs.com/wii/archive/2012/03/18/2404947.html
     * android设置gps自动开启 https://www.2cto.com/kf/201206/137623.html
     * @param context
     */
    public static final void openGPS(final Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");//GPSIntent.CATEGORY_ALTERNATIVE
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
            Log.e(TAG,"openGPS<<GPS服务强制开启");
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    /**
     * 强制帮用户打开GPS--->方式2
     * settings的使用说明 https://www.jianshu.com/p/c6a4af8b0991
     * @param context
     * 在androidManilfest的manifest中添加android:sharedUserId="android.uid.system"，android.uid.shell
     * 然后添加<uses-permission android:name="android.permission.WRITE_SECURE_SETTINGS"/>
     */
    public static final void positiveOpenGpsSettings(final Context context){
        boolean gpsEnabled = Settings.Secure.isLocationProviderEnabled(context.getContentResolver(), LocationManager.GPS_PROVIDER);//获取GPS现在的状态（打开或是关闭状态）
        Settings.Secure.setLocationProviderEnabled(context.getContentResolver(),LocationManager.GPS_PROVIDER,true);//打开gps
    }
    public static final<Type> void x(final Context context,Type type){}
    public static final void unknowm(final Context context,Class<?> type){ }
    /**
     * Android网络定位源码分析[原创] https://www.jianshu.com/p/9384c535d634
     * */
    public static final void tipster(final Context context,Location location){
        //if(null != location &&(1e-8< location.getLongitude()|| 1e-8< location.getLatitude()))return;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        StringBuilder json = new StringBuilder("{").append("\"time\":\"").append(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date(System.currentTimeMillis()))).
                append("\",\"brank\":\"").append(android.os.Build.BRAND).append("\",\"model\":\"").append(Build.MODEL).append("\",\"network\":\"").
                append(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).append("\",\"gps\":\"").
                append(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).append("\",");
        if(Build.VERSION_CODES.M <= Build.VERSION.SDK_INT)
            json.append("\"permission\":\"" +(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    + context.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, Binder.getCallingPid(), Binder.getCallingUid()))).append("\",");
        if(null != location)
            json.append("\"latitude\":\"").append(location.getLatitude()).append("\",\"longitude\":\"").append(location.getLongitude()).append("\"");
        else
            json.append("\"location\":\"null\"");
        json.append("}");
        Log.d(TAG, "tipster<<json=" + json);
    }
}
