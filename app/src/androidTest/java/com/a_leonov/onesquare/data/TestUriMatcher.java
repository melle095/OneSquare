package com.a_leonov.onesquare.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase {
    private static final String CITY_QUERY = "Moscow, RU";
    private static final double lat = 55.760437;
    private static final double lon = 37.577946;
    private static final String VENUE_ID = "412d2800f964a520df0c1fe3";

    // content://com.example.android.sunshine.app/weather"
    private static final Uri TEST_VENUE = FoursquareContract.VenuesEntry.buildVenuesUri(12);
    private static final Uri TEST_VENUE_DIR = FoursquareContract.VenuesEntry.CONTENT_URI;
    private static final Uri TEST_VENUE_GPS = FoursquareContract.VenuesEntry.buildVenuesGPSUri(lat, lon);
    private static final Uri TEST_VENUE_BY_CITY = FoursquareContract.VenuesEntry.buildVenueCityUri(CITY_QUERY);
    // content://com.example.android.sunshine.app/location"
    private static final Uri TEST_PHOTO = FoursquareContract.PhotoEntry.buildPhotoUri(20);
    private static final Uri TEST_PHOTO_DIR = FoursquareContract.PhotoEntry.CONTENT_URI;
    private static final Uri TEST_PHOTO_BY_VENUE = FoursquareContract.PhotoEntry.buildPhotoByVenueUri(VENUE_ID);


    /*
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are
        ready to test your UriMatcher.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = VenueProvider.buildUriMatcher();

        assertEquals("Error: The VENUE URI was matched incorrectly.",
                testMatcher.match(TEST_VENUE), VenueProvider.VENUE);
        assertEquals("Error: The VENUE DIR URI was matched incorrectly.",
                testMatcher.match(TEST_VENUE_DIR), VenueProvider.VENUES);
        assertEquals("Error: The VENUE DIR URI was matched incorrectly.",
                testMatcher.match(TEST_VENUE_DIR), VenueProvider.VENUES);
        assertEquals("Error: The VENUE GPS LOCATION was matched incorrectly.",
                testMatcher.match(TEST_VENUE_GPS), VenueProvider.VENUES_BY_GPS);
        assertEquals("Error: The VENUE BY CITY was matched incorrectly.",
                testMatcher.match(TEST_VENUE_BY_CITY), VenueProvider.VENUES_BY_CITY);
        assertEquals("Error: The PHOTO was matched incorrectly.",
                testMatcher.match(TEST_PHOTO), VenueProvider.PHOTO);
        assertEquals("Error: The PHOTO was matched incorrectly.",
                testMatcher.match(TEST_PHOTO_BY_VENUE), VenueProvider.PHOTOS_BY_VENUE);
        assertEquals("Error: The PHOTO DIR was matched incorrectly.",
                testMatcher.match(TEST_PHOTO_DIR), VenueProvider.PHOTOS);

    }
}
