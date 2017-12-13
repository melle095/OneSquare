package com.a_leonov.onesquare.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.a_leonov.onesquare.BuildConfig;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

/**
 * Created by Пользователь on 20.11.2017.
 */

public class OneService extends IntentService {

    private final String LOG_TAG = getClass().getSimpleName();

    public static final String radius = "1000";
    public static final String CITY_EXTRA = "gce";
    public static final String lat = "lat";
    public static final String lon = "lon";

    public static final String CATEGORY = "cat";


    private final String VENUE_ID = "id";
    private final String NAME = "name";
    private final String PHONE = "phone";
    private final String FORMATTEDPHONE = "formattedPhone";
    private final String TWITTER = "twitter";
    private final String INSTAGRAMM = "instagram";
    private final String FACEBOOK = "facebook";
    private final String FACEBOOKUSER = "facebookUsername";
    private final String FACEBOOKNAME = "facebookName";
    private final String VERIFIED = "verified";
    private final String URL = "url";
    private final String STATUS = "hours";
    private final String ISOPEN = "isOpen";
    private final String ISLOCALHOLIDAY = "isLocalHoliday";
    private final String POPULAR = "popular";
    private final String RATING = "rating";
    private final String SHORTURL = "shortUrl";
    private final String CANONICALURL = "canonicalUrl";
    private final String ADDRESS = "address";
    private final String CROSSSTREET = "crossStreet";
    private final String COORD_LAT = "lat";
    private final String COORD_LONG = "lng";
    private final String POSTALCODE = "postalCode";
    private final String CC = "cc";
    private final String CITY = "city";
    private final String STATE = "state";
    private final String COUNTRY = "country";

    String category;
    String current_lat;
    String current_lon;


