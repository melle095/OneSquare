package com.a_leonov.onesquare.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.a_leonov.onesquare.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;
import java.util.Map;

/**
 * Created by Пользователь on 13.01.2018.
 */

public class FeedbackAdapter extends CursorRecyclerViewAdapter<FeedbackAdapter.TipViewHolder> {

//    public static final String TABLE_NAME = "tip";
//    public static final String COLUMN_VENUE_ID = "venue_id";
//    public static final String COLUMN_TIP_ID = "tip_id";
//    public static final String COLUMN_TEXT = "text";
//    public static final String COLUMN_FIRSTNAME = "tip_firstname";
//    public static final String COLUMN_LASTNAME = "tip_lastname";
//    public static final String COLUMN_USER_PHOTO_PREFIX = "tip_user_prefix";
//    public static final String COLUMN_USER_PHOTO_SUFFIX = "tip_user_suffix";

    private final int COL_ID = 0;
    private final int COL_TEXT = 1;
    private final int COL_FIRSTNAME = 2;
    private final int COL_LASTNAME = 3;
    private final int COL_PHOTOPREFIX = 4;
    private final int COL_PHOTOSUFFIX = 5;

    private Context context;


    public FeedbackAdapter(Context context, Cursor cursor) {
        super(cursor);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(TipViewHolder viewHolder, Cursor cursor) {

        viewHolder.feedback_text.setText(cursor.getString(COL_TEXT));
        viewHolder.feedback_username.setText(cursor.getString(COL_FIRSTNAME) + " " + cursor.getString(COL_LASTNAME));

        Glide.clear(viewHolder.photoAutor);
        Glide.with(viewHolder.photoAutor.getContext())
                .load(cursor.getString(COL_PHOTOPREFIX) + context.getString(R.string.tip_thumbnail_size) + cursor.getString(COL_PHOTOSUFFIX))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(viewHolder.photoAutor);
    }

    @Override
    public TipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feedback, parent, false);
        final TipViewHolder vh = new TipViewHolder(view);
        return  vh;
    }

    public static class TipViewHolder extends RecyclerView.ViewHolder {
        public final ImageView photoAutor;
        public final TextView feedback_text;
        public final TextView feedback_username;

        public TipViewHolder(View view) {
            super(view);
            photoAutor = (ImageView) view.findViewById(R.id.user_photo);
            feedback_text = (TextView) view.findViewById(R.id.item_text_feedback);
            feedback_username = (TextView) view.findViewById(R.id.item_text_username);
        }
    }
}
