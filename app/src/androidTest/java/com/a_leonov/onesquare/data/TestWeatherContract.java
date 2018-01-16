
package com.a_leonov.onesquare.data;


import android.net.Uri;
import android.test.AndroidTestCase;

public class TestWeatherContract extends AndroidTestCase {

    private static final long TEST_VENUE_LOCATION = 2341233;

     public void testBuildWeatherLocation() {
        Uri locationUri = FoursquareContract.VenuesEntry.buildVenuesUri(TEST_VENUE_LOCATION);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildWeatherLocation in " +
                        "WeatherContract.",
                locationUri);
        assertEquals("Error: Weather location not properly appended to the end of the Uri",
                TEST_VENUE_LOCATION, locationUri.getLastPathSegment());
        assertEquals("Error: Weather location Uri doesn't match our expected result",
                locationUri.toString(),
                "content://com.example.android.sunshine.app/weather/%2FNorth%20Pole");
    }
}
