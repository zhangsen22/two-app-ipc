package com.watchdog.ipc.observers;

import static com.watchdog.ipc.LaunchAppManager.INSTALL_SUCCEEDED;

import android.content.pm.IPackageInstallObserver;
import android.os.RemoteException;
import com.watchdog.ipc.Logger;
import com.watchdog.ipc.interfaces.OnPackagedInstallObserver;

/**
 * 静默安装监听
 */
public class PackageInstallObserver extends IPackageInstallObserver.Stub {
    private OnPackagedInstallObserver mOnPackagedInstallObserver;
    public PackageInstallObserver(OnPackagedInstallObserver onPackagedInstallObserver) {
        this.mOnPackagedInstallObserver = onPackagedInstallObserver;
    }

    @Override
    public void packageInstalled(String packageName, int returnCode) throws RemoteException {
        Logger.i("packageName: "+packageName+" returnCode: "+returnCode);
        if(returnCode == INSTALL_SUCCEEDED){
            if(mOnPackagedInstallObserver != null){
                mOnPackagedInstallObserver.packageInstalled(packageName);
            }
        }
    }
}
