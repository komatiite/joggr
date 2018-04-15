package com.example.kcroz.joggr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kcroz.joggr.ListRuns.ListRunsActivity;
import com.example.kcroz.joggr.ViewRoute.ViewRouteActivity;

import java.util.List;
import java.util.Map;

public class EditRunActivity extends AppCompatActivity {

    private TextView tvEditRunTitle;
    private EditText etRunTitle;
    private EditText etRating;
    private EditText etComment;
    private DatabaseHelper _dbHelper;
    private Map<String, String> _runData;
    private static String RUN_ID = "runID";
    private int _runID;
    private String _title;
    private float _distance;
    private int _rating;
    private String _comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_run);

        Bundle bundle = getIntent().getExtras();
        _runID = Integer.parseInt(bundle.getString(RUN_ID));

        tvEditRunTitle = findViewById(R.id.tvEditRunTitle);
        etRunTitle = findViewById(R.id.etRunTitle);
        etRating = findViewById(R.id.etRating);
        etComment = findViewById(R.id.etComment);

        loadRunData();

        Button btnSaveRun = findViewById(R.id.btnSaveRun);

        btnSaveRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRunEntry();

                if (getNumberOfPoints() == 0) {
                    Intent intent = new Intent(EditRunActivity.this, ListRunsActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(EditRunActivity.this, ViewRouteActivity.class);
                    intent.putExtra("RUN_ID", String.valueOf(_runID));
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent listIntent = new Intent(EditRunActivity.this, ListRunsActivity.class);
        startActivity(listIntent);
    }

    private int getNumberOfPoints() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        return dbHelper.loadPointsForRun(_runID).size();
    }

    private void updateRunEntry() {
        _dbHelper = new DatabaseHelper(this);

        _title = etRunTitle.getText().toString();
        _rating = Integer.parseInt(etRating.getText().toString());
        _comment = etComment.getText().toString();


        if (_distance == 0) {
            _distance = JoggrHelper.calculateDistance(getRouteData());
        }

        _dbHelper.updateRun(_runID, _title, _rating, _comment);
    }

    public void loadRunData() {
        _dbHelper = new DatabaseHelper(this);
        _runData = _dbHelper.loadRunByID(_runID);

        Log.d("Date", _runData.get("Date"));
        //Log.d("Title", _runData.get("Title"));
        Log.d("Distance", _runData.get("Distance"));
        Log.d("Rating", _runData.get("Rating"));
        Log.d("Run Time", _runData.get("RunTime"));
        //Log.d("Comment", _runData.get("Comment"));





        _title = _runData.get("Title");
        //_distance = Float.parseFloat(_runData.get("Distance"));
        _rating = Integer.parseInt(_runData.get("Rating"));
        _comment = _runData.get("Comment");

        tvEditRunTitle.setText("Edit run data for " + _runData.get("Date"));
        etRunTitle.setText(_title);
        etRating.setText(String.valueOf(_rating));
        etComment.setText(_comment);
    }

    private List<Map<String,String>> getRouteData() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        return dbHelper.loadPointsForRun(_runID);
    }
}
