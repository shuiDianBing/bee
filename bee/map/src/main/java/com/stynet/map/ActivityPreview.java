package com.stynet.map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.stynet.map.service.LocationService;

/**
 * Created by xx shuiDianBing, 2018/08/01-11:35:11:35.Refer to the website: nullptr
 **/

public class ActivityPreview extends AppCompatActivity {
    private ServiceConnection serviceConnection;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        bindService(new Intent(this,LocationService.class), serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ((LocationService.LocationBinder)service).getService(new LocationService.LocationListener() {
                    @Override
                    public void pushout(Location location) {
                        Log.d("ActivityPreview","onCreate->serviceConnection->onServiceConnected->LocationService.LocationListener.pushLocation:"+location);
                    }
                },1000,1).onStartCommand(null,0,0);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {}
        }, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
