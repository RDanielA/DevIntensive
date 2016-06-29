package com.softdesign.devintensive.util;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by danil on 29.06.2016.
 */
public class DevIntensiveApplication extends Application {
    public static SharedPreferences sSharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public  static SharedPreferences getSharedPreferences(){
        return sSharedPreferences;
    }
}
