package com.a_leonov.onesquare.ui;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.a_leonov.onesquare.R;
import com.a_leonov.onesquare.Utils;
import com.a_leonov.onesquare.data.FoursquareContract;
import com.a_leonov.onesquare.service.VenueDetailsService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

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
            FoursquareContract.VenuesEntry.COLUMN_VEN_KEY,
            FoursquareContract.VenuesEntry.COLUMN_COORD_LONG,
            FoursquareContract.VenuesEntry.COLUMN_COORD_LAT,
            FoursquareContract.VenuesEntry.COLUMN_RATING
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
    static final int COL_LONG = 9;
    static final int COL_LAT = 10;
    static final int COL_RATING = 11;

    private int COL_PHOTO_VENUE_ID = 0;
    private int COL_PHOTO_HEIGHT = 1;
    private int COL_PHOTO_WIDTH = 2;
    private int COL_PHOTO_PREFIX = 3;
    private int COL_PHOTO_SUFFIX = 4;
    private int COL_PHOTO_PHOTO_ID = 5;

    private ViewPager viewPager;
    private ImagePagerAdapter myImagePagerAdapter;
    private RatingBar venue_ratingbar;
    private String ven_lat;
    private String ven_long;
    private String target_name;
    private TextView venue_address;
    private TextView venue_phone;
    private String distance;
    private String twitter_id;
    private String instagramm_id;
    private String facebook_id;
    private ImageButton btn_distance;
    private ImageButton btn_twitter;
    private ImageButton btn_instagramm;
    private ImageButton btn_facebook;
    private FloatingActionButton mShareFab;
    private RecyclerView listFeedbacks;
    private int position;
    private CollapsingToolbarLayout toolbarLayout;
    private final String DETAIL_TAG = "vendb_id";
    private final String VEN_ID = "ven_id";
    private String photo_id;
    private ArrayList<String> photosList;
    private FeedbackAdapter feedbackAdapter;

    public static final String EXTENDED_DATA_STATUS =
            "com.a_leonov.onesquare.STATUS";
    public static final String BROADCAST_ACTION =
            "com.a_leonov.onesquare.BROADCAST";

    private final int DETAIL_LOADER = 3;
    private final int DETAIL_PHOTO_LOADER = 4;
    private final int DETAIL_FEED_LOADER = 5;

    private long venueDbID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        toolbarLayout = findViewById(R.id.toolbar_layout);
        viewPager = findViewById(R.id.viewPager);
        venue_ratingbar = findViewById(R.id.venue_rating);
        btn_distance = findViewById(R.id.bnt_distance);
        venue_address = findViewById(R.id.venue_address);
        venue_phone = findViewById(R.id.venue_phone);
        btn_twitter = findViewById(R.id.bnt_twitter);
        btn_instagramm = findViewById(R.id.bnt_instagramm);
        btn_facebook = findViewById(R.id.bnt_facebook);
        listFeedbacks = findViewById(R.id.venue_feedbacks);

        mShareFab = findViewById(R.id.share_fab);

        getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);

        Intent intent = getIntent();
        if (intent != null) {
            venueDbID = intent.getLongExtra(DETAIL_TAG, 0);
            getSupportLoaderManager().initLoader(DETAIL_PHOTO_LOADER, null, this);
            getSupportLoaderManager().initLoader(DETAIL_FEED_LOADER, null, this);

        }

        btn_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("geo:0,0?q=" + ven_lat + "," + ven_long + "(" + target_name + ")"));
                startActivity(intent);
            }
        });

        btn_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + facebook_id)));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + facebook_id)));
                }
            }
        });

        btn_instagramm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getPackageManager().getPackageInfo("com.instagram.android", 0);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/" + instagramm_id))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/" + instagramm_id)));
                }
            }
        });

        btn_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // get the Twitter app if possible
                    getPackageManager().getPackageInfo("com.twitter.android", 0);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=" + twitter_id))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } catch (Exception e) {
                    // no Twitter app, revert to browser
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + twitter_id)));
                }
            }
        });


        feedbackAdapter = new FeedbackAdapter(this, null);

        feedbackAdapter.setHasStableIds(true);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(null, LinearLayoutManager.VERTICAL, false);
        listFeedbacks.setLayoutManager(linearLayoutManager);
        listFeedbacks.setAdapter(feedbackAdapter);
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
            case  DETAIL_FEED_LOADER: {
                Uri feedUri = FoursquareContract.TipEntry.buildTipByVenueId(venueDbID);
                return new CursorLoader(this,
                        feedUri,
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
                target_name = data.getString(COL_NAME);
                venue_ratingbar.setRating(Math.round(data.getFloat(COL_RATING)));
                ven_long = data.getString(COL_LONG);
                ven_lat = data.getString(COL_LAT);

                if (data.isNull(COL_FACEBOOK)) {
                    btn_facebook.setEnabled(false);
                    btn_facebook.getDrawable().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
                } else {
                    btn_facebook.setEnabled(true);
                    btn_facebook.getDrawable().setColorFilter(null);
                    facebook_id = data.getString(COL_FACEBOOK);
                }

                if (data.isNull(COL_TWITTER)) {
                    btn_twitter.setEnabled(false);
                    btn_twitter.getDrawable().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
                }else {
                    btn_twitter.setEnabled(true);
                    btn_twitter.getDrawable().setColorFilter(null);
                    twitter_id = data.getString(COL_TWITTER);
                }

                if (data.isNull(COL_INSTAGRAMM)) {
                    btn_instagramm.setEnabled(false);
                    btn_instagramm.getDrawable().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
                }else {
                    btn_instagramm.setEnabled(true);
                    btn_instagramm.getDrawable().setColorFilter(null);
                    instagramm_id = data.getString(COL_INSTAGRAMM);
                }

                venue_address.setText(getString(R.string.venue_address, data.getString(COL_ADDRESS)) + ". " +
                        getString(R.string.venue_distance, data.getLong(COL_DISTANCE)));
                venue_phone.setText(getString(R.string.venue_phone, data.isNull(COL_PHONE) ? "" : data.getString(COL_PHONE)));

                Intent intentPhotos = new Intent(this, VenueDetailsService.class)
                        .putExtra(VEN_ID, data.getString(COL_VENKEY))
                        .putExtra(DETAIL_TAG, String.valueOf(venueDbID));
//
                startService(intentPhotos);
                break;
            }
            case DETAIL_PHOTO_LOADER: {

                if (data != null) {

                    photosList = new ArrayList<>();
                    while (data.moveToNext()) {
                        photosList.add(data.getString(COL_PHOTO_PREFIX) + getString(R.string.venue_detail_size) + data.getString(COL_PHOTO_SUFFIX));
                    }

                    myImagePagerAdapter =
                            new ImagePagerAdapter(this, photosList);

                    viewPager.setAdapter(myImagePagerAdapter);
                    viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            DetailActivity.this.position = position;
                        }
                    });

                    break;
                }
            }
            case DETAIL_FEED_LOADER: {
                if (data != null) {
                    Log.i(getLocalClassName(), DatabaseUtils.dumpCursorToString(data));

                    feedbackAdapter.swapCursor(data);
                }
            }
        }

        mShareFab.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (Utils.checkPermissions(DetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Glide.with(DetailActivity.this)
                            .load(photosList.get(position))
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .dontAnimate()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    storeImage(resource);
                                }
                            });

                } else if (!Utils.checkPermissions(DetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Utils.requestPermissions(DetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        venue_title.setText(null);
//        venue_distance.setText(null);
        venue_address.setText(null);
        venue_phone.setText(null);
//        venue_twitter.setText(null);
//        venue_instagramm.setText(null);
//        venue_facebook.setText(null);
    }

    @Override
    public void onRefresh() {

    }

    private void storeImage(Bitmap image) {

        File mediaStorageDir = Utils.getAlbumStorageDir(this, "oneSquare_images");
        String mImageName = "Image-" + photo_id + ".jpg";
        File pictureFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            boolean result = image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            Log.d(getClass().getSimpleName(), "img dir: " + pictureFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (pictureFile == null) {
            Log.d(getClass().getSimpleName(),
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        MediaScannerConnection.scanFile(this, new String[]{pictureFile.toString()}, new String[]{"image/*"}, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String s, Uri uri) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("image/jpeg");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
            }
        });
    }

}
