package com.example.karthik.dronsample;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ToggleButton armToggleButton;
    Button homeButton;
    Button micButton;
    Button videoRecButton;
    Button droneUpButton;
    Button droneDownButton;
    Button forwardButton;
    Button reverseButton;
    Button leftButton;
    Button rightButton;
    Button captureButton;
    Button sensorBtn;
    ToggleButton takeOffToggleButton;
    Toast mToast;
    static String url;
    private static final int REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        armToggleButton = (ToggleButton) findViewById(R.id.tbtnArm);
        homeButton = (Button) findViewById(R.id.btnReturn);
        micButton = (Button) findViewById(R.id.btnRecord);
        videoRecButton = (Button) findViewById(R.id.btnVideoRec);
        droneUpButton = (Button) findViewById(R.id.btnDroneUp);
        droneDownButton = (Button) findViewById(R.id.btnDroneDown);
        forwardButton = (Button) findViewById(R.id.btnUp);
        reverseButton = (Button) findViewById(R.id.btnDown);
        leftButton = (Button) findViewById(R.id.btnLeft);
        rightButton = (Button) findViewById(R.id.btnRight);
        captureButton = (Button) findViewById(R.id.btnCapture);
        takeOffToggleButton = (ToggleButton) findViewById(R.id.tbtnTakeOff);
        sensorBtn = (Button) findViewById(R.id.sensor);
        // Disable button if no voice recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            micButton.setEnabled(false);
            micButton.setText("Recognizer not present");
        }

        url = getIntent().getStringExtra("URL");

        homeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent toMap = new Intent (getApplicationContext(), MapsActivity.class);
                startActivity(toMap);
            }
        });
        sensorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSensor = new Intent (getApplicationContext(), TiltActivity.class);
                startActivity(toSensor);
            }
        });
        armToggleButton.setOnClickListener(new View.OnClickListener(){
            JSONObject json;
            String message;
            @Override
            public void onClick(View v){
                if (armToggleButton.isChecked()) {
                    json = ConnectActivity.getJSONObject("ACTION", "arm");
                    message = "Drone Armed";
                } else {
                    json = ConnectActivity.getJSONObject("ACTION", "disarm");
                    message = "Drone Disarmed";
                }
                POST(json.toString(), message);
            }
        });
        takeOffToggleButton.setOnClickListener(new View.OnClickListener(){
                JSONObject json;
                String message;
                @Override
                public void onClick(View v){
                    if (takeOffToggleButton.isChecked()) {
                        json = ConnectActivity.getJSONObject("ACTION", "autoTakeoff");
                        message = "Auto Take Off initiated";
                    } else {
                        json = ConnectActivity.getJSONObject("ACTION", "autoLand");
                        message = "Auto Landing initiated";
                    }
                    POST(json.toString(), message);
                }
            });
        droneUpButton.setOnTouchListener(new View.OnTouchListener() {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "up");
            String message = "Drone move up";
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                POST(json.toString(), message);
                return false;
            }
        });
        droneDownButton.setOnTouchListener(new View.OnTouchListener() {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "down");
            String message = "Drone move down";
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                POST(json.toString(), message);
                return false;
            }
        });
        forwardButton.setOnTouchListener(new View.OnTouchListener() {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "forward");
            String message = "Drone move forward";
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                POST(json.toString(), message);
                return false;
            }
        });
        reverseButton.setOnTouchListener(new View.OnTouchListener() {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "reverse");
            String message = "Drone move reverse";
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                POST(json.toString(), message);
                return false;
            }
        });
        leftButton.setOnTouchListener(new View.OnTouchListener() {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "left");
            String message = "Drone Roll Left";
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                POST(json.toString(), message);
                return false;
            }
        });
        rightButton.setOnTouchListener(new View.OnTouchListener() {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "right");
            String message = "Drone Roll Right";
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                POST(json.toString(), message);
                return false;
            }
        });
        videoRecButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                JSONObject json = ConnectActivity.getJSONObject("ACTION", "video");
                String message = "Video Record";
                POST(json.toString(), message);
            }
        });
        captureButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                JSONObject json = ConnectActivity.getJSONObject("ACTION", "picture");
                String message = "Image Captured";
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
        if (response != null) {
            showAToast(message);
        } else {
            showAToast(message);
        }
    }

    public void showAToast (String message){
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }
}