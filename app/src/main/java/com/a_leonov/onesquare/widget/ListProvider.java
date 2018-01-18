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
import android.util.Log;
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

    private Context context = null;
    private Uri contentUri;
    private String sortOrder;
    private boolean mBound = false;
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
            FoursquareContract.VenuesEntry.COLUMN_RATING

    };

    static final int COL_VENUE_ID = 0;
    static final int COL_NAME = 1;
    static final int COL_ADDRESS = 2;
    static final int COL_CAT = 3;
    static final int COL_LAT = 4;
    static final int COL_LON = 5;
    static final int COL_DIST = 6;
    static final int COL_RAT = 8;

    public ListProvider(Context context, Intent intent) {
        this.context = context;
//        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
//                AppWidgetManager.INVALID_APPWIDGET_ID);

    }

    @Override
    public void onCreate() {

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
        if (cursor!=null)
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

    @Override
    public RemoteViews getViewAt(int position) {
        cursor.moveToPosition(position);

        String itemName = cursor.getString(COL_NAME);
        String itemAddress = cursor.getString(COL_ADDRESS);
        String itemRating = context.getString(R.string.venue_widget_rating,Math.round(cursor.getFloat(COL_RAT)));
        final RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_listitem);

        remoteView.setTextViewText(R.id.widget_item_title, itemName);
        remoteView.setTextViewText(R.id.widget_item_address, itemAddress);
        remoteView.setTextViewText(R.id.widget_item_rating, itemRating);

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

}
