package com.example.kcroz.joggr.RecordRoute;

import android.content.Context;
import android.os.AsyncTask;

import com.example.kcroz.joggr.AsyncResponse;
import com.example.kcroz.joggr.DatabaseHelper;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InsertRun extends AsyncTask<Void, Void, Integer> {

    private Context _context;
    private DatabaseHelper _dbHelper;
    private AsyncResponse _delegate;

    public InsertRun (Context context, AsyncResponse delegate){
        _context = context;
        _delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        _dbHelper = new DatabaseHelper(_context);
    }

    @Override
    protected Integer doInBackground(Void... param) {
        long runID = _dbHelper.insertRunValues(getCurrentDate(), // Date
                                  null,             // Title
                                  0,                // Distance
                                  0,                // Run time
                                  0,                // Rating
                                  null);            // Comment

        return (int)runID;
    }

    protected void onProgressUpdate(Void... progress) { }

    protected void onPostExecute(Integer result) {
        _delegate.processRun(result);
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy / MM / dd ");

        return mdformat.format(calendar.getTime());
    }
}