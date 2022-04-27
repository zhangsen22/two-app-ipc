// IServiceManager.aidl
package com.watchdog.ipc;

// Declare any non-default types here with import statements

interface IServiceManager {

    IBinder getService(String serviceName);
}