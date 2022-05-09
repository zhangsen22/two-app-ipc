package com.watchdog.ipc.services;

import android.os.RemoteException;

import com.watchdog.ipc.IClientCallback;
import com.watchdog.ipc.IClientDiedService;
import com.watchdog.ipc.WatchDogDispatcher;
import com.watchdog.ipc.entry.AppInfo;

public class ClientDiedServiceImpl extends IClientDiedService.Stub{

    //private
    private ClientDiedServiceImpl() {

    }

    private static class SingletonInstance{
        private final static ClientDiedServiceImpl S = new ClientDiedServiceImpl();
    }
    // 3 返回对象
    public static ClientDiedServiceImpl getInstance() {
        return ClientDiedServiceImpl.SingletonInstance.S;
    }

    @Override
    public void registerClientCallback(IClientCallback callBack, AppInfo appinfo) throws RemoteException {
        if(WatchDogDispatcher.getInstance().getCallbackList() != null){
            WatchDogDispatcher.getInstance().getCallbackList().register(callBack,appinfo);
        }
    }
}
