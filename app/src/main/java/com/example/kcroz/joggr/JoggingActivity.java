package com.example.kcroz.joggr;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kcroz.joggr.ListRuns.ListRunsActivity;
import com.example.kcroz.joggr.RecordRoute.EditSource;
import com.example.kcroz.joggr.RecordRoute.GPSService;
import com.example.kcroz.joggr.RecordRoute.InsertRoutePoint;
import com.example.kcroz.joggr.RecordRoute.InsertRun;
import com.example.kcroz.joggr.RecordRoute.RoutePoint;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Date;

public class JoggingActivity extends FragmentActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Context _context;
    private Button btnStartJogging;
    private Button btnStopJogging;
    private GoogleMap mMap;
    private BroadcastReceiver _receiver;
    private static String GPS_LOCATION = "GPS_Location";
    private static String LATITUDE = "Latitude";
    private static String LONGITUDE = "Longitude";
    private static String TIMESTAMP = "Timestamp";
    private double _latitude;
    private double _longitude;
    private long _timeStamp;
    private Date _time;
    private double _prevLatitude;
    private double _prevLongitude;
    private int _runID;
    private static float DEFAULT_ZOOM = 18.0f;
    private int pointCount = 0;
    private boolean _running = false;
    private boolean _connectedGPS = false;
    private ProgressBar pbGPS;
    private TextView tvProgress;
    //private boolean _hasServices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogging);

        _context = getApplicationContext();
        //_hasServices = Boolean.parseBoolean(getIntent().getStringExtra("has_services"));

        pbGPS = findViewById(R.id.pbGPS);
        tvProgress = findViewById(R.id.tvProgress);
        pbGPS.setVisibility(View.VISIBLE);

        // Removed due to bug: https://issuetracker.google.com/issues/35822385
        /*if (_hasServices) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }*/

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        btnStartJogging = findViewById(R.id.btnStartJogging);
        btnStopJogging = findViewById(R.id.btnStopJogging);
        btnStartJogging.setVisibility(View.INVISIBLE);
        btnStopJogging.setVisibility(View.INVISIBLE);

        enableStartStopButtons();

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.joggingMap);
        mapFragment.getMapAsync(this);

    }

    private void enableStartStopButtons() {

        btnStartJogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(btnStartJogging, "alpha", 1f, 0f);
                animator.setDuration(500L);
                animator.start();

                animator = ObjectAnimator.ofFloat(btnStopJogging, "alpha", 0f, 1f);
                animator.setDuration(500L);
                animator.start();
                btnStopJogging.setVisibility(View.VISIBLE);

                new InsertRun(_context, new AsyncResponse(){
                    @Override
                    public void processRun(int runID){
                        _runID = runID;
                    }
                }).execute();

                _running = true;
            }
        });

        btnStopJogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _running = false;

                stopGPSService();

                Intent editRunIntent = new Intent(JoggingActivity.this, EditRunActivity.class);
                editRunIntent.putExtra("runID", String.valueOf(_runID));
                editRunIntent.putExtra("source", EditSource.NewRun);
                startActivity(editRunIntent);
            }
        });
    }

    private void stopGPSService() {
        Intent serviceIntent = new Intent(getApplicationContext(), GPSService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopGPSService();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (_receiver == null) {
            _receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    if (_running) {
                        if (pointCount > 0) {
                            drawRoute();
                        }

                        pointCount++;

                        updateRoute();
                    }

                    if (!_connectedGPS) {
                        _connectedGPS = true;
                        tvProgress.setVisibility(View.GONE);
                        pbGPS.setVisibility(View.GONE);

                        Toast.makeText(JoggingActivity.this,
                                       "GPS initialized!",
                                       Toast.LENGTH_LONG).show();

                        ObjectAnimator animator = ObjectAnimator.ofFloat(btnStartJogging, "alpha", 0f, 1f);
                        animator.setDuration(500L);
                        animator.start();
                        btnStartJogging.setVisibility(View.VISIBLE);

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            };
        }

        registerReceiver(_receiver, new IntentFilter(GPS_LOCATION));
    }

    private void drawRoute() {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.add(new LatLng(_prevLatitude, _prevLongitude),
                            new LatLng(_latitude, _longitude));
        polylineOptions
                .width(5)
                .color(Color.RED);

        mMap.addPolyline(polylineOptions);
    }

    private void updateRoute() {
        RoutePoint point = new RoutePoint(_latitude, _longitude, _time, _runID);
        new InsertRoutePoint(this, point).execute();
    }

    @Override
    public void onBackPressed() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        goBackToMain();
    }

    private void goBackToMain() {
        Intent listIntent = new Intent(JoggingActivity.this, MainActivity.class);
        startActivity(listIntent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getDeviceLocation();
            startGPSService();
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            final Task location = mFusedLocationProviderClient.getLastLocation();

            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),
                                currentLocation.getLongitude()), DEFAULT_ZOOM));

                        //moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);

                    }
                    else {
                        Toast.makeText(JoggingActivity.this, "Error: Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void startGPSService() {
        Intent intent = new Intent(getApplicationContext(), GPSService.class);
        startService(intent);
    }
}