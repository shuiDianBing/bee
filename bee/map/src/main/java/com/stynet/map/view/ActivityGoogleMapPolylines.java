package com.stynet.map.view;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.stynet.map.R;
import com.stynet.map.assist.LocationFused;

/**
 * Created by xx shuiDianBing, 2018/11/29-09:12:09:12.Refer to the website: nullptr
 * android GoogleMap画导航线路图 路径规划 https://blog.csdn.net/zhan10001/article/details/78461477
 * android和ios GoogleMap画导航线路图 路径规划（Directions）https://blog.csdn.net/mad1989/article/details/9734667
 * android和ios GoogleMap画导航线路图路径规划（Directions） http://www.voidcn.com/article/p-ykijattk-bqt.html
 **/

public class ActivityGoogleMapPolylines extends ActivityMap {
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private MapView mapView;
    private GoogleMap mGoogleMap;
    private LocationFused locationFused;
    private Location location;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map0);
        mapView = findViewById(R.id.mapView);
        Bundle mapViewBundle = null;
        if (null != savedInstanceState)
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                mGoogleMap = googleMap;
                googleMap.setMyLocationEnabled(true);
                googleMap.setTrafficEnabled(true);
                googleMap.setIndoorEnabled(true);
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.getUiSettings().setZoomControlsEnabled(true);//手势缩放
                googleMap.getUiSettings().setScrollGesturesEnabled(true);//手势拖动
                googleMap.getUiSettings().setMapToolbarEnabled(false);//禁用精简模式下右下角的工具栏
                googleMap.animateCamera(CameraUpdateFactory.zoomBy(0x10));//缩放级别
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
        locationFused = new LocationFused(this,new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if(null != locationResult)
                    moveLocationPlace(locationResult.getLastLocation());
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        });
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
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        locationFused.startLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        locationFused.stopLocation();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        locationFused.startLocationUpdates();
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
