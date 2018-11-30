package com.stynet.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.stynet.map.view.ActivityGoogleMapView;
import com.stynet.map.view.ActivityMap;

import java.util.Arrays;
import java.util.List;

/**
 * Created by xx shuiDianBing, 2018/11/27-10:42:10:42.Refer to the website: nullptr
 * Android GoogleMap定位集成 https://www.jianshu.com/p/f5c2fc714a0c
 **/

public class ActivityGoogleMap extends ActivityMap {
    private LocationManager locationManager;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        startActivity(new Intent(this,com.stynet.map.view.ActivityGoogleMap.class));
        if(null == map)
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback(){
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    //googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
                    circle(googleMap);
                    marker(googleMap);
                    pathAreaLineShape(googleMap);
                }
            });
    }
    public void circle(GoogleMap googleMap){
        Circle circle = googleMap.addCircle(new CircleOptions().center(new LatLng(-33.87365, 151.20689)).radius(10000).strokeColor(Color.RED).fillColor(Color.BLUE));
    }
    private void marker(GoogleMap googleMap){//使用标记添加地图 https://developers.google.com/maps/documentation/android-sdk/map-with-marker
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;

    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    // Create a stroke pattern of a gap followed by a dash.
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private static final List<PatternItem> PATTERN_POLYGON_BETA = Arrays.asList(DOT, GAP, DASH, GAP);
    private void pathAreaLineShape(GoogleMap googleMap){//用于表示路径和区域的折线和多边形 https://developers.google.com/maps/documentation/android-sdk/polygon-tutorial
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(-35.016, 143.321),
                        new LatLng(-34.747, 145.592),
                        new LatLng(-34.364, 147.891),
                        new LatLng(-33.501, 150.217),
                        new LatLng(-32.306, 149.248),
                        new LatLng(-32.491, 147.309)));
        // Store a data object with the polyline, used here to indicate an arbitrary type.
        polyline1.setTag("A");
        // Style the polyline.
        stylePolyline(polyline1);

        Polyline polyline2 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(-29.501, 119.700),
                        new LatLng(-27.456, 119.672),
                        new LatLng(-25.971, 124.187),
                        new LatLng(-28.081, 126.555),
                        new LatLng(-28.848, 124.229),
                        new LatLng(-28.215, 123.938)));
        polyline2.setTag("B");
        stylePolyline(polyline2);
        // Add polygons to indicate areas on the map.
        final Polygon polygon1 = googleMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .add(
                        new LatLng(-27.457, 153.040),
                        new LatLng(-33.852, 151.211),
                        new LatLng(-37.813, 144.962),
                        new LatLng(-34.928, 138.599)));
        // Store a data object with the polygon, used here to indicate an arbitrary type.
        polygon1.setTag("alpha");
        // Style the polygon.
        stylePolygon(polygon1);

        Polygon polygon2 = googleMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .add(
                        new LatLng(-31.673, 128.892),
                        new LatLng(-31.952, 115.857),
                        new LatLng(-17.785, 122.258),
                        new LatLng(-12.4258, 130.7932)));
        polygon2.setTag("beta");
        stylePolygon(polygon2);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-23.684, 133.903), 4));//设置缩放
        googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener(){
            @Override
            public void onPolylineClick(Polyline polyline) {
                polyline.setPattern(null == polyline.getPattern()||! polyline.getPattern().contains(DOT)? PATTERN_POLYLINE_DOTTED :null);//从实体笔画切换到虚线笔画模式or 默认模式是实描
                Toast.makeText(ActivityGoogleMap.this, "Route type " + polyline.getTag().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        googleMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener(){
            @Override
            public void onPolygonClick(Polygon polygon) {
                // Flip the values of the red, green, and blue components of the polygon's color
                polygon.setStrokeColor(polygon.getStrokeColor()^ 0x00ffffff);
                polygon.setFillColor(polygon.getFillColor()^ 0x00ffffff);
                Toast.makeText(ActivityGoogleMap.this, "Area type " + polygon.getTag().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * Styles the polyline, based on type.
     * @param polyline The polyline object that needs styling.
     */
    private void stylePolyline(Polyline polyline){
        if(null != polyline.getTag())
            switch (polyline.getTag().toString()){
                // If no type is given, allow the API to use the default.
                case "A":// Use a custom bitmap as the cap at the start of the line
                    polyline.setStartCap(new CustomCap(BitmapDescriptorFactory.fromResource(R.mipmap.icon_circle_brain),10));
                    break;
                case "B":// Use a round cap at the start of the line.
                    polyline.setStartCap(new RoundCap());
                    break;
            }
            polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
    }
    /**
     * Styles the polygon, based on type.
     * @param polygon The polygon object that needs styling.
     */
    private void stylePolygon(Polygon polygon){
        List<PatternItem> pattern = null;
        int strokeColor = COLOR_BLACK_ARGB;
        int fillColor = COLOR_WHITE_ARGB;
        if(null != polygon.getTag())
            switch (polygon.getTag().toString()){
                // If no type is given, allow the API to use the default.
                case "alpha":
                    // Apply a stroke pattern to render a dashed line, and define colors.
                    pattern = PATTERN_POLYGON_ALPHA;
                    strokeColor = COLOR_GREEN_ARGB;
                    fillColor = COLOR_PURPLE_ARGB;
                    break;
                case "beta"://Apply a stroke pattern to render a line of dots and dashes, and define colors.
                    pattern = PATTERN_POLYGON_BETA;
                    strokeColor = COLOR_ORANGE_ARGB;
                    fillColor = COLOR_BLUE_ARGB;
                    break;
            }
            polygon.setStrokePattern(pattern);
            polygon.setStrokeColor(strokeColor);
            polygon.setStrokeColor(strokeColor);
            polygon.setFillColor(fillColor);
    }

    private static final String TAG = ActivityGoogleMap.class.getSimpleName();
    private CameraPosition mCameraPosition;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    private void requestLocationPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            mLocationPermissionGranted = true;
        else
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if(0< grantResults.length && PackageManager.PERMISSION_GRANTED == grantResults[0])
                    mLocationPermissionGranted = true;
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if(null != map){
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState, outPersistentState);
        }
    }
}
