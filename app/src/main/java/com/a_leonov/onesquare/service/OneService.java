package com.a_leonov.onesquare.service;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.a_leonov.onesquare.BuildConfig;
import com.a_leonov.onesquare.PointD;
import com.a_leonov.onesquare.R;
import com.a_leonov.onesquare.Utils;
import com.a_leonov.onesquare.data.FoursquareContract;
import com.a_leonov.onesquare.widget.WidgetProvider;

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

import static com.a_leonov.onesquare.Utils.BUNDLE_LAT;
import static com.a_leonov.onesquare.Utils.BUNDLE_LON;
import static com.a_leonov.onesquare.Utils.putJsonValue;


public class OneService extends IntentService {

    private final String LOG_TAG = getClass().getSimpleName();

    public static final String radius = "1000";
    public static final String CITY_EXTRA = "gce";
    public static final String lat = "lat";
    public static final String lon = "lon";

    public static final String CATEGORY = "cat";

    //**********venue fields*****************************************
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
    private final String STATUS = "status";
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
    private final String PREFIX = "prefix";
    private final String SUFFIX = "suffix";
    //*********************************************************

    String category;
    double current_lat;
    double current_lon;




    public OneService() {
        super("onesquare");
    }

    public static void startOneService(Context context, Location location) {
        if (location != null) {
            Intent venueIntent = new Intent(context, OneService.class);
            venueIntent.putExtra(OneService.CATEGORY, FoursquareContract.CATEGORY_FOOD);
            venueIntent.putExtra(BUNDLE_LAT, location.getLatitude());
            venueIntent.putExtra(BUNDLE_LON, location.getLongitude());

            context.startService(venueIntent);
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        category = intent.getStringExtra(CATEGORY);

        current_lat = intent.getDoubleExtra(lat, 0);
        current_lon = intent.getDoubleExtra(lon, 0);

        if (Utils.hasInternetConnection(this)) {
            try {
                getVenueDataFromJson(parseJSONVenue(null));
                updateWidgets();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

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
            JSONArray items = jsonObj.getJSONObject("response")
                    .getJSONArray("groups")
                    .getJSONObject(0)
                    .getJSONArray("items");

            int length = items.length();

            Vector<ContentValues> cVVector = new Vector<ContentValues>(length);

            if (length > 0) {
                for (int i = 0; i < length; i++) {

                    JSONObject item = items.getJSONObject(i).getJSONObject("venue");

                    ContentValues venueValues = new ContentValues();

                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_VEN_KEY, VENUE_ID, 1);
                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_NAME, NAME, 1);
                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_VERIFIED, VERIFIED, 1);
                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_URL, URL, 1);
//                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_ISOPEN, ISOPEN, 1);
//                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_ISLOCALHOLIDAY, ISLOCALHOLIDAY, 1);
//                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_POPULAR, POPULAR, 1);
                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_RATING, RATING, 1);
                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_SHORTURL, SHORTURL, 1);
                    putJsonValue(venueValues, item, FoursquareContract.VenuesEntry.COLUMN_CANONICALURL, CANONICALURL, 1);


                    try {
                        JSONObject hours = (JSONObject) item.getJSONObject("hours");
                        putJsonValue(venueValues, hours, FoursquareContract.VenuesEntry.COLUMN_STATUS, STATUS, 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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

                    double target_lat = location.getDouble(COORD_LAT);
                    double target_lon = location.getDouble(COORD_LONG);

                    PointD targetPoint = new PointD(target_lat, target_lon);
                    PointD currentPoint = new PointD(current_lat, current_lon);

                    venueValues.put(FoursquareContract.VenuesEntry.COLUMN_DISTANCE, Utils.getDistanceBetweenTwoPoints(targetPoint, currentPoint));

                    JSONObject contact = item.getJSONObject("contact");

                    putJsonValue(venueValues, contact, FoursquareContract.VenuesEntry.COLUMN_PHONE, PHONE, 1);
                    putJsonValue(venueValues, contact, FoursquareContract.VenuesEntry.COLUMN_FORMATTEDPHONE, FORMATTEDPHONE, 1);
                    putJsonValue(venueValues, contact, FoursquareContract.VenuesEntry.COLUMN_TWITTER, TWITTER, 1);
                    putJsonValue(venueValues, contact, FoursquareContract.VenuesEntry.COLUMN_INSTAGRAMM, INSTAGRAMM, 1);
                    putJsonValue(venueValues, contact, FoursquareContract.VenuesEntry.COLUMN_FACEBOOK, FACEBOOK, 1);
                    putJsonValue(venueValues, contact, FoursquareContract.VenuesEntry.COLUMN_FACEBOOKNAME, FACEBOOKNAME, 1);
                    putJsonValue(venueValues, contact, FoursquareContract.VenuesEntry.COLUMN_FACEBOOKUSER, FACEBOOKUSER, 1);

                    JSONObject photo = item.getJSONObject("photos")
                            .getJSONArray("groups")
                            .getJSONObject(0)
                            .getJSONArray("items")
                            .getJSONObject(0);

                    putJsonValue(venueValues, photo, FoursquareContract.VenuesEntry.COLUMN_PHOTO_PREFIX, PREFIX, 1);
                    putJsonValue(venueValues, photo, FoursquareContract.VenuesEntry.COLUMN_PHOTO_SUFFIX, SUFFIX, 1);

//                    venueValues.put(FoursquareContract.VenuesEntry.COLUMN_CATERGORY, category);

                    cVVector.add(venueValues);
                }
                int inserted = 0;
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = this.getContentResolver().bulkInsert(FoursquareContract.VenuesEntry.CONTENT_URI, cvArray);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private String parseJSONVenue(String venue_id) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String oneSquareJsonStr = null;
        String format = "json";

        try {
            final String API_URL = "https://api.foursquare.com/v2";
            final String VENUES = "venues";
            final String SEARCH = "explore";
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

            Uri builtUri = null;
            if (venue_id == null) {
                builtUri = Uri.parse(API_URL).buildUpon()
                        .appendPath(VENUES)
                        .appendPath(SEARCH)
                        .encodedQuery("&" + geo_loc + "=" + current_lat + "," + current_lon)
                        .appendQueryParameter("section", "food")
                        .appendQueryParameter("venuePhotos", "1")
                        .appendQueryParameter(LIMIT, "50")
                        .appendQueryParameter(RADIUS, "500")
                        .appendQueryParameter(CLIENT_ID, BuildConfig.CLIENT_ID)
                        .appendQueryParameter(CLIENT_SECRET, BuildConfig.CLIENT_SECRET)
                        .appendQueryParameter(CURRENT_DATE, timeMilisToString(System.currentTimeMillis()))
                        .build();
            } else {
                builtUri = Uri.parse(API_URL).buildUpon()
                        .appendPath(VENUES)
                        .appendPath(venue_id)
                        .appendPath(PHOTOS)
                        .appendQueryParameter(CLIENT_ID, BuildConfig.CLIENT_ID)
                        .appendQueryParameter(CLIENT_SECRET, BuildConfig.CLIENT_SECRET)
                        .appendQueryParameter(CURRENT_DATE, timeMilisToString(System.currentTimeMillis()))
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
                // Nothing to do.
                return null;
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
                return null;
            }

            return buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
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

    private void updateWidgets(){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, WidgetProvider.class));
        if (appWidgetIds.length > 0) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
            WidgetProvider.updateVenueWidgets(this, appWidgetManager, appWidgetIds);
        }
    }
}
