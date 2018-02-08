package com.xl.updatelib.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xilinch on 2016/5/23.
 */
public class UtilSharePreference {


    private static final String S_DEFAULT_FILE = "saveFile";


    public UtilSharePreference() {
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(S_DEFAULT_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(Context context,String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(S_DEFAULT_FILE, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, defaultValue);
        return value;
    }

    public static void putLong(Context context,String key, Long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(S_DEFAULT_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static Long getLong(Context context,String key, long defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(S_DEFAULT_FILE, Context.MODE_PRIVATE);
        Long value = sharedPreferences.getLong(key, defaultValue);
        return value;
    }

    public static void putBoolean(Context context,String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(S_DEFAULT_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBoolean(Context context,String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(S_DEFAULT_FILE, Context.MODE_PRIVATE);
        boolean value = sharedPreferences.getBoolean(key, defaultValue);
        return value;
    }

}
