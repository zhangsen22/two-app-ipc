package com.example;

import android.app.Application;

import com.watchdog.demoa.ipc.IWatchDogManager;


public class App extends Application {



    @Override
    public void onCreate() {
        super.onCreate();
        IWatchDogManager.getInstance().registerRemoteService(getApplicationContext(),null,null);
    }



}
