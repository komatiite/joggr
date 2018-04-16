package com.example.kcroz.joggr;

import android.app.Activity;
import android.location.Location;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.example.kcroz.joggr.ExportType.AllPointData;

public class JoggrHelper {

    public static String getMonthName(int month) {
        String monthName;

        switch (month) {
            case 1:
                monthName = "Jan";
                break;
            case 2:
                monthName = "Feb";
                break;
            case 3:
                monthName = "Mar";
                break;
            case 4:
                monthName = "Apr";
                break;
            case 5:
                monthName = "May";
                break;
            case 6:
                monthName = "Jun";
                break;
            case 7:
                monthName = "Jul";
                break;
            case 8:
                monthName = "Aug";
                break;
            case 9:
                monthName = "Sep";
                break;
            case 10:
                monthName = "Oct";
                break;
            case 11:
                monthName = "Nov";
                break;
            case 12:
                monthName = "Dec";
                break;
            default:
                monthName = "Wow";
        }

        return monthName;
    }

    public static float calculateDistance(List<Map<String,String>> routeData) {

        Location startLocation = new Location("start");
        Location endLocation = new Location("end");
        int count = 0;
        float distance = 0;

        for (Map<String, String> entry : routeData) {

            if (count == 0) {
                startLocation.setLatitude(Double.parseDouble(entry.get("Latitude")));
                startLocation.setLongitude(Double.parseDouble(entry.get("Longitude")));

                count++;
            } else {
                endLocation.setLatitude(Double.parseDouble(entry.get("Latitude")));
                endLocation.setLongitude(Double.parseDouble(entry.get("Longitude")));

                distance += startLocation.distanceTo(endLocation);

                startLocation.setLatitude(endLocation.getLatitude());
                startLocation.setLongitude(endLocation.getLongitude());
            }
        }

        return ((int) distance / 1000);
    }

    public static float[] calculateRunTimes(List<Map<String,String>> routeData) {
        final int RUN_TIME = 0;
        final int WARM_UP = 1;
        final int COOL_DOWN = 2;
        final int TOTAL_TIME = 3;

        int count = 0;
        int lastIndex = routeData.size() - 1;
        Location startLocation = new Location("start");
        Location endLocation = new Location("end");

        Log.d("DATE: ", routeData.get(0).get("Timestamp"));

        Date startTime = new Date(routeData.get(0).get("Timestamp"));
        Date endTime = new Date(routeData.get(lastIndex).get("Timestamp"));

        float[] runningAverage = { 0, 0, 0, 0, 0};
        float[] results = { 0, 0, 0, 0 };
        boolean runFlag = false;
        int runAverageCount = 0;

        long totalTimeDifference = endTime.getTime() - startTime.getTime();

        Log.d("ABC", ""+totalTimeDifference);

        results[TOTAL_TIME] = totalTimeDifference / 1000;

        for(Map<String,String> point : routeData) {

            if (count == 0) {
                startLocation.setLatitude(Double.parseDouble(point.get("Latitude")));
                startLocation.setLongitude(Double.parseDouble(point.get("Longitude")));

                count++;
            }
            else {
                endLocation.setLatitude(Double.parseDouble(point.get("Latitude")));
                endLocation.setLongitude(Double.parseDouble(point.get("Longitude")));

                float distance = startLocation.distanceTo(endLocation);

                Log.d("DISTANCE", "" + distance);


                startLocation.setLatitude(endLocation.getLatitude());
                startLocation.setLongitude(endLocation.getLongitude());

                endTime = new Date(point.get("Timestamp"));

                long timeDifference = (endTime.getTime() - startTime.getTime()) / 1000;
                Log.d("TIME DIFF", ""+timeDifference);

                float metresPerSecond = distance / timeDifference;
                Log.d("MPS", metresPerSecond + "");

                float kmPerHour = metresPerSecond * 3.6f;

                Log.d("KM/HR", kmPerHour + "");


                runningAverage[0] = runningAverage[1];
                runningAverage[1] = runningAverage[2];
                runningAverage[2] = runningAverage[3];
                runningAverage[3] = runningAverage[4];
                runningAverage[4] = kmPerHour;

                float total = 0;

                for (int i = 0; i < runningAverage.length; i++) {
                    total += runningAverage[i];
                }

                float average = total / runningAverage.length;
                Log.d("AVG", average + "");


                if (average <= 4 && !runFlag) {
                    results[WARM_UP] += timeDifference;
                    Log.d("WARMUP", results[1] + "");

                }
                else if (average > 4) {
                    results[RUN_TIME] += timeDifference;

                    Log.d("RUN", results[0] + "");


                    // Ensure that user is well into run
                    if (runAverageCount == 100) {
                        runFlag = true;
                    }
                    else {
                        runAverageCount++;
                    }
                }
                else { // cool down
                    results[COOL_DOWN] += timeDifference;
                    Log.d("COOL", results[2]+"");

                }

                startTime = endTime;
            }
        }

        Log.d("0", results[RUN_TIME]+"");
        Log.d("1", results[WARM_UP]+"");
        Log.d("2", results[COOL_DOWN]+"");
        Log.d("3", results[TOTAL_TIME]+"");


        Log.d("end", "***********************************************");

        return results;
    }

    public static void exportToCSV(View view, List<Map<String, String>> inputData, ExportType type) {
        File directory = new File(Environment.getExternalStorageDirectory(), "joggr");
        directory.mkdir();
        String filename = type == AllPointData ? getCurrentDate() + ".csv" : "run_" + inputData.get(0).get("RunID") + ".csv" ;

        File file = new File(directory, filename);

        try {
            file.createNewFile();
            CSVWriter writer = new CSVWriter(new FileWriter(file));

            String[] header = { "PointID", "Latitude", "Longitude", "Timestamp", "RunID" };
            writer.writeNext(header);

            for (Map<String, String> entry : inputData) {
                String[] data = { entry.get("PointID"),
                                  entry.get("Latitude"),
                                  entry.get("Longitude"),
                                  entry.get("Timestamp"),
                                  entry.get("RunID"),
                };

                writer.writeNext(data);
            }

            writer.close();

            Toast.makeText(view.getContext(), "CSV exported to /joggr/" + filename , Toast.LENGTH_LONG).show();
        }
        catch (IOException e) {
            Toast.makeText(view.getContext(), "Error exporting CSV!", Toast.LENGTH_LONG).show();
            Log.d("error", "EROOR");
            e.printStackTrace();
        }
    }

    private static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

        return format.format(calendar.getTime());
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager manager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
