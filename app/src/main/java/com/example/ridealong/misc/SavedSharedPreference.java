package com.example.ridealong.misc;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.ridealong.api.models.User;
import com.google.gson.Gson;

public class SavedSharedPreference
{
    static final String PREF_PROFILE= "profile";
    static final String PREF_PASSWORD= "pwd";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }
    public static void clear(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear(); //clear all stored data
        editor.commit();
    }

    public static void setProfile(Context ctx, User driver)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        Gson gson = new Gson();
        String s = gson.toJson(driver);
        editor.putString(PREF_PROFILE, s);
        editor.commit();
    }
    public static void setPassword(Context ctx, String pwd)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();

        editor.putString(PREF_PASSWORD, pwd);
        editor.commit();
    }
    public static User getProfile(Context ctx)
    {
        return new Gson().fromJson(getSharedPreferences(ctx).getString(PREF_PROFILE, ""),User.class);

    }
    public static String getPassword(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_PASSWORD, "");

    }
}