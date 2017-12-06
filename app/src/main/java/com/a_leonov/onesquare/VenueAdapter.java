package com.a_leonov.onesquare;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.a_leonov.onesquare.data.VenueProvider;

/**
 * Created by Пользователь on 24.11.2017.
 */

public class VenueAdapter extends CursorAdapter {

    PointD currentPoint;
    PointD targetPoint;


    public VenueAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.currentPoint = currentPoint;
    }

    public static class ViewHolder {
//        public final ImageView iconView;
        public final TextView nameView;
        public final TextView addressView;
        public final RatingBar ratingView;
        public final TextView catText;
        public final TextView distText;

        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.text_name);
            addressView = (TextView) view.findViewById(R.id.text_address);
            ratingView = (RatingBar) view.findViewById(R.id.rating_bar);
            catText = (TextView) view.findViewById(R.id.text_category);
            distText = (TextView) view.findViewById(R.id.text_distance);

        }
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String venueName = cursor.getString(VenueListFragment.COL_NAME);
        String venueAddress = cursor.getString(VenueListFragment.COL_ADDRESS);
        float venueRating = cursor.getFloat(VenueListFragment.COL_RATING);
        String venueCategory = cursor.getString(VenueListFragment.COL_CAT);

        double lat = cursor.getDouble(VenueListFragment.COL_LAT);
        double lon = cursor.getDouble(VenueListFragment.COL_LON);
        targetPoint = new PointD(lat, lon);
        double dist = VenueProvider.getDistanceBetweenTwoPoints(currentPoint,targetPoint);
        viewHolder.nameView.setText(venueName);
        viewHolder.addressView.setText(venueAddress);
        viewHolder.ratingView.setRating(venueRating);
        viewHolder.catText.setText(venueCategory);
        viewHolder.distText.setText("Distanse: " + String.valueOf(dist));
    }

    public void setCurrentPoint(PointD currentPoint){
        this.currentPoint = currentPoint;
    }
}
