package com.example.kcroz.joggr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.kcroz.joggr.ListRuns.ListRunsActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void startJoggingActivity(View view) {
        Intent joggingIntent = new Intent(MainActivity.this, JoggingActivity.class);
        MainActivity.this.startActivity(joggingIntent);
    }

    public void startViewRunsActivity(View view) {
        Intent viewRunIntent = new Intent(MainActivity.this, ListRunsActivity.class);
        MainActivity.this.startActivity(viewRunIntent);
    }
}
