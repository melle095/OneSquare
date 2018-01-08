package com.a_leonov.onesquare.ui;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.a_leonov.onesquare.R;
import com.a_leonov.onesquare.data.FoursquareContract;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class FragmentPager extends Fragment {

    private ImageView imageView;
    public static final String ARG_PAGE = "page";
    private int mPageNumber;
    private String imageUrl;


    public static FragmentPager init(String imageUrl) {
        FragmentPager fragment = new FragmentPager();
        Bundle args = new Bundle();
        args.putString(ARG_PAGE, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentPager(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_PAGE)) {
            this.imageUrl = getArguments().getString(ARG_PAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.viewpager_item, container, false);

        imageView = rootView.findViewById(R.id.image_pager);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Glide.clear(imageView);
//        Glide.with(this)
//                .load(imageUrl)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .dontAnimate()
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model,
//                                                   Target<GlideDrawable> target,
//                                                   boolean isFromMemoryCache, boolean isFirstResource) {
////                        Bitmap bitmap = ((GlideBitmapDrawable) resource.getCurrent()).getBitmap();
////                        Palette palette = Palette.generate(bitmap);
////                        int defaultColor = 0xFF333333;
////                        int color = palette.getDarkMutedColor(defaultColor);
////                        viewHolder.itemView.setBackgroundColor(color);
//                        return false;
//                    }
//                })
//                .into(imageView);

    }

    public int getPageNumber() {
        return mPageNumber;
    }

}
