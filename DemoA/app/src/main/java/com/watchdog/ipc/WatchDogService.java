package com.watchdog.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;

/**
 * 管理链接
 */
public class WatchDogService extends Service {
    private static final String TAG = "WatchDogService";

    public WatchDogService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "[WatchDogService] onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"[WatchDogService] onBind");
        return WatchDogDispatcher.getInstance().getServiceManager().asBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "[WatchDogService] onUnbind");
        /**
         * 后期优化  代表client端断开连接
         */
//        LaunchAppManager.getInstance().openPackage(this,"com.example.demob");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "[WatchDogService] onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "[WatchDogService] onDestroy");
        super.onDestroy();
    }
}
