package com.a_leonov.onesquare.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.Nullable;
import android.util.Log;

import com.a_leonov.onesquare.data.FoursquareContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Пользователь on 20.11.2017.
 */

public class OneService extends IntentService {

    private final String LOG_TAG = getClass().getSimpleName();

    public static final String CITY_EXTRA = "gce";

    public OneService() {
        super("onesquare");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String cityExtra = intent.getStringExtra(CITY_EXTRA);

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String oneSquareJsonStr = null;
        String format = "json";

        try
        {
            final String API_URL = "https://api.foursquare.com/v2/venues/search?";
            final String CLIENT_ID = "client_id";
            final String CLIENT_SECRET = "client_secret";
            final String CURRENT_DATE = "v";
            final String CITY = "near";
            final String CATEGORY_ID = "categoryId";
            final String city_param = "Moscow, RU";

            Uri builtUri = Uri.parse(API_URL).buildUpon()
                    .appendQueryParameter(CITY, city_param)
                    .appendQueryParameter(CATEGORY_ID, FoursquareContract.CATEGORY_COFFEE)
                    .appendQueryParameter(CLIENT_ID, FoursquareContract.client_id)
                    .appendQueryParameter(CLIENT_SECRET, FoursquareContract.client_secret)
                    .appendQueryParameter(CURRENT_DATE, timeMilisToString(System.currentTimeMillis()))
                    .build();
            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            oneSquareJsonStr = buffer.toString();


            Log.d(LOG_TAG, oneSquareJsonStr );
//            getWeatherDataFromJson(forecastJsonStr, locationQuery);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        }

    }

    private String timeMilisToString(long milis) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(milis);

        return sd.format(calendar.getTime());
    }
}
