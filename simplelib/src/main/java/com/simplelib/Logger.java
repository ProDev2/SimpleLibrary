package com.simplelib;

import android.util.Log;

public class Logger {
    // Logging
    public static final String TAG = "Log";

    // Logging levels
    public static final int OFF = 0;
    public static final int ALL = -1;

    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int FATAL = 7;

    // Logging level
    public static int LEVEL = !BuildConfig.DEBUG ? OFF : DEBUG;

    /** Loggable level check **/
    public static final boolean isLoggableLevel(int level) {
        return level == ALL ||
                (level != OFF && level >= LEVEL);
    }

    /** Log Level Verbose **/
    public static final void v(String tag, String msg) {
        if (isLoggableLevel(VERBOSE))
            Log.v(tag, msg);
    }

    /** Log Level Verbose **/
    public static final void v(String tag, String msg, Throwable tr) {
        if (isLoggableLevel(VERBOSE))
            Log.v(tag, msg, tr);
    }

    /** Log Level Debug **/
    public static final void d(String tag, String msg) {
        if (isLoggableLevel(DEBUG))
            Log.d(tag, msg);
    }

    /** Log Level Debug **/
    public static final void d(String tag, String msg, Throwable tr) {
        if (isLoggableLevel(DEBUG))
            Log.d(tag, msg, tr);
    }

    /** Log Level Information **/
    public static final void i(String tag, String msg) {
        if (isLoggableLevel(INFO))
            Log.i(tag, msg);
    }

    /** Log Level Information **/
    public static final void i(String tag, String msg, Throwable tr) {
        if (isLoggableLevel(INFO))
            Log.i(tag, msg, tr);
    }

    /** Log Level Warning **/
    public static final void w(String tag, String msg) {
        if (isLoggableLevel(WARN))
            Log.w(tag, msg);
    }

    /** Log Level Warning **/
    public static final void w(String tag, String msg, Throwable tr) {
        if (isLoggableLevel(WARN))
            Log.w(tag, msg, tr);
    }

    /** Log Level Error **/
    public static final void e(String tag, String msg) {
        if (isLoggableLevel(ERROR))
            Log.e(tag, msg);
    }

    /** Log Level Error **/
    public static final void e(String tag, String msg, Throwable tr) {
        if (isLoggableLevel(ERROR))
            Log.e(tag, msg, tr);
    }

    /** Log Level Terrible Failure **/
    public static final void wtf(String tag, String msg) {
        if (isLoggableLevel(FATAL))
            Log.wtf(tag, msg);
    }

    /** Log Level Terrible Failure **/
    public static final void wtf(String tag, String msg, Throwable tr) {
        if (isLoggableLevel(FATAL))
            Log.wtf(tag, msg, tr);
    }

    /** Logging **/
    public static final String tagOf(Class<?> targetClass) {
        try {
            return targetClass.getSimpleName();
        } catch (Throwable tr) {
            e(TAG, "Tag for target class could not be found", tr);
            return TAG;
        }
    }
}
