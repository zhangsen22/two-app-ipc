package com.watchdog.demoa.ipc.callback;

import android.os.RemoteException;

import com.watchdog.demoa.ipc.IClientCallback;

public class ClientDiedCallBack extends IClientCallback.Stub{
    @Override
    public void clientDiedCallBack() throws RemoteException {

    }
}
