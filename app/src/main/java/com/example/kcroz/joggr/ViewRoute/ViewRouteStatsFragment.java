package com.example.kcroz.joggr.ViewRoute;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kcroz.joggr.DatabaseHelper;
import com.example.kcroz.joggr.R;

import java.util.Map;

public class ViewRouteStatsFragment extends Fragment {

    private Context context;
    private int runID;
    private TextView tvStatsTitle;
    private TextView tvStatsDate;
    private TextView tvStatsRunTime;
    private TextView tvStatsDistance;
    private TextView tvStatsRating;
    private TextView tvStatsComment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_route_stats, container, false);

        context = getActivity();
        runID = Integer.parseInt(getArguments().getString("RUN_ID"));

        tvStatsTitle = view.findViewById(R.id.tvStatsTitle);
        tvStatsDate = view.findViewById(R.id.tvStatsDate);
        tvStatsRunTime = view.findViewById(R.id.tvStatsRunTimeValue);
        tvStatsDistance = view.findViewById(R.id.tvStatsDistanceValue);
        tvStatsRating = view.findViewById(R.id.tvStatsRatingValue);
        tvStatsComment = view.findViewById(R.id.tvStatsCommentValue);

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        Map<String, String>  runData = dbHelper.loadRunByID(runID);

        tvStatsTitle.setText(runData.get("Title"));
        tvStatsDate.setText("For " + runData.get("Date"));
        tvStatsRunTime.setText(runData.get("RunTime"));
        tvStatsDistance.setText(runData.get("Distance"));
        tvStatsRating.setText(runData.get("Rating"));
        tvStatsComment.setText(runData.get("Comment"));

        return view;
    }
}