    public OneService() {
        super("onesquare");
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

//        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
//        NetworkInfo ni = cm.getActiveNetworkInfo();
//        if (ni == null || !ni.isConnected()) {
//            Log.w(LOG_TAG, "Not online, not refreshing.");
//            return;
//        }

        category = intent.getStringExtra(CATEGORY);

        Log.i(LOG_TAG, " Start OneService. By category " + category);


        current_lat = intent.getStringExtra(lat);
        current_lon = intent.getStringExtra(lon);


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String oneSquareJsonStr = null;
        String format = "json";

        try {
            final String API_URL = "https://api.foursquare.com/v2";
            final String VENUES = "venues";
            final String SEARCH = "search";
            final String NEAR = "near";
            final String geo_loc = "ll";
            final String INTENT = "intent";
            final String PHOTOS = "photos";
            final String CLIENT_ID = "client_id";
            final String CLIENT_SECRET = "client_secret";
            final String CURRENT_DATE = "v";
            final String CATEGORY_ID = "categoryId";
            final String LIMIT = "limit";
            final String RADIUS = "radius";

            Uri builtUri = Uri.parse(API_URL).buildUpon()
                    .appendPath(VENUES)
                    .appendPath(SEARCH)
                    .encodedQuery("&" + geo_loc + "=" + current_lat + "," + current_lon)
                    .appendQueryParameter(CATEGORY_ID, category)
                    .appendQueryParameter(LIMIT, "50")
                    .appendQueryParameter(RADIUS, "500")
                    .appendQueryParameter(CLIENT_ID, BuildConfig.CLIENT_ID)
                    .appendQueryParameter(CLIENT_SECRET, BuildConfig.CLIENT_SECRET)
                    .appendQueryParameter(CURRENT_DATE, timeMilisToString(System.currentTimeMillis()))
                    .build();

            URL url = new URL(builtUri.toString());

            Log.d(LOG_TAG, builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

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

//            getContentResolver().delete(FoursquareContract.VenuesEntry.CONTENT_URI, null, null);

            getVenueDataFromJson(oneSquareJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
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
        return;
    }

    private String timeMilisToString(long milis) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(milis);

        return sd.format(calendar.getTime());
    }

    private void getVenueDataFromJson(String venueJsonStr) throws JSONException {

        try {

            JSONObject jsonObj = (JSONObject) new JSONTokener(venueJsonStr).nextValue();
            JSONArray venues = jsonObj.getJSONObject("response").getJSONArray("venues");

            int length = venues.length();

            Vector<ContentValues> cVVector = new Vector<ContentValues>(length);

            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    JSONObject item = (JSONObject) venues.get(i);

                    ContentValues venueValues = new ContentValues();

                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_VEN_KEY, VENUE_ID, 1);
                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_NAME, NAME, 1);
                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_VERIFIED, VERIFIED, 1);
                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_URL, URL, 1);
                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_STATUS, STATUS, 1);
                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_ISOPEN, ISOPEN, 1);
                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_ISLOCALHOLIDAY, ISLOCALHOLIDAY, 1);
                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_POPULAR, POPULAR, 1);
                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_RATING, RATING, 1);
                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_SHORTURL, SHORTURL, 1);
                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_CANONICALURL, CANONICALURL, 1);

                    JSONObject location = (JSONObject) item.getJSONObject("location");

                    putJsonValue(venueValues, location, FoursquareContract.VenuesEntry.COLUMN_ADDRESS, ADDRESS, 1);
                    putJsonValue(venueValues, location, FoursquareContract.VenuesEntry.COLUMN_CROSSSTREET, CROSSSTREET, 1);
                    putJsonValue(venueValues, location, FoursquareContract.VenuesEntry.COLUMN_CITY, CITY, 1);
                    putJsonValue(venueValues, location, FoursquareContract.VenuesEntry.COLUMN_CC, CC, 1);
                    putJsonValue(venueValues, location, FoursquareContract.VenuesEntry.COLUMN_POSTALCODE, POSTALCODE, 2);
                    putJsonValue(venueValues, location, FoursquareContract.VenuesEntry.COLUMN_COUNTRY, COUNTRY, 1);
                    putJsonValue(venueValues, location, FoursquareContract.VenuesEntry.COLUMN_COORD_LAT, COORD_LAT, 3);
                    putJsonValue(venueValues, location, FoursquareContract.VenuesEntry.COLUMN_COORD_LONG, COORD_LONG, 3);
                    putJsonValue(venueValues, location, FoursquareContract.VenuesEntry.COLUMN_STATE, STATE, 1);

                    JSONObject contact = item.getJSONObject("contact");

                    putJsonValue(venueValues, contact, FoursquareContract.VenuesEntry.COLUMN_PHONE, PHONE, 1);
                    putJsonValue(venueValues, contact, FoursquareContract.VenuesEntry.COLUMN_FORMATTEDPHONE, FORMATTEDPHONE, 1);
                    putJsonValue(venueValues, contact, FoursquareContract.VenuesEntry.COLUMN_TWITTER, TWITTER, 1);
                    putJsonValue(venueValues, contact, FoursquareContract.VenuesEntry.COLUMN_INSTAGRAMM, INSTAGRAMM, 1);
                    putJsonValue(venueValues, contact, FoursquareContract.VenuesEntry.COLUMN_FACEBOOK, FACEBOOK, 1);
                    putJsonValue(venueValues, contact, FoursquareContract.VenuesEntry.COLUMN_FACEBOOKNAME, FACEBOOKNAME, 1);
                    putJsonValue(venueValues, contact, FoursquareContract.VenuesEntry.COLUMN_FACEBOOKUSER, FACEBOOKUSER, 1);

                    venueValues.put(FoursquareContract.VenuesEntry.COLUMN_CATERGORY, category);

//                    JSONArray category = item.getJSONArray("categories");
//                    JSONObject category_item;
//
//                    for (int j=0; j < category.length(); j++) {
//                        category_item = category.getJSONObject(j);
////                        if (category_item.getString("Primary") == "true")
//                            putJsonValue(venueValues,category_item, FoursquareContract.VenuesEntry.COLUMN_CATERGORY,"id",1);
//                    }

                    cVVector.add(venueValues);
                }
                int inserted = 0;
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = this.getContentResolver().bulkInsert(FoursquareContract.VenuesEntry.CONTENT_URI, cvArray);

                }

                Log.d(LOG_TAG, "OneService Complete. " + cVVector.size() + " Inserted of category " + inserted);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
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
