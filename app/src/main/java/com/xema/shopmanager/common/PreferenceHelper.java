package com.xema.shopmanager.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.xema.shopmanager.enums.SortType;
import com.xema.shopmanager.model.User;

import static android.content.Context.MODE_PRIVATE;

public class PreferenceHelper {
    private static final String PREF_KEY_LOCAL = "setting_data";
    private static final String PREF_KEY_LOCAL_SORT_MODE = "setting_data_sort_mode";
    private static final String PREF_KEY_LOCAL_QUICK_PANEL = "setting_data_quick_panel";
    private static final String PREF_KEY_LOCAL_USER = "setting_data_user";

    public static void saveSortMode(Context context, SortType sort) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_KEY_LOCAL, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putString(PREF_KEY_LOCAL_SORT_MODE, sort.toString());
        prefsEditor.apply();
    }

    public static SortType loadSortMode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_KEY_LOCAL, MODE_PRIVATE);
        String s = preferences.getString(PREF_KEY_LOCAL_SORT_MODE, SortType.NAME.toString());
        return SortType.toSortType(s);
    }

    public static void saveUser(Context context, User user) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_KEY_LOCAL, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        String userStr = new Gson().toJson(user);
        prefsEditor.putString(PREF_KEY_LOCAL_USER, userStr);
        // TODO: 2018-08-17 delete log
        Log.d("user : ", userStr);
        prefsEditor.apply();
    }

    // TODO: 2018-08-17 error handling : json syntax error
    public static User loadUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_KEY_LOCAL, MODE_PRIVATE);
        String s = preferences.getString(PREF_KEY_LOCAL_USER, null);
        return !TextUtils.isEmpty(s) ? new Gson().fromJson(s, User.class) : null;
    }

    @Deprecated
    public static void saveQuickPanel(Context context, boolean checked) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_KEY_LOCAL, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putBoolean(PREF_KEY_LOCAL_QUICK_PANEL, checked);
        prefsEditor.apply();
    }

    @Deprecated
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
