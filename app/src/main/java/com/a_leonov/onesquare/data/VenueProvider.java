package com.a_leonov.onesquare.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.PointF;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class VenueProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FoursquareDbHelper mOpenHelper;

    static final int VENUE          = 99;
    static final int VENUES_BY_GPS  = 100;
    static final int VENUES_BY_CITY = 101;
    static final int PHOTO          = 102;
    static final int PHOTOS_BY_VENUE = 103;

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

    private static final String sVenueNearSelection =
            " ( " + FoursquareContract.VenuesEntry.TABLE_NAME + "." + FoursquareContract.VenuesEntry.COLUMN_COORD_LAT  + " BETWEEN ? AND ? ) AND (" +
            FoursquareContract.VenuesEntry.TABLE_NAME + "." + FoursquareContract.VenuesEntry.COLUMN_COORD_LONG + " BETWEEN ? AND ? )";

    private static final String sPhotosSelection =
            FoursquareContract.PhotoEntry.TABLE_NAME+
                    "." + FoursquareContract.PhotoEntry.COLUMN_VENUE_ID + " = ? ";

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

    private Cursor getVenueNear(Uri uri, String[] projection, String sortOrder) {
        float current_lon = Long.parseLong(uri.getPathSegments().get(1));
        float current_lat = Long.parseLong(uri.getPathSegments().get(2));

        PointF current_point = new PointF(current_lon,current_lat);
        //TODO: Остановился на этом месте. Составить правильно выборку из квадрата в 4 точки
        PointF left_bound = calculateDerivedPosition(current_point,500,180);
        PointF right_bound = calculateDerivedPosition(current_point,500,0);
        PointF up_bound = calculateDerivedPosition(current_point,500,90);
        PointF down_bound = calculateDerivedPosition(current_point,500,360);

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

    @Override
    public boolean onCreate() {
        mOpenHelper = new FoursquareDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
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
                return FoursquareContract.PhotoEntry.CONTENT_ITEM_TYPE;
            case PHOTOS_BY_VENUE:
                return FoursquareContract.PhotoEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FoursquareContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FoursquareContract.PATH_VENUES + "/#", VENUE);
        matcher.addURI(authority, FoursquareContract.PATH_VENUES + "/*", VENUES_BY_CITY);
        matcher.addURI(authority, FoursquareContract.PATH_VENUES + "/geo/#/#", VENUES_BY_GPS);
        //matcher.addURI(authority, FoursquareContract.PATH_PHOTO + "/#/#", PHOTO);
        matcher.addURI(authority, FoursquareContract.PATH_PHOTO + "/#", PHOTOS_BY_VENUE);

        return matcher;
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
