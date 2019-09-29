package com.mj.drinkmorewater.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.mj.drinkmorewater.R;

public class SharedPreferenceUtils {

    private static final String PREFERENCE_FILE_KEY = String.valueOf(R.string.preference_file_key);

    public static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
    }

    public void writeInt(Context context, String key, Integer value) {
        SharedPreferences preferences = getSharedPreference(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void writeBoolean(Context context, String key, Boolean value) {
        SharedPreferences preferences = getSharedPreference(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void writeFloat(Context context, String key, Float value) {
        SharedPreferences preferences = getSharedPreference(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public void writeString(Context context, String key, String value) {
        SharedPreferences preferences = getSharedPreference(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void writeLong(Context context, String key, Long value) {
        SharedPreferences preferences = getSharedPreference(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public int readInt(Context context, String key, int defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreference(context);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public boolean readBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreference(context);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public Float readInt(Context context, String key, float defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreference(context);
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public String readString(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreference(context);
        return sharedPreferences.getString(key, defaultValue);
    }

    public Long readInt(Context context, String key, Long defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreference(context);
        return sharedPreferences.getLong(key, defaultValue);
    }

}

