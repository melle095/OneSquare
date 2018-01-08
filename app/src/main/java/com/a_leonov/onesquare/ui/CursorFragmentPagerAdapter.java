package com.a_leonov.onesquare.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.a_leonov.onesquare.data.FoursquareContract;

public class CursorFragmentPagerAdapter extends FragmentPagerAdapter {
    private int count = 5;
    private FragmentManager fm;
    Context mContext;
    long venue_id;
    Cursor mCursor;



    public CursorFragmentPagerAdapter(FragmentManager fm, Context context, long venue_id) {
        super(fm);
        mContext = context;
        this.venue_id = venue_id;
    }


    @Override
    public Fragment getItem(int position) {
        return FragmentPager.init(null);
    }

    @Override
    public int getCount() {
        return count;
    }

}
