package com.example.karthik.dronsample;

import android.os.AsyncTask;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class GetResponseFromServer extends AsyncTask<String, Void, String> {

    private static final String TAG = "GetResponse";

    @Override
    protected String doInBackground(String...params) {
        String responseString = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("X-Client-Type", "Android")
                .url(params[0])
                .build();
        try {
            Response response = client.newCall(request).execute();
            responseString = response.body().string();
            Log.v(TAG, responseString);
        } catch (Exception e) {
            Log.v(TAG, "Network Failure");
            e.printStackTrace();
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}

