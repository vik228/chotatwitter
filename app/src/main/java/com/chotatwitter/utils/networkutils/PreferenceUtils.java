package com.chotatwitter.utils.networkutils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by vikas-pc on 24/11/15.
 */
public class PreferenceUtils {

    private static final String TAG = "PreferenceUtils";
    private static final String PREFERENCE_NAME = "prefFile";

    private static SharedPreferences getPreference(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static int getPreferenceValue(Context context, String key, int defaultVal) {
        SharedPreferences pref = getPreference(context);
        return pref.getInt(key, defaultVal);
    }

    public static boolean getPreferenceValue(Context context, String key, boolean defaultVal) {
        SharedPreferences pref = getPreference(context);
        return pref.getBoolean(key, defaultVal);
    }

    public static String getPreferenceValue(Context context, String key, String defaultVal) {
        SharedPreferences pref = getPreference(context);
        return pref.getString(key, defaultVal);
    }

    public static long getPreferenceValue(Context context, String key, long defaultVal) {
        SharedPreferences pref = getPreference(context);
        return pref.getLong(key, defaultVal);
    }

    public static void setPreferenceValue(Context context, String key, int val) {
        Logger.logd(context, "Value changed key : " + key + ", value " + val);
        SharedPreferences pref = getPreference(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, val);
        editor.apply();
    }

    public static void setPreferenceValue(Context context, String key, long val) {
        Logger.logd(context, "Value changed key : " + key + ", value " + val);
        SharedPreferences pref = getPreference(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, val);
        editor.apply();
    }

    public static void setPreferenceValue(Context context, String key, boolean val) {
        Logger.logd(context, "Value changed key : " + key + ", value " + val);
        SharedPreferences pref = getPreference(context);
        SharedPreferences.Editor editor = pref.edit();
        if (!val) {
            editor.remove(key);
        } else {
            editor.putBoolean(key, true);
        }
        editor.apply();
    }

    public static void setPreferenceValue(Context context, String key, String val) {
        Logger.logd(context, "Value changed key : " + key + ", value " + val);
        SharedPreferences pref = getPreference(context);
        SharedPreferences.Editor editor = pref.edit();
        if (null == val) {
            editor.remove(key);
        } else {
            editor.putString(key, val);
        }
        editor.apply();
    }

    public static void clear(Context context) {
        SharedPreferences prefs = getPreference(context);
        prefs.edit().clear().apply();
    }
}
