package com.a_leonov.onesquare.widget;

import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.a_leonov.onesquare.R;
import com.a_leonov.onesquare.data.FoursquareContract;
import com.a_leonov.onesquare.service.LocationUpdatesService;
import com.a_leonov.onesquare.service.OneService;

import java.util.ArrayList;

/**
 * Created by a_leonov on 15.01.2018.
 */

class ListProvider implements RemoteViewsService.RemoteViewsFactory {
    private String BUNDLE_LAT = "lat";
    private String BUNDLE_LON = "lon";

    private ArrayList<ListItem> listItemList;
    private Context context = null;
    private int appWidgetId;
    private Location currentLocation = null;
    private CursorLoader mCursorLoader;
    private static final int LOADER_ID_NETWORK = 1;
    private Uri contentUri;
    private String sortOrder;
    private LocationUpdatesService mService = null;
    private boolean mBound = false;
    private BroadcastReceiver myReceiver;
    private Cursor cursor;

    private static final String[] VENUE_COLUMNS = {
            FoursquareContract.VenuesEntry.TABLE_NAME + "." + FoursquareContract.VenuesEntry._ID,
            FoursquareContract.VenuesEntry.COLUMN_NAME,
            FoursquareContract.VenuesEntry.COLUMN_ADDRESS,
            FoursquareContract.VenuesEntry.COLUMN_CATERGORY,
            FoursquareContract.VenuesEntry.COLUMN_COORD_LAT,
            FoursquareContract.VenuesEntry.COLUMN_COORD_LONG,
            FoursquareContract.VenuesEntry.COLUMN_STATUS,
            FoursquareContract.VenuesEntry.COLUMN_DISTANCE,

    };

    static final int COL_VENUE_ID = 0;
    static final int COL_NAME = 1;
    static final int COL_ADDRESS = 2;
    static final int COL_CAT = 3;
    static final int COL_LAT = 4;
    static final int COL_LON = 5;
    static final int COL_DIST = 6;
//    private TextView wiget_title;
//    private TextView widget_rating;
//    private ImageView widget_imageview;

//    private ServiceConnection mConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName className,
//                                       IBinder service) {
//            // We've bound to LocalService, cast the IBinder and get LocalService instance
//            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
//            mService = binder.getService();
//            mBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            mBound = false;
//        }
//    };

    public ListProvider(Context context, Intent intent) {
        this.context = context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

    }

    @Override
    public void onCreate() {
//        sortOrder = FoursquareContract.VenuesEntry.COLUMN_DISTANCE + " ASC LIMIT 5";
//        contentUri = FoursquareContract.VenuesEntry.CONTENT_URI;
////        myReceiver = new MyReceiver();
//
//        mCursorLoader = new CursorLoader(context, contentUri, VENUE_COLUMNS, null, null, sortOrder);
//        mCursorLoader.registerListener(LOADER_ID_NETWORK, this);
//        mCursorLoader.startLoading();

//        context.getApplicationContext().bindService(new Intent(context, LocationUpdatesService.class), mConnection,
//                Context.BIND_AUTO_CREATE);
//
//        LocalBroadcastManager.getInstance(context).registerReceiver(myReceiver,
//                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
    }

    @Override
    public void onDataSetChanged() {

        sortOrder = FoursquareContract.VenuesEntry.COLUMN_DISTANCE + " ASC LIMIT 5";
        contentUri = FoursquareContract.VenuesEntry.CONTENT_URI;

        if (cursor != null) cursor.close();
        this.cursor = context.getApplicationContext().getContentResolver().query(contentUri, VENUE_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onDestroy() {
        cursor.close();
    }

    @Override
    public int getCount() {
        if (cursor == null) return 0;
        return cursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /*
    *Similar to getView of Adapter where instead of View
    *we return RemoteViews
    *
    */
    @Override
    public RemoteViews getViewAt(int position) {
        cursor.moveToPosition(position);

        String itemName = cursor.getString(COL_NAME);
        String itemAddress = cursor.getString(COL_ADDRESS);
        final RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_listitem);

        remoteView.setTextViewText(R.id.widget_ratingbar, itemName);
        remoteView.setTextViewText(R.id.widget_title, itemAddress);
//        remoteView.setImageViewBitmap(R.id.widget_imageView, listItem.getItem_image());

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

//    @Override
//    public void onLoadComplete(@NonNull Loader<Cursor> loader, @Nullable Cursor data) {
//        if (data != null) {
//
//            listItemList = new ArrayList<>();
//            while (data.moveToNext()) {
//                listItemList.add(new ListItem(data.getString(COL_NAME),
//                        data.getString(COL_ADDRESS), null));
//            }
//
//        }
//    }

}
