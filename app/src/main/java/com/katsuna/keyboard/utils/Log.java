package com.katsuna.keyboard.utils;

@SuppressWarnings({"ConstantConditions", "PointlessBooleanExpression"})
public class Log {

    private static final String TAG = "KatsounaKeyboard";
    private static final boolean FORCE_DEBUG = false; /* STOPSHIP if true */
    private static final boolean DEBUG = FORCE_DEBUG ||
            android.util.Log.isLoggable(TAG, android.util.Log.DEBUG);
    private static final boolean VERBOSE = FORCE_DEBUG ||
            android.util.Log.isLoggable(TAG, android.util.Log.VERBOSE);
    private static final String TAG_DELIMETER = " - ";

    public static void d(String tag, String msg) {
        if (DEBUG) {
            android.util.Log.d(TAG, delimit(tag) + msg);
        }
    }

    public static void d(Object obj, String msg) {
        if (DEBUG) {
            android.util.Log.d(TAG, getPrefix(obj) + msg);
        }
    }

    public static void d(Object obj, String str1, Object str2) {
        if (DEBUG) {
            android.util.Log.d(TAG, getPrefix(obj) + str1 + str2);
        }
    }

    public static void v(Object obj, String msg) {
        if (VERBOSE) {
            android.util.Log.v(TAG, getPrefix(obj) + msg);
        }
    }

    public static void v(Object obj, String str1, Object str2) {
        if (VERBOSE) {
            android.util.Log.d(TAG, getPrefix(obj) + str1 + str2);
        }
    }

    public static void e(String tag, String msg, Exception e) {
        android.util.Log.e(TAG, delimit(tag) + msg, e);
    }

    public static void e(String tag, String msg) {
        android.util.Log.e(TAG, delimit(tag) + msg);
    }

    public static void e(Object obj, String msg, Exception e) {
        android.util.Log.e(TAG, getPrefix(obj) + msg, e);
    }

    public static void e(Object obj, String msg) {
        android.util.Log.e(TAG, getPrefix(obj) + msg);
    }

    public static void i(String tag, String msg) {
        android.util.Log.i(TAG, delimit(tag) + msg);
    }

    public static void i(Object obj, String msg) {
        android.util.Log.i(TAG, getPrefix(obj) + msg);
    }

    public static void w(Object obj, String msg) {
        android.util.Log.w(TAG, getPrefix(obj) + msg);
    }

    public static void wtf(Object obj, String msg) {
        android.util.Log.wtf(TAG, getPrefix(obj) + msg);
    }

    private static String getPrefix(Object obj) {
        return (obj == null ? "" : (obj.getClass().getSimpleName() + TAG_DELIMETER));
    }

    private static String delimit(String tag) {
        return tag + TAG_DELIMETER;
    }
}
