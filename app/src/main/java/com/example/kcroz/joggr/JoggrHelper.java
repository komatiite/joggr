package com.example.kcroz.joggr;

import android.location.Location;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

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
            }
            else {
                endLocation.setLatitude(Double.parseDouble(entry.get("Latitude")));
                endLocation.setLongitude(Double.parseDouble(entry.get("Longitude")));

                distance += startLocation.distanceTo(endLocation);

                startLocation.setLatitude(endLocation.getLatitude());
                startLocation.setLongitude(endLocation.getLongitude());
            }
        }

        return distance;
    }

    public static void exportToCSV(View view, List<Map<String, String>> inputData) {
        File directory = new File(Environment.getExternalStorageDirectory(), "joggr");
        directory.mkdir();
        String filename = getCurrentDate() + ".csv";
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
}
