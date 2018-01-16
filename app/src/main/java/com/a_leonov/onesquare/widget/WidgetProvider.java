package com.a_leonov.onesquare.widget;

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
import android.widget.RemoteViews;

import com.a_leonov.onesquare.R;
import com.a_leonov.onesquare.data.FoursquareContract;


/**
 * Created by a_leonov on 15.01.2018.
 */

public class WidgetProvider extends AppWidgetProvider {

    private Location currentLocation = null;
    private Context context;

    @Override
    public void onUpdate(Context context, AppWidgetManager
            appWidgetManager, int[] appWidgetIds) {

        this.context = context;

        for (int i = 0; i < appWidgetIds.length; ++i) {

            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            rv.setRemoteAdapter(appWidgetIds[i], R.id.widget_list,intent);
            rv.setEmptyView(R.id.widget_list, R.id.empty_view);
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


}
