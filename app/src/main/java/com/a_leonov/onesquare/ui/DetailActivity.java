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

import com.a_leonov.onesquare.R;

public class DetailActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<Cursor>{


    private final String DETAIL_TAG = "itemID";
    private final int DETAIL_LOADER = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

//        @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
//        @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
//        @BindView(R.id.toolbar) Toolbar mToolbar;

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

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onRefresh() {

    }
}
