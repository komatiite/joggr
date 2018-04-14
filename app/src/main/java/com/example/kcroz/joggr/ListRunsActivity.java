package com.example.kcroz.joggr;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.kcroz.joggr.ViewRoute.ViewRouteActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListRunsActivity extends AppCompatActivity {
    private ListView lvRuns;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_runs);
        context = getApplicationContext();

        lvRuns = findViewById(R.id.lvRuns);

        SimpleAdapter adapter = new SimpleAdapter(this,
                                             getRunsList(),
                                             R.layout.listview_run,
                                             new String[] {"RunID", "Date"},
                                             new int[] {R.id.tvRunID, R.id.tvDate});


        lvRuns.setAdapter(adapter);

        /*lvRuns.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent routeIntent = new Intent(ListRunsActivity.this, ViewRouteActivity.class);
                ListRunsActivity.this.startActivity(routeIntent);
            }
        });*/

        lvRuns.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                HashMap<String,String> hashMap =(HashMap<String,String>)lvRuns.getItemAtPosition(myItemInt);
                String runID = hashMap.get("RunID");

                Intent routeIntent = new Intent(ListRunsActivity.this, ViewRouteActivity.class);
                routeIntent.putExtra("RUN_ID", runID);

                ListRunsActivity.this.startActivity(routeIntent);
            }
        });
    }

    private List<Map<String,String>> getRunsList() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        return dbHelper.loadRunDataToHash();
    }

    public void deleteRuns(View view) {
        this.getApplicationContext().deleteDatabase("JoggerDB");
    }
}
