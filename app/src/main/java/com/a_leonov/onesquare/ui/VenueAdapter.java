package com.a_leonov.onesquare.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.a_leonov.onesquare.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * Created by Пользователь on 24.11.2017.
 */

public class VenueAdapter extends CursorRecyclerViewAdapter<VenueAdapter.ViewHolder> {

    private Context mContext;

    static final int COL_VENUE_ID = 0;
    static final int COL_NAME = 1;
    static final int COL_ADDRESS = 2;
    static final int COL_RATING = 3;
    static final int COL_CAT = 4;
    static final int COL_LAT = 5;
    static final int COL_LON = 6;
    static final int COL_HOURS = 7;
    static final int COL_DIST = 8;
    static final int COL_PREFIX     = 9;
    static final int COL_SUFFIX     = 10;
    static final int COL_VEN_ID     = 11;

    private final String VEN_ID = "ven_id";
    private final String VENDB_ID = "vendb_id";

    public VenueAdapter(Context context, Cursor cursor) {
        super(cursor);
        mContext = context;
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor) {
//        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String venueName = cursor.getString(COL_NAME);
        String venueAddress = cursor.getString(COL_ADDRESS);
        int venueRating = Math.round(cursor.getFloat(COL_RATING));
        String venueWorkhours = cursor.getString(COL_HOURS);
        double venueDistance = cursor.getDouble(COL_DIST);
        viewHolder.venueName.setText(venueName);
        viewHolder.venueAddress.setText(mContext.getString(R.string.venue_address, venueAddress));
        viewHolder.venueRating.setRating(venueRating);
        viewHolder.workHours.setText(venueWorkhours);
        viewHolder.venueDistance.setText(mContext.getString(R.string.venue_distance, Math.round(venueDistance)));
        viewHolder.venue_id = cursor.getString(COL_VEN_ID);

        Glide.clear(viewHolder.venueThumbnail);
        Glide.with(viewHolder.venueThumbnail.getContext())
                .load(cursor.getString(COL_PREFIX) + mContext.getString(R.string.venue_thumbnail_size) + cursor.getString(COL_SUFFIX))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model,
                                                   Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {

                        return false;
                    }
                })
                .into(viewHolder.venueThumbnail);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_article, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(parent.getContext(),DetailActivity.class)
//                        .putExtra(VEN_ID, vh.venue_id)
                        .putExtra(VENDB_ID, getItemId(vh.getAdapterPosition()));
//
                parent.getContext().startActivity(intent);
            }
        });
        return vh;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView venueThumbnail;
        public final TextView venueName;
        public final TextView venueAddress;
        public final RatingBar venueRating;
        public final TextView venueDistance;
        public final TextView workHours;
        public String venue_id = "";


        public ViewHolder(View view) {
            super(view);
            venueThumbnail   = (ImageView) view.findViewById(R.id.thumbnail);
            venueName        = (TextView) view.findViewById(R.id.venue_name);
            venueAddress     = (TextView) view.findViewById(R.id.venue_address);
            venueRating      = (RatingBar) view.findViewById(R.id.venue_rating);
            venueDistance    = (TextView) view.findViewById(R.id.venue_distance);
            workHours        = (TextView) view.findViewById(R.id.venue_workhours);
        }
    }

}