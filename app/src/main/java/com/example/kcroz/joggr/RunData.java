package com.example.kcroz.joggr;

import android.os.Parcel;
import android.os.Parcelable;

public class RunData implements Parcelable {
    private int _runID;
    private String _date;
    private String _title;
    private float _distance;
    private long _runTime;
    private long _totalRunTime;
    private long _warmUpTime;
    private long _coolDownTime;
    private String _rating;
    private String _comment;

    public RunData(int runID) {
        _runID = runID;
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

    public RunData setRunTime(long runTime) {
        _runTime = runTime;
        return this;
    }

    public RunData setTotalRunTime(long totalRunTime) {
        _totalRunTime = totalRunTime;
        return this;
    }

    public RunData setWarmUpTime(long warmUpTime) {
        _warmUpTime = warmUpTime;
        return this;
    }

    public RunData setCoolDownTime(long coolDownTime) {
        _coolDownTime = coolDownTime;
        return this;
    }

    public RunData setRating(String rating) {
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

    public long getRunTime() {
        return _runTime;
    }

    public long getTotalRunTime() {
        return _totalRunTime;
    }

    public long getWarmUpTime() {
        return _warmUpTime;
    }

    public long getCoolDownTime() {
        return _coolDownTime;
    }

    public String getRating() {
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
        _runTime = in.readLong();
        _totalRunTime = in.readLong();
        _warmUpTime = in.readLong();
        _coolDownTime = in.readLong();
        _rating = in.readString();
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
        dest.writeLong(_runTime);
        dest.writeLong(_totalRunTime);
        dest.writeLong(_warmUpTime);
        dest.writeLong(_coolDownTime);
        dest.writeString(_rating);
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
