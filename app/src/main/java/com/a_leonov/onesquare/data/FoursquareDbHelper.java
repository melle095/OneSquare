package com.a_leonov.onesquare.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FoursquareDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "venues.db";

    public FoursquareDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + FoursquareContract.LocationEntry.TABLE_NAME + " (" +
                FoursquareContract.LocationEntry._ID + " INTEGER PRIMARY KEY," +
                //LocationEntry.COLUMN_LOCATION_SETTING + " TEXT UNIQUE NOT NULL, " +
                FoursquareContract.LocationEntry.COLUMN_ADDRESS + " TEXT , " +
                FoursquareContract.LocationEntry.COLUMN_CROSSSTREET + " TEXT , " +
                FoursquareContract.LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                FoursquareContract.LocationEntry.COLUMN_COORD_LONG + " REAL NOT NULL, " +
                FoursquareContract.LocationEntry.COLUMN_POSTALCODE + " INTEGER NOT NULL, " +
                FoursquareContract.LocationEntry.COLUMN_CC + " TEXT NOT NULL, " +
                FoursquareContract.LocationEntry.COLUMN_CITY + " TEXT NOT NULL, " +
                FoursquareContract.LocationEntry.COLUMN_STATE + " TEXT , " +
                FoursquareContract.LocationEntry.COLUMN_COUNTRY + " TEXT NOT NULL, " +
                " );";

        final String SQL_CREATE_VENUES_TABLE = "CREATE TABLE " + FoursquareContract.VenuesEntry.TABLE_NAME + " (" +

                FoursquareContract.VenuesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                FoursquareContract.VenuesEntry.COLUMN_VEN_KEY + " INTEGER NOT NULL, " +
                FoursquareContract.VenuesEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                FoursquareContract.VenuesEntry.COLUMN_PHONE + " TEXT, " +
                FoursquareContract.VenuesEntry.COLUMN_FORMATTEDPHONE + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_TWITTER + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_INSTAGRAMM + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_FACEBOOK + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_FACEBOOKUSER + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_FACEBOOKNAME + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                FoursquareContract.VenuesEntry.COLUMN_VERIFIED + " BOOLEAN , " +
                FoursquareContract.VenuesEntry.COLUMN_URL + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_STATUS + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_ISOPEN + " BOOLEAN , " +
                FoursquareContract.VenuesEntry.COLUMN_ISLOCALHOLIDAY + " BOOLEAN , " +
                FoursquareContract.VenuesEntry.COLUMN_POPULAR + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_PRICE + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_RATING + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_PHOTOS + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_SHORTURL + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_CANONICALURL + " TEXT , " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + FoursquareContract.VenuesEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                FoursquareContract.LocationEntry.TABLE_NAME + " (" + FoursquareContract.LocationEntry._ID + "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VENUES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
