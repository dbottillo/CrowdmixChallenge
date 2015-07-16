package com.danielebottillo.crowdmixchallenge.util;

import android.util.Log;

import com.danielebottillo.crowdmixchallenge.BuildConfig;

public class LOG {

    public static final String defaultTag = "TW_LOG";

    public static void e(String message) {
        LOG.e(defaultTag, message);
    }

    public static void e(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void d(String message) {
        LOG.d(defaultTag, message);
    }

    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }
}
