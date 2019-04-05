package com.stynet.map.assist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.stynet.map.R;

/**
 * Created by xx shuiDianBing, 2018/12/06-15:52:15:52.Refer to the website: nullptr
 * android 定位更新 （Google LocationRequest）https://www.cnblogs.com/CharlesGrant/p/8242413.html
 * Google Play服务版本12.0.0之前可用
 **/

public class LocationApi {
    private static final String TAG = LocationApi.class.getName();
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationListener locationListener;
    public LocationApi(final Activity activity,final LocationListener listener){
        googleApiClient = new GoogleApiClient.Builder(activity).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                if(ConnectionResult.SERVICE_MISSING == connectionResult.getErrorCode())// 裝置沒有安裝Google Play服務
                    Toast.makeText(activity, activity.getString(R.string.uninstalledGooglePlayService), Toast.LENGTH_SHORT).show();
            }
        }).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @SuppressLint("MissingPermission")
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                if(googleApiClient.isConnected()) {
                    Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener = listener);
                }
            }

            @Override
            public void onConnectionSuspended(int i) {}
        }).addApi(LocationServices.API).addApi(Places.PLACE_DETECTION_API).addApi(Places.GEO_DATA_API).build();
        locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setSmallestDisplacement(1).
                setInterval(0xfa0).setFastestInterval(0x3e8);
    }
    @SuppressLint("MissingPermission")
    public void requestLocationUpdate(){
        if(null != googleApiClient && null != locationRequest && null != locationListener && googleApiClient.isConnected())
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,locationListener);
    }
    @SuppressLint("MissingPermission")
    public void requestLocation(){
        if(googleApiClient.isConnected())
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,locationListener);
        else if(googleApiClient.isConnecting())
            Log.i(TAG,"连接中...");
        else
            googleApiClient.connect();
    }
    @SuppressLint("MissingPermission")
    public void stopUpdate(){
        if(googleApiClient.isConnected())
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,locationListener);
    }
    public void stopLocation(){
        if(googleApiClient.isConnected()|| googleApiClient.isConnecting())
            googleApiClient.disconnect();
    }
    public boolean isConnected(){
        return null != googleApiClient && googleApiClient.isConnected();
    }
}
