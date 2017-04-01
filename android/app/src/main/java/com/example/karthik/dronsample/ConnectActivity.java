package com.example.karthik.dronsample;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConnectActivity extends AppCompatActivity{
    AutoCompleteTextView txtIpAddress;
    AutoCompleteTextView txtPort;
    Button btnConnect;
    public String url = null;
    public Boolean resp = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect);

        txtIpAddress = (AutoCompleteTextView) findViewById(R.id.ipAddress);
        txtPort = (AutoCompleteTextView) findViewById(R.id.port);
        btnConnect = (Button) findViewById(R.id.btnConnect);

        btnConnect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(validateIpPort()){
                    getUrl();
                    String json, message;
                    json = "{\"ACTION\":\"connect\"}";
                    message = "Connection Established";
                    String[] myParams = {url, json, message};
                    new PostResponseToServer().execute(myParams);
                    if (resp) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("URL", url);
                        Log.v("url intent check", "111");
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void getUrl(){
        url = "http://" + txtIpAddress.getText().toString() +
                ":" + txtPort.getText().toString();
    }

    private boolean isValidIp(){
        String ip = txtIpAddress.getText().toString();
        boolean isValid = true;
        for (String s: ip.split(".")){
            try {
                if (!((s.length() <= 3) && (Integer.parseInt(s) >= 0) && (Integer.parseInt(s) < 256))) {
                    isValid = false;
                }
            } catch (Exception exception){
                isValid = false;
                exception.printStackTrace();
            }
        }
        return isValid;
    }

    private boolean isValidPort(){
        String port = txtPort.getText().toString();
        boolean isValid = true;
        try {
            if (!(port.length() == 4 && Integer.parseInt(port) < 10000)) {
                isValid = false;
            }
        } catch (Exception exception) {
            isValid = false;
            exception.printStackTrace();;
        }
        return isValid;
    }

    private boolean validateIpPort(){
        boolean isValid = true;
        if (!isValidIp()){
            txtIpAddress.setError("Invalid IP (IP eg: 192.168.10.2)");
            isValid = false;
        }
        if (!isValidPort()){
            txtPort.setError("Invalid Port (Port eg: 8080)");
            isValid = false;
        }
        return isValid;
    }

    public class PostResponseToServer extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String...params){
            final String url, json, message;
            url = params[0];
            json = params[1];
            message = params[2];
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, json);
            final Request request = new Request.Builder()
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
                                    resp = true;
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
