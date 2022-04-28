package com.example.demob;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.watchdog.ipc.IBuyApple;
import com.watchdog.ipc.IMessageService;
import com.watchdog.ipc.IServiceManager;

import java.util.concurrent.ConcurrentHashMap;

public class IWatchDogManager {
    private static final String TAG = "IWatchDogManager";
    private static final String WATCHDOG_ACTION = "com.watchdog.ipc.WatchDogService";
    private static final String WATCHDOG_PACKAGE = "com.watchdog.ipc";

    private boolean isBind = false;

    private ConcurrentHashMap<Class<? extends android.os.IInterface>, android.os.IInterface> mCrashService = new ConcurrentHashMap<>();//缓存创建连接后的service

    //private
    private IWatchDogManager() {

    }

    private static class SingletonInstance {
        private final static IWatchDogManager S = new IWatchDogManager();
    }

    // 3 返回对象
    public static IWatchDogManager getInstance() {
        return SingletonInstance.S;
    }


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, this.toString() + "-->onServiceConnected");
            isBind = true;
            IServiceManager serviceManagerProxy = IServiceManager.Stub.asInterface(service);
            if (serviceManagerProxy != null) {
                mCrashService.put(IServiceManager.class, serviceManagerProxy);
                try {
                    IMessageService messageServiceProxy = IMessageService.Stub.asInterface(serviceManagerProxy.getService(IMessageService.class.getSimpleName()));
                    if (messageServiceProxy != null) {
                        mCrashService.put(IMessageService.class, messageServiceProxy);
                    }
                    IBuyApple buyAppleServiceProxy = IBuyApple.Stub.asInterface(serviceManagerProxy.getService(IBuyApple.class.getSimpleName()));
                    if (buyAppleServiceProxy != null) {
                        mCrashService.put(IBuyApple.class, buyAppleServiceProxy);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, this.toString() + "-->onServiceDisconnected");
            isBind = false;
            mCrashService.clear();
        }
    };

    public <T extends IBinder> void registerRemoteService(Context context, String serviceCanonicalName, T stubBinder) {
        Intent mIntent = new Intent();
        mIntent.setAction(WATCHDOG_ACTION);
        mIntent.setPackage(WATCHDOG_PACKAGE);
//        mIntent.setComponent(new ComponentName(WATCHDOG_PACKAGE, WATCHDOG_ACTION));
        context.bindService(mIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public <T extends IBinder> void unRegisterRemoteService(Context context, String serviceCanonicalName, T stubBinder) {
        if (isBind) {
            try {
                IMessageService remoteService = getRemoteService(IMessageService.class);
                if(remoteService != null){
                    remoteService.unRegistMessageReceiveListener(null);//全部注销监听
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            context.unbindService(serviceConnection);
        }
    }

    public <T extends android.os.IInterface> T getRemoteService(@NonNull Class<T> serviceClass) {
        if (mCrashService == null) {
            throw new IllegalStateException("aidl no connect you must connect service");
        }

        if (null == serviceClass) {
            throw new IllegalStateException("aidl no connect you must connect service");
        }

        if (!mCrashService.containsKey(serviceClass)) {
            throw new RuntimeException("Not found " + serviceClass.getSimpleName() + "   in this map!");
        }


        android.os.IInterface obj = (android.os.IInterface) mCrashService.get(serviceClass);
        if (obj == null) {
            throw new RuntimeException("found " + serviceClass.getSimpleName() + " in this map is null !");
        }


        String simpleName = serviceClass.getSimpleName();
        Log.d(TAG, this.toString() + "-->getRemoteService,serviceName:" + simpleName);

        return serviceClass.cast(obj);
    }
}
