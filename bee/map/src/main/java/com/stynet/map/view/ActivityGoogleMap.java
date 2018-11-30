package com.stynet.map.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.stynet.map.R;
import com.stynet.map.assist.Geocoding;
import com.stynet.map.assist.LocationServer;
import com.stynet.map.vfx.MapRadar;
import com.stynet.map.vfx.MapRipple;

/**
 * Created by xx shuiDianBing, 2018/11/29-17:07:17:07.Refer to the website: nullptr
 * android定位的实现 https://www.jianshu.com/p/a20bde066ce3
 * 参考 https://github.com/aarsy/GoogleMapsAnimations
 **/

public class ActivityGoogleMap extends ActivityMap {
    public static final String LOCATION = "location";
    private final int ANIMATION_TYPE_RIPPLE = 0;
    private final int ANIMATION_TYPE_RADAR = 1;
    private MapRipple mapRipple;
    private MapRadar mapRadar;
    private int whichAnimationWasRunning = ANIMATION_TYPE_RIPPLE;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        //applyPermission(0x0,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION);
        ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if(Build.VERSION_CODES.M <= Build.VERSION.SDK_INT)
                    if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, Binder.getCallingPid(),Binder.getCallingUid())== PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(ActivityGoogleMap.this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED)
                        googleMap.setMyLocationEnabled(true);
                else
                    googleMap.setMyLocationEnabled(true);
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                googleMap.setTrafficEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);//手势缩放
                googleMap.getUiSettings().setScrollGesturesEnabled(true);//手势拖动
                googleMap.getUiSettings().setAllGesturesEnabled(true);
                googleMap.getUiSettings().setMapToolbarEnabled(false);//禁用精简模式下右下角的工具栏
                //googleMap.animateCamera(CameraUpdateFactory.zoomBy(0x10));//缩放级别
                LatLng latlng;
                if(null == googleMap.getMyLocation()) {
                    latlng = new LatLng(37.969317,23.733131);//3.121139,101.675245);
                    googleMap.addMarker(new MarkerOptions().position(latlng).zIndex(0.0f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_side_brain)).
                            anchor(0.5f, 1).infoWindowAnchor(0.5f, 0).title("default").snippet("lat = "+ latlng.latitude +
                            ",lng = "+ latlng.longitude).draggable(true).visible(true).flat(false).rotation(0).alpha(1));
                    startLocation(googleMap);
                }else{
                    getIntent().putExtra(LOCATION, googleMap.getMyLocation());
                    latlng = new LatLng(googleMap.getMyLocation().getLatitude(),googleMap.getMyLocation().getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(latlng).zIndex(0.0f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_circle_brain)).
                            anchor(0.5f, 1).infoWindowAnchor(0.5f, 0).title("current").snippet("lat = "+ latlng.latitude +
                            ",lng = "+latlng.longitude).draggable(true).visible(true).flat(false).rotation(0).alpha(1));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,0x10));
                }
                /*mapRipple = new MapRipple(googleMap,latlng,ActivityGoogleMap.this);
//                mapRipple.withNumberOfRipples(3);
//                mapRipple.withFillColor(Color.parseColor("#FFA3D2E4"));
//                mapRipple.withStrokeColor(Color.BLACK);
//                mapRipple.withStrokewidth(0);      // 10dp
//                mapRipple.withDistance(2000);      // 2000 metres radius
//                mapRipple.withRippleDuration(12000);    //12000ms
//                mapRipple.withTransparency(0.5f);
                mapRipple.startRippleMapAnimation();
                mapRadar = new MapRadar(googleMap,latlng,ActivityGoogleMap.this);
                mapRadar.withClockwiseAnticlockwise(true);
                mapRadar.withDistance(0xb80);
                mapRadar.withClockwiseAnticlockwiseDuration(2);
                //mapRadar.withOuterCircleFillColor(Color.parseColor("#12000000"));
                mapRadar.withOuterCircleStrokeColor(Color.parseColor("#fccd29"));
                //mapRadar.withRadarColors(Color.parseColor("#00000000"), Color.parseColor("#ff000000"));  //starts from transparent to fuly black
                mapRadar.withRadarColors(Color.parseColor("#00fccd29"), Color.parseColor("#fffccd29"));  //starts from transparent to fuly black
                //mapRadar.withOuterCircleStrokewidth(7);
                //mapRadar.withRadarSpeed(5);
                mapRadar.withOuterCircleTransparency(0.5f);
                mapRadar.withRadarTransparency(0.5f);
                mapRadar.startRadarAnimation();//雷达扫描
                mapRipple.startRippleMapAnimation();//扩散波纹
            */}
        });
    }
    private void startLocation(final GoogleMap googleMap){
        new LocationServer((LocationManager) getSystemService(LOCATION_SERVICE),0,1000).gpsWorknet(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(null != location)
                    fixedPoint(googleMap,location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        });
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Location location = getIntent().getParcelableExtra(LOCATION);
                if(null != location)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
                return false;
            }
        });
    }

    /**
     *
     * @param googleMap
     * @param location
     */
    private void fixedPoint(GoogleMap googleMap,Location location){
        if(null == location)return;
        googleMap.clear();
        LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
        if(null == getIntent().getParcelableExtra(LOCATION))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,0x10));
        getIntent().putExtra(LOCATION,location);
        Address address = Geocoding.getAddress(this,location.getLatitude(),location.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(latlng).zIndex(0.0f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_circle_brain)).
                anchor(0.5f, 1).infoWindowAnchor(0.5f, 0).title(address.getLocality()).snippet("lat = "+ latlng.latitude +
                ",lng = "+latlng.longitude).draggable(true).visible(true).flat(false).rotation(0).alpha(1));
    }
    //开始结束雷达侦测动画
    public void startstopAnimation(View view) {
        if (mapRadar.isAnimationRunning() || mapRipple.isAnimationRunning()) {
            if (mapRadar.isAnimationRunning())
                mapRadar.stopRadarAnimation();
            if (mapRipple.isAnimationRunning())
                mapRipple.stopRippleMapAnimation();
            ((Button) view).setText("Start Animation");
        } else {
            if (whichAnimationWasRunning == ANIMATION_TYPE_RADAR)
                mapRadar.startRadarAnimation();
            else
                mapRipple.startRippleMapAnimation();
            //startstoprippleBtn.setText("Stop Animation");
        }
    }
    public void advancedRipple(View view) {
        mapRadar.stopRadarAnimation();
        mapRipple.stopRippleMapAnimation();
        mapRipple.withNumberOfRipples(3);
        mapRipple.withFillColor(Color.parseColor("#FFA3D2E4"));
        mapRipple.withStrokewidth(0);      //0dp
        mapRipple.startRippleMapAnimation();
        //startstoprippleBtn.setText("Stop Animation");
        whichAnimationWasRunning = ANIMATION_TYPE_RIPPLE;
    }
    public void radarAnimation(View view) {
        mapRipple.stopRippleMapAnimation();
        mapRadar.startRadarAnimation();
        //startstoprippleBtn.setText("Stop Animation");
        whichAnimationWasRunning = ANIMATION_TYPE_RADAR;
    }
    public void simpleRipple(View view) {
        mapRadar.stopRadarAnimation();
        mapRipple.stopRippleMapAnimation();
        mapRipple.withNumberOfRipples(1);
        mapRipple.withFillColor(Color.parseColor("#00000000"));
        mapRipple.withStrokeColor(Color.BLACK);
        mapRipple.withStrokewidth(10);      // 10dp
        mapRipple.startRippleMapAnimation();
        //startstoprippleBtn.setText("Stop Animation");
        whichAnimationWasRunning = ANIMATION_TYPE_RIPPLE;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mapRipple.isAnimationRunning())
            mapRipple.stopRippleMapAnimation();
    }
}
