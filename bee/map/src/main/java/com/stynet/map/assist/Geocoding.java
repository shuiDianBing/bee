package com.stynet.map.assist;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by xx shuiDianBing, 2018/11/29-19:03:19:03.Refer to the website: nullptr
 **/

public final class Geocoding {

    /**
     * 获取本地
     * android定位的实现 https://www.jianshu.com/p/a20bde066ce3
     * @param context
     * @return
     */
    public static final Location getLocation(Context context){
        LocationManager systemService = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 返回所有已知的位置提供者的名称列表，包括未获准访问或调用活动目前已停用的。
        //List<String> lp = systemService.getAllProviders();
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(false);
        //设置位置服务免费
        criteria.setAccuracy(Criteria.ACCURACY_COARSE); //设置水平位置精度
        //getBestProvider 只有允许访问调用活动的位置供应商将被返回
        String  providerName = systemService.getBestProvider(criteria, true);
        return null == providerName ? null : systemService.getLastKnownLocation(providerName);
    }
    /**
     * 逆地理编码 得到地址
     * 【从 0 开始开发一款直播 APP】15 Android 定位详解之 LocationManager & Geocoder 实现直播定位 https://www.jianshu.com/p/2976f51408c5
     * demo例子 https://github.com/angelOnly/LiveStreaming
     * @param context
     * @param latitude
     * @param longitude
     * @return ●
     */
    public static final Address getAddress(Context context,double latitude,double longitude){
        Geocoder geocoder = new Geocoder(context,Locale.getDefault());
        geocoder.isPresent();
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            geocoder.isPresent();
            if(0< addresses.size()) {
                Log.i("location", "current position" + addresses + "'\n"
                        + " longitude<<" + String.valueOf(addresses.get(0).getLongitude()) + "\n"
                        + "latitude<<" + String.valueOf(addresses.get(0).getLatitude()) + "\n"
                        + "country<<" + addresses.get(0).getCountryName() + "\n"
                        + "city<<" + addresses.get(0).getLocality() + "\n"
                        + "nam<<" + addresses.get(0).getAddressLine(1) + "\n"
                        + "street<<" + addresses.get(0).getAddressLine(0));
                return addresses.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Determines whether one Location reading is better than the current
     * Location fix
     * @param location
     *            The new Location that you want to evaluate
     * @param place
     *            The current Location fix, to which you want to compare the new
     *            one
     * android定位的实现 https://www.jianshu.com/p/a20bde066ce3
     * isBetterLocation(Location location, Location currentBestLocation)方法是参考的Google官方提供的,可自行查看
     * https://link.jianshu.com/?t=https%3A%2F%2Fdeveloper.android.com%2Fguide%2Ftopics%2Flocation%2Fstrategies.html
     */
    public static final boolean isBetterLocation(Location location,Location place){
        if(null == place)
            return true;
        // Check whether the new location fix is newer or older
        long timeDalta = location.getTime()- place.getTime();
        boolean isSignificatlyNewer = timeDalta > 0x1d4c0;
        boolean isSignificantlyOlder = timeDalta < -0x1d4c0;
        boolean isNewer = timeDalta >0;
        // If it's been more than two minutes since the current location, use
        // the new location
        // because the user has likely moved
        if(isSignificatlyNewer)
            return true;
        else if(isSignificantlyOlder)// If the new location is more than two minutes older, it must be
            return false;
        // Check whether the new location fix is more or less accurate
        int accuracyDelta  = (int)(location.getAccuracy() - place.getAccuracy());
        boolean isLessAccurate = accuracyDelta >0;
        boolean isMoreAccurate = accuracyDelta <0;
        boolean isSignificantlyLessAccurate  = accuracyDelta > 0xb8;
        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(), place.getProvider());
        // Determine location quality using a combination of timeliness and
        // accuracy
        if(isMoreAccurate)
            return true;
        else if(isNewer && !isLessAccurate)
            return true;
        else if(isNewer &&! isSignificantlyLessAccurate && isFromSameProvider)
            return true;
        return false;
    }

    /**
     * Checks whether two providers are the same
     * @param provider1
     * @param provider2
     * @return
     */
    private static final boolean isSameProvider(String provider1, String provider2){
        if(null == provider1)
            return null == provider2;
        return provider1.equals(provider2);
    }
}
