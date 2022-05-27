package com.watchdog.demoa.observers;

import static com.watchdog.demoa.LaunchAppManager.INSTALL_SUCCEEDED;

import android.content.pm.IPackageInstallObserver;
import android.os.RemoteException;
import com.watchdog.demoa.utils.Logger;
import com.watchdog.demoa.interfaces.OnPackagedInstallObserver;

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
