package com.example.kcroz.joggr;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kcroz.joggr.RecordRoute.GPSService;
import com.example.kcroz.joggr.RecordRoute.InsertRoutePoint;
import com.example.kcroz.joggr.RecordRoute.InsertRun;
import com.example.kcroz.joggr.RecordRoute.RoutePoint;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class JoggingActivity extends FragmentActivity implements OnMapReadyCallback {

    //private FusedLocationProviderClient mFusedLocationProviderClient;

    private TextView tvOutput;



    private Context _context;
    private Button btnStartJogging;
    private Button btnStopJogging;
    private GoogleMap mMap;
    private static int REQUEST_ACCESS_LOCATION = 1;
    private BroadcastReceiver _receiver;
    private static String GPS_LOCATION = "GPS_Location";
    private static String LATITUDE = "Latitude";
    private static String LONGITUDE = "Longitude";
    private static String TIMESTAMP = "Timestamp";
    private double _latitude;
    private double _longitude;
    private long _timeStamp;
    private double _prevLatitude;
    private double _prevLongitude;
    private int _runID;
    private static float DEFAULT_ZOOM = 13.0f;
    private int pointCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogging);

        _context = getApplicationContext();

        btnStartJogging = findViewById(R.id.btnStartJogging);
        btnStopJogging = findViewById(R.id.btnStopJogging);

        tvOutput = findViewById(R.id.tvOutput);

        if (hasPermissions()) {
            enableStartStopButtons();
        }

        //mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.joggingMap);
        mapFragment.getMapAsync(this);
    }

    private boolean hasPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION },
                    REQUEST_ACCESS_LOCATION);

            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        // Check if the user accepted the permissions prompt
        if (requestCode == REQUEST_ACCESS_LOCATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableStartStopButtons();
        }
        else {
            Toast.makeText(this, "Error: Access fine location permission denied", Toast.LENGTH_LONG).show();
        }
    }

    private void enableStartStopButtons() {
        btnStartJogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new InsertRun(_context, new AsyncResponse(){
                    @Override
                    public void processRun(int runID){
                        _runID = runID;
                    }
                }).execute();

                Intent intent = new Intent(getApplicationContext(), GPSService.class);
                startService(intent);

                Log.d("JoggingActivity", "Start Jogging clicked.");
            }
        });

        btnStopJogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(getApplicationContext(), GPSService.class);
                stopService(serviceIntent);

                Log.d("JoggingActivity", "Value of RunID: " + _runID);

                Intent activityIntent = new Intent(JoggingActivity.this, EditRunActivity.class);
                activityIntent.putExtra("runID", String.valueOf(_runID));
                startActivity(activityIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (_receiver == null) {
            _receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    updateLocation(intent);

                    if (pointCount > 0) {
                        drawRoute();
                    }

                    pointCount++;

                    updateRoute();
                }
            };
        }

        registerReceiver(_receiver, new IntentFilter(GPS_LOCATION));
    }

    private void updateLocation(Intent intent) {
        if (pointCount > 0) {
            _prevLatitude = _latitude;
            _prevLongitude = _longitude;
        }
        _latitude = Double.parseDouble(intent.getExtras().get(LATITUDE).toString());
        _longitude = Double.parseDouble(intent.getExtras().get(LONGITUDE).toString());
        _timeStamp = Long.parseLong(intent.getExtras().get(TIMESTAMP).toString());

        tvOutput.setText(String.valueOf(_latitude));
    }

    private void drawRoute() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(_latitude, _longitude), DEFAULT_ZOOM));

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.add(new LatLng(_prevLatitude, _prevLongitude),
                            new LatLng(_latitude, _longitude));
        polylineOptions
                .width(5)
                .color(Color.RED);

        mMap.addPolyline(polylineOptions);
    }

    private void updateRoute() {
        RoutePoint point = new RoutePoint(_latitude, _longitude, _timeStamp, _runID);
        new InsertRoutePoint(this, point).execute();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(49.8794123, -97.253401), DEFAULT_ZOOM));
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }
}