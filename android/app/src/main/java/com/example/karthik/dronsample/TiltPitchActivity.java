package com.example.karthik.dronsample;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;

public class TiltPitchActivity extends Activity implements SensorEventListener {
    private static final String TAG = "TiltPitchActivity";

    private TextView tv;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] mGravity;
    private float[] mGeomagnetic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tilt_acticity);

        // Keep the screen on
        // https://developer.android.com/training/scheduling/wakelock.html#screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Grab the layout TextView
        tv = (TextView) findViewById(R.id.tv_tilt);

        // Setup the sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer == null) {
            Log.d(TAG, "accelerometer is null");
        }
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magnetometer == null) {
            Log.d(TAG, "magnetometer is null");
        }

        // Detect the window position
        switch (getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0:
                Log.d(TAG, "Rotation 0");
                break;
            case Surface.ROTATION_90:
                Log.d(TAG, "Rotation 90");
                break;
            case Surface.ROTATION_180:
                Log.d(TAG, "Rotation 180");
                break;
            case Surface.ROTATION_270:
                Log.d(TAG, "Rotation 270");
                break;
            default:
                Log.w(TAG, "Rotation unknown");
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    /**
     * Convert degrees to absolute tilt value between 0-100
     */
    private int degreesToPower(int degrees) {
        // Tilted back towards user more than -90 deg
        if (degrees < -90) {
            degrees = -90;
        }
        // Tilted forward past 90 deg
        else if (degrees > 90) {
            degrees = 90;
        }
        // Normalize into a positive value
//        degrees *= -1;
        // Invert from 90-0 to 0-90
//        degrees = 90 - degrees;
        // Convert to scale of 0-100
        float degFloat = degrees / 90f * 100f;
        return (int) degFloat;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Log.d(TAG, "onSensorChanged()");
        if (event.values == null) {
            Log.w(TAG, "event.values is null");
            return;
        }
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                mGravity = event.values;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mGeomagnetic = event.values;
                break;
            default:
                Log.w(TAG, "Unknown sensor type " + sensorType);
                return;
        }
        if (mGravity == null) {
            Log.w(TAG, "mGravity is null");
            return;
        }
        if (mGeomagnetic == null) {
            Log.w(TAG, "mGeomagnetic is null");
            return;
        }
        float R[] = new float[9];
        if (! SensorManager.getRotationMatrix(R, null, mGravity, mGeomagnetic)) {
            Log.w(TAG, "getRotationMatrix() failed");
            return;
        }

        float orientation[] = new float[9];
        SensorManager.getOrientation(R, orientation);
        // Orientation contains: azimuth, pitch and roll - we'll use roll
        //float roll = orientation[2];
        float pitch = orientation[1];
        //float azimuth = orientation[0];
        int pitchDeg = (int) Math.round(Math.toDegrees(pitch));
        //int rollDeg = (int) Math.round(Math.toDegrees(roll));
        //int azimuthDeg = (int) Math.round(Math.toDegrees(azimuth));
        int power = degreesToPower(pitchDeg);
        Log.d(TAG, "deg=" + pitchDeg + " power=" + power);
        tv.setText(String.valueOf(power));
//        tv.setText("Yaw:"+String.valueOf(azimuthDeg)+"|Roll:"+String.valueOf(rollDeg)+
//                "|Pitch:"+String.valueOf(pitchDeg));
//        tv.setText(String.valueOf(pitchDeg));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}

