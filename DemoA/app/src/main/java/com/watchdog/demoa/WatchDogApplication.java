package com.watchdog.demoa;

import android.app.Application;

public class WatchDogApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LaunchAppManager.getInstance().startWatchDogService(getApplicationContext());
    }
}
