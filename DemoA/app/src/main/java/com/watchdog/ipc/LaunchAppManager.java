package com.watchdog.ipc;

import android.content.Context;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.watchdog.ipc.interfaces.OnPackagedDeleteObserver;
import com.watchdog.ipc.interfaces.OnPackagedInstallObserver;
import com.watchdog.ipc.observers.PackageDeleteObserver;
import com.watchdog.ipc.observers.PackageInstallObserver;
import com.watchdog.ipc.utils.PackageInfoManager;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LaunchAppManager {
    private static final String TAG = "LaunchAppManager";
    public static final int INSTALL_SUCCEEDED = 1;
    public static final int DELETE_SUCCEEDED = 1;
    public static final int INSTALL_FLAG = 2;
    public static final int UNINSTALL_FLAG = 0;


    /**
     * 单例设计模式
     * 需求分析:
     * 1.A点击拉起B；
     * <p>
     * 2.如果B没安装，下载安装；
     * <p>
     * 3.如果B已安转，未在后台运行点击打开B；
     * <p>
     * 4.如果B已安装，且正在后台运行，A打开B直接显示在后台运行的页面；
     */
    private static class SingletonHolder {
        private static LaunchAppManager instance = new LaunchAppManager();
    }

    private LaunchAppManager() {
    }

    public static LaunchAppManager getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * 判断应用是否安装
     *
     * @param context
     * @param packname
     * @return
     */
    public boolean checkPackInfo(Context context, String packname) {
        return PackageInfoManager.checkPackInfo(context, packname);
    }

    /**
     * 判断应用是否在后台运行并直接打开
     *
     * @param context
     * @param packname
     * @return
     */

    public boolean openPackage(Context context, String packname) {
        return PackageInfoManager.openPackage(context, packname);
    }

    /**
     * 反射PackageManager中的静默安装方法installPackage并进行调用
     * 安装指定apk
     *
     * @param context
     * @param path apk路径
     */
    public void installApk(Context context, String path, OnPackagedInstallObserver onPackagedInstallObserver) {
        PackageManager pm = context.getPackageManager();
        Uri data = Uri.fromFile(new File(path)) ;
        String packageName = context.getPackageName();

        Class<?>[] types = new Class[]{Uri.class, IPackageInstallObserver.class, int.class, String.class};
        try {
            Method installmethod = pm.getClass().getMethod("installPackage", types);
            installmethod.invoke(pm, new Object[]{data, new PackageInstallObserver(onPackagedInstallObserver), INSTALL_FLAG,packageName});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 卸载指定apk
     *
     * @param context
     * @param packagename apk包名
     */
    public void unInstallApk(Context context, String packagename, OnPackagedDeleteObserver onPackagedDeleteObserver) {
        Log.i(TAG, "[unInstallApk] 1");
//        PackageInfoManager.killProcess(context,packagename);//先杀死进程
        Log.i(TAG, "[unInstallApk] 2");
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        Log.i(TAG, "[unInstallApk] 3");
        PackageManager pm = context.getPackageManager();
        Class<?>[] uninstalltypes = new Class[] {String.class, IPackageDeleteObserver.class, int.class};
        try {
            Method uninstallmethod = pm.getClass().getMethod("deletePackage", uninstalltypes);
            uninstallmethod.invoke(pm, new Object[] {packagename, new PackageDeleteObserver(onPackagedDeleteObserver), 0});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
