package com.stynet.map.assist;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by xx shuiDianBing, 2018/12/13-15:17:15:17.Refer to the website: nullptr
 * Android 开发包标准有8个传感器：https://www.cnblogs.com/wangmars/p/3241904.html
 * Android-关于传感器你需要知道的 https://www.jianshu.com/p/4c141d851346
 **/

public class SensoryNeurons {
    private static final String TAG = SensorManager.class.getName();
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private float gyroAngle;
    float[] accelerometerValues = new float[3];
    float[] magneticFieldValues = new float[3];
    float[] values = new float[3];
    float floats;
    float[] R2 = new float[9];
    /**
     * 打开陀螺仪
     * android高德地图自定义定位图标并具有类似陀螺仪旋转功能 https://blog.csdn.net/qq_34169248/article/details/52192890
     * @param context
     */
    public SensoryNeurons(Context context,SensorEventListener sensorEventListener) {
        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);//获取传感器管理服务
        this.sensorEventListener = sensorEventListener;
//        sensorEventListener = new SensorEventListener() {
//            @Override
//            public void onSensorChanged(SensorEvent sensorEvent) {
//                switch (sensorEvent.sensor.getType()) {
//                    case SensorManager.SENSOR_DELAY_GAME:
//                    //Math.abs(sensorEvent.values[SensorManager.DATA_X]);//用于地图当前位置旋转角度
//                    gyroAngle = sensorEvent.values[SensorManager.DATA_X];
//                    break;
//                }
//            }
//
//            @Override
//            public void onAccuracyChanged(Sensor sensor, int i) {
//                Log.d(TAG,"onAccuracyChanged");
//            }
//        };
    }
    public void registerListener(){
        //sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);//陀螺仪
        //为系统的方向传感器注册监听器
        sensorManager.registerListener(sensorEventListener,sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }
    /**
     * 取消注册传感器监听
     */
    public void unregister(){
        if(null != sensorManager)
            sensorManager.unregisterListener(sensorEventListener);
    }

    public float getGyroAngle() {
        return gyroAngle;
    }
}
