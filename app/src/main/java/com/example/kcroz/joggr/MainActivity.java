package com.example.kcroz.joggr;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.kcroz.joggr.ListRuns.ListRunsActivity;

public class MainActivity extends AppCompatActivity {

    private static int REQUEST_ACCESS_LOCATION = 1;
    private boolean _hasPermission = false;
    private int _count = 0;
    private Button btnStart;
    private Button btnView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btnStart);
        btnView = findViewById(R.id.btnView);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        }
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this,
                                          new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION },
                                          REQUEST_ACCESS_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_LOCATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            _hasPermission = true;
            enableMenuButtons();
        }
        else {
            if (_count == 0) {
                Toast.makeText(this, "This app requires location permission to run!", Toast.LENGTH_LONG).show();
                requestPermission();
                _count++;
            }
            else {
                Toast.makeText(this, "Functionality disabled.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void enableMenuButtons() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent joggingIntent = new Intent(MainActivity.this, JoggingActivity.class);
                MainActivity.this.startActivity(joggingIntent);
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewRunIntent = new Intent(MainActivity.this, ListRunsActivity.class);
                MainActivity.this.startActivity(viewRunIntent);
            }
        });
    }
}
