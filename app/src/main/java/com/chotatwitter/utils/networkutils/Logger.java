package com.chotatwitter.utils.networkutils;

import android.util.Log;

/**
 * Created by vikas-pc on 24/11/15.
 */
public class Logger {

    private static final String TAG = "Logger";

    public static final boolean DEBUG_FLAG = true;

    public static void logd(Object caller, String text) {
        if (DEBUG_FLAG) {
            String tag = null;
            if (caller instanceof Class) {
                tag = ((Class) caller).getSimpleName();
            } else {
                tag = caller.getClass().getSimpleName();
            }
            Log.d(tag, text);
        }
    }
}
