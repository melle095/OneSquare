
package com.a_leonov.onesquare.data;


import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;


public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();
    private static final String CITY_QUERY = "Moscow";
    private static final double lat = 55.760437;
    private static final double lon = 37.577946;
    private static final String VENUE_ID = "412d2800f964a520df0c1fe3";
    private static final long venue_row_id = 12;
    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.

       Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
       the delete functionality in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                FoursquareContract.VenuesEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                FoursquareContract.PhotoEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                FoursquareContract.VenuesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                FoursquareContract.PhotoEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Location table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
        Students: Uncomment this test to make sure you've correctly registered the WeatherProvider.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                VenueProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: WeatherProvider registered with authority: " + providerInfo.authority +
                    " instead of authority: " + FoursquareContract.CONTENT_AUTHORITY,
                    providerInfo.authority, FoursquareContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
            This test doesn't touch the database.  It verifies that the ContentProvider returns
            the correct type for each type of URI that it can handle.
            Students: Uncomment this test to verify that your implementation of GetType is
            functioning correctly.
         */
    public void testGetType() {
        // content://com.example.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(FoursquareContract.VenuesEntry.buildVenuesUri(12));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the VenuesEntry.buildVenuesUri(12) should return CONTENT_ITEM_TYPE",
                FoursquareContract.VenuesEntry.CONTENT_ITEM_TYPE, type);

        // content://com.example.android.sunshine.app/weather/
        type = mContext.getContentResolver().getType(FoursquareContract.VenuesEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the VenuesEntry.CONTENT_URI should return CONTENT_TYPE",
                FoursquareContract.VenuesEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine.app/weather/94074
        type = mContext.getContentResolver().getType(
                FoursquareContract.VenuesEntry.buildVenueCityUri(FoursquareContract.CATEGORY_COFFEE, CITY_QUERY));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the VenuesEntry.buildVenueCityUri(CITY_QUERY) should return CONTENT_TYPE",
                FoursquareContract.VenuesEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine.app/weather/94074/20140612
        type = mContext.getContentResolver().getType(
                FoursquareContract.VenuesEntry.buildVenuesGPSUri(FoursquareContract.CATEGORY_COFFEE, lat, lon));
        // vnd.android.cursor.item/com.example.android.sunshine.app/weather/1419120000
        assertEquals("Error: the VenuesEntry.buildVenuesGPSUri(lat, lon)should return CONTENT_TYPE",
                FoursquareContract.VenuesEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine.app/location/
        type = mContext.getContentResolver().getType(FoursquareContract.PhotoEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals("Error: the PhotoEntry.CONTENT_URI should return CONTENT_TYPE",
                FoursquareContract.PhotoEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(FoursquareContract.PhotoEntry.buildPhotoUri(venue_row_id));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals("Error: the PhotoEntry.buildPhotoUri(12) should return CONTENT_ITEM_TYPE",
                FoursquareContract.PhotoEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(FoursquareContract.PhotoEntry.buildPhotoByVenueUri(VENUE_ID));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals("Error: the PhotoEntry.buildPhotoByVenueUri(VENUE_ID) should return CONTENT_TYPE",
                FoursquareContract.PhotoEntry.CONTENT_TYPE, type);
    }


    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if the basic weather query functionality
        given in the ContentProvider is working correctly.
     */
    public void testBasicVenueQuery() {
        // insert our test records into the database
        FoursquareDbHelper dbHelper = new FoursquareDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues venuesValues = TestUtilities.createVenuesValues(VENUE_ID);
        long venueRowId = TestUtilities.insertVenueValues(mContext);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues photoValues = TestUtilities.createPhotoValues(venueRowId);

        long photoRowId = db.insert(FoursquareContract.PhotoEntry.TABLE_NAME, null, photoValues);
        assertTrue("Unable to Insert photoEntry into the Database", photoRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor venueCursor = mContext.getContentResolver().query(
                FoursquareContract.VenuesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicVenueQuery", venueCursor, venuesValues);
    }

    public void testCityVenueQuery() {
        // insert our test records into the database
        FoursquareDbHelper dbHelper = new FoursquareDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues venuesValues = TestUtilities.createVenuesValues(VENUE_ID);
        long venueRowId = TestUtilities.insertVenueValues(mContext);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues photoValues = TestUtilities.createPhotoValues(venueRowId);

        long photoRowId = db.insert(FoursquareContract.PhotoEntry.TABLE_NAME, null, photoValues);
        assertTrue("Unable to Insert photoEntry into the Database", photoRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor venueCursor = mContext.getContentResolver().query(
                FoursquareContract.VenuesEntry.buildVenueCityUri(FoursquareContract.CATEGORY_COFFEE, CITY_QUERY),
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testCityVenueQuery", venueCursor, venuesValues);
    }


    public void testBasicPhotoQueries() {
        // insert our test records into the database
        FoursquareDbHelper dbHelper = new FoursquareDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues photoValues = TestUtilities.createPhotoValues(venue_row_id);
        long photoRowId = TestUtilities.insertPhotoVenueValues(mContext);

        // Test the basic content provider query
        Cursor photoCursor = mContext.getContentResolver().query(
                FoursquareContract.PhotoEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicPhotoQueries, photo query", photoCursor, photoValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Photo Query did not properly set NotificationUri",
                    photoCursor.getNotificationUri(), FoursquareContract.PhotoEntry.CONTENT_URI);
        }
    }

    /*
        This test uses the provider to insert and then update the data. Uncomment this test to
        see if your update location is functioning correctly.
     */
    public void testUpdateVenue() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createVenuesValues(VENUE_ID);

        Uri venueUri = mContext.getContentResolver().
                insert(FoursquareContract.VenuesEntry.CONTENT_URI, values);
        long venueRowId = ContentUris.parseId(venueUri);
        // Verify we got a row back.
        assertTrue(venueRowId != -1);
        Log.d(LOG_TAG, "New row id: " + venueRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(FoursquareContract.VenuesEntry._ID, venueRowId);
        updatedValues.put(FoursquareContract.VenuesEntry.COLUMN_NAME, "Starbucs coffee");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor venueCursor = mContext.getContentResolver().query(FoursquareContract.VenuesEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        venueCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                FoursquareContract.VenuesEntry.CONTENT_URI, updatedValues, FoursquareContract.VenuesEntry._ID + "= ?",
                new String[] { Long.toString(venueRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        venueCursor.unregisterContentObserver(tco);
        venueCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                FoursquareContract.VenuesEntry.CONTENT_URI,
                null,   // projection
                FoursquareContract.VenuesEntry._ID + " = " + venueRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateVenue.  Error validating venue entry update.",
                cursor, updatedValues);

        cursor.close();
    }


    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the insert functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createVenuesValues(VENUE_ID);

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(FoursquareContract.VenuesEntry.CONTENT_URI, true, tco);
        Uri venue_Id = mContext.getContentResolver().insert(FoursquareContract.VenuesEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long venueRowId = ContentUris.parseId(venue_Id);

        // Verify we got a row back.
        assertTrue(venueRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                FoursquareContract.VenuesEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating VenuesEntry.",
                cursor, testValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues photoValues = TestUtilities.createPhotoValues(venueRowId);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(FoursquareContract.PhotoEntry.CONTENT_URI, true, tco);

        Uri photoInsertUri = mContext.getContentResolver().insert(FoursquareContract.PhotoEntry.CONTENT_URI, photoValues);
        assertTrue(photoInsertUri != null);

        // Did our content observer get called?  Students:  If this fails, your insert weather
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor photoCursor = mContext.getContentResolver().query(
                FoursquareContract.PhotoEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating PhotoEntry insert.",
                photoCursor, photoValues);

        // Add the location values in with the weather data so that we can make
        // sure that the join worked and we actually get all the values back
        photoValues.putAll(testValues);

        // Get the joined Weather and Location data
        photoCursor = mContext.getContentResolver().query(
                FoursquareContract.VenuesEntry.buildVenuesUri(venueRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        Log.d(getClass().getSimpleName(), "TestProvider: " + DatabaseUtils.dumpCursorToString(photoCursor));
        Log.d(getClass().getSimpleName(), "TestValue: " + photoValues.toString());

        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined photo Data.",
                photoCursor, photoValues);

    }


    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our location delete.
       TestUtilities.TestContentObserver venueObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(FoursquareContract.VenuesEntry.CONTENT_URI, true, venueObserver);

        // Register a content observer for our weather delete.
        TestUtilities.TestContentObserver photoObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(FoursquareContract.PhotoEntry.CONTENT_URI, true, photoObserver);

        deleteAllRecordsFromProvider();

        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        venueObserver.waitForNotificationOrFail();
        photoObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(venueObserver);
        mContext.getContentResolver().unregisterContentObserver(photoObserver);
    }


    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertVenueValues(String Venue_Id) {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues venueValues = new ContentValues();
            venueValues.put(FoursquareContract.VenuesEntry.COLUMN_VEN_KEY, Venue_Id + String.valueOf(i));
            venueValues.put(FoursquareContract.VenuesEntry.COLUMN_NAME, "Starbuks #" + String.valueOf(i));
            venueValues.put(FoursquareContract.VenuesEntry.COLUMN_CITY, "Moscow");
            venueValues.put(FoursquareContract.VenuesEntry.COLUMN_POSTALCODE, "123103");
            venueValues.put(FoursquareContract.VenuesEntry.COLUMN_CC, "RU");
            venueValues.put(FoursquareContract.VenuesEntry.COLUMN_COUNTRY, "Russia");
            venueValues.put(FoursquareContract.VenuesEntry.COLUMN_COORD_LAT, String.valueOf(55.34));
            venueValues.put(FoursquareContract.VenuesEntry.COLUMN_COORD_LONG, String.valueOf(37.43));
            returnContentValues[i] = venueValues;
        }
        return returnContentValues;
    }

    // Student: Uncomment this test after you have completed writing the BulkInsert functionality
    // in your provider.  Note that this test will work with the built-in (default) provider
    // implementation, which just inserts records one-at-a-time, so really do implement the
    // BulkInsert ContentProvider function.
    public void testBulkInsert() {

        ContentValues[] bulkInsertContentValues = createBulkInsertVenueValues(VENUE_ID);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver venueObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(FoursquareContract.VenuesEntry.CONTENT_URI, true, venueObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(FoursquareContract.VenuesEntry.CONTENT_URI, bulkInsertContentValues);

        venueObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(venueObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                FoursquareContract.VenuesEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order == by DATE ASCENDING
        );

//        Log.d(getClass().getSimpleName(),"Dump cursor: " + DatabaseUtils.dumpCursorToString(cursor));

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating VenuesEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}
