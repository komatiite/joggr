package com.example.kcroz.joggr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kcroz.joggr.ViewRoute.ViewRouteActivity;

import java.util.Map;

public class EditRunActivity extends AppCompatActivity {

    private TextView tvRunDate;
    private EditText etRunTitle;
    private EditText etRating;
    private EditText etComment;
    private DatabaseHelper _dbHelper;
    private Map<String, String> _runData;
    private static String RUN_ID = "runID";
    private int _runID;
    private String _title;
    private int _rating;
    private String _comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_run);

        Bundle bundle = getIntent().getExtras();
        _runID = Integer.parseInt(bundle.getString(RUN_ID));

        tvRunDate = findViewById(R.id.tvRunDate);
        etRunTitle = findViewById(R.id.etRunTitle);
        etRating = findViewById(R.id.etRating);
        etComment = findViewById(R.id.etComment);

        loadRunData();

        Button btnSaveRun = findViewById(R.id.btnSaveRun);

        btnSaveRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRunEntry();

                Intent intent = new Intent(EditRunActivity.this, ViewRouteActivity.class);
                intent.putExtra("RUN_ID", String.valueOf(_runID));
                startActivity(intent);
            }
        });
    }

    private void updateRunEntry() {
        _dbHelper = new DatabaseHelper(this);
        _dbHelper.updateRun(_runID, _title, _rating, _comment);
    }

    public void loadRunData() {
        _dbHelper = new DatabaseHelper(this);
        _runData = _dbHelper.loadRunByID(_runID);

        _title = _runData.get("Title");
        _rating = Integer.parseInt(_runData.get("Rating"));
        _comment = _runData.get("Comment");

        tvRunDate.setText(_runData.get("Date"));
        etRunTitle.setText(_title);
        etRating.setText(String.valueOf(_rating));
        etComment.setText(_comment);
    }
}
