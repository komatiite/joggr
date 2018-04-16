package com.example.kcroz.joggr.ViewRoute;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kcroz.joggr.DatabaseHelper;
import com.example.kcroz.joggr.R;

import java.util.Map;

public class ViewRouteStatsFragment extends Fragment {

    private int _runID;

    private TextView tvViewMainTitle;
    private TextView tvViewDate;
    private TextView tvViewDistance;
    private TextView tvViewRunTime;
    private TextView tvViewWarmUp;
    private TextView tvViewCoolDown;
    private TextView tvViewTotalRun;
    private TextView tvViewRatings;
    private TextView tvViewRunLog;

    private Map<String, String> _runData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_route_stats, container, false);

        _runID = Integer.parseInt(getArguments().getString("RUN_ID"));

        findIDs(view);
        loadData();
        setFields();

        return view;
    }

    private void findIDs(View view) {
        tvViewMainTitle = view.findViewById(R.id.tvViewMainTitle);
        tvViewDate = view.findViewById(R.id.tvViewDate);
        tvViewDistance = view.findViewById(R.id.tvViewDistance);
        tvViewRunTime = view.findViewById(R.id.tvViewRunTime);
        tvViewWarmUp = view.findViewById(R.id.tvViewWarmUp);
        tvViewCoolDown = view.findViewById(R.id.tvViewCoolDown);
        tvViewTotalRun = view.findViewById(R.id.tvViewTotalRun);
        tvViewRatings = view.findViewById(R.id.tvViewRating);
        tvViewRunLog = view.findViewById(R.id.tvViewRunLog);
    }

    private void loadData() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        _runData = dbHelper.loadRunByID(_runID);
    }

    private void setFields() {
        Log.d("View stats", _runData.get("Rating"));

        tvViewMainTitle.setText(_runData.get("Title"));
        tvViewDate.setText("For " + _runData.get("Date"));
        tvViewDistance.setText("Distance: " + _runData.get("Distance") + " km");
        tvViewRunTime.setText(_runData.get("RunTime"));
        tvViewWarmUp.setText(_runData.get("WarmUpTime"));
        tvViewCoolDown.setText(_runData.get("CoolDownTime"));
        tvViewTotalRun.setText(_runData.get("TotalRunTime"));
        tvViewRatings.setText(_runData.get("Rating"));
        tvViewRunLog.setText(_runData.get("Comment"));
    }
}
