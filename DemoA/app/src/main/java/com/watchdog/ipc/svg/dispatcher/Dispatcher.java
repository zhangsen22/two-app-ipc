package com.watchdog.ipc.svg.dispatcher;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.watchdog.ipc.Logger;
import com.watchdog.ipc.svg.Andromeda;
import com.watchdog.ipc.svg.IDispatcher;
import com.watchdog.ipc.svg.backup.EmergencyHandler;
import com.watchdog.ipc.svg.backup.IEmergencyHandler;
import com.watchdog.ipc.svg.bean.BinderBean;
import com.watchdog.ipc.svg.dispatcher.event.EventDispatcher;
import com.watchdog.ipc.svg.dispatcher.event.IEventDispatcher;
import com.watchdog.ipc.svg.event.Event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangallen on 2018/1/24.
 */

public class Dispatcher extends IDispatcher.Stub {
    private static final String TAG = "Andromeda";

    public static Dispatcher sInstance;

    public static Dispatcher getInstance() {
        if (null == sInstance) {
            synchronized (Dispatcher.class) {
                if (null == sInstance) {
                    sInstance = new Dispatcher();
                }
            }
        }
        return sInstance;
    }

    private IEmergencyHandler emergencyHandler;

    private Map<String, BinderBean> remoteBinderCache = new ConcurrentHashMap<>();

    private IEventDispatcher eventDispatcher;

    private Dispatcher() {
        eventDispatcher = new EventDispatcher();
        emergencyHandler = new EmergencyHandler();
    }

    //给同进程的DispatcherService调用的和远程调用
    @Override
    public synchronized void registerRemoteTransfer(int pid, IBinder transferBinder) {
        if (pid < 0) {
            return;
        }
        eventDispatcher.registerRemoteTransferLocked(pid, transferBinder);
    }


    @Override
    public synchronized BinderBean getTargetBinder(String serviceCanonicalName) throws RemoteException {
        Log.d(TAG, "ServiceDispatcher-->getTargetBinderLocked,serivceName:" + serviceCanonicalName + ",pid:" + android.os.Process.myPid() + ",thread:" + Thread.currentThread().getName());
        BinderBean bean = remoteBinderCache.get(serviceCanonicalName);
        if (null == bean) {
            return null;
        } else {
            return bean;
        }
    }

    @Override
    public synchronized void registerRemoteService(final String serviceCanonicalName, String processName, IBinder binder) throws RemoteException {
        Log.d(TAG, "ServiceDispatcher-->registerRemoteServiceLocked,serviceCanonicalName:" + serviceCanonicalName + ",pid:" + android.os.Process.myPid() + ",thread:" + Thread.currentThread().getName());
        if (binder != null) {
            binder.linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    Logger.d("ServiceDispatcher-->binderDied,serviceCanonicalName:" + serviceCanonicalName);
                    BinderBean bean = remoteBinderCache.remove(serviceCanonicalName);
                    //实际上这里是还没实现线程同步，但是并不会影响执行结果，所以其实下面这句就没有同步的必要。
                    if (bean != null) {
                        emergencyHandler.handleBinderDied(Andromeda.getAppContext(), bean.getProcessName());
                    }
                }
            }, 0);
            remoteBinderCache.put(serviceCanonicalName, new BinderBean(binder, processName));
            Logger.d("ServiceDispatcher-->registerRemoteServiceLocked(),binder is not null");
        } else {
            Log.d(TAG, "ServiceDispatcher-->registerRemoteServiceLocked(),binder is null");
        }
    }

    @Override
    public synchronized void unregisterRemoteService(String serviceCanonicalName) throws RemoteException {
        remoteBinderCache.remove(serviceCanonicalName);
        //然后让EventDispatcher通知各个进程清除缓存
        eventDispatcher.unregisterRemoteServiceLocked(serviceCanonicalName);
    }

    @Override
    public synchronized void publish(Event event) throws RemoteException {
        eventDispatcher.publishLocked(event);
    }

}
