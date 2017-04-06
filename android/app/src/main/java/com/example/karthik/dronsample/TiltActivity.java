package com.example.karthik.dronsample;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
//import android.widget.ImageView;
import android.widget.TextView;

import static java.lang.Math.toDegrees;

public class TiltActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private TextView textView;
//    private ImageView image;
    private Sensor accelerometer;
    private Sensor magnetometer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tilt);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        initListeners();
        textView = (TextView) findViewById(R.id.txt);
//        image = (ImageView) findViewById(R.id.img);
    }

    public void initListeners()
    {
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onDestroy()
    {
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        mSensorManager.unregisterListener(this);
        super.onBackPressed();
    }

    @Override
    public void onResume()
    {
        initListeners();
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {}

    float[] mGravity;
    float[] mGeomagnetic;
    float orientation[] = new float[3];
    double pitch, roll, yaw;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            mGravity = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
        }
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

            if (success) {
                SensorManager.getOrientation(R, orientation);

                // Assuming the angles are in radians.

                // getOrientation() values:
                // values[0]: azimuth, rotation around the Z axis.
                // values[1]: pitch, rotation around the X axis.
                // values[2]: roll, rotation around the Y axis.

                // Heading, Azimuth, Yaw
                double c1 = Math.cos(orientation[0] / 2);
                double s1 = Math.sin(orientation[0] / 2);

                // Pitch, Attitude
                // The equation assumes the pitch is pointed in the opposite direction
                // of the orientation vector provided by Android, so we invert it.
                double c2 = Math.cos(-orientation[1] / 2);
                double s2 = Math.sin(-orientation[1] / 2);

                // Roll, Bank
                double c3 = Math.cos(orientation[2] / 2);
                double s3 = Math.sin(orientation[2] / 2);

                double c1c2 = c1 * c2;
                double s1s2 = s1 * s2;

//                double w = c1c2 * c3 - s1s2 * s3;
                roll = c1c2 * s3 + s1s2 * c3;
                yaw = s1 * c2 * c3 + c1 * s2 * s3;
                pitch = c1 * s2 * c3 - s1 * c2 * s3;

                // The quaternion in the equation does not share the same coordinate
                // system as the Android gyroscope quaternion we are using. We reorder
                // it here.

                // Android X (pitch) = Equation Z (pitch)
                // Android Y (roll) = Equation X (roll)
                // Android Z (azimuth) = Equation Y (azimuth)
                textView.setText("Yaw, Pitch, Roll: " + String.valueOf(toDegrees(yaw)) + "," +
                        String.valueOf(toDegrees(pitch)) + "," +
                        String.valueOf(toDegrees(roll)));

            }
        }
    }
}

//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        float[] orientation = new float[3];
//        orientation = sensorManager.getOrientation()
//        float x = event.values[0];
//        float y = event.values[1];
//        if (x > y) {
//            if (x < 0) {
//                image.setImageResource(R.drawable.left);
//                textView.setText(String.valueOf(toDegrees(x)));
//            }
//            if (x > 0) {
//                image.setImageResource(R.drawable.down);
//                textView.setText(String.valueOf(toDegrees(x)));
//            }
//        } else {
//            if (y < 0) {
//                image.setImageResource(R.drawable.up);
//                textView.setText(String.valueOf(toDegrees(y)));
//            }
//            if (y > 0) {
//                image.setImageResource(R.drawable.right);
//                textView.setText(String.valueOf(toDegrees(y)));
//            }
//        }
//        if (x > (-2) && x < (2) && y > (-2) && y < (2)) {
//            image.setImageResource(R.drawable.returnhome);
//            textView.setText("Not tilt device");
//        }
//    }