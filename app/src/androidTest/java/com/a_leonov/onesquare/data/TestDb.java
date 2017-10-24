package com.a_leonov.onesquare.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by Лена on 17.10.2017.
 */

public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();
    static final String TEST_VENUE_ID = "412d2800f964a520df0c1fe3";

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(FoursquareDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(FoursquareContract.PhotoEntry.TABLE_NAME);
        tableNameHashSet.add(FoursquareContract.VenuesEntry.TABLE_NAME);

        mContext.deleteDatabase(FoursquareDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new FoursquareDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + FoursquareContract.VenuesEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> venuesColumnHashSet = new HashSet<String>();
        venuesColumnHashSet.add(FoursquareContract.VenuesEntry._ID);
        venuesColumnHashSet.add(FoursquareContract.VenuesEntry.COLUMN_ADDRESS);
        venuesColumnHashSet.add(FoursquareContract.VenuesEntry.COLUMN_CC);
        venuesColumnHashSet.add(FoursquareContract.VenuesEntry.COLUMN_CITY);
        venuesColumnHashSet.add(FoursquareContract.VenuesEntry.COLUMN_COORD_LAT);
        venuesColumnHashSet.add(FoursquareContract.VenuesEntry.COLUMN_COORD_LONG);
        venuesColumnHashSet.add(FoursquareContract.VenuesEntry.COLUMN_COUNTRY);
        venuesColumnHashSet.add(FoursquareContract.VenuesEntry.COLUMN_CROSSSTREET);
        venuesColumnHashSet.add(FoursquareContract.VenuesEntry.COLUMN_POSTALCODE);
        venuesColumnHashSet.add(FoursquareContract.VenuesEntry.COLUMN_STATE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            venuesColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                venuesColumnHashSet.isEmpty());
        db.close();
    }

    public void testVenuesTable() {
        insertVenue();
    }

    public void testPhotoTable() {
        long venueRowId = insertVenue();

        // Make sure we have a valid row ID.
        assertFalse("Error: Location Not Inserted Correctly", venueRowId == -1L);

        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        FoursquareDbHelper dbHelper = new FoursquareDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step (Weather): Create weather values
        ContentValues photoValues = TestUtilities.createPhotoValues(venueRowId);

        // Third Step (Weather): Insert ContentValues into database and get a row ID back
        long photoRowId = db.insert(FoursquareContract.PhotoEntry.TABLE_NAME, null, photoValues);
        assertTrue( photoRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor photoCursor = db.query(
                FoursquareContract.PhotoEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue( "Error: No Records returned from location query", photoCursor.moveToFirst() );

        // Fifth Step: Validate the location Query
        TestUtilities.validateCurrentRecord("testInsertReadDb photoEntry failed to validate",
                photoCursor, photoValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from weather query",
                photoCursor.moveToNext() );

        // Sixth Step: Close cursor and database
        photoCursor.close();
        dbHelper.close();
    }



    public long insertVenue() {

        FoursquareDbHelper dbHelper = new FoursquareDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues venuesValues = TestUtilities.createVenuesValues(TEST_VENUE_ID);

        long venueRowId;
        venueRowId = db.insert(FoursquareContract.VenuesEntry.TABLE_NAME, null, venuesValues);

        // Verify we got a row back.
        assertTrue(venueRowId != -1);

        Cursor cursor = db.query(
                FoursquareContract.VenuesEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, venuesValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return venueRowId;
    }
}
