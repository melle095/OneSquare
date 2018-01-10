package com.a_leonov.onesquare;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by a_leonov on 15.12.2017.
 */

public class Utils {

    private static final String LOG_TAG = "Utils";

    public Utils() {
        return;
    }

    public static double getDistanceBetweenTwoPoints(PointD p1, PointD p2) {
        double R = 6371000; // m
        double dLat = Math.toRadians(p2.x - p1.x);
        double dLon = Math.toRadians(p2.y - p1.y);
        double lat1 = Math.toRadians(p1.x);
        double lat2 = Math.toRadians(p2.x);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
                * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;

        return d;
    }

    public static PointD calculateDerivedPosition(PointD point, double range, double bearing) {
        double EarthRadius = 6371000; // m

        double latA = Math.toRadians(point.x);
        double lonA = Math.toRadians(point.y);
        double angularDistance = range / EarthRadius;
        double trueCourse = Math.toRadians(bearing);

        double lat = Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance)
                                * Math.cos(trueCourse));

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance)
                        * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);

        PointD newPoint = new PointD(lat, lon);

        return newPoint;

    }

    public static String timeMilisToString(long milis) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(milis);

        return sd.format(calendar.getTime());
    }

    public static boolean hasInternetConnection(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Log.w(LOG_TAG, "Not online, not refreshing.");
            return false;
        }

        return true;
    }

    public static void putJsonValue(ContentValues value, JSONObject item, String contractName, String paramName, int mode) {
        if (!item.isNull(paramName)) {
            try {
                switch (mode) {
                    case 1: {
                        value.put(contractName, item.getString(paramName));
                        break;
                    }
                    case 2: {
                        value.put(contractName, item.getInt(paramName));
                        break;
                    }
                    case 3: {
                        value.put(contractName, String.valueOf(item.getDouble(paramName)));
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static File getOutputMediaFile(Context context){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getApplicationContext().getPackageName()
                + "/Files");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        File mediaFile;
        Random generator = new Random();
        int n = 1000;
        n = generator.nextInt(n);
        String mImageName = "Image-"+ n +".jpg";

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }


}
