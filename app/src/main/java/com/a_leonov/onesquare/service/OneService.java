package com.a_leonov.onesquare.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

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

    public static final String CITY_EXTRA = "gce";

    private final String VENUE_ID = "id";
    private final String NAME = "name";
//    private final String CATERGORY = "venue_category";
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

            try {
                getVenueDataFromJson(oneSquareJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    private void getVenueDataFromJson(String venueJsonStr) throws JSONException {
//        Log.v(TAG, response);

        JSONObject jsonObj = (JSONObject) new JSONTokener(venueJsonStr).nextValue();

        JSONArray venues = (JSONArray) jsonObj.getJSONObject("response").getJSONArray("venues");

        int length = venues.length();

        Vector<ContentValues> cVVector = new Vector<ContentValues>(length);

        if (length > 0) {
            for (int i = 0; i < length; i++) {
                JSONObject item = (JSONObject) venues.get(i);

//                FsqVenue venue = new FsqVenue();
                ContentValues venueValues = new ContentValues();

                venueValues.put(FoursquareContract.VenuesEntry.COLUMN_VEN_KEY, item.getString(VENUE_ID));
                venueValues.put(FoursquareContract.VenuesEntry.COLUMN_NAME, item.getString(NAME));
                venueValues.put(FoursquareContract.VenuesEntry.COLUMN_VERIFIED, item.getString(VERIFIED));
                venueValues.put(FoursquareContract.VenuesEntry.COLUMN_URL, item.getString(URL));
//                venueValues.put(FoursquareContract.VenuesEntry.COLUMN_STATUS, item.getString(STATUS));
//                venueValues.put(FoursquareContract.VenuesEntry.COLUMN_ISOPEN, item.getString(ISOPEN));
//                venueValues.put(FoursquareContract.VenuesEntry.COLUMN_ISLOCALHOLIDAY, item.getString(ISLOCALHOLIDAY));
//                venueValues.put(FoursquareContract.VenuesEntry.COLUMN_POPULAR, item.getString(POPULAR));
//                venueValues.put(FoursquareContract.VenuesEntry.COLUMN_RATING, item.getString(RATING));
//                venueValues.put(FoursquareContract.VenuesEntry.COLUMN_SHORTURL, item.getString(SHORTURL));
//                venueValues.put(FoursquareContract.VenuesEntry.COLUMN_CANONICALURL, item.getString(CANONICALURL));

                JSONObject location = (JSONObject) item.getJSONObject("location");
                venueValues.put(FoursquareContract.VenuesEntry.COLUMN_ADDRESS, location.getString(ADDRESS));
                venueValues.put(FoursquareContract.VenuesEntry.COLUMN_CROSSSTREET, location.getString(CROSSSTREET));
                venueValues.put(FoursquareContract.VenuesEntry.COLUMN_CITY, location.getString(CITY));
                venueValues.put(FoursquareContract.VenuesEntry.COLUMN_CC, location.getString(CC));
                venueValues.put(FoursquareContract.VenuesEntry.COLUMN_POSTALCODE, location.getInt(POSTALCODE));
                venueValues.put(FoursquareContract.VenuesEntry.COLUMN_COUNTRY, location.getString(COUNTRY));
                venueValues.put(FoursquareContract.VenuesEntry.COLUMN_COORD_LAT, location.getDouble("lat"));
                venueValues.put(FoursquareContract.VenuesEntry.COLUMN_COORD_LONG, location.getDouble("lng"));

                cVVector.add(venueValues);
            }
        }
    }
}
