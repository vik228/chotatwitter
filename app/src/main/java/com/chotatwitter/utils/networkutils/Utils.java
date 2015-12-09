package com.chotatwitter.utils.networkutils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by vikas-pc on 24/11/15.
 */
public class Utils {

    private static final String TAG = "utils";

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
