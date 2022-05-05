// IConnectionService.aidl
package com.watchdog.ipc;

//连接服务
interface IConnectionService {

   oneway void connection();

   void disconnection();

   boolean isConnection();

}