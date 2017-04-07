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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    LocationRequest mLocationRequest;
    ArrayList<LatLng> markerPoints;
    Button submitBtn;
    Marker marker;

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
                if (markerPoints != null) {
                    final Dialog dialog = new Dialog(MapsActivity.this);
                    dialog.setContentView(R.layout.altitude_coordinates_popup);
                    final EditText droneAlt = (EditText) dialog.findViewById(R.id.droneAlt);
                    Button dialogButton = (Button) dialog.findViewById(R.id.btn_ok);
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LatLng location = null;
                            try {
                                location = stringToLatLong(getDroneLocation());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            JSONObject json = ConnectActivity.getJSONObject("ACTION", "goWayPoint");
                            try {
                                json.put("MARKERS", getLatLongJSONArray());
                                json.put("MARKER_ALT", getAltitudeJSONArray());
                                json.put("RELATIVE_ALT", droneAlt.getText().toString());
                                json.put("ABSOLUTE_ALT", getAltitude(location));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String message = "Coordinates sent";
                            POST(json.toString(), message);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    Toast.makeText(MapsActivity.this, "Atleast one location needed",
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
                    LatLng location = null;
                    try {
                        location = stringToLatLong(getDroneLocation());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                                marker = mMap.addMarker(markerOptions);
                            }
                        });
                    }
                }
            }, 0, 4, TimeUnit.SECONDS);
        } catch (Exception e) {e.printStackTrace();}
    }

    private String getDroneLocation() throws Exception{
        String response, location;
        JSONObject json = ConnectActivity.getJSONObject("ACTION", "getLocation");
        response = POST(json.toString(), null);
        JSONObject jsonResponse;
        jsonResponse = new JSONObject(response);
        location = jsonResponse.get("POSITION").toString();
        return location;
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
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
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
        //BOUND_PADDING is an int to specify padding of bound.. try 100.
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
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        //mCurrLocationMarker = mMap.addMarker(markerOptions);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(21));
        //stop location updates
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
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
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

    private String getLatLongJSONArray(){
        JSONArray latLongArray = new JSONArray();
        for (LatLng latLong : markerPoints){
            latLongArray.put("(" + String.valueOf(latLong.latitude) + "," +
                    String.valueOf(latLong.longitude) + ")");
            }
        return latLongArray.toString();
    }

    private String getAltitudeJSONArray() throws Exception {
        JSONArray altitudeArray = new JSONArray();
        for (LatLng latLong : markerPoints){
            altitudeArray.put(getAltitude(latLong));
        }
        return altitudeArray.toString();
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
