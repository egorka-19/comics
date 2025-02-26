package com.example.comics.paint;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class FreePaintApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}