package com.a_leonov.onesquare.ui;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.a_leonov.onesquare.R;
import com.a_leonov.onesquare.Utils;
import com.a_leonov.onesquare.data.FoursquareContract;
import com.a_leonov.onesquare.service.OneService;


public class VenueListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener, MainActivity.FragmentLocationListener {

    private RecyclerView mRecyclerView;
    private static final String SAVED_SUPER_STATE = "super-state";
    private static final String SAVED_LAYOUT_MANAGER = "layout-manager-state";
    private static final int VENUE_LOADER = 0;
    private VenueAdapter mVenueAdapter;
    private Location currentLocation = null;
    private int mPosition;
    SwipeRefreshLayout mSwipeRefreshLayout;
    LoaderManager loaderManager;
    MyLayoutManager linearLayoutManager;
    Parcelable mLayoutManagerSavedState;
    boolean needScroll = false;

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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        Bundle linearState = (Bundle) linearLayoutManager.onSaveInstanceState();

        super.onSaveInstanceState(linearState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.include_article_list, container, false);

        mVenueAdapter = new VenueAdapter(getActivity(), null);

        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        linearLayoutManager = new MyLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(linearLayoutManager);
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

        if (savedInstanceState != null) {
            linearLayoutManager.onRestoreInstanceState(savedInstanceState);
        }

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
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mVenueAdapter.swapCursor(null);
    }

    @Override
    public void onRefresh() {
        if (Utils.hasInternetConnection(getActivity())) {
            OneService.startOneService(getActivity(), currentLocation);
        } else {
            Toast.makeText(getActivity(), getContext().getString(R.string.noInternet),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationUpdate(Location location) {
        currentLocation = location;
        OneService.startOneService(getActivity(), currentLocation);
    }

    private class MyLayoutManager extends LinearLayoutManager{
        private boolean isRestored;

        public MyLayoutManager(Context context) {
            super(context);
        }

        public MyLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public MyLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void onLayoutCompleted(RecyclerView.State state) {
            super.onLayoutCompleted(state);
            if(isRestored && mPosition >-1) {
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MyLayoutManager.this.scrollToPosition(mPosition);
                    }
                },400);

            }
            isRestored=false;
        }

        @Override
        public Parcelable onSaveInstanceState() {
            Parcelable savedInstanceState = super.onSaveInstanceState();
            mPosition = this.findLastVisibleItemPosition();
            Bundle bundle=new Bundle();
            bundle.putParcelable("saved_state",savedInstanceState);
            bundle.putInt("position", mPosition);
            return bundle;
        }

        @Override
        public void onRestoreInstanceState(Parcelable state) {
            Parcelable savedState = ((Bundle)state).getParcelable("saved_state");
            mPosition = ((Bundle)state).getInt("position",-1);
            isRestored=true;
            super.onRestoreInstanceState(savedState);
        }
    }
}
