package com.watchdog.ipc;

import android.content.Context;
import com.watchdog.ipc.utils.PackageInfoManager;

public class LaunchAppManager {

    /**
     * 单例设计模式
     * 需求分析:
     * 1.A点击拉起B；
     *
     * 2.如果B没安装，下载安装；
     *
     * 3.如果B已安转，未在后台运行点击打开B；
     *
     * 4.如果B已安装，且正在后台运行，A打开B直接显示在后台运行的页面；
     */
    private static class SingletonHolder{
        private static LaunchAppManager instance = new LaunchAppManager();
    }
    private LaunchAppManager(){}
    public static LaunchAppManager getInstance(){
        return SingletonHolder.instance;
    }

    /**
     * 判断应用是否安装
     * @param context
     * @param packname
     * @return
     */
    public boolean checkPackInfo(Context context, String packname){
       return PackageInfoManager.checkPackInfo(context, packname);
    }

    /**
     * 判断应用是否在后台运行并直接打开
     * @param context
     * @param packname
     * @return
     */

    public boolean openPackage(Context context, String packname) {
        return PackageInfoManager.openPackage(context, packname);
    }
}
