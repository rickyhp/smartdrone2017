package com.example.karthik.dronsample;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    ToggleButton armToggleBtn;
    Button mapBtn;
    Button micBtn;
    Button videoBtn;
    Button upBtn;
    Button downBtn;
    Button rollLeftBtn;
    Button rollRightBtn;
    Button forwardBtn;
    Button reverseBtn;
    Button leftBtn;
    Button rightBtn;
    Button pictureBtn;
    ToggleButton tiltBtn;
    Button returnHomeBtn;
    Button sensorBtn;
    ToggleButton takeOffToggleBtn;
    Toast mToast;
    TextView tvTilt;
    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] mGravity;
    private float[] mGeomagnetic;
    static String url;
    public static volatile JSONObject jsonTilt;
    public static volatile boolean stopReq;
    private Thread tiltThread;
    private static final int REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize all the variables
        armToggleBtn = (ToggleButton) findViewById(R.id.tbtnArm);
        mapBtn = (Button) findViewById(R.id.btnMap);
        micBtn = (Button) findViewById(R.id.btnMic);
        videoBtn = (Button) findViewById(R.id.btnVideo);
        upBtn = (Button) findViewById(R.id.btnUp);
        downBtn = (Button) findViewById(R.id.btnDown);
        rollLeftBtn = (Button) findViewById(R.id.btnRollLeft);
        rollRightBtn = (Button) findViewById(R.id.btnRollRight);
        forwardBtn = (Button) findViewById(R.id.btnForward);
        reverseBtn = (Button) findViewById(R.id.btnReverse);
        leftBtn = (Button) findViewById(R.id.btnLeft);
        rightBtn = (Button) findViewById(R.id.btnRight);
        pictureBtn = (Button) findViewById(R.id.btnPicture);
        takeOffToggleBtn = (ToggleButton) findViewById(R.id.tbtnTakeOff);
        tiltBtn = (ToggleButton) findViewById(R.id.btnTilt);
        sensorBtn = (Button) findViewById(R.id.btnSensor);
        returnHomeBtn = (Button) findViewById(R.id.btnReturnHome);
        tvTilt = (TextView) findViewById(R.id.tvTilt);

        // Keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Setup the sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // Disable button if no voice recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            micBtn.setEnabled(false);
            micBtn.setText("Recognizer not present");
        }

        //Assign url vale from Connect Activity
        url = getIntent().getStringExtra("URL");

        //Functionality of all the buttons
        mapBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent toMap = new Intent (getApplicationContext(), MapsActivity.class);
                startActivity(toMap);
            }
        });
        tiltBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tiltBtn.isChecked()){
                    JSONObject json = ConnectActivity.getJSONObject("ACTION", "tiltOn");
                    POST(json.toString(), "TIlt mode On");
                    sensorManager.registerListener(MainActivity.this, accelerometer,
                            SensorManager.SENSOR_DELAY_NORMAL);
                    sensorManager.registerListener(MainActivity.this, magnetometer,
                            SensorManager.SENSOR_DELAY_NORMAL);
                    stopReq = false;
                    PostTiltValue();
                } else {
                    JSONObject json = ConnectActivity.getJSONObject("ACTION", "tiltOff");
                    POST(json.toString(), "TIlt mode Off");
                    tvTilt.setText("Tilt: Mode Off");
                    sensorManager.unregisterListener(MainActivity.this);
                    stopReq = true;
                    if (tiltThread != null) {
                        tiltThread.interrupt();
                    }
                }
            }
        });
        sensorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent toSensor = new Intent (getApplicationContext(), SensorActivity.class);
