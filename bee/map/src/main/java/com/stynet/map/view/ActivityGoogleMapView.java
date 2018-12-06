package com.stynet.map.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.stynet.map.R;

/**
 * Created by xx shuiDianBing, 2018/11/28-18:30:18:30.Refer to the website: nullptr
 * googleMap基础只有定位功能
 * Android GoogleMap不完全指南 （一）https://www.jianshu.com/p/574919db97c0
 * Android GoogleMap不完全指南 （二）https://www.jianshu.com/p/cfe784fe13d7
 * Android GoogleMap不完全指南 （三）https://www.jianshu.com/p/d1ff89526b60
 * demo https://gitee.com/amqr/gmap
 * Android利用谷歌地图获取并解析经纬度对应的地理位置 https://blog.csdn.net/csdn319414/article/details/78563936
 * 集成GoogleMap,实现定位和获取位置信息 https://github.com/Linyaodai/MyLocationDemo
 **/

public class ActivityGoogleMapView extends ActivityMap {
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private MapView mapView;
    private GoogleMap mGoogleMap;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Location location;
    private boolean locationPermission;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map0);
        mapView = findViewById(R.id.mapView);
        Bundle mapViewBundle = null;
        if (null != savedInstanceState)
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        mapView.onCreate(mapViewBundle);
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, Binder.getCallingPid(), Binder.getCallingUid()) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationPermission = true;
        else
            applyPermission(0x0, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                if (locationPermission)
                    googleMap.setMyLocationEnabled(true);
                mGoogleMap = googleMap;
                googleMap.setTrafficEnabled(true);
                googleMap.setIndoorEnabled(true);
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.getUiSettings().setZoomControlsEnabled(true);//手势缩放
                googleMap.getUiSettings().setScrollGesturesEnabled(true);//手势拖动
                googleMap.getUiSettings().setMapToolbarEnabled(false);//禁用精简模式下右下角的工具栏
                googleMap.animateCamera(CameraUpdateFactory.zoomBy(16));//缩放级别
                LatLng latlng = new LatLng(37.984443, 23.733131);//3.121139,101.675245);
                googleMap.addMarker(new MarkerOptions().position(latlng).zIndex(0.0f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_side_brain)).
                        anchor(0.5f, 1).infoWindowAnchor(0.5f, 0).title("default").snippet("lat = 37.984443,lng = 23728190").draggable(true).visible(true).flat(false).
                        rotation(0).alpha(1));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        moveLocationPlace(location);
                        return false;
                    }
                });
                googleMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
                    @Override
                    public void onMyLocationClick(@NonNull Location location) {
                        moveLocationPlace(location);
                    }
                });
            }
        });
        locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setSmallestDisplacement(1).
                setInterval(0xfa0).setFastestInterval(0x3e8);
        //if(!((LocationManager) getSystemService(LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER))//检测gps
        //    displayActionDialog(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),RESULT_CANCELED,R.drawable.icon_ring_down,R.string.warmPrompt,R.string.whetherTheJumpSettingTurnsOnGPS);//提示打开gps
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @SuppressLint("MissingPermission")
            @Override
            public void onConnected(@Nullable Bundle bundle) {// 异步进行连接，获取位置 (关键方法)
                if (locationPermission) {
                    /*
                    * Android的LocationServices.FusedLocationApi已弃用 http://androidcookie.com/androidlocationservices-fusedlocationapi.html
                    * 请继续使用FusedLocationProviderApi类，并且不要迁移到FusedLocationProviderClient类，直到Google Play服务版本12.0.0可用
                    * （预计将在2018年初发布）。在版本12.0.0之前使用FusedLocationProviderClient会导致客户端应用程序在Google Play服务已在设备上更新
                    * android 定位更新 （Google LocationRequest）https://www.cnblogs.com/CharlesGrant/p/8242413.html
                    * 如何解决无法使用设备开启位置权限？ https://cloud.tencent.com/developer/ask/129350
                    * */
                    moveLocationPlace(LocationServices.FusedLocationApi.getLastLocation(googleApiClient));
                    //if (googleApiClient.isConnected())//start location
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            moveLocationPlace(location);
                        }
                    });
//                    LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, new LocationListener() {
//                        @Override
//                        public void onLocationChanged(Location location) {}
//                    });
                    Task<Location> task = LocationServices.getFusedLocationProviderClient(ActivityGoogleMapView.this).getLastLocation();
                    if(null != task && task.isSuccessful())
                        moveLocationPlace(task.getResult());
                    LocationServices.getFusedLocationProviderClient(ActivityGoogleMapView.this).requestLocationUpdates(locationRequest,new LocationCallback(){
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            if(null != locationResult && null != locationResult.getLastLocation())
                                moveLocationPlace(locationResult.getLastLocation());
                        }

                        @Override
                        public void onLocationAvailability(LocationAvailability locationAvailability) {
                            super.onLocationAvailability(locationAvailability);
                        }
                    }, Looper.myLooper());
                }
            }

            @Override//当客户端断开
            public void onConnectionSuspended(int i) {}
        }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {//连接失败时调用
                if (connectionResult.hasResolution())
                    try {
                        connectionResult.startResolutionForResult(ActivityGoogleMapView.this, RESULT_CANCELED);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                else
                    Toast.makeText(ActivityGoogleMapView.this, getString(R.string.connectionFail), Toast.LENGTH_SHORT).show();
            }
        }).addApi(LocationServices.API).addApi(Places.PLACE_DETECTION_API).addApi(Places.GEO_DATA_API).build();
    }
    /**
     * 定位
     */
    private void moveLocationPlace(final Location location) {
        if (null != location) {
            this.location = location;
            mGoogleMap.clear();
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.addMarker(new MarkerOptions().position(latlng).zIndex(0.0f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_circle_brain)).
                    anchor(0.5f, 1).infoWindowAnchor(0.5f, 0).title("pace").snippet("lat = " + location.getLatitude() + ",lng = " +
                    location.getLongitude()).draggable(true).visible(true).flat(false).rotation(0).alpha(1));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        } else
            Log.d("ActivityGoogleMapView", "moveLocationPace<<null== location ");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (null == mapViewBundle)
            outState.putBundle(MAPVIEW_BUNDLE_KEY, new Bundle());
        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_CANCELED:
                if (null != googleApiClient)
                    googleApiClient.connect();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0x0:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED)
                    mGoogleMap.setMyLocationEnabled(true);
                locationPermission = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                googleApiClient.connect();
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        if(null != googleApiClient)
            googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        if(null != googleApiClient)
            googleApiClient.disconnect();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        if(null != googleApiClient)
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {}
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
