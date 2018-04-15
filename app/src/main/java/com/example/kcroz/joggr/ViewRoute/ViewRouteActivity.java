package com.example.kcroz.joggr.ViewRoute;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kcroz.joggr.DatabaseHelper;
import com.example.kcroz.joggr.EditRunActivity;
import com.example.kcroz.joggr.JoggingActivity;
import com.example.kcroz.joggr.ListRuns.ListRunsActivity;
import com.example.kcroz.joggr.R;

import java.util.List;
import java.util.Map;

public class ViewRouteActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private int runID;
    private Bundle runBundle;
    private Context _context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_route);
        _context = this;

        runID = Integer.parseInt(getIntent().getStringExtra("RUN_ID"));
        List<Map<String,String>> routeData = getRouteList();


        runBundle = new Bundle();
        runBundle.putString("RUN_ID", String.valueOf(runID));
        runBundle.putString("ROUTE_HASH", String.valueOf(routeData));


        Toolbar toolbar = findViewById(R.id.viewRunToolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    private List<Map<String,String>> getRouteList() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        return dbHelper.loadPointsForRun(runID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_run, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuExportCSV) {
            Log.d("Toolbar", "export to csv");
            return true;
        }

        if (id == R.id.menuEditRun) {
            Log.d("Toolbar", "edit run");

            Intent activityIntent = new Intent(ViewRouteActivity.this, EditRunActivity.class);
            activityIntent.putExtra("runID", String.valueOf(runID));
            startActivity(activityIntent);

            return true;
        }

        if (id == R.id.menuDeleteRun) {
            Log.d("Toolbar", "delete run");

            AlertDialog dialog = confirmDelete();
            dialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private AlertDialog confirmDelete()
    {
        Log.d("dialog", "in delete");

        return new AlertDialog.Builder(this)
                .setTitle("Delete Run?")
                .setMessage("Are you sure you want to delete this run?")
                //.setIcon(R.drawable.delete)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        DatabaseHelper dbHelper = new DatabaseHelper(_context);
                        dbHelper.deleteRun(runID);

                        Intent listIntent = new Intent(ViewRouteActivity.this, ListRunsActivity.class);
                        startActivity(listIntent);

                        dialog.dismiss();
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
    }

    @Override
    public void onBackPressed() {
        Intent listIntent = new Intent(ViewRouteActivity.this, ListRunsActivity.class);
        startActivity(listIntent);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    ViewRouteStatsFragment statsTab = new ViewRouteStatsFragment();
                    statsTab.setArguments(runBundle);
                    return statsTab;
                case 1:
                    ViewRouteMapFragment mapTab = new ViewRouteMapFragment();
                    mapTab.setArguments(runBundle);
                    return mapTab;
                case 2:
                    ViewRouteDataFragment dataTab = new ViewRouteDataFragment();
                    dataTab.setArguments(runBundle);
                    return dataTab;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}