//                startActivity(toSensor);
            }
        });
        armToggleBtn.setOnClickListener(new View.OnClickListener(){
            JSONObject json;
            String message;
            @Override
            public void onClick(View v){
                if (armToggleBtn.isChecked()) {
                    json = ConnectActivity.getJSONObject("ACTION", "arm");
                    message = "Drone Armed";
                } else {
                    json = ConnectActivity.getJSONObject("ACTION", "disarm");
                    message = "Drone Disarmed";
                }
                POST(json.toString(), message);
            }
        });
        takeOffToggleBtn.setOnClickListener(new View.OnClickListener(){
                JSONObject json;
                String message;
                @Override
                public void onClick(View v){
                    if (takeOffToggleBtn.isChecked()) {
                        json = ConnectActivity.getJSONObject("ACTION", "autoTakeoff");
                        message = "Auto Take Off initiated";
                    } else {
                        json = ConnectActivity.getJSONObject("ACTION", "autoLand");
                        message = "Auto Landing initiated";
                    }
                    POST(json.toString(), message);
                }
            });
        upBtn.setOnTouchListener(new View.OnTouchListener() {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "up");
            String message = "Drone move up";
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                POST(json.toString(), message);
                return false;
            }
        });
        downBtn.setOnTouchListener(new View.OnTouchListener() {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "down");
            String message = "Drone move down";
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                POST(json.toString(), message);
                return false;
            }
        });
        forwardBtn.setOnTouchListener(new View.OnTouchListener() {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "forward");
            String message = "Drone move forward";
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                POST(json.toString(), message);
                return false;
            }
        });
        reverseBtn.setOnTouchListener(new View.OnTouchListener() {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "reverse");
            String message = "Drone move reverse";
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                POST(json.toString(), message);
                return false;
            }
        });
        leftBtn.setOnTouchListener(new View.OnTouchListener() {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "left");
            String message = "Drone turn Left";
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                POST(json.toString(), message);
                return false;
            }
        });
        rightBtn.setOnTouchListener(new View.OnTouchListener() {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "right");
            String message = "Drone turn Right";
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                POST(json.toString(), message);
                return false;
            }
        });
        rollLeftBtn.setOnTouchListener(new View.OnTouchListener() {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "rollLeft");
            String message = "Drone Roll Left";
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                POST(json.toString(), message);
                return false;
            }
        });
        rollRightBtn.setOnTouchListener(new View.OnTouchListener() {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "rollRight");
            String message = "Drone Roll Right";
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                POST(json.toString(), message);
                return false;
            }
        });
        videoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                JSONObject json = ConnectActivity.getJSONObject("ACTION", "video");
                String message = "Video Record";
                POST(json.toString(), message);
            }
        });
        pictureBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                JSONObject json = ConnectActivity.getJSONObject("ACTION", "picture");
                String message = "Image Captured";
                POST(json.toString(), message);
            }
        });
        returnHomeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                JSONObject json = ConnectActivity.getJSONObject("ACTION", "returnHome");
                String message = "return Home";
                POST(json.toString(), message);
            }
        });
    }

    public void micButtonClicked(View v){
        startVoiceRecognitionActivity();
    }

    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening...");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Populate the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String word = matches.get(0);
            Toast.makeText(getBaseContext(), word, Toast.LENGTH_SHORT).show();
            JSONObject json = ConnectActivity.getJSONObject("ACTION", word);
            String message = "Sending voice command";
            POST(json.toString(), message);
       }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void POST(String json, String message){
        String response = null;
        String[] myParams = {url, json};
        try {
            response = new PostResponseToServer().execute(myParams).get();
        } catch (Exception e) {e.printStackTrace();}
        if (message != null) {
            if (response != null) {
                showAToast(message);
            } else {
                showAToast(message);
            }
        }
    }

    public void showAToast (String message){
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tiltBtn.isChecked()){
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        stopReq = true;
        if (tiltThread != null) {
            tiltThread.interrupt();
        }
    }

    private double degreesTilt(double degrees) {
        // Tilted back towards user more than -30 deg
        if (degrees < -30) {
            degrees = -30;
        }
        // Tilted forward past 30 deg
        else if (degrees > 30) {
            degrees = 30;
        }
        return degrees;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values == null) {
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
                Log.d(TAG, "Unknown sensor type " + sensorType);
                return;
        }
        if (mGravity == null) {
            Log.d(TAG, "mGravity is null");
            return;
        }
        if (mGeomagnetic == null) {
            Log.d(TAG, "mGeomagnetic is null");
            return;
        }
        float R[] = new float[9];
        if (!SensorManager.getRotationMatrix(R, null, mGravity, mGeomagnetic)) {
            Log.d(TAG, "getRotationMatrix() failed");
            return;
        }
        float orientation[] = new float[9];
        SensorManager.getOrientation(R, orientation);
        float pitch = orientation[1];
        //int pitchDeg = (int) degreesTilt((int) Math.round(Math.toDegrees(pitch)));
        double pitchDeg = degreesTilt(Math.toDegrees(pitch));
        tvTilt.setText("Tilt: " + String.valueOf(pitchDeg));
        jsonTilt = ConnectActivity.getJSONObject("ACTION", "tiltValue");
        try {
            jsonTilt.put("ROLL", String.valueOf(pitchDeg));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void PostTiltValue(){
        tiltThread = new Thread(new Runnable() {
            public void run(){
                while (true) {
                    if (stopReq) return;
                    if (jsonTilt != null) POST(jsonTilt.toString(), null);
                }
            }
        });
        tiltThread.start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
