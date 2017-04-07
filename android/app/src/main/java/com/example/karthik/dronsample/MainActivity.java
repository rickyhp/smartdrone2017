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
    Button tiltBtn;
    Button returnHomeBtn;
    Button sensorBtn;
    ToggleButton takeOffToggleBtn;
    Toast mToast;
    static String url;
    private static final int REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        tiltBtn = (Button) findViewById(R.id.btnTilt);
        sensorBtn = (Button) findViewById(R.id.btnSensor);
        returnHomeBtn = (Button) findViewById(R.id.btnReturnHome);
        // Disable button if no voice recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            micBtn.setEnabled(false);
            micBtn.setText("Recognizer not present");
        }

        url = getIntent().getStringExtra("URL");

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
                Intent toSensor = new Intent (getApplicationContext(), TiltActivity.class);
                startActivity(toSensor);
            }
        });
        sensorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSensor = new Intent (getApplicationContext(), TiltActivity.class);
                startActivity(toSensor);
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
