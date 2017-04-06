package com.example.karthik.dronsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SensorActivity extends AppCompatActivity{

    TextView humidity;
    Button getValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);
        humidity = (TextView) findViewById(R.id.humidity);
        getValues = (Button) findViewById(R.id.btnGetValues);
        getValues.setOnClickListener(new View.OnClickListener() {
            String json = ConnectActivity.getJSONObject("ACTION", "sensor").toString();
            @Override
            public void onClick(View v) {
                String response = null;
                String[] myParams = {MainActivity.url, json};
                try {
                    response = new PostResponseToServer().execute(myParams).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response != null){
                    Toast.makeText(SensorActivity.this, "Sensor details published",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
