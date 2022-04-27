// IRemoteService.aidl
package com.watchdog.ipc.svg;
import com.watchdog.ipc.svg.event.Event;
// Declare any non-default types here with import statements
import com.watchdog.ipc.svg.bean.BinderBean;

interface IDispatcher {

   BinderBean getTargetBinder(String serviceCanonicalName);

   void registerRemoteTransfer(int pid,IBinder remoteTransferBinder);

   void registerRemoteService(String serviceCanonicalName,String processName,IBinder binder);

   void unregisterRemoteService(String serviceCanonicalName);

   void publish(in Event event);

}
