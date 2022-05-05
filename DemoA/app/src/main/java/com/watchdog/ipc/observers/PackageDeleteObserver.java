package com.watchdog.ipc.observers;

import static com.watchdog.ipc.LaunchAppManager.DELETE_SUCCEEDED;

import android.content.pm.IPackageDeleteObserver;
import android.os.RemoteException;

import com.watchdog.ipc.Logger;
import com.watchdog.ipc.interfaces.OnPackagedDeleteObserver;

/**
 * 静默卸载监听
 */
public class PackageDeleteObserver extends IPackageDeleteObserver.Stub {
    private OnPackagedDeleteObserver mOnPackagedDeleteObserver;

    public PackageDeleteObserver(OnPackagedDeleteObserver onPackagedDeleteObserver) {
        this.mOnPackagedDeleteObserver = onPackagedDeleteObserver;
    }

    @Override
    public void packageDeleted(String packageName, int returnCode) throws RemoteException {
        Logger.i("packageName: " + packageName + " returnCode: " + returnCode);
        if (returnCode == DELETE_SUCCEEDED) {
            if (mOnPackagedDeleteObserver != null) {
                mOnPackagedDeleteObserver.packageDeleted(packageName);
            }
        }
    }
}
