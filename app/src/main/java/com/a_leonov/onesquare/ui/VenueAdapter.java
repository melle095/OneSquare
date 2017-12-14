package com.a_leonov.onesquare.ui;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.a_leonov.onesquare.PointD;
import com.a_leonov.onesquare.R;
import com.a_leonov.onesquare.data.VenueProvider;

/**
 * Created by Пользователь on 24.11.2017.
 */

public class VenueAdapter extends CursorRecyclerViewAdapter<VenueAdapter.ViewHolder> {

    private PointD currentPoint;
    private PointD targetPoint;
    private Cursor mCursor;
    double dist = 0;

    public VenueAdapter(Cursor cursor) {
        super(cursor);
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor) {
//        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String thumbnail = null;
        String venueName = cursor.getString(VenueListFragment.COL_NAME);
        String venueAddress = cursor.getString(VenueListFragment.COL_ADDRESS);
        float venueRating = cursor.getFloat(VenueListFragment.COL_RATING);
        String venueWorkhours = cursor.getString(VenueListFragment.COL_HOURS);

        double lat = cursor.getDouble(VenueListFragment.COL_LAT);
        double lon = cursor.getDouble(VenueListFragment.COL_LON);

        targetPoint = new PointD(lat, lon);

        if ((targetPoint != null)&&(currentPoint != null))
            dist = VenueProvider.getDistanceBetweenTwoPoints(currentPoint, targetPoint);


        viewHolder.venueName.setText(venueName);
        viewHolder.venueAddress.setText(venueAddress);
        viewHolder.venueRating.setRating(venueRating);
        viewHolder.workHours.setText(venueWorkhours);
        viewHolder.venueDistance.setText("Distanse: " + String.valueOf(dist));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_article, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                parent.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
//                        ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))));
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


        public ViewHolder(View view) {
            super(view);
            venueThumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            venueName = (TextView) view.findViewById(R.id.venue_name);
            venueAddress = (TextView) view.findViewById(R.id.venue_address);
            venueRating = (RatingBar) view.findViewById(R.id.venue_rating);
            venueDistance = (TextView) view.findViewById(R.id.venue_distance);
            workHours = (TextView) view.findViewById(R.id.venue_workhours);

        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    public void setCurrentPoint(PointD currentPoint) {
        this.currentPoint = currentPoint;
    }
}
