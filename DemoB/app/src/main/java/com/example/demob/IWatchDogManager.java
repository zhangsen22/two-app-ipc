package com.example.demob;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.watchdog.ipc.IBuyApple;
import com.watchdog.ipc.IMessageService;
import com.watchdog.ipc.IServiceManager;

public class IWatchDogManager {
    private static final String TAG = "IWatchDogManager";

    private IServiceManager serviceManagerProxy;

    //private
    private IWatchDogManager() {

    }

    private static class SingletonInstance{
        private final static IWatchDogManager S = new IWatchDogManager();
    }
    // 3 返回对象
    public static IWatchDogManager getInstance() {
        return SingletonInstance.S;
    }

    public <T extends IBinder> void registerRemoteService(Context context, String serviceCanonicalName, T stubBinder) {
        Intent mIntent = new Intent();
        mIntent.setAction("com.watchdog.ipc.WatchDogService");
        mIntent.setPackage("com.watchdog.ipc");
//        mIntent.setComponent(new ComponentName("com.watchdog.ipc", "com.watchdog.ipc.WatchDogService"));
        context.bindService(mIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                serviceManagerProxy = IServiceManager.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }

    public IBinder getRemoteService(Class<?> serviceClass) {
        if (null == serviceClass) {
            return null;
        }

            String simpleName = serviceClass.getSimpleName();

            Log.d(TAG,this.toString() + "-->getRemoteService,serviceName:" + simpleName);
            if (TextUtils.isEmpty(simpleName)) {
                return null;
            }
            if (serviceManagerProxy == null) {
                Log.e(TAG,"Found no binder for "+simpleName+"! Please check you have register implementation for it or proguard reasons!");
                return null;
            }
            try {
                return serviceManagerProxy.getService(simpleName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        return null;

    }

    public IMessageService getMessageServiceProxy(){
        IMessageService iMessageService = IMessageService.Stub.asInterface(getRemoteService(IMessageService.class));
        return iMessageService;
    }

    public IBuyApple getBuyAppleProxy() {
        IBuyApple buyApple = IBuyApple.Stub.asInterface(getRemoteService(IBuyApple.class));
        return buyApple;
    }
}
