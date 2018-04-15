package com.example.kcroz.joggr.Heatmap;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.kcroz.joggr.DatabaseHelper;
import com.example.kcroz.joggr.EditRunActivity;
import com.example.kcroz.joggr.ListRuns.ListRunsActivity;
import com.example.kcroz.joggr.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class HeatmapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HeatmapTileProvider mProvider;

    private int[] colours = {
            Color.rgb(0, 148, 255), // blue
            Color.rgb(102, 225, 0), // green
            Color.rgb(255, 0, 0)    // red
    };

    private float[] startPoints = {
            0.25f, .5f, 1f
    };

    private Gradient gradient = new Gradient(colours, startPoints);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heatmap);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mpHeatmap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(49.8794123, -97.253401), 13.0f));
        addHeatMap();
    }

    private void addHeatMap() {
        List<LatLng> list = new ArrayList<>();
        List<Map<String,String>> pointData = getPointData();

        if (pointData.size() > 0) {
            for(Map<String, String> entry : pointData) {
                list.add(new LatLng(Double.parseDouble(entry.get("Latitude")),
                        Double.parseDouble(entry.get("Longitude"))));
            }

            mProvider = new HeatmapTileProvider.Builder().data(list)
                    .radius(15)
                    .opacity(0.85)
                    .gradient(gradient)
                    .build();
            mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }
        else {
            Toast.makeText(this, "No route data to display!", Toast.LENGTH_LONG).show();

            Intent listIntent = new Intent(this, ListRunsActivity.class);
            startActivity(listIntent);
        }
    }

    private List<Map<String,String>> getPointData() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        return dbHelper.loadPointDataToHash();
    }
}
