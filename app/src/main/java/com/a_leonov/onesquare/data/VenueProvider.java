package com.a_leonov.onesquare.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
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
        matcher.addURI(authority, FoursquareContract.PATH_VENUES, VENUES_BY_GPS);
        matcher.addURI(authority, FoursquareContract.PATH_PHOTO + "/#/#", PHOTO);
        matcher.addURI(authority, FoursquareContract.PATH_PHOTO + "/#", PHOTOS_BY_VENUE);

        return matcher;
    }

}
