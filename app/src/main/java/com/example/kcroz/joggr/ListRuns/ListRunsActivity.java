package com.example.kcroz.joggr.ListRuns;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.kcroz.joggr.DatabaseHelper;
import com.example.kcroz.joggr.EditRunActivity;
import com.example.kcroz.joggr.ExportType;
import com.example.kcroz.joggr.Heatmap.HeatmapActivity;
import com.example.kcroz.joggr.JoggrHelper;
import com.example.kcroz.joggr.MainActivity;
import com.example.kcroz.joggr.R;
import com.example.kcroz.joggr.RunData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ListRunsActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_runs);

        Toolbar toolbar = findViewById(R.id.tbListRuns);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager = findViewById(R.id.listRunsSectionPager);
        setupSectionPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.listRunsMonthlyTabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    private void setupSectionPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        List<RunData> data = getRunsData();
        Collections.reverse(data);
        ArrayList<RunData> monthData = new ArrayList<>();
        Bundle bundle;

        int year = 0;
        int month = 0;
        int previous = 0;
        int count = 0;

        for (int i = 0; i < data.size(); i++) {
            String date = data.get(i).getDate();
            year = Integer.parseInt(String.valueOf(date.charAt(2)) + date.charAt(3));
            month = Integer.parseInt(String.valueOf(date.charAt(7)) + date.charAt(8));

            //Log.d("M/Y", String.valueOf(month) + " - " + year);

            if (count == 0) {
                monthData.add(data.get(i));
                previous = month;
            }
            else if (month == previous) {
                monthData.add(data.get(i));
                previous = month;
            }
            else {
                MonthlyFragment fragment = new MonthlyFragment();
                bundle = new Bundle();
                bundle.putParcelableArrayList("RunData", monthData);
                fragment.setArguments(bundle);
                adapter.addFragment(fragment, previous, year);

                monthData = new ArrayList<>();
                monthData.add(data.get(i));
                previous = month;
            }

            count++;
        }

        MonthlyFragment fragment = new MonthlyFragment();
        bundle = new Bundle();
        bundle.putParcelableArrayList("RunData", monthData);
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, month, year);

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_list_runs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuViewHeatmap) {
            Log.d("Toolbar", "view heatmap");

            Intent listIntent = new Intent(this, HeatmapActivity.class);
            startActivity(listIntent);

            return true;
        }

        if (id == R.id.menuExportAll) {
            Log.d("Toolbar", "export all data");

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                        != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2);
            }
            else {
               exportToCSV();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void exportToCSV() {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        View view = rootView.findViewById(R.id.lvRuns);

        JoggrHelper.exportToCSV(view, getPointData(), ExportType.AllPointData);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 2
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            exportToCSV();
        }
        else {
            Toast.makeText(this, "Error: Write external storage permission denied", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(ListRunsActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }

    private List<RunData> getRunsData() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        return dbHelper.loadRunDataToObject();
    }

    private List<Map<String,String>> getPointData() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        return dbHelper.loadPointDataToHash();
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void addFragment(Fragment fragment, int month, int year) {
            String monthName = JoggrHelper.getMonthName(month);
            String title = monthName + " " + year;
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }
}
