package com.example.karthik.dronsample;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.Manifest;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.karthik.dronsample.R.id.linearLayoutEdit;


public class MainActivity extends AppCompatActivity implements SensorEventListener,
        TextToSpeech.OnInitListener {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private ToggleButton armToggleBtn, takeOffToggleBtn;
    private Toast mToast;
    private TextView tvTilt;
    private TextToSpeech tts;
    private volatile JSONObject jsonTilt;
    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    private Sensor accelerometer, magnetometer;
    private float[] mGravity, mGeomagnetic;
    private Thread tiltThread;
    private Date lastUpdate;
    private double appliedAcceleration, velocity, avgVelocity;
    private ArrayList<Double> velocityList;
    private int count = 0;
    private static volatile boolean stopReq;
    private static final int REQUEST_CODE = 1234;
    private String item = "Stabilize";
    private String previousItem = "Stabilize";
    public static String url;
    public static Bitmap decodedByte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);

        //Initialize all the variables
        Button mapBtn = (Button) findViewById(R.id.btnMap);
        Button micBtn = (Button) findViewById(R.id.btnMic);
        Button videoBtn = (Button) findViewById(R.id.btnVideo);
        Button upBtn = (Button) findViewById(R.id.btnUp);
        Button downBtn = (Button) findViewById(R.id.btnDown);
        Button rollLeftBtn = (Button) findViewById(R.id.btnRollLeft);
        Button rollRightBtn = (Button) findViewById(R.id.btnRollRight);
        Button forwardBtn = (Button) findViewById(R.id.btnForward);
        Button reverseBtn = (Button) findViewById(R.id.btnReverse);
        Button leftBtn = (Button) findViewById(R.id.btnLeft);
        Button rightBtn = (Button) findViewById(R.id.btnRight);
        Button pictureBtn = (Button) findViewById(R.id.btnPicture);
        Button sensorBtn = (Button) findViewById(R.id.btnSensor);
        Button returnHomeBtn = (Button) findViewById(R.id.btnReturnHome);
        Spinner flightModes = (Spinner) findViewById(R.id.flightModes);
        armToggleBtn = (ToggleButton) findViewById(R.id.tbtnArm);
        takeOffToggleBtn = (ToggleButton) findViewById(R.id.tbtnTakeOff);
        tvTilt = (TextView) findViewById(R.id.tvTilt);
        tts = new TextToSpeech(MainActivity.this, MainActivity.this);

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
        if (activities.size() == 0) {
            micBtn.setEnabled(false);
            micBtn.setText("Recognizer not present");
        }

        //Assign url value from Connect Activity
        url = getIntent().getStringExtra("URL");

        //Functionality of all the buttons
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMap = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(toMap);
            }
        });
        flightModes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                item = parent.getItemAtPosition(pos).toString();
                if (previousItem.equals("Co-operative") && !item.equals("Co-operative")) {
                    JSONObject json = ConnectActivity.getJSONObject("ACTION", "tiltOff");
                    POST(json.toString(), "Co-operative control Off");
                    tvTilt.setText("Tilt: Mode Off");
                    sensorManager.unregisterListener(MainActivity.this);
                    stopReq = true;
                    if (tiltThread != null) {
                        tiltThread.interrupt();
                    }
                }
                previousItem = item;
                switch (item) {
                    case "Co-operative": {
                        JSONObject json = ConnectActivity.getJSONObject("ACTION", "tiltOn");
                        POST(json.toString(), "Co-operative control on");
                        sensorManager.registerListener(MainActivity.this, accelerometer,
                                SensorManager.SENSOR_DELAY_NORMAL);
                        sensorManager.registerListener(MainActivity.this, magnetometer,
                                SensorManager.SENSOR_DELAY_NORMAL);
                        lastUpdate = new Date(System.currentTimeMillis());
                        appliedAcceleration = 0;
                        velocity = 0;
                        avgVelocity = 0;
                        count = 0;
                        stopReq = false;
                        velocityList = new ArrayList<>();
                        PostTiltValue();
                        break;
                    }
                    case "3D Scan":
                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.scan_mode_popup);
                        LinearLayout linearLayout =
                                (LinearLayout) dialog.findViewById(linearLayoutEdit);
                        final ArrayList<EditText> editTextArray = new ArrayList<>();
                        for (int i = 0; i < 2; i++) {
                            EditText editText = new EditText(MainActivity.this);
                            if (i == 0) {
                                editText.setHint("Radius");
                            } else {
                                editText.setHint("Altitude");
                            }
                            editTextArray.add(editText);
                            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                            linearLayout.addView(editText);
                        }
                        Button dialogButton = (Button) dialog.findViewById(R.id.btn_ok);
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                JSONObject json = ConnectActivity.getJSONObject("ACTION", "3D Scan");
                                try {
                                    json.put("RADIUS", editTextArray.get(0).getText().toString());
                                    json.put("ALTITUDE", editTextArray.get(1).getText().toString());
                                } catch (Exception e) {
                                    Log.v(TAG, e.toString());
                                    e.printStackTrace();
                                }
                                String message = "3D scan mode on";
                                POST(json.toString(), message);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                    default: {
                        JSONObject json = ConnectActivity.getJSONObject("ACTION", item);
                        POST(json.toString(), item);
                        break;
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        sensorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSensor = new Intent(getApplicationContext(), SensorActivity.class);
                startActivity(toSensor);
            }
        });
        armToggleBtn.setOnClickListener(new View.OnClickListener() {
            JSONObject json;
            String message;

            @Override
            public void onClick(View v) {
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
        takeOffToggleBtn.setOnClickListener(new View.OnClickListener() {
            JSONObject json;
            String message;

            @Override
            public void onClick(View v) {
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
            String message = "Move Up";

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                POST(json.toString(), message);
                return false;
            }
        });
        downBtn.setOnTouchListener(new View.OnTouchListener() {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "down");
            String message = "Move Down";

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                POST(json.toString(), message);
                return false;
            }
        });
        forwardBtn.setOnTouchListener(new View.OnTouchListener() {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "forward");
            String message = "Move Forward";

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                POST(json.toString(), message);
                return false;
            }
        });
        reverseBtn.setOnTouchListener(new View.OnTouchListener() {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "reverse");
            String message = "Move Reverse";

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
            String message = "Turn Right";

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                POST(json.toString(), message);
                return false;
            }
        });
        rollLeftBtn.setOnTouchListener(new View.OnTouchListener() {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "rollLeft");
            String message = "Roll Left";

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                POST(json.toString(), message);
                return false;
            }
        });
        rollRightBtn.setOnTouchListener(new View.OnTouchListener() {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "rollRight");
            String message = "Roll Right";

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                POST(json.toString(), message);
                return false;
            }
        });
        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = ConnectActivity.getJSONObject("ACTION", "video");
                String message = "Video Record";
                POST(json.toString(), message);
            }
        });
        pictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = ConnectActivity.getJSONObject("ACTION", "picture");
                String message = "Image Captured";
                String response = POST(json.toString(), message);
                if (response != null) {
                    byte[] decodedString = Base64.decode(response, Base64.DEFAULT);
                    decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,
                            decodedString.length);
                    saveToStorage();
                }
            }
        });
        returnHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = ConnectActivity.getJSONObject("ACTION", "returnHome");
                String message = "return Home";
                POST(json.toString(), message);
            }
        });
    }

    private String saveToStorage(){
        File directory = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        String imgName = "image_" + String.valueOf(new Date().getTime()) + ".jpg";
        File myPath=new File(directory, imgName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            decodedByte.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    @Override
    public void onInit(int Text2SpeechCurrentStatus) {
        if (Text2SpeechCurrentStatus == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.US);
        }
    }

    public void textToSpeech(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void micButtonClicked(View v) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String word = matches.get(0);
            showAToast(word);
            JSONObject json = ConnectActivity.getJSONObject("ACTION", word);
            POST(json.toString(), null);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String POST(String json, String message) {
        String response = null;
        String[] myParams = {url, json};
        try {
            response = new PostResponseToServer().execute(myParams).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message != null) {
            if (response != null) {
                showAToast(message);
            } else {
                showAToast(message);
            }
        }
        return response;
    }

    public void showAToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        textToSpeech(message);
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (item.equals("Co-operative")) {
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

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private double degreesTilt(double degrees) {
        if (degrees < -30) {
            degrees = -30;
        } else if (degrees > 30) {
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
        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];
        double a = (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)) - 9.8) / 9.8;
        Date timeNow = new Date(System.currentTimeMillis());
        long timeDelta = timeNow.getTime() - lastUpdate.getTime();
        lastUpdate.setTime(timeNow.getTime());
        double deltaVelocity = appliedAcceleration * timeDelta / 1000;
        appliedAcceleration = (float) a;
        float orientation[] = new float[9];
        SensorManager.getOrientation(R, orientation);
        float pitch = orientation[1];
        double pitchDeg = degreesTilt(Math.toDegrees(pitch));
        count += 1;
        if (count == 10) {
            velocityList.add(deltaVelocity);
            avgVelocity(velocityList);
            velocityList = new ArrayList<>();
            if (pitchDeg < 0) {
                velocity -= avgVelocity;
            } else {
                velocity += avgVelocity;
            }
            tvTilt.setText("Tilt Degrees: " + String.valueOf(pitchDeg) + ", Tilt Velocity: "
                    + String.valueOf(velocity));
            jsonTilt = ConnectActivity.getJSONObject("ACTION", "tiltValue");
            try {
                jsonTilt.put("ROLL", String.valueOf(pitchDeg));
                jsonTilt.put("VELOCITY", String.valueOf(velocity));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            count = 0;
        } else {
            velocityList.add(deltaVelocity);
        }
    }

    private void avgVelocity(ArrayList<Double> velocityArray) {
        double sumVelocity = 0;
        for (int i = 0; i < velocityArray.size(); i++) {
            sumVelocity += velocityArray.get(i);
        }
        avgVelocity = sumVelocity / velocityArray.size();
    }

    private void PostTiltValue() {
        tiltThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if (stopReq) return;
                    if (jsonTilt != null) POST(jsonTilt.toString(), null);
                }
            }
        });
        tiltThread.start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
