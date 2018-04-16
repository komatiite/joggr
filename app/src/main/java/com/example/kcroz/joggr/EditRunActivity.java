package com.example.kcroz.joggr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kcroz.joggr.ListRuns.ListRunsActivity;
import com.example.kcroz.joggr.RecordRoute.EditSource;
import com.example.kcroz.joggr.RecordRoute.RunRating;
import com.example.kcroz.joggr.ViewRoute.ViewRouteActivity;

import java.util.List;
import java.util.Map;

public class EditRunActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static String RUN_ID = "runID";
    private static String SOURCE = "source";
    private int _runID;
    private EditSource _source;

    private TextView tvERMainTitle;
    private TextView tvERDate;
    private TextView tvERDistance;
    private TextView tvERRunTime;
    private TextView tvERWarmUp;
    private TextView tvERCoolDown;
    private TextView tvERTotalRun;
    private EditText etEditTitle;
    private Spinner spnRatings;
    private EditText etRunLog;
    private Button btnSaveRun;

    private DatabaseHelper _dbHelper;
    private Map<String, String> _runData;
    private List<Map<String,String>> _routeData;

    private float _distance;
    private String[] _runTimes;
    private String _title;
    private String _rating;
    private String _runLog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_run);
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        View view = rootView.findViewById(R.id.EditRunContainer);
        setUIListener(view);

        Bundle bundle = getIntent().getExtras();
        _runID = Integer.parseInt(bundle.getString(RUN_ID));
        _source = (EditSource)getIntent().getSerializableExtra(SOURCE);

        findIDs();
        setupSpinner();
        setSaveButtonListener();
        loadRunData();
        loadRouteData();

        if (_source == EditSource.NewRun) {
            calculateDistance();
            calculateTimes();
        }

        setFields();
    }

    private void findIDs() {
        tvERMainTitle = findViewById(R.id.tvERMainTitle);
        tvERDate = findViewById(R.id.tvERDate);
        tvERDistance = findViewById(R.id.tvERDistance);
        tvERRunTime = findViewById(R.id.tvERRunTime);
        tvERWarmUp = findViewById(R.id.tvERWarmUp);
        tvERCoolDown = findViewById(R.id.tvERCoolDown);
        tvERTotalRun = findViewById(R.id.tvERTotalRun);
        etEditTitle = findViewById(R.id.etEditTitle);
        spnRatings = findViewById(R.id.spnRatings);
        etRunLog = findViewById(R.id.etRunLog);
        btnSaveRun = findViewById(R.id.btnSaveRun);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ratings_array,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnRatings.setAdapter(adapter);
        spnRatings.setOnItemSelectedListener(this);
    }

    RunRating _ratingValue;


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                _ratingValue = RunRating.Awful;
                break;
            case 1:
                _ratingValue = RunRating.Bad;
                break;
            case 2:
                _ratingValue = RunRating.Meh;
                break;
            case 3:
                _ratingValue = RunRating.Good;
                break;
            case 4:
                _ratingValue = RunRating.Great;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setSaveButtonListener() {
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

    public void loadRunData() {
        _dbHelper = new DatabaseHelper(this);
        _runData = _dbHelper.loadRunByID(_runID);

        _runTimes = new String[4];
        _runTimes[0] = _runData.get("RunTime");
        _runTimes[1] = _runData.get("WarmUpTime");
        _runTimes[2] = _runData.get("CoolDownTime");
        _runTimes[3] = _runData.get("TotalRunTime");
    }

    private void loadRouteData() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        _routeData = dbHelper.loadPointsForRun(_runID);
    }

    private void calculateDistance() {
        _distance = JoggrHelper.calculateDistance(_routeData);
    }

    private void calculateTimes() {
        float[] results = JoggrHelper.calculateRunTimes(_routeData);

        for (int i = 0; i < results.length; i++) {
            int seconds = (int)results[i] % 60;
            int minutes = (int)results[i] / 60;
            int hours = 0;

            if (minutes >= 60) {
                hours = minutes / 60;
                int hoursMinutes = hours * 60;
                minutes = minutes - hoursMinutes;
            }

            _runTimes[i] = hours + ":" + minutes + ":" + seconds;
        }
    }

    private void setFields() {
        tvERMainTitle.setText(_runData.get("Title"));
        tvERDate.setText(_runData.get("Date"));
        tvERDistance.setText(_runData.get("Distance") + " km");
        tvERRunTime.setText(_runTimes[0]);
        tvERWarmUp.setText(_runTimes[1]);
        tvERCoolDown.setText(_runTimes[2]);
        tvERTotalRun.setText(_runTimes[3]);

        if (_source == EditSource.EditRun) {
            etEditTitle.setText(_runData.get("Title"));
            spnRatings.setSelection(getIndex(spnRatings, _runData.get("Rating")));
            etRunLog.setText(_runData.get("Comment"));
        }
    }

    private int getIndex(Spinner spinner, String selection) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(selection)) {
                return i;
            }
        }

        return 0;
    }

    private void updateRunEntry() {
        _dbHelper = new DatabaseHelper(this);

        _title = etEditTitle.getText().toString();
        _rating = spnRatings.getSelectedItem().toString();
        _runLog = etRunLog.getText().toString();

        if (_source == EditSource.NewRun) {
            _dbHelper.updateNewRun(_runID, _title, _distance, _runTimes, _rating, _runLog);
        }
        else {
            _dbHelper.updateEditRun(_runID, _title, _rating, _runLog);
        }
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

    private void setUIListener(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    JoggrHelper.hideSoftKeyboard(EditRunActivity.this);
                    return false;
                }
            });
        }
    }
}
