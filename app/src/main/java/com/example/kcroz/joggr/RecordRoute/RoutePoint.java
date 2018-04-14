package com.example.kcroz.joggr.RecordRoute;

public class RoutePoint {
    private double _latitude;
    private double _longitude;
    private long _timeStamp;
    private int _runID;

    public RoutePoint(double latitude, double longitude, long timeStamp, int runID) {
        _latitude = latitude;
        _longitude = longitude;
        _timeStamp = timeStamp;
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

    public int getRunID() {
        return _runID;
    }
}
