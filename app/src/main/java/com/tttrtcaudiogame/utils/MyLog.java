package com.tttrtcaudiogame.utils;

import android.util.Log;

/**
 * Created by wangzhiguo on 17/10/31.
 */

public class MyLog {
    public static final String TAG = "WSTECH";

    public static void i(String msg) {
        Log.i(TAG, msg);
    }

    public static void d(String msg) {
        Log.w(TAG, msg);
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }
}
