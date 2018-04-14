package com.example.kcroz.joggr.RecordRoute;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class GPSService extends Service {

    private LocationManager _locationManager;
    private LocationListener _locationListener;
    private static String GPS_LOCATION = "GPS_Location";
    private static String LATITUDE = "Latitude";
    private static String LONGITUDE = "Longitude";
    private static String TIMESTAMP = "Timestamp";

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("GPSService", "On Create");

        _locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent intent = new Intent(GPS_LOCATION);

                intent.putExtra(LATITUDE, location.getLatitude());
                intent.putExtra(LONGITUDE, location.getLongitude());
                intent.putExtra(TIMESTAMP, location.getTime());

                sendBroadcast(intent);

                Log.d("GPSService", String.valueOf(location.getLatitude()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        _locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, _locationListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (_locationManager != null) {
            _locationManager.removeUpdates(_locationListener);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}