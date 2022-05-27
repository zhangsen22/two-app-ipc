package com.watchdog.demoa.ipc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.watchdog.demoa.ipc.callback.ClientDiedCallBack;
import com.watchdog.demoa.ipc.entry.AppInfo;

import java.util.concurrent.ConcurrentHashMap;

public class IWatchDogManager {
    private static final String TAG = "IWatchDogManager";
    private static final String WATCHDOG_ACTION = "com.watchdog.demoa.WatchDogService";
    private static final String WATCHDOG_PACKAGE = "com.watchdog.demoa";
    private Context mApplicationContext;

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
//            synchronized (IWatchDogManager.class) {
                isBind = true;
                IServiceManager serviceManagerProxy = IServiceManager.Stub.asInterface(service);
                if (serviceManagerProxy != null) {
                    mCrashService.put(IServiceManager.class, serviceManagerProxy);
                    try {
                        IMessageService messageServiceProxy = IMessageService.Stub.asInterface(serviceManagerProxy.getService(IMessageService.class.getSimpleName()));
                        if (messageServiceProxy != null) {
                            mCrashService.put(IMessageService.class, messageServiceProxy);
                        }

                        IAppRunningService appRunningServiceProxy = IAppRunningService.Stub.asInterface(serviceManagerProxy.getService(IAppRunningService.class.getSimpleName()));
                        if (appRunningServiceProxy != null) {
                            AppInfo appInfo = new AppInfo();
                            appInfo.setPackageName("com.example.demob");
                            appRunningServiceProxy.registerClientCallback(new ClientDiedCallBack(),appInfo);
                            mCrashService.put(IAppRunningService.class, appRunningServiceProxy);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
//            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, this.toString() + "-->onServiceDisconnected");
            unRegisterRemoteService(mApplicationContext,null,null);
            /**
             * service端断开连接
             */
            isBind = false;
            mCrashService.clear();
        }

        @Override
        public void onBindingDied(ComponentName name) {
            Log.d(TAG, this.toString() + "-->onBindingDied");
            ServiceConnection.super.onBindingDied(name);
            registerRemoteService(mApplicationContext,null,null);
        }

        @Override
        public void onNullBinding(ComponentName name) {
            Log.d(TAG, this.toString() + "-->onNullBinding");
            ServiceConnection.super.onNullBinding(name);
        }
    };

    /**
     * bindService -> WatchDogService
     * @param context
     * @param serviceCanonicalName
     * @param stubBinder
     * @param <T>
     */
    public <T extends IBinder> void registerRemoteService(Context context, String serviceCanonicalName, T stubBinder) {
        mApplicationContext = context;
        Intent mIntent = new Intent();
        mIntent.setAction(WATCHDOG_ACTION);
        mIntent.setPackage(WATCHDOG_PACKAGE);
//        mIntent.setComponent(new ComponentName(WATCHDOG_PACKAGE, WATCHDOG_ACTION));
        context.bindService(mIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * unbindService -> WatchDogService
     * @param context
     * @param serviceCanonicalName
     * @param stubBinder
     * @param <T>
     */
    public <T extends IBinder> void unRegisterRemoteService(Context context, String serviceCanonicalName, T stubBinder) {
        if (isBind) {
//            try {
//                IMessageService remoteService = getRemoteService(IMessageService.class);
//                if(remoteService != null){
//                    remoteService.unRegistMessageReceiveListener(null);//全部注销监听
//                }
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
            context.unbindService(serviceConnection);
        }
    }

    /**
     * 获取的返回值需要判空
     * @param serviceClass
     * @param <T>
     * @return
     */
    public <T extends android.os.IInterface> T getRemoteService(@NonNull Class<T> serviceClass) {

        if(!isBind){
            throw new IllegalStateException("service is die");
        }

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
