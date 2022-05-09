// IClientDiedService.aidl
package com.watchdog.ipc;
import com.watchdog.ipc.IClientCallback;
import com.watchdog.ipc.entry.AppInfo;

interface IClientDiedService {
        void registerClientCallback(IClientCallback callBack,in AppInfo appinfo);//增加一个参数
}