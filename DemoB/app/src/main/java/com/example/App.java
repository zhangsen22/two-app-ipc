package com.example;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.watchdog.ipc.IConnectionService;
import com.watchdog.ipc.IMessageService;
import com.watchdog.ipc.IServiceManager;

public class App extends Application {

    private IConnectionService connectionServiceProxy;
    private IMessageService messageServiceProxy;
    private IServiceManager serviceManagerProxy;

    @Override
    public void onCreate() {
        super.onCreate();
        bindService();
    }

    private void bindService() {
        Intent mIntent = new Intent();
        mIntent.setAction("com.watchdog.ipc.WatchDogService");
        mIntent.setPackage("com.watchdog.ipc");
//        mIntent.setComponent(new ComponentName("com.watchdog.ipc", "com.watchdog.ipc.WatchDogService"));
        getApplicationContext().bindService(mIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                serviceManagerProxy = IServiceManager.Stub.asInterface(service);
                try {
                    connectionServiceProxy = IConnectionService.Stub.asInterface(serviceManagerProxy.getService(IConnectionService.class.getSimpleName()));
                    messageServiceProxy = IMessageService.Stub.asInterface(serviceManagerProxy.getService(IMessageService.class.getSimpleName()));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }

    public IConnectionService getConnectionServiceProxy() {
        return connectionServiceProxy;
    }

    public IMessageService getMessageServiceProxy() {
        return messageServiceProxy;
    }

    public IServiceManager getServiceManagerProxy() {
        return serviceManagerProxy;
    }
}
