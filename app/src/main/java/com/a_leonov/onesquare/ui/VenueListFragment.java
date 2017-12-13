package com.a_leonov.onesquare.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.a_leonov.onesquare.PointD;
import com.a_leonov.onesquare.R;
import com.a_leonov.onesquare.data.FoursquareContract;

/**
 * Created by a_leonov on 24.11.2017.
 */

public class VenueListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String BUNDLE_CATEGORY = "category";
    private String BUNDLE_LAT = "lat";
    private String BUNDLE_LON = "lon";
    private RecyclerView mRecyclerView;
    private static final String SELECTED_KEY = "selected_position";
    private static final int VENUE_LOADER = 0;

    private VenueAdapter mVenueAdapter;
    private int mPosition;

    private static final String[] VENUE_COLUMNS = {
            FoursquareContract.VenuesEntry.TABLE_NAME + "." + FoursquareContract.VenuesEntry._ID,
            FoursquareContract.VenuesEntry.COLUMN_NAME,
            FoursquareContract.VenuesEntry.COLUMN_ADDRESS,
            FoursquareContract.VenuesEntry.COLUMN_RATING,
            FoursquareContract.VenuesEntry.COLUMN_CATERGORY,
            FoursquareContract.VenuesEntry.COLUMN_COORD_LAT,
            FoursquareContract.VenuesEntry.COLUMN_COORD_LONG,
            FoursquareContract.VenuesEntry.COLUMN_STATUS
    };

    static final int COL_VENUE_ID = 0;
    static final int COL_NAME = 1;
    static final int COL_ADDRESS = 2;
    static final int COL_RATING = 3;
    static final int COL_CAT = 4;
    static final int COL_LAT = 5;
    static final int COL_LON = 6;
    static final int COL_HOURS = 7;

    PointD currentLoc;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.include_article_list, container, false);

        mVenueAdapter = new VenueAdapter(null);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mVenueAdapter);


        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();

        getLoaderManager().initLoader(VENUE_LOADER, bundle, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = FoursquareContract.VenuesEntry.COLUMN_NAME + " ASC";

        if (args != null) {
            String category = args.getString(BUNDLE_CATEGORY);
            double lat      = args.getDouble(BUNDLE_LAT);
            double lon      = args.getDouble(BUNDLE_LON);
            mVenueAdapter.setCurrentPoint(new PointD(lat, lon));
            Uri venuesUri = FoursquareContract.VenuesEntry.buildVenuesGPSUri(category, lat, lon);

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
}
