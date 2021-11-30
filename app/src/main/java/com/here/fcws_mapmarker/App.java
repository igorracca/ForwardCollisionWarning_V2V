package com.here.fcws_mapmarker;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import net.time4j.android.ApplicationStarter;

public class App extends Application {
    private static App mInstance;
    private static Resources res;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        res = getResources();
        ApplicationStarter.initialize(this, true);
        App.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return App.context;
    }

    public static App getInstance() {
        return mInstance;
    }

    public static Resources getRes() {
        return res;
    }
}
