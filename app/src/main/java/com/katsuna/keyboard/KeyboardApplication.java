package com.katsuna.keyboard;

import android.app.Application;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

public class KeyboardApplication extends Application {

    private static final String TAG = "KeyboardApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            // disable firebase crash collection for debug
            FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

}
