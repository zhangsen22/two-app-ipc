// IAppRunningService.aidl
package com.watchdog.demoa.ipc;

import com.watchdog.demoa.ipc.entry.AppInfo;
import com.watchdog.demoa.ipc.IClientCallback;

interface IAppRunningService {

        void synchronizeAppInfo(in AppInfo appinfo);

        void onAppForeground(in AppInfo appinfo);

        void onAppBackground(in AppInfo appinfo);

        void registerClientCallback(IClientCallback callBack,in AppInfo appinfo);//增加一个参数

        void onKillProcess();
}