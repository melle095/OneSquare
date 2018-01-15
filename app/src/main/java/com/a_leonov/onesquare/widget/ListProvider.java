package com.a_leonov.onesquare.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.a_leonov.onesquare.R;

import java.util.ArrayList;

/**
 * Created by a_leonov on 15.01.2018.
 */

class ListProvider implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<ListItem> listItemList;
    private Context context = null;
    private int appWidgetId;
//    private TextView wiget_title;
//    private TextView widget_rating;
//    private ImageView widget_imageview;


    public ListProvider(Context context, Intent intent, ArrayList<ListItem> listItemList) {
        this.context = context;
        this.listItemList = listItemList;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return listItemList.size();
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
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.widget_listitem);
        ListItem listItem = listItemList.get(position);
        remoteView.setTextViewText(R.id.widget_ratingbar, listItem.getItem_rating());
        remoteView.setTextViewText(R.id.widget_title, listItem.getItem_name());
        remoteView.setImageViewBitmap(R.id.widget_imageView, listItem.getItem_image());

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }
}
