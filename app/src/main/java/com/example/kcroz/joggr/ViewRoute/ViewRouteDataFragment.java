package com.example.kcroz.joggr.ViewRoute;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.kcroz.joggr.DatabaseHelper;
import com.example.kcroz.joggr.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewRouteDataFragment extends Fragment {

    private Context context;
    private int runID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_route_data, container, false);
        context = getActivity();

        runID = Integer.parseInt(getArguments().getString("RUN_ID"));

        populateListView(view);

        return view;
    }

    private void populateListView(View view) {
        ListView lvRoute = view.findViewById(R.id.lvRoute);

        SimpleAdapter adapter = new SimpleAdapter(context,
                getRouteList(),
                R.layout.listview_route,
                new String[] {"PointID", "Latitude", "Longitude", "Timestamp", "RunID"},
                new int[] {R.id.tvPointID, R.id.tvLatitude, R.id.tvLongitude, R.id.tvTimeStamp, R.id.tvRunID2});

        lvRoute.setAdapter(adapter);
    }

    private List<Map<String,String>> getRouteList() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        List<Map<String,String>> data = dbHelper.loadPointsForRun(runID);

        Map<String, String> map = new HashMap<>();

        map.put("PointID", "Point ID");
        map.put("Latitude", "Latitude");
        map.put("Longitude", "Longitude");
        map.put("Timestamp", "Timestamp");
        map.put("RunID", "Run ID");

        data.add(0, map);

        return data;
    }
}
