package com.example.kcroz.joggr.RecordRoute;

import java.util.Date;

public class RoutePoint {
    private double _latitude;
    private double _longitude;
    private long _timeStamp;
    private Date _time;
    private int _runID;

    /*public RoutePoint(double latitude, double longitude, long timeStamp, int runID) {
        _latitude = latitude;
        _longitude = longitude;
        _timeStamp = timeStamp;
        _runID = runID;
    }*/

    public RoutePoint(double latitude, double longitude, Date time, int runID) {
        _latitude = latitude;
        _longitude = longitude;
        _time = time;
        _runID = runID;
    }

    public double getLatitude() {
        return _latitude;
    }

    public double getLongitude() {
        return _longitude;
    }

    public long getTimeStamp() {
        return _timeStamp;
    }

    public Date getTime() {
        return _time;
    }

    public int getRunID() {
        return _runID;
    }
}
