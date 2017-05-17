package com.example.karthik.dronsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SensorActivity extends AppCompatActivity{

    TextView humidity;
    TextView temperature;
    TextView sensor1;
    TextView sensor2;
    TextView sensor3;
    TextView sensor4;
    private static final String TAG = "SensorActivity";
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);
        humidity = (TextView) findViewById(R.id.Humidity);
        temperature = (TextView) findViewById(R.id.Temperature);
        sensor1 = (TextView) findViewById(R.id.Sensor1);
        sensor2 = (TextView) findViewById(R.id.Sensor2);
        sensor3 = (TextView) findViewById(R.id.Sensor3);
        sensor4 = (TextView) findViewById(R.id.Sensor4);
        getSensorValues();
    }

    private void getSensorValues() {
        try {
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    final HashMap<String, String> sensorData = getDroneSensorData();
                    SensorActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sensor1.setText(sensorData.get("SENSOR1"));
                            sensor2.setText(sensorData.get("SENSOR2"));
                            sensor3.setText(sensorData.get("SENSOR3"));
                            sensor4.setText(sensorData.get("SENSOR4"));
                            humidity.setText(sensorData.get("HUMIDITY"));
                            temperature.setText(sensorData.get("TEMPERATURE"));
                        }
                    });
                }
            }, 0, 5, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> getDroneSensorData() {
        HashMap<String, String> sensorData = new HashMap<>();
        try {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "getSensor");
            String response = POST(json.toString(), null);
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject sensor = jsonResponse.getJSONObject("SENSOR");
            sensorData.put("SENSOR1", sensor.getString("sonar-1"));
            sensorData.put("SENSOR2", sensor.getString("sonar-2"));
            sensorData.put("SENSOR3", sensor.getString("sonar-3"));
            sensorData.put("SENSOR4", sensor.getString("sonar-4"));
            sensorData.put("HUMIDITY", sensor.getString("humidity"));
            sensorData.put("TEMPERATURE", sensor.getString("temperature"));
        } catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
        return sensorData;
    }

    private String POST(String json, String message){
        String response = null;
        String[] myParams = {MainActivity.url, json};
        try {
            response = new PostResponseToServer().execute(myParams).get();
        } catch (Exception e) {e.printStackTrace();}
        if (message != null) {
            if (response != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Network Failure", Toast.LENGTH_SHORT).show();
            }
        }
        return response;
    }

    @Override
    public void onDestroy() {
        scheduler.shutdown();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        scheduler.shutdown();
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        getSensorValues();
        super.onResume();
    }

    @Override
    public void onPause(){
        getSensorValues();
        super.onPause();
    }
}
