package org.wdd.app.android.interestcollection.utils;

import android.util.Log;

/**
 * Created by richard on 12/5/16.
 */

public class LogUtils {

    private static final String TAG = "SeeDoctor";

    public static void e(String tag, String msg) {
        if (!Constants.DEBUG) return;
        Log.e(tag, msg);
    }

    public static void e(String msg) {
        if (!Constants.DEBUG) return;
        Log.e(TAG, msg);
    }

    public static void w(String tag, String msg) {
        if (!Constants.DEBUG) return;
        Log.e(tag, msg);
    }

    public static void w(String msg) {
        if (!Constants.DEBUG) return;
        Log.w(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (!Constants.DEBUG) return;
        Log.d(tag, msg);
    }

    public static void d(String msg) {
        if (!Constants.DEBUG) return;
        Log.d(TAG, msg);
    }

    public static void v(String tag, String msg) {
        if (!Constants.DEBUG) return;
        Log.v(tag, msg);
    }

    public static void v(String msg) {
        if (!Constants.DEBUG) return;
        Log.v(TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (!Constants.DEBUG) return;
        Log.i(tag, msg);
    }

    public static void i(String msg) {
        if (!Constants.DEBUG) return;
        Log.i(TAG, msg);
    }
}
