package com.example.karthik.dronsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class ConnectActivity extends AppCompatActivity {
    AutoCompleteTextView txtIpAddress;
    AutoCompleteTextView txtPort;
    Button btnConnect;
    public String response;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect);

        txtIpAddress = (AutoCompleteTextView) findViewById(R.id.ipAddress);
        txtPort = (AutoCompleteTextView) findViewById(R.id.port);
        btnConnect = (Button) findViewById(R.id.btnConnect);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateIpPort()) {
                    JSONObject json = getJSONObject("ACTION", "connect");
                    String[] myParams = {getUrl(), json.toString()};
                    try {
                        response = new PostResponseToServer().execute(myParams).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //if (response. != null) {
                        Toast.makeText(ConnectActivity.this, "Connection Established",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("URL", getUrl());
                        startActivity(intent);
                    //} else {
                    //    Toast.makeText(ConnectActivity.this, "Network Failure",
                    //            Toast.LENGTH_SHORT).show();
                    //}
                }
            }
        });
    }

    private String getUrl() {
        return "http://" + txtIpAddress.getText().toString() + ":" + txtPort.getText().toString();
    }

    private boolean isValidIp() {
        String ip = txtIpAddress.getText().toString();
        boolean isValid = true;
        for (String s : ip.split(".")) {
            try {
                if (!((s.length() <= 3) && (Integer.parseInt(s) >= 0) && (Integer.parseInt(s) < 256))) {
                    isValid = false;
                }
            } catch (Exception exception) {
                isValid = false;
                exception.printStackTrace();
            }
        }
        return isValid;
    }

    private boolean isValidPort() {
        String port = txtPort.getText().toString();
        boolean isValid = true;
        try {
            if (!(port.length() == 4 && Integer.parseInt(port) < 10000)) {
                isValid = false;
            }
        } catch (Exception exception) {
            isValid = false;
            exception.printStackTrace();
        }
        return isValid;
    }

    private boolean validateIpPort() {
        boolean isValid = true;
        if (!isValidIp()) {
            txtIpAddress.setError("Invalid IP (IP eg: 192.168.10.2)");
            isValid = false;
        }
        if (!isValidPort()) {
            txtPort.setError("Invalid Port (Port eg: 8080)");
            isValid = false;
        }
        return isValid;
    }

    public static JSONObject getJSONObject(String tag, String value){
        JSONObject json = new JSONObject();
        try {
            json.put(tag, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}