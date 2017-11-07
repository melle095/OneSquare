package com.a_leonov.onesquare.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.a_leonov.onesquare.data.FoursquareContract.VenuesEntry.COLUMN_VEN_KEY;

public class FoursquareDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "venues.db";

    public FoursquareDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_PHOTO_TABLE = "CREATE TABLE " + FoursquareContract.PhotoEntry.TABLE_NAME + " (" +
                FoursquareContract.PhotoEntry._ID + " INTEGER PRIMARY KEY," +
                FoursquareContract.PhotoEntry.COLUMN_HEIGHT + " INTEGER , " +
                FoursquareContract.PhotoEntry.COLUMN_WIDTH + " INTEGER , " +
                FoursquareContract.PhotoEntry.COLUMN_PREFIX + " TEXT , " +
                FoursquareContract.PhotoEntry.COLUMN_SOURCE + " TEXT , " +
                FoursquareContract.PhotoEntry.COLUMN_SUFFIX + " TEXT , " +
                FoursquareContract.PhotoEntry.COLUMN_VENUE_ID + " INTEGER NOT NULL , " +
                FoursquareContract.PhotoEntry.COLUMN_PHOTO_ID + " CHAR NOT NULL , " +
                FoursquareContract.PhotoEntry.COLUMN_VISIBILITY + " CHAR NOT NULL, " +

                " FOREIGN KEY (" + FoursquareContract.PhotoEntry.COLUMN_VENUE_ID + ") REFERENCES " +
                FoursquareContract.VenuesEntry.TABLE_NAME + " (" + FoursquareContract.VenuesEntry._ID + ")" +

                " UNIQUE (" + FoursquareContract.PhotoEntry.COLUMN_PHOTO_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_VENUES_TABLE = "CREATE TABLE " + FoursquareContract.VenuesEntry.TABLE_NAME + " (" +

                FoursquareContract.VenuesEntry._ID + " INTEGER PRIMARY KEY ," +
                FoursquareContract.VenuesEntry.COLUMN_VEN_KEY + " CHAR NOT NULL ," +
                FoursquareContract.VenuesEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                FoursquareContract.VenuesEntry.COLUMN_CATERGORY + "TEXT ," +
                FoursquareContract.VenuesEntry.COLUMN_PHONE + " TEXT, " +
                FoursquareContract.VenuesEntry.COLUMN_FORMATTEDPHONE + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_TWITTER + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_INSTAGRAMM + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_FACEBOOK + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_FACEBOOKUSER + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_FACEBOOKNAME + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_VERIFIED + " CHAR , " +
                FoursquareContract.VenuesEntry.COLUMN_ISOPEN + " STRING , " +
                FoursquareContract.VenuesEntry.COLUMN_ISLOCALHOLIDAY + " CHAR , " +
                FoursquareContract.VenuesEntry.COLUMN_POPULAR + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_RATING + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_STATUS + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_CITY + " TEXT NOT NULL, " +
                FoursquareContract.VenuesEntry.COLUMN_POSTALCODE + " INTEGER NOT NULL, " +
                FoursquareContract.VenuesEntry.COLUMN_CC + " TEXT NOT NULL, " +
                FoursquareContract.VenuesEntry.COLUMN_ADDRESS + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_CROSSSTREET + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                FoursquareContract.VenuesEntry.COLUMN_COORD_LONG + " REAL NOT NULL, " +
                FoursquareContract.VenuesEntry.COLUMN_STATE + " TEXT , " +
                FoursquareContract.VenuesEntry.COLUMN_COUNTRY + " TEXT NOT NULL ," +

                " UNIQUE (" + COLUMN_VEN_KEY + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_VENUES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PHOTO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FoursquareContract.VenuesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FoursquareContract.PhotoEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
