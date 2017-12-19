package com.a_leonov.onesquare.ui;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.a_leonov.onesquare.PointD;
import com.a_leonov.onesquare.R;
import com.a_leonov.onesquare.data.FoursquareContract;


public class VenueListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private static final String SELECTED_KEY = "selected_position";
    private static final int VENUE_LOADER = 0;
    private VenueAdapter mVenueAdapter;
    private Location currentLocation = null;
    private int mPosition;
    SwipeRefreshLayout mSwipeRefreshLayout;

    private static final String[] VENUE_COLUMNS = {
            FoursquareContract.VenuesEntry.TABLE_NAME + "." + FoursquareContract.VenuesEntry._ID,
            FoursquareContract.VenuesEntry.COLUMN_NAME,
            FoursquareContract.VenuesEntry.COLUMN_ADDRESS,
            FoursquareContract.VenuesEntry.COLUMN_RATING,
            FoursquareContract.VenuesEntry.COLUMN_CATERGORY,
            FoursquareContract.VenuesEntry.COLUMN_COORD_LAT,
            FoursquareContract.VenuesEntry.COLUMN_COORD_LONG,
            FoursquareContract.VenuesEntry.COLUMN_STATUS,
            FoursquareContract.VenuesEntry.COLUMN_DISTANCE,
            FoursquareContract.VenuesEntry.COLUMN_PHOTO_PREFIX,
            FoursquareContract.VenuesEntry.COLUMN_PHOTO_SUFFIX
    };
//
//    static final int COL_VENUE_ID = 0;
//    static final int COL_NAME = 1;
//    static final int COL_ADDRESS = 2;
//    static final int COL_RATING = 3;
//    static final int COL_CAT = 4;
//    static final int COL_LAT = 5;
//    static final int COL_LON = 6;
//    static final int COL_HOURS = 7;
//    static final int COL_DIST = 8;

    OnListUpdateListener onListUpdateListener;

    PointD currentLoc;

    public interface OnListUpdateListener {
        public void onListUpdate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onListUpdateListener = (OnListUpdateListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onListUpdateListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.include_article_list, container, false);

        mVenueAdapter = new VenueAdapter(getActivity(), null);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mVenueAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mVenueAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(null, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(VENUE_LOADER,null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = FoursquareContract.VenuesEntry.COLUMN_DISTANCE + " ASC";

        if (currentLocation != null) {
            String category = FoursquareContract.CATEGORY_FOOD;
            Uri venuesUri = FoursquareContract.VenuesEntry.buildVenuesGPSUri(category, currentLocation.getLatitude(), currentLocation.getLongitude());

            return new CursorLoader(getActivity(),
                    venuesUri,
                    VENUE_COLUMNS,
                    null,
                    null,
                    sortOrder);

        }

        return new CursorLoader(getActivity(),
                FoursquareContract.VenuesEntry.CONTENT_URI,
                VENUE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mVenueAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mRecyclerView.smoothScrollToPosition(mPosition);
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mVenueAdapter.swapCursor(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
        onListUpdateListener.onListUpdate();
    }
}
