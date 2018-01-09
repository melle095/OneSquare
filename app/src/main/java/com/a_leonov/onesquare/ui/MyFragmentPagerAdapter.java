package com.a_leonov.onesquare.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private FragmentManager fm;
    private int pagesCount = 1;
    ArrayList<String> photoList = null;


    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (photoList != null) {
            FragmentPager fragment = FragmentPager.init(photoList.get(position), position);
            return fragment;
        }

        FragmentPager fragment = FragmentPager.init(null,0);

        return fragment;
    }

    @Override
    public int getCount() {
        return pagesCount;
    }

    public void setData(ArrayList<String> photoList) {
        this.photoList = photoList;
        this.pagesCount = photoList.size();
        notifyDataSetChanged();
    }

    public int getItemPosition(Object item) {

            return POSITION_NONE;
    }

}
