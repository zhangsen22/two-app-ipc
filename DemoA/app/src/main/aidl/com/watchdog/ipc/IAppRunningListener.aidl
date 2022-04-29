// IAppRunningListener.aidl
package com.watchdog.ipc;

import com.watchdog.ipc.entry.AppInfo;

interface IAppRunningListener {

        void synchronizeAppInfo(in AppInfo appinfo);

        void onAppForeground();

        void onAppBackground();

        void onKillProcess();
}