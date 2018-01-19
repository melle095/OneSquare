package com.a_leonov.onesquare.ui;

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
import android.widget.Toast;

import com.a_leonov.onesquare.R;
import com.a_leonov.onesquare.Utils;
import com.a_leonov.onesquare.data.FoursquareContract;
import com.a_leonov.onesquare.service.OneService;


public class VenueListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private static final String SELECTED_KEY = "selected_position";
    private static final int VENUE_LOADER = 0;
    private VenueAdapter mVenueAdapter;
    private Location currentLocation = null;
    private int mPosition;
    SwipeRefreshLayout mSwipeRefreshLayout;
    LoaderManager loaderManager;

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
            FoursquareContract.VenuesEntry.COLUMN_PHOTO_SUFFIX,
            FoursquareContract.VenuesEntry.COLUMN_VEN_KEY
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.include_article_list, container, false);

        mVenueAdapter = new VenueAdapter(getActivity(), null);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(null, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        mVenueAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mVenueAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        loaderManager = getLoaderManager();
        if (loaderManager.getLoader(VENUE_LOADER) == null)
            loaderManager.initLoader(VENUE_LOADER, null, this);
        else
            loaderManager.restartLoader(VENUE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = FoursquareContract.VenuesEntry.COLUMN_RATING + " DESC";

        Uri contentUri = FoursquareContract.VenuesEntry.CONTENT_URI;

        if (currentLocation != null) {
            sortOrder = FoursquareContract.VenuesEntry.COLUMN_DISTANCE + " ASC";
            contentUri = FoursquareContract.VenuesEntry
                    .buildVenuesGPSUri(FoursquareContract.CATEGORY_FOOD, currentLocation.getLatitude(), currentLocation.getLongitude());
        }

        return new CursorLoader(getActivity(),
                contentUri,
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
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
        loaderManager.restartLoader(VENUE_LOADER, null, this);
        if (Utils.hasInternetConnection(getActivity())) {
//            onListUpdateListener.onListUpdate();
            OneService.startOneService(getActivity(), currentLocation);
        } else {
            Toast.makeText(getActivity(), getContext().getString(R.string.noInternet),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void updateLocation(Location location) {
        
    }

}
