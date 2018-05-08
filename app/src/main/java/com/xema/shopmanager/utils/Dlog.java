package com.xema.shopmanager.utils;

import android.util.Log;

import com.xema.shopmanager.common.BaseApplication;

/**
 * Created by xema0 on 2018-02-10.
 */

public class Dlog {
    private static final String TAG = "ShopManager";

    /**
     * Log Level Error
     **/
    public static void e(String message) {
        if (BaseApplication.DEBUG) Log.e(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Warning
     **/
    public static void w(String message) {
        if (BaseApplication.DEBUG) Log.w(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Information
     **/
    public static void i(String message) {
        if (BaseApplication.DEBUG) Log.i(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Debug
     **/
    public static void d(String message) {
        if (BaseApplication.DEBUG) Log.d(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Verbose
     **/
    public static void v(String message) {
        if (BaseApplication.DEBUG) Log.v(TAG, buildLogMsg(message));
    }

    private static String buildLogMsg(String message) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(ste.getFileName().replace(".java", ""));
        sb.append("::");
        sb.append(ste.getMethodName());
        sb.append("]");
        sb.append(message);
        return sb.toString();
    }

}
