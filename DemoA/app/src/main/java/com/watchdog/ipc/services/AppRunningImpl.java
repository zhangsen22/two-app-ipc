package com.watchdog.ipc.services;

import android.os.RemoteException;

import com.watchdog.ipc.IAppRunningService;
import com.watchdog.ipc.IClientCallback;
import com.watchdog.ipc.WatchDogDispatcher;
import com.watchdog.ipc.entry.AppInfo;

public class AppRunningImpl extends IAppRunningService.Stub {

    private static class SingletonInstance{
        private final static AppRunningImpl S = new AppRunningImpl();
    }
    // 3 返回对象
    public static AppRunningImpl getInstance() {
        return AppRunningImpl.SingletonInstance.S;
    }


    //private
    private AppRunningImpl() {

    }

    @Override
    public void synchronizeAppInfo(AppInfo appinfo) throws RemoteException {

    }

    @Override
    public void onAppForeground(AppInfo appinfo) throws RemoteException {

    }

    @Override
    public void onAppBackground(AppInfo appinfo) throws RemoteException {

    }

    @Override
    public void registerClientCallback(IClientCallback callBack, AppInfo appinfo) throws RemoteException {
        if(WatchDogDispatcher.getInstance().getCallbackList() != null){
            WatchDogDispatcher.getInstance().getCallbackList().register(callBack,appinfo);
        }
    }

    @Override
    public void onKillProcess() throws RemoteException {

    }
}
