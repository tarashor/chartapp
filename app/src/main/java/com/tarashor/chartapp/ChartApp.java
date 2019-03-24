package com.tarashor.chartapp;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

public class ChartApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO);
    }
}
