package com.a_leonov.onesquare.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.a_leonov.onesquare.BuildConfig;
import com.a_leonov.onesquare.Utils;
import com.a_leonov.onesquare.data.FoursquareContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;


public class VenueDetailsService extends IntentService {

    private final String LOG_TAG = getClass().getSimpleName();

    public static final String VEN_ID = "ven_id";
    public static final String VENDB_ID = "vendb_id";

    public static final String BROADCAST_ACTION =
            "com.a_leonov.onesquare.BROADCAST";

    public static final String EXTENDED_DATA_STATUS =
            "com.a_leonov.onesquare.STATUS";

    //**********venue fields*****************************************
    private final String PHOTO_ID = "id";
    private final String PREFIX = "prefix";
    private final String SUFFIX = "suffix";
    private final String FIRSTSNAME = "firstName";
    private final String LASTNAME = "lastName";
    private final String TIP_ID = "id";
    private final String TIP_TEXT = "text";
    //*********************************************************
    String venue_id;
    String venuedb_id;
    boolean isSuccessful;

    double current_lat;
    double current_lon;

    public VenueDetailsService() {
        super("onesquare");
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        isSuccessful = false;
        venue_id = intent.getStringExtra(VEN_ID);
        venuedb_id = intent.getStringExtra(VENDB_ID);

        Log.i(LOG_TAG, " Start VenueDetailsService. By ID: " + venue_id);

        if (Utils.hasInternetConnection(this)) {
            try {
                getVenueDataFromJson(parseJSONVenue(venue_id));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    String parseJSONVenue(String venue_id) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String oneSquareJsonStr = null;

        try {
            final String API_URL = "https://api.foursquare.com/v2/venues";
            final String CLIENT_ID = "client_id";
            final String CLIENT_SECRET = "client_secret";
            final String CURRENT_DATE = "v";

            Uri builtUri = null;
            if (venue_id != null) {
                builtUri = Uri.parse(API_URL).buildUpon()
                        .appendPath(venue_id)
                        .appendQueryParameter(CLIENT_ID, BuildConfig.CLIENT_ID)
                        .appendQueryParameter(CLIENT_SECRET, BuildConfig.CLIENT_SECRET)
                        .appendQueryParameter(CURRENT_DATE, Utils.timeMilisToString(System.currentTimeMillis()))
                        .build();
            }

            URL url = new URL(builtUri.toString());

            Log.d(LOG_TAG, builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {

                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            return buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    private void getVenueDataFromJson(String venueJsonStr) throws JSONException {

        try {

            JSONObject jsonObj = (JSONObject) new JSONTokener(venueJsonStr).nextValue();
            JSONObject venue = jsonObj.getJSONObject("response")
                    .getJSONObject("venue");

            JSONArray items = venue.getJSONObject("photos")
                    .getJSONArray("groups")
                    .getJSONObject(0)
                    .getJSONArray("items");

            JSONArray tips = venue.getJSONObject("tips")
                    .getJSONArray("groups")
                    .getJSONObject(0)
                    .getJSONArray("items");

            int length_items = items.length();
            int length_tips = tips.length();

            Vector<ContentValues> cVVector = new Vector<ContentValues>(length_items);
            Vector<ContentValues> cVVector_tips = new Vector<ContentValues>(length_tips);


            if (length_items > 0) {
                for (int i = 0; i < length_items; i++) {

                    JSONObject item = items.getJSONObject(i);

                    ContentValues venueValues = new ContentValues();

                    putJsonValue(venueValues, item, FoursquareContract.PhotoEntry.COLUMN_PHOTO_ID, PHOTO_ID, 1);
                    putJsonValue(venueValues, item, FoursquareContract.PhotoEntry.COLUMN_PREFIX, PREFIX, 1);
                    putJsonValue(venueValues, item, FoursquareContract.PhotoEntry.COLUMN_SUFFIX, SUFFIX, 1);

                    venueValues.put(FoursquareContract.PhotoEntry.COLUMN_VENUE_ID, venuedb_id);

                    cVVector.add(venueValues);
                }

                int inserted = 0;

                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = this.getContentResolver().bulkInsert(FoursquareContract.PhotoEntry.CONTENT_URI, cvArray);

                    Log.d(LOG_TAG, "VenueDetailService Complete. " + cVVector.size() + " Inserted of photos " + inserted);
                }
            }

            if (length_tips > 0) {
                for (int i = 0; i < length_tips; i++) {

                    JSONObject tip = tips.getJSONObject(i);

                    ContentValues tipValues = new ContentValues();

                    putJsonValue(tipValues, tip, FoursquareContract.TipEntry.COLUMN_TIP_ID, TIP_ID, 1);
                    putJsonValue(tipValues, tip, FoursquareContract.TipEntry.COLUMN_FIRSTNAME, FIRSTSNAME, 1);
                    putJsonValue(tipValues, tip, FoursquareContract.TipEntry.COLUMN_LASTNAME, LASTNAME, 1);
                    putJsonValue(tipValues, tip, FoursquareContract.TipEntry.COLUMN_TEXT, TIP_TEXT, 1);
                    putJsonValue(tipValues, tip, FoursquareContract.TipEntry.COLUMN_USER_PHOTO_PREFIX, PREFIX, 1);
                    putJsonValue(tipValues, tip, FoursquareContract.TipEntry.COLUMN_USER_PHOTO_SUFFIX, SUFFIX, 1);

                    tipValues.put(FoursquareContract.TipEntry.COLUMN_VENUE_ID, venuedb_id);

                    cVVector_tips.add(tipValues);
                }

                int inserted_tips = 0;

                if (cVVector_tips.size() > 0) {
                    ContentValues[] cvArray_tip = new ContentValues[cVVector_tips.size()];
                    cVVector_tips.toArray(cvArray_tip);
                    inserted_tips = this.getContentResolver().bulkInsert(FoursquareContract.TipEntry.CONTENT_URI, cvArray_tip);
                }

                Log.d(LOG_TAG, "VenueDetailService Complete. " + cVVector.size() + " Inserted of tips " + inserted_tips);
                isSuccessful = true;
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            isSuccessful = false;
        }
    }

    private void putJsonValue(ContentValues value, JSONObject item, String contractName, String paramName, int mode) {
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

}
