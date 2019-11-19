package com.cxgame.datatest.util;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSp {
    public static boolean isFirstOpen(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("is", Context.MODE_PRIVATE);
        boolean isFirstOpen = sharedPreferences.getBoolean("isFirstOpen", true);
        if (isFirstOpen) {
            sharedPreferences.edit().putBoolean("isFirstOpen", false).apply();
        }
        return isFirstOpen;
    }
}
