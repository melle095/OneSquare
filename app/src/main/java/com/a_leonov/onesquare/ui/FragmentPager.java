package com.a_leonov.onesquare.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.a_leonov.onesquare.R;
import com.a_leonov.onesquare.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FragmentPager extends Fragment{

    private ImageView imageView;
    public static final String ARG_URL = "url";
    public static final String ARG_PAGE = "page";
    private int mPageNumber;
    private String imageUrl;


    public static FragmentPager init(String imageUrl, int mPageNumber) {
        FragmentPager fragment = new FragmentPager();
        Bundle args = new Bundle();
        args.putString(ARG_URL, imageUrl);
        args.putInt(ARG_PAGE, mPageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentPager(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_PAGE)&& getArguments().containsKey(ARG_URL)) {
            this.mPageNumber = getArguments().getInt(ARG_PAGE);
            this.imageUrl = getArguments().getString(ARG_URL);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.viewpager_item, container, false);

        imageView = rootView.findViewById(R.id.image_pager);

        Glide.clear(imageView);
        Glide.with(this)
                .load(imageUrl)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
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
//                        Bitmap bitmap = ((GlideBitmapDrawable) resource.getCurrent()).getBitmap();
//                        Palette palette = Palette.generate(bitmap);
//                        int defaultColor = 0xFF333333;
//                        int color = palette.getDarkMutedColor(defaultColor);
//                        imageView.setBackgroundColor(color);
//                        return false;
//                    }
//                })
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setImageBitmap(resource);
                        storeImage(resource);
                    }
                });


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public int getPageNumber() {
        return mPageNumber;
    }

    private void storeImage(Bitmap image) {
        File pictureFile = Utils.getOutputMediaFile(getActivity());
        if (pictureFile == null) {
            Log.d(getClass().getSimpleName(),
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            Log.d(getClass().getSimpleName(), "img dir: " + pictureFile);
        } catch (FileNotFoundException e) {
            Log.d(getClass().getSimpleName(), "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(getClass().getSimpleName(), "Error accessing file: " + e.getMessage());
        }
    }




}
