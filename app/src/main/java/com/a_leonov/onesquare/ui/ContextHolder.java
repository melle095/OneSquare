package com.a_leonov.onesquare.ui;

import android.content.Context;

/**
 * Created by a_leonov on 15.01.2018.
 */

public class ContextHolder {
    public static Context context = null;

    public static void setContext(Context context) {
        ContextHolder.context = context;
    }

    public static Context getContext() {
        return context;
    }
}

