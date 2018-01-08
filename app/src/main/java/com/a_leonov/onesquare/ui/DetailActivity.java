package com.a_leonov.onesquare.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.a_leonov.onesquare.R;
import com.a_leonov.onesquare.data.FoursquareContract;
import com.a_leonov.onesquare.service.VenueDetailsService;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] VENUE_COLUMNS = {
            FoursquareContract.VenuesEntry.TABLE_NAME + "." + FoursquareContract.VenuesEntry._ID,
            FoursquareContract.VenuesEntry.COLUMN_NAME,
            FoursquareContract.VenuesEntry.COLUMN_DISTANCE,
            FoursquareContract.VenuesEntry.COLUMN_ADDRESS,
            FoursquareContract.VenuesEntry.COLUMN_FORMATTEDPHONE,
            FoursquareContract.VenuesEntry.COLUMN_TWITTER,
            FoursquareContract.VenuesEntry.COLUMN_INSTAGRAMM,
            FoursquareContract.VenuesEntry.COLUMN_FACEBOOK,
            FoursquareContract.VenuesEntry.COLUMN_VEN_KEY
    };

    static final int COL_ID         = 0;
    static final int COL_NAME       = 1;
    static final int COL_DISTANCE   = 2;
    static final int COL_ADDRESS    = 3;
    static final int COL_PHONE      = 4;
    static final int COL_TWITTER    = 5;
    static final int COL_INSTAGRAMM = 6;
    static final int COL_FACEBOOK   = 7;
    static final int COL_VENKEY     = 8;

    private int COL_PHOTO_VENUE_ID = 0;
    private int COL_PHOTO_HEIGHT = 1;
    private int COL_PHOTO_WIDTH = 2;
    private int COL_PHOTO_PREFIX = 3;
    private int COL_PHOTO_SUFFIX = 4;
    private int COL_PHOTO_PHOTO_ID = 5;

    private ViewPager viewPager;
    private CursorFragmentPagerAdapter cursorFragmentPagerAdapter;
    private ImageView venue_Photo;
    private TextView venue_title;
    private TextView venue_distance;
    private TextView venue_address;
    private TextView venue_phone;
    private TextView venue_twitter;
    private TextView venue_instagramm;
    private TextView venue_facebook;
    private final String DETAIL_TAG     = "vendb_id";
    private final String VEN_ID         = "ven_id";

    public static final String EXTENDED_DATA_STATUS =
            "com.a_leonov.onesquare.STATUS";
    public static final String BROADCAST_ACTION =
            "com.a_leonov.onesquare.BROADCAST";

    private final int DETAIL_LOADER     = 3;
    private final int DETAIL_PHOTO_LOADER     = 4;

    private long venueDbID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

//      venue_Photo      = findViewById(R.id.photo);
        viewPager        = findViewById(R.id.viewPager);
//        venue_title         = findViewById(R.id.venue_title);
        venue_distance   = findViewById(R.id.venue_distance);
        venue_address    = findViewById(R.id.venue_address);
        venue_phone      = findViewById(R.id.venue_phone);
        venue_twitter    = findViewById(R.id.venue_twitter);
        venue_instagramm = findViewById(R.id.venue_instagramm);
        venue_facebook   = findViewById(R.id.venue_facebook);

        getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);


        cursorFragmentPagerAdapter =
                new CursorFragmentPagerAdapter(getSupportFragmentManager(), this, venueDbID);
        viewPager.setAdapter(cursorFragmentPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
//                FragmentPager.init(position);
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            venueDbID = intent.getLongExtra(DETAIL_TAG, 0);
            getSupportLoaderManager().initLoader(DETAIL_PHOTO_LOADER, null, this);
        }

        IntentFilter statusIntentFilter = new IntentFilter(
                BROADCAST_ACTION);

        statusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        DownloadStateReceiver mDownloadStateReceiver =
                new DownloadStateReceiver();
        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mDownloadStateReceiver,
                statusIntentFilter);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case DETAIL_LOADER: {
                if (venueDbID > 0) {
                    return new CursorLoader(this,
                            FoursquareContract.VenuesEntry.buildVenuesUri(venueDbID),
                            VENUE_COLUMNS,
                            null,
                            null,
                            null);
                } else
                    return null;
            }
            case DETAIL_PHOTO_LOADER: {
                Uri photoUri = FoursquareContract.PhotoEntry.buildPhotoByVenueUri(venueDbID);

                return new CursorLoader(this,
                        photoUri,
                        null,
                        null,
                        null,
                        null);
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case DETAIL_LOADER: {
                data.moveToFirst();
//        Log.d("DetailActivity", DatabaseUtils.dumpCursorToString(data));
                //venue_Photo = null;

                venue_distance.setText(data.isNull(COL_DISTANCE) ? "no data" : data.getString(COL_DISTANCE));
                venue_address.setText(data.isNull(COL_ADDRESS) ? "no data" : data.getString(COL_ADDRESS));
                venue_phone.setText(data.isNull(COL_PHONE) ? "no data" : data.getString(COL_PHONE));
                venue_twitter.setText(data.isNull(COL_TWITTER) ? "no data" : data.getString(COL_TWITTER));
                venue_instagramm.setText(data.isNull(COL_INSTAGRAMM) ? "no data" : data.getString(COL_INSTAGRAMM));
                venue_facebook.setText(data.isNull(COL_FACEBOOK) ? "no data" : data.getString(COL_FACEBOOK));

                Intent intentPhotos = new Intent(this, VenueDetailsService.class)
                        .putExtra(VEN_ID, data.getString(COL_VENKEY))
                        .putExtra(DETAIL_TAG, String.valueOf(venueDbID));
//
                startService(intentPhotos);
            }
            case DETAIL_PHOTO_LOADER: {
                ArrayList<String> photosList = new ArrayList<>();
                Log.i(getClass().getSimpleName(),DatabaseUtils.dumpCursorToString(data));

                while (data.moveToNext()) {
                    photosList.add(data.getString(COL_PHOTO_PREFIX) + "width500" + data.getString(COL_PHOTO_SUFFIX));
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        venue_distance.setText(null);
        venue_address.setText(null);
        venue_phone.setText(null);
        venue_twitter.setText(null);
        venue_instagramm.setText(null);
        venue_facebook.setText(null);
    }

    @Override
    public void onRefresh() {

    }

    private class DownloadStateReceiver extends BroadcastReceiver
    {
        // Prevents instantiation
        private DownloadStateReceiver() {
        }
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getBooleanExtra(EXTENDED_DATA_STATUS,false)) {
                Log.i(getLocalClassName(), "Venue detail service broadcast!");
//                Log.i(getLocalClassName(), DatabaseUtils.dumpCursorToString(photoCur));
            }

        }
    }
}
