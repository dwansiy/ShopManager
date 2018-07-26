package com.xema.shopmanager.common;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PreferenceHelper {
    private static final String PREF_KEY_LOCAL = "setting_data";
    private static final String PREF_KEY_LOCAL_SORT_MODE = "setting_data_sort_mode";
    private static final String PREF_KEY_LOCAL_QUICK_PANEL = "setting_data_quick_panel";

    public static void saveSortMode(Context context, Constants.Sort sort) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_KEY_LOCAL, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putString(PREF_KEY_LOCAL_SORT_MODE, sort.toString());
        prefsEditor.apply();
    }

    public static Constants.Sort loadSortMode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_KEY_LOCAL, MODE_PRIVATE);
        String s = preferences.getString(PREF_KEY_LOCAL_SORT_MODE, Constants.Sort.NAME.toString());
        return Constants.Sort.toSort(s);
    }

    public static void saveQuickPanel(Context context, boolean checked) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_KEY_LOCAL, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putBoolean(PREF_KEY_LOCAL_QUICK_PANEL, checked);
        prefsEditor.apply();
    }

    public static boolean loadQuickPanel(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_KEY_LOCAL, MODE_PRIVATE);
        return preferences.getBoolean(PREF_KEY_LOCAL_QUICK_PANEL, true);
    }

    public static void resetAll(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_KEY_LOCAL, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.clear().apply();
    }
}
