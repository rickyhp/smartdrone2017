package com.example.karthik.dronsample;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Callback;

public class MainActivity extends AppCompatActivity {
    ToggleButton armToggleButton;
    Button homeButton;
    Button micButton;
    Button videoRecButton;
    Button droneUpButton;
    Button droneDownButton;
    Button droneRightButton;
    Button droneLeftButton;
    Button forwardButton;
    Button reverseButton;
    Button leftButton;
    Button rightButton;
    Button captureButton;
    String url;
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
        droneRightButton = (Button) findViewById(R.id.btnDroneRight);
        droneLeftButton = (Button) findViewById(R.id.btnDroneLeft);
        forwardButton = (Button) findViewById(R.id.btnUp);
        reverseButton = (Button) findViewById(R.id.btnDown);
        leftButton = (Button) findViewById(R.id.btnLeft);
        rightButton = (Button) findViewById(R.id.btnRight);
        captureButton = (Button) findViewById(R.id.btnCapture);
        // Disable button if no voice recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            micButton.setEnabled(false);
            micButton.setText("Recognizer not present");
        }
        url = "http://192.168.43.53:8080/";
        homeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String json, message;
                json = "{\"ACTION\":\"return home\"}";
                message = "Returning Home";
                String[] myParams = {url, json, message};
                new PostResponseToServer().execute(myParams);
            }
        });
        armToggleButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String json, message;
                if (armToggleButton.isChecked()) {
                    json = "{\"ACTION\" : \"arm\"}";
                    message = "Drone Armed";
                } else {
                    json = "{\"ACTION\" : \"disarm\"}";
                    message = "Drone Disarmed";
                }
                String[] myParams = {url, json, message};
                new PostResponseToServer().execute(myParams);
            }
        });
        droneUpButton.setOnTouchListener(new View.OnTouchListener() {
            String json = "{\"ACTION\" : \"up\"}";
            String message = "Drone Move Up";
            String[] myParams = {url, json, message};
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new PostResponseToServer().execute(myParams);
                return false;
            }
        });
        droneDownButton.setOnTouchListener(new View.OnTouchListener() {
            String json = "{\"ACTION\" : \"down\"}";
            String message = "Drone Move Down";
            String[] myParams = {url, json, message};
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new PostResponseToServer().execute(myParams);
                return false;
            }
        });
        droneLeftButton.setOnTouchListener(new View.OnTouchListener() {
            String json = "{\"ACTION\" : \"turn left\"}";
            String message = "Drone Turn Left";
            String[] myParams = {url, json, message};
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new PostResponseToServer().execute(myParams);
                return false;
            }
        });
        droneRightButton.setOnTouchListener(new View.OnTouchListener() {
            String json = "{\"ACTION\" : \"turn right\"}";
            String message = "Drone Turn Right";
            String[] myParams = {url, json, message};
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new PostResponseToServer().execute(myParams);
                return false;
            }
        });
        forwardButton.setOnTouchListener(new View.OnTouchListener() {
            String json = "{\"ACTION\" : \"forward\"}";
            String message = "Drone Move Forward";
            String[] myParams = {url, json, message};
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new PostResponseToServer().execute(myParams);
                return false;
            }
        });
        reverseButton.setOnTouchListener(new View.OnTouchListener() {
            String json = "{\"ACTION\" : \"reverse\"}";
            String message = "Drone Move Reverse";
            String[] myParams = {url, json, message};
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new PostResponseToServer().execute(myParams);
                return false;
            }
        });
        leftButton.setOnTouchListener(new View.OnTouchListener() {
            String json = "{\"ACTION\" : \"roll left\"}";
            String message = "Drone Roll Left";
            String[] myParams = {url, json, message};
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new PostResponseToServer().execute(myParams);
                return false;
            }
        });
        rightButton.setOnTouchListener(new View.OnTouchListener() {
            String json = "{\"ACTION\" : \"roll right\"}";
            String message = "Drone Roll Right";
            String[] myParams = {url, json, message};
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new PostResponseToServer().execute(myParams);
                return false;
            }
        });
        videoRecButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String json = "{\"ACTION\" : \"video\"}";
                String message = "Video Record";
                String[] myParams = {url, json, message};
                new PostResponseToServer().execute(myParams);
            }
        });
        captureButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String json = "{\"ACTION\" : \"picture\"}";
                String message = "Image Captured";
                String[] myParams = {url, json, message};
                new PostResponseToServer().execute(myParams);
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
            word = word.concat("\"}");
            String json = "{\"ACTION\" : \"".concat(word);
            String message = "Sending voice command";
            String[] myParams = {url, json, message};
            new PostResponseToServer().execute(myParams);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public class GetResponseFromServer extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String...params){
            String url = params[0];
            final String message = params[2];
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "Network Fail", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.v("Response", response.body().string());
                                if (response != null) {
                                   Toast.makeText(getBaseContext(), message,Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        return null;
        }
    }
    private class PostResponseToServer extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String...params){
            final String url, json, message;
            url = params[0];
            json = params[1];
            message = params[2];
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                        .header("X-Client-Type", "Android")
                        .url(url)
                        .post(body)
                        .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "Network Fail", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (response != null) {
                                    Log.v("Response body", response.body().string());
                                    Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            return null;
        }
    }
}