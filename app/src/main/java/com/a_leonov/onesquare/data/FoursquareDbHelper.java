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

        final String SQL_CREATE_PHOTO_TABLE = "CREATE TABLE " + FoursquareContract.PhotoEntry.TABLE_NAME + " (" +
                FoursquareContract.PhotoEntry._ID + " CHAR PRIMARY KEY," +
                FoursquareContract.PhotoEntry.COLUMN_HEIGHT + " INTEGER , " +
                FoursquareContract.PhotoEntry.COLUMN_WIDTH + " INTEGER , " +
                FoursquareContract.PhotoEntry.COLUMN_PREFIX + " TEXT , " +
                FoursquareContract.PhotoEntry.COLUMN_SOURCE + " TEXT , " +
                FoursquareContract.PhotoEntry.COLUMN_SUFFIX + " TEXT , " +
                FoursquareContract.PhotoEntry.COLUMN_VENUE_ID + " CHAR NOT NULL , " +
                FoursquareContract.PhotoEntry.COLUMN_VISIBILITY + " CHAR NOT NULL " +

                " FOREIGN KEY (" + FoursquareContract.PhotoEntry.COLUMN_VENUE_ID + ") REFERENCES " +
                FoursquareContract.VenuesEntry.TABLE_NAME + " (" + FoursquareContract.VenuesEntry._ID + "));";

        final String SQL_CREATE_VENUES_TABLE = "CREATE TABLE " + FoursquareContract.VenuesEntry.TABLE_NAME + " (" +

                FoursquareContract.VenuesEntry._ID + " CHAR PRIMARY KEY ," +

                // the ID of the location entry associated with this weather data
                FoursquareContract.VenuesEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                FoursquareContract.VenuesEntry.COLUMN_PHONE + " TEXT, " +
                FoursquareContract.VenuesEntry.COLUMN_FORMATTEDPHONE + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_TWITTER + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_INSTAGRAMM + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_FACEBOOK + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_FACEBOOKUSER + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_FACEBOOKNAME + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                FoursquareContract.VenuesEntry.COLUMN_VERIFIED + " CHAR , " +
                FoursquareContract.VenuesEntry.COLUMN_URL + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_STATUS + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_ISOPEN + " STRING , " +
                FoursquareContract.VenuesEntry.COLUMN_ISLOCALHOLIDAY + " CHAR , " +
                FoursquareContract.VenuesEntry.COLUMN_POPULAR + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_PRICE + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_RATING + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_SHORTURL + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_CANONICALURL + " TEXT ," +
                FoursquareContract.VenuesEntry.COLUMN_ADDRESS + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_CROSSSTREET + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                FoursquareContract.VenuesEntry.COLUMN_COORD_LONG + " REAL NOT NULL, " +
                FoursquareContract.VenuesEntry.COLUMN_POSTALCODE + " INTEGER NOT NULL, " +
                FoursquareContract.VenuesEntry.COLUMN_CC + " TEXT NOT NULL, " +
                FoursquareContract.VenuesEntry.COLUMN_CITY + " TEXT NOT NULL, " +
                FoursquareContract.VenuesEntry.COLUMN_STATE + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_COUNTRY + " TEXT NOT NULL );";

        sqLiteDatabase.execSQL(SQL_CREATE_VENUES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PHOTO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
