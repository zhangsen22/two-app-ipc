package com.watchdog.demoa;

import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import com.watchdog.demoa.interfaces.OnPackagedDeleteObserver;
import com.watchdog.demoa.interfaces.OnPackagedInstallObserver;
import com.watchdog.demoa.observers.PackageDeleteObserver;
import com.watchdog.demoa.observers.PackageInstallObserver;
import com.watchdog.demoa.scheduler.JobSchedulerService;
import com.watchdog.demoa.utils.PackageInfoManager;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LaunchAppManager {
    public static final int INSTALL_SUCCEEDED = 1;
    public static final int DELETE_SUCCEEDED = 1;
    public static final int INSTALL_FLAG = 2;
    public static final int UNINSTALL_FLAG = 0;
    private Intent watchDogService = null;
    private Intent jobSchedulerService = null;


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

    /**
     * startService -> WatchDogService
     * @param context
     */
    public void startWatchDogService(Context context){
        //开启服务
        if(watchDogService == null) {
            watchDogService = new Intent(context, WatchDogService.class);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(watchDogService);
        } else {
            context.startService(watchDogService);
        }
    }

    /**
     * stopService -> WatchDogService
     * @param context
     */
    public void stopWatchDogService(Context context){
        if(watchDogService != null){
            context.stopService(watchDogService);
        }
    }

    /**
     * startService -> JobSchedulerService
     * @param context
     */
    public void startJobSchedulerService(Context context){
        //开启服务
        // 启动服务并提供一种与此类通信的方法。
        if(jobSchedulerService == null) {
            jobSchedulerService = new Intent(context, JobSchedulerService.class);
        }
        context.startService(jobSchedulerService);
    }

    /**
     * stopService -> JobSchedulerService
     * @param context
     */
    public void stopJobSchedulerService(Context context){
        // 服务可以是“开始”和/或“绑定”。 在这种情况下，它由此Activity“启动”
        // 和“绑定”到JobScheduler（也被JobScheduler称为“Scheduled”）。
        // 对stopService（）的调用不会阻止处理预定作业。
        // 然而，调用stopService（）失败将使它一直存活。
        if(jobSchedulerService != null) {
            context.stopService(jobSchedulerService);
        }
    }
}
