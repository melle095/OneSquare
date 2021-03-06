package com.a_leonov.onesquare.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class FoursquareContract {

    public static final String CONTENT_AUTHORITY = "com.a_leonov.onesquare.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_VENUES = "venues";
    public static final String PATH_PHOTO = "photo";
    public static final String PATH_TIP = "tip";


    public static final String CATEGORY_FOOD          = "4d4b7105d754a06374d81259";
    public static final String CATEGORY_OUTDOOR       = "4d4b7105d754a06377d81259";
    public static final String CATEGORY_Entertainment = "4d4b7104d754a06370d81259";
    public static final String CATEGORY_NIGHTLIFE     = "4d4b7105d754a06376d81259";

    public static final class VenuesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VENUES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VENUES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VENUES;

        public static final String TABLE_NAME = "venues";
        public static final String COLUMN_VEN_KEY = "venue_key_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CATERGORY = "venue_category";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_FORMATTEDPHONE = "formattedPhone";
        public static final String COLUMN_TWITTER = "twitter";
        public static final String COLUMN_INSTAGRAMM = "instagram";
        public static final String COLUMN_FACEBOOK = "facebook";
        public static final String COLUMN_FACEBOOKUSER = "facebookUsername";
        public static final String COLUMN_FACEBOOKNAME = "facebookName";
        public static final String COLUMN_VERIFIED = "verified";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_STATUS = "hours";
        public static final String COLUMN_ISOPEN = "isOpen";
        public static final String COLUMN_ISLOCALHOLIDAY = "isLocalHoliday";
        public static final String COLUMN_POPULAR = "popular";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_SHORTURL = "shortUrl";
        public static final String COLUMN_CANONICALURL = "canonicalUrl";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_CROSSSTREET = "crossStreet";
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";
        public static final String COLUMN_POSTALCODE = "postal_code";
        public static final String COLUMN_CC = "country_code";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_COUNTRY = "country";
        public static final String COLUMN_DISTANCE = "dist";
        public static final String COLUMN_PHOTO_PREFIX = "photo_prefix";
        public static final String COLUMN_PHOTO_SUFFIX = "photo_suffix";


        public static Uri buildVenuesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildVenuesGPSUri(String cat, double lat, double lon) {
            return CONTENT_URI.buildUpon()
                    .appendPath(cat)
                    .appendPath("geo")
                    .appendPath(String.valueOf(lat))
                    .appendPath(String.valueOf(lon))
                    .build();
        }

        public static Uri buildVenueCityUri(String cat, String city) {
            return CONTENT_URI.buildUpon()
                    .appendPath(cat)
                    .appendPath("city")
                    .appendPath(city)
                    .build();
        }

    }

    public static final class PhotoEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PHOTO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PHOTO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PHOTO;

        // Table name
        public static final String TABLE_NAME       = "photo";
        public static final String COLUMN_VENUE_ID  = "venue_id";
        public static final String COLUMN_PHOTO_ID  = "photo_id";
        public static final String COLUMN_PREFIX    = "prefix";
        public static final String COLUMN_HEIGHT    = "height";
        public static final String COLUMN_WIDTH     = "width";
        public static final String COLUMN_SUFFIX    = "suffix";

        public static Uri buildPhotoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildPhotoByVenueUri(long venue_id) {
            return CONTENT_URI.buildUpon()
                    .appendPath("venue_id")
                    .appendPath(String.valueOf(venue_id))
                    .build();
        }
    }

    public static final class TipEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TIP).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TIP;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TIP;

        // Table name
        public static final String TABLE_NAME = "tip";
        public static final String COLUMN_VENUE_ID = "venue_id";
        public static final String COLUMN_TIP_ID = "tip_id";
        public static final String COLUMN_TEXT = "text";
        public static final String COLUMN_FIRSTNAME = "tip_firstname";
        public static final String COLUMN_LASTNAME = "tip_lastname";
        public static final String COLUMN_USER_PHOTO_PREFIX = "tip_user_prefix";
        public static final String COLUMN_USER_PHOTO_SUFFIX = "tip_user_suffix";

        public static Uri buildTipUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTipByVenueId(long venue_id) {
            return CONTENT_URI.buildUpon()
                    .appendPath("tips_by_venue_id")
                    .appendPath(String.valueOf(venue_id))
                    .build();
        }
    }
}
