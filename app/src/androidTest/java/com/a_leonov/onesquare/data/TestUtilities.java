package com.a_leonov.onesquare.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;


public class TestUtilities extends AndroidTestCase {
    static final String TEST_LOCATION = "Moscow";
    static final String TEST_QUERY = "coffee";

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /*
        Students: Use this to create some default weather values for your database tests.
     */
    static ContentValues createWeatherValues(long locationRowId) {
        ContentValues venuesValues = new ContentValues();
        venuesValues.put(FoursquareContract.VenuesEntry.COLUMN_LOC_KEY, locationRowId);
        venuesValues.put(FoursquareContract.VenuesEntry.COLUMN_VEN_KEY, "412d2800f964a520df0c1fe3");
        venuesValues.put(FoursquareContract.VenuesEntry.COLUMN_FACEBOOK, "37965424481");
        venuesValues.put(FoursquareContract.VenuesEntry.COLUMN_FACEBOOKNAME, "Central Park");
        venuesValues.put(FoursquareContract.VenuesEntry.COLUMN_FACEBOOKUSER, "centralparknyc");
        venuesValues.put(FoursquareContract.VenuesEntry.COLUMN_FORMATTEDPHONE,"(212) 310-6600" );
        venuesValues.put(FoursquareContract.VenuesEntry.COLUMN_INSTAGRAMM, "centralparknyc" );
        venuesValues.put(FoursquareContract.VenuesEntry.COLUMN_TWITTER, "centralparknyc");
        venuesValues.put(FoursquareContract.VenuesEntry.COLUMN_VERIFIED, "true");
        venuesValues.put(FoursquareContract.VenuesEntry.COLUMN_ISLOCALHOLIDAY,"true" );
        venuesValues.put(FoursquareContract.VenuesEntry.COLUMN_ISOPEN,"false" );
        venuesValues.put(FoursquareContract.VenuesEntry.COLUMN_STATUS, "Open until 1:00 AM");
        venuesValues.put(FoursquareContract.VenuesEntry.COLUMN_NAME,"Central Park" );
        venuesValues.put(FoursquareContract.VenuesEntry.COLUMN_PHONE, "2123106600");
        venuesValues.put(FoursquareContract.VenuesEntry.COLUMN_POPULAR, "");
        venuesValues.put(FoursquareContract.VenuesEntry.COLUMN_RATING,9.8 );

        return venuesValues;
    }

    /*
        Students: You can uncomment this helper function once you have finished creating the
        LocationEntry part of the WeatherContract.
     */
    static ContentValues createMoscowLocationValues() {
        // Create a new map of values, where column names are the keys
        ContentValues locationValues = new ContentValues();
        locationValues.put(FoursquareContract.LocationEntry.COLUMN_CITY, "Moscow");
        locationValues.put(FoursquareContract.LocationEntry.COLUMN_POSTALCODE, "127006");
        locationValues.put(FoursquareContract.LocationEntry.COLUMN_CC, "RU");
        locationValues.put(FoursquareContract.LocationEntry.COLUMN_COUNTRY, "RUSSIA");
        locationValues.put(FoursquareContract.LocationEntry.COLUMN_COORD_LAT, 55.7734);
        locationValues.put(FoursquareContract.LocationEntry.COLUMN_COORD_LONG, 37.6028);

        return locationValues;
    }

    /*
        Students: You can uncomment this function once you have finished creating the
        LocationEntry part of the WeatherContract as well as the WeatherDbHelper.
     */
    static long insertMoscowLocationValues(Context context) {
        // insert our test records into the database
        FoursquareDbHelper dbHelper = new FoursquareDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMoscowLocationValues();

        long locationRowId;
        locationRowId = db.insert(FoursquareContract.LocationEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        return locationRowId;
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new com.a_leonov.onesquare.utils.PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
