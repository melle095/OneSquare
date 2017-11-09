package com.a_leonov.onesquare.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.PointF;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class VenueProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FoursquareDbHelper mOpenHelper;

    static final int VENUE          = 100;
    static final int VENUES         = 105;
    static final int VENUES_BY_GPS  = 110;
    static final int VENUES_BY_CITY = 115;
    static final int PHOTO          = 120;
    static final int PHOTOS         = 121;
    static final int PHOTOS_BY_VENUE = 125;

    static final double radius = 500;

    private static final SQLiteQueryBuilder sVenuesQueryBuilder;

    static{
        sVenuesQueryBuilder = new SQLiteQueryBuilder();

        sVenuesQueryBuilder.setTables(
                FoursquareContract.VenuesEntry.TABLE_NAME + " LEFT JOIN " +
                        FoursquareContract.VenuesEntry.TABLE_NAME +
                        " ON " + FoursquareContract.VenuesEntry.TABLE_NAME +
                        "." + FoursquareContract.VenuesEntry._ID +
                        " = " + FoursquareContract.PhotoEntry.TABLE_NAME +
                        "." + FoursquareContract.PhotoEntry.COLUMN_VENUE_ID);
    }


    private static final String sVenueIDSelection =
            FoursquareContract.VenuesEntry.TABLE_NAME+
                    "." + FoursquareContract.VenuesEntry._ID + " = ? ";

    private static final String sVenueByCitySelection =
            FoursquareContract.VenuesEntry.TABLE_NAME+
                    "." + FoursquareContract.VenuesEntry.COLUMN_CITY + " = ? ";

    private static final String sPhotoByVenueIDSelection =
            FoursquareContract.PhotoEntry.TABLE_NAME +
                    "." + FoursquareContract.PhotoEntry.COLUMN_VENUE_ID + " = ? ";

    private static final String sVenueNearSelection =
            " ( " + FoursquareContract.VenuesEntry.TABLE_NAME + "." + FoursquareContract.VenuesEntry.COLUMN_COORD_LAT  + " BETWEEN ? AND ? ) AND (" +
            FoursquareContract.VenuesEntry.TABLE_NAME + "." + FoursquareContract.VenuesEntry.COLUMN_COORD_LONG + " BETWEEN ? AND ? )";

    private Cursor getVenueById(Uri uri, String[] projection, String sortOrder) {
        String venueId = uri.getPathSegments().get(1);

        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{venueId};
        selection = sVenueIDSelection;

        return sVenuesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getVenueByCity(Uri uri, String[] projection, String sortOrder) {
        String venue_city = uri.getPathSegments().get(1);

        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{venue_city};
        selection = sVenueByCitySelection;

        return sVenuesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getPhotoByVenue(Uri uri, String[] projection, String sortOrder) {
        String venue_id = uri.getPathSegments().get(1);

        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{venue_id};
        selection = sPhotoByVenueIDSelection;

        return sVenuesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getVenueNear(Uri uri, String[] projection, String sortOrder) {
        float current_lon = Long.parseLong(uri.getPathSegments().get(1));
        float current_lat = Long.parseLong(uri.getPathSegments().get(2));

        PointF center = new PointF(current_lat, current_lon);
        final double mult = 1; // mult = 1.1; is more reliable
        PointF p1 = calculateDerivedPosition(center, mult * radius, 0);
        PointF p2 = calculateDerivedPosition(center, mult * radius, 90);
        PointF p3 = calculateDerivedPosition(center, mult * radius, 180);
        PointF p4 = calculateDerivedPosition(center, mult * radius, 270);

        String[] selectionArgs;
        String selection;
        selectionArgs = new String[]{String.valueOf(p1.y), String.valueOf(p3.y),String.valueOf(p4.x),String.valueOf(p2.x)};
        selection = sVenueNearSelection;

        return sVenuesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new FoursquareDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case VENUES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FoursquareContract.VenuesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case VENUE:{
                retCursor = getVenueById(uri, projection,sortOrder);
                break;
            }
            case VENUES_BY_GPS: {
                retCursor = getVenueNear(uri, projection, sortOrder);
                break;
            }
            case VENUES_BY_CITY: {
                retCursor = getVenueByCity(uri, projection, sortOrder);
                break;
            }
            case PHOTOS_BY_VENUE: {
                retCursor = getPhotoByVenue(uri, projection, sortOrder);
                break;
            }
            case PHOTOS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FoursquareContract.PhotoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case VENUE:
                return FoursquareContract.VenuesEntry.CONTENT_ITEM_TYPE;
            case VENUES_BY_GPS:
                return FoursquareContract.VenuesEntry.CONTENT_TYPE;
            case VENUES_BY_CITY:
                return FoursquareContract.VenuesEntry.CONTENT_TYPE;
            case PHOTO:
                return FoursquareContract.PhotoEntry.CONTENT_TYPE;
            case PHOTOS_BY_VENUE:
                return FoursquareContract.PhotoEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case VENUE: {
                long _id = db.insert(FoursquareContract.VenuesEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = FoursquareContract.VenuesEntry.buildVenuesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case PHOTO: {
                long _id = db.insert(FoursquareContract.PhotoEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = FoursquareContract.PhotoEntry.buildPhotoUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case VENUE:
                rowsDeleted = db.delete(
                        FoursquareContract.VenuesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PHOTO:
                rowsDeleted = db.delete(
                        FoursquareContract.PhotoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case VENUE:
                rowsUpdated = db.update(FoursquareContract.VenuesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case PHOTO:
                rowsUpdated = db.update(FoursquareContract.PhotoEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case VENUE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(FoursquareContract.VenuesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FoursquareContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FoursquareContract.PATH_VENUES, VENUES);
        matcher.addURI(authority, FoursquareContract.PATH_VENUES + "/#", VENUE);
        matcher.addURI(authority, FoursquareContract.PATH_VENUES + "/city/*", VENUES_BY_CITY);
        matcher.addURI(authority, FoursquareContract.PATH_VENUES + "/geo/*/*", VENUES_BY_GPS);
        matcher.addURI(authority, FoursquareContract.PATH_PHOTO, PHOTOS);
        matcher.addURI(authority, FoursquareContract.PATH_PHOTO + "/#", PHOTO);
        matcher.addURI(authority, FoursquareContract.PATH_PHOTO + "/venue_id/*", PHOTOS_BY_VENUE);

        return matcher;
    }
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

    public static PointF calculateDerivedPosition(PointF point, double range, double bearing)
    {
        double EarthRadius = 6371000; // m

        double latA = Math.toRadians(point.x);
        double lonA = Math.toRadians(point.y);
        double angularDistance = range / EarthRadius;
        double trueCourse = Math.toRadians(bearing);

        double lat = Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance)
                                * Math.cos(trueCourse));

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance)
                        * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);

        PointF newPoint = new PointF((float) lat, (float) lon);

        return newPoint;

    }
}
