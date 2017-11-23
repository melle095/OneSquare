package com.a_leonov.onesquare;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by Пользователь on 24.11.2017.
 */

public class VenueAdapter extends CursorAdapter {
    public VenueAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
//        public final ImageView iconView;
        public final TextView nameView;
        public final TextView addressView;
        public final RatingBar ratingView;

        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.text_name);
            addressView = (TextView) view.findViewById(R.id.text_address);
            ratingView = (RatingBar) view.findViewById(R.id.rating_bar);
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

    }
}
