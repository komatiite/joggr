package com.example.kcroz.joggr.RecordRoute;

import android.content.Context;
import android.os.AsyncTask;

import com.example.kcroz.joggr.DatabaseHelper;

public class InsertRoutePoint extends AsyncTask<RoutePoint, Void, String> {

    private Context _context;
    private DatabaseHelper _dbHelper;
    private RoutePoint _point;

    public InsertRoutePoint (Context context, RoutePoint point){
        _context = context;
        _point = point;
    }

    @Override
    protected void onPreExecute() {
        _dbHelper = new DatabaseHelper(_context);
    }

    @Override
    protected String doInBackground(RoutePoint... point) {
        _dbHelper.insertPointValues(_point.getLatitude(),
                                    _point.getLongitude(),
                                    _point.getTimeStamp(),
                                    _point.getRunID());
        return "";
    }

    protected void onProgressUpdate(Void... progress) { }

    protected void onPostExecute(String result) { }
}
