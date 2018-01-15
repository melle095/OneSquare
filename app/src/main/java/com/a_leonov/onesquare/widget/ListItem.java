package com.a_leonov.onesquare.widget;

import android.graphics.Bitmap;

/**
 * Created by a_leonov on 15.01.2018.
 */

public class ListItem {
    private String item_name;
    private String item_rating;
    private Bitmap item_image;

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_rating() {
        return item_rating;
    }

    public void setItem_rating(String item_rating) {
        this.item_rating = item_rating;
    }

    public Bitmap getItem_image() {
        return item_image;
    }

    public void setItem_image(Bitmap item_image) {
        this.item_image = item_image;
    }

    public ListItem(String item_name, String item_rating, Bitmap item_image) {

        this.item_name = item_name;
        this.item_rating = item_rating;
        this.item_image = item_image;
    }
}
