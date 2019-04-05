package com.stynet.map.assist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.stynet.map.R;

/**
 * Created by xx shuiDianBing, 2018/12/06-13:45:13:45.Refer to the website: nullptr
 * android 定位更新 （Google LocationRequest）https://www.cnblogs.com/CharlesGrant/p/8242413.html
 * Android的LocationServices.FusedLocationApi已弃用 http://androidcookie.com/androidlocationservices-fusedlocationapi.html
 * 请继续使用FusedLocationProviderApi类，并且不要迁移到FusedLocationProviderClient类，直到Google Play服务版本12.0.0可用
 * （预计将在2018年初发布）。在版本12.0.0之前使用FusedLocationProviderClient会导致客户端应用程序在Google Play服务已在设备上更新
 * Google Play服务版本12.0.0可用
 **/

public final class LocationFused {
    private static final String TAG = LocationFused.class.getName();
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    @SuppressLint("RestrictedApi")
    public LocationFused(final Activity activity, LocationCallback locationCallback){
        googleApiClient = new GoogleApiClient.Builder(activity).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {//连接google服务失败监听器
                if(ConnectionResult.SERVICE_MISSING == connectionResult.getErrorCode())// 裝置沒有安裝Google Play服務
                    Toast.makeText(activity, activity.getString(R.string.uninstalledGooglePlayService), Toast.LENGTH_SHORT).show();
                else if (connectionResult.hasResolution())
                    try {
                        connectionResult.startResolutionForResult(activity, activity.RESULT_CANCELED);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                else
                    Toast.makeText(activity, activity.getString(R.string.connectionFail), Toast.LENGTH_SHORT).show();
            }
        }).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {}

            @Override
            public void onConnectionSuspended(int i) {}
        }).addApi(LocationServices.API).addApi(Places.PLACE_DETECTION_API).addApi(Places.GEO_DATA_API).build();
        locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setSmallestDisplacement(1).
                setInterval(0xfa0).setFastestInterval(0x3e8);
        this.locationCallback = locationCallback;
        requestLocationUpdate(activity);
    }
    @SuppressLint({"RestrictedApi", "MissingPermission"})
    public void requestLocationUpdate(final Activity activity){
        if(null == fusedLocationProviderClient){
            fusedLocationProviderClient = new FusedLocationProviderClient(activity);
            LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
            SettingsClient settingsClient = LocationServices.getSettingsClient(activity);
            settingsClient.checkLocationSettings(locationSettingsRequest).addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    if(null != fusedLocationProviderClient)
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.myLooper());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    switch (((ApiException)e).getStatusCode()){
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade location settings ");
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(activity, activity.RESULT_FIRST_USER);
                            } catch (IntentSender.SendIntentException e1) {
                                e1.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Toast.makeText(activity, "Location settings are inadequate, and cannot be fixed here. Fix in Settings.", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            });
        }
        if(googleApiClient.isConnected())
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
        else if(googleApiClient.isConnecting())
            Log.i(TAG,"连接中...");
        else
            googleApiClient.connect();
    }
    @SuppressLint("MissingPermission")
    public void startLocationUpdates(){
        if(null != fusedLocationProviderClient)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.myLooper());
    }
    public void stopUpdate(LocationListener locationListener){
        if(googleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
    }
    public void stopLocation(){
        if(null != fusedLocationProviderClient)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        if(googleApiClient.isConnected()|| googleApiClient.isConnecting())
            googleApiClient.disconnect();
    }
    public void recycleResource(){
        if(googleApiClient.isConnected())
            googleApiClient.disconnect();
        fusedLocationProviderClient = null;
    }
}
