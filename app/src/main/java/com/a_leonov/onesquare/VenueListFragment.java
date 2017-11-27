package com.a_leonov.onesquare;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.a_leonov.onesquare.data.FoursquareContract;

/**
 * Created by a_leonov on 24.11.2017.
 */

public class VenueListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView mListView;
    private static final String SELECTED_KEY = "selected_position";
    private static final int VENUE_LOADER = 0;
    String category;
    private static final double lat = 55.7604;
    private static final double lon = 37.5779;
    private VenueAdapter mVenueAdapter;
    private int mPosition;

    private static final String[] VENUE_COLUMNS = {
            FoursquareContract.VenuesEntry.TABLE_NAME + "." + FoursquareContract.VenuesEntry._ID,
            FoursquareContract.VenuesEntry.COLUMN_NAME,
            FoursquareContract.VenuesEntry.COLUMN_ADDRESS,
            FoursquareContract.VenuesEntry.COLUMN_CITY,
            FoursquareContract.VenuesEntry.COLUMN_RATING
    };

    static final int COL_VENUE_ID = 0;
    static final int COL_NAME = 1;
    static final int COL_ADDRESS = 2;
    static final int COL_CITY = 3;
    static final int COL_RATING = 4;


    public interface Callback {
        public void onItemSelected(Uri dateUri);
    }

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
    public void onAttach(Context context) {
        super.onAttach(context);

        MainActivity activiy = (MainActivity) context;
        category = activiy.getCategory();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(VENUE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = FoursquareContract.VenuesEntry.COLUMN_NAME + " ASC";

//        String locationSetting = Utility.getPreferredLocation(getActivity());
        MainActivity mainActivity = (MainActivity) getActivity();
//        Uri venuesUri = FoursquareContract.VenuesEntry.buildVenuesGPSUri(mainActivity.getCategory(),55.76154361388457,37.6122227347307);
        Uri venuesUri = FoursquareContract.VenuesEntry.CONTENT_URI;

        Cursor cursor = getActivity().getContentResolver().query(
                venuesUri,
                null,
                null,
                null,
                null
        );

        Log.d(getClass().getSimpleName(), DatabaseUtils.dumpCursorToString(cursor));

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
