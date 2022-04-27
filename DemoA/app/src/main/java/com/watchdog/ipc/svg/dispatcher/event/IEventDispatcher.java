package com.watchdog.ipc.svg.dispatcher.event;

import android.os.IBinder;
import android.os.RemoteException;

import com.watchdog.ipc.svg.event.Event;

/**
 * Created by wangallen on 2018/1/24.
 */

public interface IEventDispatcher {

    void registerRemoteTransferLocked(int pid, IBinder transferBinder);

    void publishLocked(Event event) throws RemoteException;

    void unregisterRemoteServiceLocked(String serviceCanonicalName) throws RemoteException;
}
