package com.here.fcws_mapmarker;

import android.app.Application;
import android.content.res.Resources;

import net.time4j.android.ApplicationStarter;

public class App extends Application {
    private static App mInstance;
    private static Resources res;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        res = getResources();
        ApplicationStarter.initialize(this, true);
    }

    public static App getInstance() {
        return mInstance;
    }

    public static Resources getRes() {
        return res;
    }
}
