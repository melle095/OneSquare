package com.a_leonov.onesquare.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.a_leonov.onesquare.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;

public class ImagePagerAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    private ImageView imageView = null;
    ArrayList<String> arrayList;
    private static Bitmap image;

    public ImagePagerAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((FrameLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.viewpager_item, container, false);

        imageView = (ImageView) itemView.findViewById(R.id.image_pager);

        Glide.clear(imageView);
        Glide.with(context)
                .load(arrayList.get(position))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setImageBitmap(resource);
                        image = resource;
//                        if (Utils.isExternalStorageWritable())
//                            storeImage(resource);
//                        else
//                            Log.d(getClass().getSimpleName(),"Write to extarnal not accessible");
                    }
                });

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }

}

