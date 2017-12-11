package com.a_leonov.onesquare.ui;

import android.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.a_leonov.onesquare.PointD;
import com.a_leonov.onesquare.data.FoursquareContract;
import com.a_leonov.onesquare.ui.VenueAdapter;

/**
 * Created by a_leonov on 24.11.2017.
 */

public class VenueListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String BUNDLE_CATEGORY = "category";
    private String BUNDLE_LAT = "lat";
    private String BUNDLE_LON = "lon";
    private ListView mListView;
    private static final String SELECTED_KEY = "selected_position";
    private static final int VENUE_LOADER = 0;
    String category;
    private double lat;
    private double lon;

    private VenueAdapter mVenueAdapter;
    private int mPosition;

    private static final String[] VENUE_COLUMNS = {
            FoursquareContract.VenuesEntry.TABLE_NAME + "." + FoursquareContract.VenuesEntry._ID,
            FoursquareContract.VenuesEntry.COLUMN_NAME,
            FoursquareContract.VenuesEntry.COLUMN_ADDRESS,
            FoursquareContract.VenuesEntry.COLUMN_RATING,
            FoursquareContract.VenuesEntry.COLUMN_CATERGORY,
            FoursquareContract.VenuesEntry.COLUMN_COORD_LAT,
            FoursquareContract.VenuesEntry.COLUMN_COORD_LONG
    };

    static final int COL_VENUE_ID = 0;
    static final int COL_NAME = 1;
    static final int COL_ADDRESS = 2;
    static final int COL_RATING = 3;
    static final int COL_CAT = 4;
    static final int COL_LAT = 5;
    static final int COL_LON = 6;

    PointD currentLoc;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mVenueAdapter = new VenueAdapter(getActivity(), null, 0);

        mListView = (ListView) rootView.findViewById(R.id.listView);
        mListView.setAdapter(mVenueAdapter);
        // We'll call our MainActivity
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                Log.d(getClass().getSimpleName(), String.valueOf(position));
//                if (cursor != null) {
//                    String locationSetting = Utility.getPreferredLocation(getActivity());
//                    ((Callback) getActivity())
//                            .onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
//                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
//                            ));
//                }
//                mPosition = position;
            }
        });

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
        if (bundle != null) {
            category = bundle.getString(BUNDLE_CATEGORY);
            lat      = bundle.getDouble(BUNDLE_LAT);
            lon      = bundle.getDouble(BUNDLE_LON);
            mVenueAdapter.setCurrentPoint(new PointD(lat, lon));
        }

        getLoaderManager().initLoader(VENUE_LOADER, null, null);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = FoursquareContract.VenuesEntry.COLUMN_NAME + " ASC";
        Uri venuesUri = FoursquareContract.VenuesEntry.buildVenuesGPSUri(category, lat, lon);
        Uri venuesTestUri = FoursquareContract.VenuesEntry.CONTENT_URI;

//        Cursor cursor = getActivity().getContentResolver().query(
//                venuesUri,
//                null,
//                null,
//                null,
//                sortOrder
//        );

//        Log.d(getClass().getSimpleName(), DatabaseUtils.dumpCursorToString(cursor));

        return new CursorLoader(getActivity(),
                venuesUri,
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
            mListView.smoothScrollToPosition(mPosition);
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
