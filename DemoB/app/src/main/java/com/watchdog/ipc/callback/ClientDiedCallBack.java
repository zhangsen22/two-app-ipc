package com.watchdog.ipc.callback;

import android.os.RemoteException;

import com.watchdog.ipc.IClientCallback;

public class ClientDiedCallBack extends IClientCallback.Stub{
    @Override
    public void clientDiedCallBack() throws RemoteException {

    }
}
