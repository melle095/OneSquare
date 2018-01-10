package com.a_leonov.onesquare.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.a_leonov.onesquare.R;
import com.a_leonov.onesquare.Utils;
import com.a_leonov.onesquare.data.FoursquareContract;
import com.a_leonov.onesquare.service.VenueDetailsService;

import java.io.File;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<Cursor>, MediaScannerConnection.OnScanCompletedListener  {

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

    static final int COL_ID = 0;
    static final int COL_NAME = 1;
    static final int COL_DISTANCE = 2;
    static final int COL_ADDRESS = 3;
    static final int COL_PHONE = 4;
    static final int COL_TWITTER = 5;
    static final int COL_INSTAGRAMM = 6;
    static final int COL_FACEBOOK = 7;
    static final int COL_VENKEY = 8;

    private int COL_PHOTO_VENUE_ID = 0;
    private int COL_PHOTO_HEIGHT = 1;
    private int COL_PHOTO_WIDTH = 2;
    private int COL_PHOTO_PREFIX = 3;
    private int COL_PHOTO_SUFFIX = 4;
    private int COL_PHOTO_PHOTO_ID = 5;

    private ViewPager viewPager;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private ImageView venue_Photo;
    private TextView venue_title;
    private TextView venue_distance;
    private TextView venue_address;
    private TextView venue_phone;
    private TextView venue_twitter;
    private TextView venue_instagramm;
    private TextView venue_facebook;
    FloatingActionButton mShareFab;
    private Uri shareUri;
    private CollapsingToolbarLayout toolbarLayout;
    private final String DETAIL_TAG = "vendb_id";
    private final String VEN_ID = "ven_id";

    public static final String EXTENDED_DATA_STATUS =
            "com.a_leonov.onesquare.STATUS";
    public static final String BROADCAST_ACTION =
            "com.a_leonov.onesquare.BROADCAST";

    private final int DETAIL_LOADER = 3;
    private final int DETAIL_PHOTO_LOADER = 4;

    private long venueDbID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        toolbarLayout = findViewById(R.id.toolbar_layout);
        viewPager = findViewById(R.id.viewPager);
        venue_title = findViewById(R.id.venue_title);
        venue_distance = findViewById(R.id.venue_distance);
        venue_address = findViewById(R.id.venue_address);
        venue_phone = findViewById(R.id.venue_phone);
        venue_twitter = findViewById(R.id.venue_twitter);
        venue_instagramm = findViewById(R.id.venue_instagramm);
        venue_facebook = findViewById(R.id.venue_facebook);

        mShareFab = findViewById(R.id.share_fab);

        getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);

        myFragmentPagerAdapter =
                new MyFragmentPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            venueDbID = intent.getLongExtra(DETAIL_TAG, 0);
            getSupportLoaderManager().initLoader(DETAIL_PHOTO_LOADER, null, this);
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
                }
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

                toolbarLayout.setTitle(data.getString(COL_NAME));
//                venue_title.setText(data.getString(COL_NAME));
                venue_distance.setText(getString(R.string.venue_distance, Math.round(data.getLong(COL_DISTANCE))));
                venue_address.setText(getString(R.string.venue_address, data.getString(COL_ADDRESS)));
                venue_phone.setText(getString(R.string.venue_phone, data.isNull(COL_PHONE)?"":data.getString(COL_PHONE)));
                venue_twitter.setText(getString(R.string.venue_twitter, data.isNull(COL_TWITTER)?"":data.getString(COL_TWITTER)));
                venue_instagramm.setText(getString(R.string.venue_instagramm, data.isNull(COL_INSTAGRAMM)?"":data.getString(COL_INSTAGRAMM)));
                venue_facebook.setText(getString(R.string.venue_facebook, data.isNull(COL_FACEBOOK)?"":data.getString(COL_FACEBOOK)));


                Intent intentPhotos = new Intent(this, VenueDetailsService.class)
                        .putExtra(VEN_ID, data.getString(COL_VENKEY))
                        .putExtra(DETAIL_TAG, String.valueOf(venueDbID));
//
                startService(intentPhotos);
                break;
            }
            case DETAIL_PHOTO_LOADER: {

                if (data != null) {
                    Log.i(getLocalClassName(), DatabaseUtils.dumpCursorToString(data));

                    ArrayList<String> photosList = new ArrayList<>();
                    while (data.moveToNext()) {
                        photosList.add(data.getString(COL_PHOTO_PREFIX) + getString(R.string.venue_detail_size) + data.getString(COL_PHOTO_SUFFIX));
                    }
                    myFragmentPagerAdapter.setData(photosList);
                    myFragmentPagerAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }

        mShareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
                shareIntent.setType("image/*");
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        venue_title.setText(null);
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

    @Override
    public void onScanCompleted(String s, Uri uri) {
        shareUri = uri;
    }
}
