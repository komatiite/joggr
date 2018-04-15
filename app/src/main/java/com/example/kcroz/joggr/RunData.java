package com.example.kcroz.joggr;

import android.os.Parcel;
import android.os.Parcelable;

public class RunData implements Parcelable {
    private int _runID;
    private String _date;
    private String _title;
    private float _distance;
    private String _runTime;
    private int _rating;
    private String _comment;

    public RunData(int runID) {
        _runID = runID;
        //_title = "Default Title";
    }

    public RunData setDate(String date) {
        _date = date;
        return this;
    }

    public RunData setTitle(String title) {
        _title = title;
        return this;
    }

    public RunData setDistance(float distance) {
        _distance = distance;
        return this;
    }

    public RunData setRunTime(String runTime) {
        _runTime = runTime;
        return this;
    }

    public RunData setRating(int rating) {
        _rating = rating;
        return this;
    }

    public RunData setComment(String comment) {
        _comment = comment;
        return this;
    }

    public int getRunID() {
        return _runID;
    }

    public String getDate() {
        return _date;
    }

    public String getTitle() {
        return _title;
    }

    public float getDistance() {
        return _distance;
    }

    public String getRunTime() {
        return _runTime;
    }

    public int getRating() {
        return _rating;
    }

    public String getComment() {
        return _comment;
    }

    protected RunData(Parcel in) {
        _runID = in.readInt();
        _date = in.readString();
        _title = in.readString();
        _distance = in.readFloat();
        _runTime = in.readString();
        _rating = in.readInt();
        _comment = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_runID);
        dest.writeString(_date);
        dest.writeString(_title);
        dest.writeFloat(_distance);
        dest.writeString(_runTime);
        dest.writeInt(_rating);
        dest.writeString(_comment);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RunData> CREATOR = new Parcelable.Creator<RunData>() {
        @Override
        public RunData createFromParcel(Parcel in) {
            return new RunData(in);
        }

        @Override
        public RunData[] newArray(int size) {
            return new RunData[size];
        }
    };
}