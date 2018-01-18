package com.a_leonov.onesquare.widget;

import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.a_leonov.onesquare.R;
import com.a_leonov.onesquare.data.FoursquareContract;
import com.a_leonov.onesquare.service.LocationUpdatesService;
import com.a_leonov.onesquare.service.OneService;

import static com.a_leonov.onesquare.service.LocationUpdatesService.ACTION_BROADCAST;


/**
 * Created by a_leonov on 15.01.2018.
 */

public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager
            appWidgetManager, int[] appWidgetIds) {

        updateVenueWidgets(context, appWidgetManager, appWidgetIds);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_BROADCAST)) {
            Location loc = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            OneService.startOneService(context, loc);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        LocalBroadcastManager.getInstance(context).registerReceiver(this,
                new IntentFilter(ACTION_BROADCAST));
        super.onEnabled(context);
    }

    public static void updateVenueWidgets(Context context, AppWidgetManager
            appWidgetManager, int[] appWidgetIds){
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            rv.setRemoteAdapter(appWidgetId, R.id.widget_list, intent);
            rv.setEmptyView(R.id.widget_list, R.id.empty_view);

            appWidgetManager.updateAppWidget(appWidgetId, rv);

            Log.i("WidgetProvider", "Update widgets");
        }
    }

}
