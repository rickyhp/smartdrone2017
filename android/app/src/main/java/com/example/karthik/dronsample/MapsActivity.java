package com.example.karthik.dronsample;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.example.karthik.dronsample.R.id.linearLayoutEdit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Polyline polyline;
    LocationRequest mLocationRequest;
    ArrayList<LatLng> markerPoints;
    Button submitBtn;
    Marker marker;
    HashMap<String, String> droneData;
    private static final String TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        markDronePosition();
        submitBtn = (Button) findViewById(R.id.btn_map);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (markerPoints.size() != 0) {
                    final Dialog dialog = new Dialog(MapsActivity.this);
                    dialog.setContentView(R.layout.altitude_coordinates_popup);
                    LinearLayout linearLayout =
                            (LinearLayout)dialog.findViewById(linearLayoutEdit);
                    final ArrayList<EditText> editTextArray = new ArrayList<>();
                    for(int i=0;i<=markerPoints.size();i++){

                        EditText editText = new EditText(MapsActivity.this);
                        if (i==0){
                            editText.setHint("Take Off Altitude");
                        }else {
                            editText.setHint("Marker Altitude" + i);
                        }
                        editTextArray.add(editText);
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        linearLayout.addView(editText);
                    }

                    Button dialogButton = (Button) dialog.findViewById(R.id.btn_ok);
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<String> altitudeArray = new ArrayList<>();
                            for (int i=0;i<editTextArray.size();i++){
                                altitudeArray.add(editTextArray.get(i).getText().toString());
                            }
                            JSONObject json = ConnectActivity.getJSONObject("ACTION", "goWayPoint");
                            try {
                                json.put("DRONE_MARKERS", getDroneMarkerArray());
                                json.put("DRONE_MARKERS_ALT", droneData.get("ALTITUDE"));
                                json.put("DRONE_RELATIVE_ALT", getRelativeAltArray(altitudeArray));
                            } catch (Exception e) {
                                Log.v(TAG, e.toString());
                                e.printStackTrace();
                            }
                            String message = "Coordinates sent";
                            POST(json.toString(), message);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    Toast.makeText(MapsActivity.this, "Minimum one location needed",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void markDronePosition() {
        try {
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    final HashMap<String, String> gpsData = getDroneGpsData();
                    String latLngLoc = gpsData.get("LATITUDE") + "," + gpsData.get("LONGITUDE");
                    final LatLng location = stringToLatLong(latLngLoc);
                    final MarkerOptions markerOptions = new MarkerOptions();
                    if (location != null) {
                        markerOptions.position(location);
                        markerOptions.title("Drone");
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (marker != null) {
                                    marker.remove();
                                }
                                if (polyline != null){
                                    polyline.remove();
                                }
                                droneData = gpsData;
                                marker = mMap.addMarker(markerOptions);
                                drawPolyLineDroneMarker(location);
                            }
                        });
                    }
                }
            }, 0, 5, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDroneLocation() {
        return droneData.get("LATITUDE") + "," + droneData.get("LONGITUDE");
    }

    private HashMap<String, String> getDroneGpsData() {
        HashMap<String, String> gpsData = new HashMap<>();
        String latitude, longitude, humidity, temperature;
        try {
            JSONObject json = ConnectActivity.getJSONObject("ACTION", "getLocation");
            String response = POST(json.toString(), null);
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject gps = jsonResponse.getJSONObject("DRONE_GPS");
            JSONArray locationData = gps.getJSONArray("location");
            latitude = locationData.getString(0);
            longitude = locationData.getString(1);
            humidity = gps.getString("humidity");
            temperature = gps.getString("temperature");
            gpsData.put("ALTITUDE", getAltitude(new LatLng(Double.valueOf(latitude),
                    Double.valueOf(longitude))));
            gpsData.put("LATITUDE", latitude);
            gpsData.put("LONGITUDE", longitude);
            gpsData.put("HUMIDITY", humidity);
            gpsData.put("TEMPERATURE", temperature);
        } catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
        return gpsData;
    }

    private LatLng stringToLatLong(String latLong){
        String[] latLng = latLong.split(",");
        double latitude = Double.parseDouble(latLng[0]);
        double longitude = Double.parseDouble(latLng[1]);
        return new LatLng(latitude, longitude);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        markerPoints = new ArrayList<>();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if (markerPoints.size() > 5) {
                    markerPoints.clear();
                    mMap.clear();
                }
                markerPoints.add(point);
                MarkerOptions options = new MarkerOptions();
                options.position(point);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(21));
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                drawPolyLineOnMap(markerPoints);
                mMap.addMarker(options);
            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                mMap.clear();
                markerPoints.clear();
            }
        });
    }

    private void drawPolyLineDroneMarker(LatLng location) {
        if (markerPoints.size() != 0) {
            polyline = mMap.addPolyline(new PolylineOptions()
                        .color(Color.RED)
                        .width(8)
                        .add(location, markerPoints.get(0)));
        }
    }

    public void drawPolyLineOnMap(List<LatLng> list) {
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.RED);
        polyOptions.width(8);
        polyOptions.addAll(list);
        mMap.addPolyline(polyOptions);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : list) {
            builder.include(latLng);
        }
        final LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        mMap.animateCamera(cu);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(21));
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
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

    private String getRelativeAltArray(ArrayList<String> arrayList) throws Exception{
        JSONArray relativeAltArray = new JSONArray();
        for (String relativeAlt:arrayList){
            relativeAltArray.put(relativeAlt);
        }
        return relativeAltArray.toString();
    }

    private String getAltitude(LatLng location) throws Exception{
        String altitudeUrl = "https://maps.googleapis.com/maps/api/elevation/json?locations=" +
                String.valueOf(location.latitude) + "," + String.valueOf(location.longitude) +
                "&key=AIzaSyAhqtoCwD_0ZBiB7caWjQe2tX1XML-TNTo&sensor=true";
        String[] myParams = {altitudeUrl};
        String response = new GetResponseFromServer().execute(myParams).get();
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray resp = jsonResponse.getJSONArray("results");
        JSONObject results = resp.getJSONObject(0);
        return results.getString("elevation");
    }

    private ArrayList<String> getDroneMarkerArray() throws Exception{
        ArrayList<String> droneMarkerArray = new ArrayList<>();
        LatLng droneLoc = stringToLatLong(getDroneLocation());
        droneMarkerArray.add(getLatLngTupleString(droneLoc));
        droneMarkerArray.add(getLatLngTupleString(droneLoc));
        for (LatLng latLng:markerPoints){
            droneMarkerArray.add(getLatLngTupleString(latLng));
        }
        return droneMarkerArray;
    }

    private String getLatLngTupleString(LatLng latLng){
        String result;
        result = "(" + String.valueOf(latLng.latitude) + "," +
                String.valueOf(latLng.longitude) + ")";
        return result;
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
        markDronePosition();
        super.onResume();
    }

    @Override
    public void onPause(){
        markDronePosition();
        super.onPause();
    }
}
