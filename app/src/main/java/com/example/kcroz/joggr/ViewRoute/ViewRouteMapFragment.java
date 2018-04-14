package com.example.kcroz.joggr.ViewRoute;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kcroz.joggr.DatabaseHelper;
import com.example.kcroz.joggr.MainActivity;
import com.example.kcroz.joggr.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewRouteMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Context context;
    private int runID;
    private List<Map<String,String>> routeData;
    private static float DEFAULT_ZOOM = 13.0f;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_route_map, container, false);

        context = getActivity();

        runID = Integer.parseInt(getArguments().getString("RUN_ID"));
        routeData = getRouteList();

        SupportMapFragment mapFragment = (SupportMapFragment)this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    private List<Map<String,String>> getRouteList() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        return dbHelper.loadPointsForRun(runID);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        ArrayList<LatLng> points = new ArrayList<>();
        mMap = googleMap;

        for (Map<String, String> entry : routeData) {
            points.add(new LatLng(Float.parseFloat(entry.get("Latitude")), Float.parseFloat(entry.get("Longitude"))));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(points.get(0).latitude, points.get(0).longitude), DEFAULT_ZOOM));

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(points);
        polylineOptions
                .width(5)
                .color(Color.RED);

        mMap.addPolyline(polylineOptions);
    }
}
