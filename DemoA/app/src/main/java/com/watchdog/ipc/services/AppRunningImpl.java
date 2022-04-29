package com.watchdog.ipc.services;

import android.os.RemoteException;

import com.watchdog.ipc.IAppRunningListener;
import com.watchdog.ipc.entry.AppInfo;

public class AppRunningImpl extends IAppRunningListener.Stub {

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
    public void onAppForeground() throws RemoteException {

    }

    @Override
    public void onAppBackground() throws RemoteException {

    }

    @Override
    public void onKillProcess() throws RemoteException {

    }
}
