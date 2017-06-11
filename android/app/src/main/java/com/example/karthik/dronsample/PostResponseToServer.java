package com.example.karthik.dronsample;

import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class PostResponseToServer extends AsyncTask<String, Void, String> {

    private static final String TAG = "PostResponse";

    @Override
    protected String doInBackground(String...params) {
        String responseString = null;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        RequestBody body = RequestBody.create(JSON, params[1]);
        Request request = new Request.Builder()
                .header("X-Client-Type", "Android")
                .url(params[0])
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                responseString = response.body().string();
                Log.v(TAG, responseString);
            }
        } catch (Exception e) {
            Log.v(TAG, e.toString());
            e.printStackTrace();
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
