package com.example.karthik.dronsample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SensorActivity extends AppCompatActivity{
    TextView altitude, humidity;
    Button getValues;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);
//        altitude = (TextView) findViewById(R.id.altitude);
        humidity = (TextView) findViewById(R.id.humidity);
        getValues = (Button) findViewById(R.id.btnGetValues);
        getValues.setOnClickListener(new View.OnClickListener() {
            String json = "{\"ACTION\" : \"sensor\"}";
            String[] params = {MainActivity.url, json, "sensor details get"};
            @Override
            public void onClick(View v) {
                new PostResponseToServer().execute(params);
            }
        });

    }

    public class PostResponseToServer extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String...params){
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
                                    Log.v("bodyres", response.body().string());
//                                    altitude.setText(response.body().string());
                                    humidity.setText(response.body().string());
                                    humidity.invalidate();

                                    Toast.makeText(getBaseContext(), response.body().string(),Toast.LENGTH_SHORT).show();
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
