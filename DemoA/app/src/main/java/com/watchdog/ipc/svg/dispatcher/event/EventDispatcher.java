package com.watchdog.ipc.svg.dispatcher.event;

import android.os.IBinder;
import android.os.RemoteException;

import com.watchdog.ipc.Logger;
import com.watchdog.ipc.svg.IRemoteTransfer;
import com.watchdog.ipc.svg.event.Event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangallen on 2018/1/24.
 */

public class EventDispatcher implements IEventDispatcher {

    private Map<Integer, IBinder> transferBinders = new ConcurrentHashMap<>();

    @Override
    public void registerRemoteTransferLocked(final int pid, IBinder transferBinder) {
        Logger.d("EventDispatcher-->registerRemoteTransferLocked,pid:" + pid);
        if (transferBinder == null) {
            return;
        }
        try {
            transferBinder.linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    transferBinders.remove(pid);
                }
            }, 0);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } finally {
            transferBinders.put(pid, transferBinder);
        }

    }

    @Override
    public void publishLocked(Event event) throws RemoteException {
        Logger.d("EventDispatcher-->publishLocked,event.name:" + event.getName());
        RemoteException ex = null;
        for (Map.Entry<Integer, IBinder> entry : transferBinders.entrySet()) {
            IRemoteTransfer transfer = IRemoteTransfer.Stub.asInterface(entry.getValue());
            //对于这种情况，如果有一个出现RemoteException,也不能就停下吧?
            if (null != transfer) {
                try {
                    transfer.notify(event);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    ex = e;
                }
            }
        }
        if (null != ex) {
            throw ex;
        }

    }

    @Override
    public void unregisterRemoteServiceLocked(String serviceCanonicalName) throws RemoteException {
        Logger.d("EventDispatcher-->unregisterRemoteServiceLocked,serviceCanonicalName:" + serviceCanonicalName);
        RemoteException e = null;
        for (Map.Entry<Integer, IBinder> entry : transferBinders.entrySet()) {
            IRemoteTransfer transfer = IRemoteTransfer.Stub.asInterface(entry.getValue());
            if (null != transfer) {
                try {
                    transfer.unregisterRemoteService(serviceCanonicalName);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                    e = ex;
                }
            }
        }
        if (null != e) {
            throw e;
        }
    }
}
