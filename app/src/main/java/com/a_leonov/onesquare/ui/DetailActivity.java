package com.a_leonov.onesquare.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.a_leonov.onesquare.R;

public class DetailActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<Cursor>{


    private ImageView venue_Photo;
    private TextView venue_title;
    private TextView venue_distance;
    private TextView venue_address;
    private TextView venue_phone;
    private TextView venue_twitter;
    private TextView venue_instagramm;
    private TextView venue_facebook;



    private final String DETAIL_TAG = "itemID";
    private final int DETAIL_LOADER = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

//        @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
//        @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
//        @BindView(R.id.toolbar) Toolbar mToolbar;

        venue_Photo = findViewById(R.id.photo);
        venue_title = findViewById(R.id.venue_title);
        venue_distance = findViewById(R.id.venue_distance);
        venue_address = findViewById(R.id.venue_address);
        venue_phone = findViewById(R.id.venue_phone);
        venue_twitter = findViewById(R.id.venue_twitter);
        venue_instagramm = findViewById(R.id.venue_instagramm);
        venue_facebook = findViewById(R.id.venue_facebook);


        getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Intent intent = getIntent();
        if (intent != null) {

            return new CursorLoader(this,
                    (Uri) intent.getParcelableExtra(DETAIL_TAG),
                    null,
                    null,
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        venue_Photo = null;
        
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onRefresh() {

    }
}
