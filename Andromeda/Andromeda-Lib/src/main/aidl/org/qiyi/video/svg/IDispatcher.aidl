// IRemoteService.aidl
package org.qiyi.video.svg;
import org.qiyi.video.svg.event.Event;
// Declare any non-default types here with import statements
import org.qiyi.video.svg.bean.BinderBean;

interface IDispatcher {

   BinderBean getTargetBinder(String serviceCanonicalName);

   void registerRemoteTransfer(int pid,IBinder remoteTransferBinder);

   void registerRemoteService(String serviceCanonicalName,String processName,IBinder binder);

   void unregisterRemoteService(String serviceCanonicalName);

   void publish(in Event event);

}
