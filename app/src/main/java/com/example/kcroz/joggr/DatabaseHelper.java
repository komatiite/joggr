package com.example.kcroz.joggr;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.kcroz.joggr.RecordRoute.RunRating;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Structure
    private static final String DB_NAME = "JoggerDB";
    private static final int DB_VERSION = 1;

    // RUNS table
    private static final String RUNS_TABLE_NAME = "Runs";
    private static final String COL_RUN_ID = "RunID";
    private static final String COL_DATE = "Date";
    private static final String COL_TITLE = "Title";
    private static final String COL_DISTANCE = "Distance";
    private static final String COL_RUN_TIME = "RunTime";
    private static final String COL_TOTAL_RUN_TIME = "TotalRunTime";
    private static final String COL_WARM_UP_TIME = "WarmUpTime";
    private static final String COL_COOL_DOWN_TIME = "CoolDownTime";
    private static final String COL_RATING = "Rating";
    private static final String COL_COMMENT = "Comment";

    // POINTS table
    private static final String POINTS_TABLE_NAME = "Points";
    private static final String COL_POINT_ID = "PointID";
    private static final String COL_LATITUDE = "Latitude";
    private static final String COL_LONGITUDE = "Longitude";
    private static final String COL_TIMESTAMP = "Timestamp";
    private static final String COL_RUN_ID_FK = "RunID";

    // Create table SQL statements
    private static final String RUNS_TABLE_CREATE = "CREATE TABLE " + RUNS_TABLE_NAME + " ("
            + COL_RUN_ID + " INTEGER NOT NULL PRIMARY KEY, "
            + COL_DATE + " NUMERIC NOT NULL, "
            + COL_TITLE + " TEXT, "
            + COL_DISTANCE + " REAL NOT NULL, "
            + COL_RUN_TIME + " TEXT, "
            + COL_TOTAL_RUN_TIME + " TEXT, "
            + COL_WARM_UP_TIME + " TEXT, "
            + COL_COOL_DOWN_TIME + " TEXT, "
            + COL_RATING + " TEXT, "
            + COL_COMMENT + " TEXT);";

    private static final String POINTS_TABLE_CREATE = "CREATE TABLE " + POINTS_TABLE_NAME + " ("
            + COL_POINT_ID + " INTEGER NOT NULL PRIMARY KEY, "
            + COL_LATITUDE + " REAL NOT NULL, "
            + COL_LONGITUDE + " REAL NOT NULL, "
            + COL_TIMESTAMP + " TEXT NOT NULL, "
            + COL_RUN_ID_FK + " INTEGER NOT NULL);";

    //Drop tables statement
    private static final String DROP_RUNS_TABLE = "DROP TABLE IF EXISTS " + RUNS_TABLE_NAME;
    private static final String DROP_POINTS_TABLE = "DROP TABLE IF EXISTS " + POINTS_TABLE_NAME;


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RUNS_TABLE_CREATE);
        db.execSQL(POINTS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_POINTS_TABLE);
        db.execSQL(DROP_RUNS_TABLE);

        onCreate(db);
    }

    public long insertRunValues(String date,
                                String title,
                                float distance,
                                long runTime,
                                long totalRunTime,
                                long warmUpTime,
                                long coolDownTime,
                                int rating,
                                String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues insertValues = new ContentValues();

        //insertValues.put(COL_RUN_ID, runID);
        insertValues.put(COL_DATE, date);
        insertValues.put(COL_TITLE, title);
        insertValues.put(COL_DISTANCE, distance);
        insertValues.put(COL_RUN_TIME, runTime);
        insertValues.put(COL_TOTAL_RUN_TIME, totalRunTime);
        insertValues.put(COL_WARM_UP_TIME, warmUpTime);
        insertValues.put(COL_COOL_DOWN_TIME, coolDownTime);
        insertValues.put(COL_RATING, rating);
        insertValues.put(COL_COMMENT, comment);

        long id = db.insert(RUNS_TABLE_NAME, null, insertValues);

        db.close();

        return id;
    }

    /*public void insertPointValues(double latitude, double longitude, long timestamp, int runID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues insertValues = new ContentValues();

        //String stamp = String.valueOf(timestamp);

        Log.d("DB: ", "" + timestamp);

        //insertValues.put(COL_POINT_ID, pointID);
        insertValues.put(COL_LATITUDE, latitude);
        insertValues.put(COL_LONGITUDE, longitude);
        insertValues.put(COL_TIMESTAMP, timestamp);
        insertValues.put(COL_RUN_ID_FK, runID);

        db.insert(POINTS_TABLE_NAME, null, insertValues);

        db.close();
    }*/

    public void insertPointValues(double latitude, double longitude, Date time, int runID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues insertValues = new ContentValues();

        //String stamp = String.valueOf(timestamp);

        //Log.d("DB: ", "" + time);

        //insertValues.put(COL_POINT_ID, pointID);
        insertValues.put(COL_LATITUDE, latitude);
        insertValues.put(COL_LONGITUDE, longitude);
        insertValues.put(COL_TIMESTAMP, String.valueOf(time));
        insertValues.put(COL_RUN_ID_FK, runID);

        //Log.d("XXX", insertValues.getAsString("Timestamp"));

        db.insert(POINTS_TABLE_NAME, null, insertValues);

        db.close();
    }

    // Custom loader for returning all points associated with a run
    public List<Map<String,String>> loadPointsForRun(int runID) {
        List<Map<String,String>> lm = new ArrayList<Map<String,String>>();
        SQLiteDatabase db = this.getReadableDatabase();

        //String timeFormat = "printf(\"%.9f\", " + COL_TIMESTAMP +")";

        String[] selection = {COL_POINT_ID, COL_LATITUDE, COL_LONGITUDE, COL_TIMESTAMP, COL_RUN_ID_FK };
        //String[] selection = {COL_POINT_ID, COL_LATITUDE, COL_LONGITUDE, timeFormat, COL_RUN_ID_FK };

        String condition = "RunID = " + runID;

        Cursor c = db.query(POINTS_TABLE_NAME,	//The name of the table to query
                selection,				//The columns to return
                COL_RUN_ID_FK + "=" + runID,					//The columns for the where clause
                null,					//The values for the where clause
                null,					//Group the rows
                null,					//Filter the row groups
                null);					//The sort order

        c.moveToFirst();

        Log.d("COUNT", String.valueOf(c.getCount()));

        for(int i=0; i < c.getCount(); i++) {
            Map<String,String> map = new HashMap<String,String>();

            //Log.d("Retrieve: ", "" + c.getDouble(3));

            map.put("PointID", String.valueOf(c.getString(0)));
            map.put("Latitude", String.valueOf(c.getFloat(1)));
            map.put("Longitude", String.valueOf(c.getFloat(2)));
            map.put("Timestamp", c.getString(3));
            map.put("RunID", String.valueOf(c.getInt(4)));

            lm.add(map);
            c.moveToNext();
        }

        c.close();
        db.close();

        return lm;
    }

    public Map<String, String> loadRunByID(int runID) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] selection = { COL_RUN_ID,
                               COL_DATE,
                               COL_TITLE,
                               COL_DISTANCE,
                               COL_RUN_TIME,
                               COL_TOTAL_RUN_TIME,
                               COL_WARM_UP_TIME,
                               COL_COOL_DOWN_TIME,
                               COL_RATING,
                               COL_COMMENT };

        Cursor c = db.query(RUNS_TABLE_NAME,	//The name of the table to query
                selection,				        //The columns to return
                COL_RUN_ID + "=" + runID,		//The columns for the where clause
                null,					        //The values for the where clause
                null,					        //Group the rows
                null,					        //Filter the row groups
                null);					        //The sort order

        c.moveToFirst();

        Map<String,String> map = new HashMap<String,String>();

        map.put("RunID", c.getString(0));
        map.put("Date", c.getString(1));
        map.put("Title", c.getString(2));
        map.put("Distance", String.valueOf(c.getFloat(3)));
        map.put("RunTime", c.getString(4));
        map.put("TotalRunTime", c.getString(5));
        map.put("WarmUpTime", c.getString(6));
        map.put("CoolDownTime", c.getString(7));
        map.put("Rating", c.getString(8));
        map.put("Comment", c.getString(9));

        c.close();
        db.close();

        return map;
    }


    public List<String> loadRunDates() {
        List<String> lm = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] selection = { COL_DATE };

        Cursor c = db.query(RUNS_TABLE_NAME,	//The name of the table to query
                selection,				//The columns to return
                null,					//The columns for the where clause
                null,					//The values for the where clause
                null,					//Group the rows
                null,					//Filter the row groups
                null);					//The sort order

        c.moveToFirst();

        for(int i=0; i < c.getCount(); i++) {
            lm.add(c.getString(0));
            c.moveToNext();
        }

        c.close();
        db.close();

        return lm;
    }

    public List<RunData> loadRunDataToObject() {
        List<RunData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] selection = {  COL_RUN_ID,
                                COL_DATE,
                                COL_TITLE,
                                COL_DISTANCE,
                                COL_RUN_TIME,
                                COL_TOTAL_RUN_TIME,
                                COL_WARM_UP_TIME,
                                COL_COOL_DOWN_TIME,
                                COL_RATING,
                                COL_COMMENT };

        Cursor c = db.query(RUNS_TABLE_NAME,	//The name of the table to query
                selection,				//The columns to return
                null,					//The columns for the where clause
                null,					//The values for the where clause
                null,					//Group the rows
                null,					//Filter the row groups
                null);					//The sort order

        c.moveToFirst();

        for(int i=0; i < c.getCount(); i++) {
            RunData run = new RunData(Integer.parseInt(c.getString(0)));

            run.setDate(c.getString(1))
                    .setTitle(c.getString(2))
                    .setDistance(c.getFloat(3))
                    .setRunTime(c.getLong(4))
                    .setTotalRunTime(c.getLong(5))
                    .setWarmUpTime(c.getLong(6))
                    .setCoolDownTime(c.getLong(7))
                    .setRating(c.getString(8))
                    .setComment(c.getString(9));
            list.add(run);
            c.moveToNext();
        }

        c.close();
        db.close();

        return list;
    }


    public List<Map<String,String>> loadRunDataToHash() {
        List<Map<String,String>> lm = new ArrayList<Map<String,String>>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] selection = {  COL_RUN_ID,
                COL_DATE,
                COL_TITLE,
                COL_DISTANCE,
                COL_RUN_TIME,
                COL_TOTAL_RUN_TIME,
                COL_WARM_UP_TIME,
                COL_COOL_DOWN_TIME,
                COL_RATING,
                COL_COMMENT };

        Cursor c = db.query(RUNS_TABLE_NAME,	//The name of the table to query
                selection,				//The columns to return
                null,					//The columns for the where clause
                null,					//The values for the where clause
                null,					//Group the rows
                null,					//Filter the row groups
                null);					//The sort order

        c.moveToFirst();

        for(int i=0; i < c.getCount(); i++) {
            Map<String,String> map = new HashMap<String,String>();

            map.put("RunID", c.getString(0));
            map.put("Date", c.getString(1));
            map.put("Title", c.getString(2));
            map.put("Distance", String.valueOf(c.getFloat(3)));
            map.put("RunTime", c.getString(4));
            map.put("TotalRunTime", c.getString(5));
            map.put("WarmUpTime", c.getString(6));
            map.put("CoolDownTime", c.getString(7));
            map.put("Rating", String.valueOf(c.getInt(8)));
            map.put("Comment", c.getString(7));

            lm.add(map);
            c.moveToNext();
        }

        c.close();
        db.close();

        return lm;
    }

    public List<Map<String,String>> loadPointDataToHash() {
        List<Map<String,String>> lm = new ArrayList<Map<String,String>>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] selection = {COL_POINT_ID, COL_LATITUDE, COL_LONGITUDE, COL_TIMESTAMP, COL_RUN_ID_FK};

        Cursor c = db.query(POINTS_TABLE_NAME,	//The name of the table to query
                selection,				//The columns to return
                null,					//The columns for the where clause
                null,					//The values for the where clause
                null,					//Group the rows
                null,					//Filter the row groups
                null);					//The sort order

        c.moveToFirst();

        for(int i=0; i < c.getCount(); i++) {
            Map<String,String> map = new HashMap<String,String>();

            map.put("PointID", String.valueOf(c.getString(0)));
            map.put("Latitude", String.valueOf(c.getFloat(1)));
            map.put("Longitude", String.valueOf(c.getFloat(2)));
            map.put("Timestamp", String.valueOf(c.getFloat(3)));
            map.put("RunID", String.valueOf(c.getInt(4)));

            lm.add(map);
            c.moveToNext();
        }

        c.close();
        db.close();

        return lm;
    }

    public void updateNewRun(int runID, String title, float distance, String[] runTimes, String rating, String comment) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues cv = new ContentValues();

        Log.d("Comment", comment);
        Log.d("DB rating", rating);

        cv.put(COL_TITLE, title);
        cv.put(COL_DISTANCE, String.valueOf(distance));
        cv.put(COL_RUN_TIME, runTimes[0]);
        cv.put(COL_WARM_UP_TIME, runTimes[1]);
        cv.put(COL_COOL_DOWN_TIME, runTimes[2]);
        cv.put(COL_TOTAL_RUN_TIME, runTimes[3]);
        cv.put(COL_RATING, rating);
        cv.put(COL_COMMENT, comment);

        db.update(RUNS_TABLE_NAME, cv, COL_RUN_ID + "=" + runID, null);

        db.close();
    }

    public void updateEditRun(int runID, String title, String rating, String comment) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues cv = new ContentValues();

        Log.d("DB rating", rating);

        cv.put(COL_TITLE, title);
        cv.put(COL_RATING, rating);
        cv.put(COL_COMMENT, comment);

        db.update(RUNS_TABLE_NAME, cv, COL_RUN_ID + "=" + runID, null);

        db.close();
    }

    public void deleteRun(int runID) {
        SQLiteDatabase db = this.getReadableDatabase();

        db.execSQL("delete from " + POINTS_TABLE_NAME + " where " + COL_RUN_ID_FK + "=" + runID);
        db.execSQL("delete from " + RUNS_TABLE_NAME + " where " + COL_RUN_ID + "=" + runID);

        db.close();
    }
}