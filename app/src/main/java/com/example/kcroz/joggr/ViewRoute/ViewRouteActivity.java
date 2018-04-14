package com.example.kcroz.joggr.ViewRoute;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kcroz.joggr.DatabaseHelper;
import com.example.kcroz.joggr.R;

import java.util.List;
import java.util.Map;

public class ViewRouteActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private int runID;
    private Bundle runBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_route);

        runID = Integer.parseInt(getIntent().getStringExtra("RUN_ID"));
        //List<Map<String,String>> routeData = getRouteList();


        runBundle = new Bundle();
        runBundle.putString("RUN_ID", String.valueOf(runID));
        //runBundle.putString("ROUTE_HASH", String.valueOf(routeData));


        Toolbar toolbar = findViewById(R.id.toolbar);
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
        Log.d("AAAAAA", String.valueOf(runID));

        return dbHelper.loadPointsForRun(runID);
        //return dbHelper.loadPointDataToHash();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    ViewRouteMapFragment mapTab = new ViewRouteMapFragment();
                    mapTab.setArguments(runBundle);
                    return mapTab;
                case 1:
                    ViewRouteDataFragment dataTab = new ViewRouteDataFragment();
                    dataTab.setArguments(runBundle);
                    return dataTab;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}