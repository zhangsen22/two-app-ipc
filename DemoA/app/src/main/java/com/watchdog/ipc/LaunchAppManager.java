package com.watchdog.ipc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import java.util.List;

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
     * 先说A拉起B可实现的几种方法
     * 方法一: 包名，特定Activity名拉起
     * B应用需要在manifest文件对应Activity添加 : android:exported="true"
     */
    public void openApp(Context context,String packageName, String activityName){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        /**知道要跳转应用的包命与目标Activity*/
        ComponentName componentName = new ComponentName(packageName, activityName);
        intent.setComponent(componentName);
        intent.putExtra("", "");//这里Intent传值
        context.startActivity(intent);
    }

/**
 * 先说A拉起B可实现的几种方法
 * 方法二: 包名拉起（这里就是进去启动页）
 */
public void openApp(Context context,String packageName){
    Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
    if (intent != null) {
        intent.putExtra("", "");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

/**
 * 先说A拉起B可实现的几种方法
 * 方法三: url拉起
 * B应用manifest文件需配置（注意：在原有intent-
 *
 * ，不是在原先里面，两个同时存在）
 * <intent-filter>
 *     <data
 *         android:host="pull.csd.demo"
 *         android:path="/cyn"
 *         android:scheme="csd" />
 *     <action android:name="android.intent.action.VIEW" />
 *     <category android:name="android.intent.category.DEFAULT" />
 *     <category android:name="android.intent.category.BROWSABLE" />
 * </intent-filter>
 */
public void openAppByUrl(Context context,String url){
    Intent intent = new Intent();
    intent.setData(Uri.parse("csd://pull.csd.demo/cyn"));
    intent.putExtra("", "");//这里Intent当然也可传递参数,但是一般情况下都会放到上面的URL中进行传递
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
}

    /**
     * 判断B应用是否安装
     *
     * @param packname
     * @return
     */
    public boolean checkPackInfo(Context context,String packname) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }


    /**
     * 判断B应用是否在后台运行并直接打开
     * @param context
     * @param packageName
     * @return
     */

    public boolean openPackage(Context context, String packageName) {
        Context pkgContext = getPackageContext(context, packageName);
        Intent intent = getAppOpenIntentByPackageName(context, packageName);
        if (pkgContext != null && intent != null) {
            pkgContext.startActivity(intent);
            return true;
        }
        return false;
    }

    public static Context getPackageContext(Context context, String packageName) {
        Context pkgContext = null;
        if (context.getPackageName().equals(packageName)) {
            pkgContext = context;
        } else {
            // 创建第三方应用的上下文环境
            try {
                pkgContext = context.createPackageContext(packageName,
                        Context.CONTEXT_IGNORE_SECURITY
                                | Context.CONTEXT_INCLUDE_CODE);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return pkgContext;
    }

    public Intent getAppOpenIntentByPackageName(Context context,String packageName){
        //Activity完整名
        String mainAct = null;
        //根据包名寻找
        PackageManager pkgMag = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED|Intent.FLAG_ACTIVITY_NEW_TASK);

        List<ResolveInfo> list = pkgMag.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
        for (int i = 0; i < list.size(); i++) {
            ResolveInfo info = list.get(i);
            if (info.activityInfo.packageName.equals(packageName)) {
                mainAct = info.activityInfo.name;
                break;
            }
        }
        if (TextUtils.isEmpty(mainAct)) {
            return null;
        }
        intent.setComponent(new ComponentName(packageName, mainAct));
        return intent;
    }

    /**
     * 获取当前设备上已经安装的所有 App
     * @param ctx
     * @return
     */
    public List<PackageInfo> getDeviceApp(Context ctx){
        PackageManager pm = ctx.getPackageManager();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        return installedPackages;
    }

    /**
     * 通过包名获取PackageInfo
     * @param ctx
     * @param packageName
     * @return
     */
    public PackageInfo getPackageInfo(Context ctx,String packageName){
        if(TextUtils.isEmpty(packageName)){
            return null;
        }
        PackageManager pm = ctx.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return packageInfo;
        }catch (Throwable ignore){

        }
return null;
    }

    /**
     * 获取版本号
     * @param ctx
     * @param packageName
     * @return
     */
    public int getVersionCode(Context ctx,String packageName){
        PackageInfo packageInfo = getPackageInfo(ctx, packageName);
        if(packageInfo != null){
            return packageInfo.versionCode;
        }
        return -1;
    }

    /**
     * 获取版本名
     * @param ctx
     * @param packageName
     * @return
     */
    public String getVersionName(Context ctx,String packageName){
        PackageInfo packageInfo = getPackageInfo(ctx, packageName);
        if(packageInfo != null){
            return packageInfo.versionName;
        }
        return null;
    }

    /**
     * 获取App的名称
     * @param ctx
     * @param packageName
     * @return
     */
    public String getApplicationLabel(Context ctx,String packageName){
        if(TextUtils.isEmpty(packageName)){
            return null;
        }
        PackageManager pm = ctx.getPackageManager();
        PackageInfo info = getPackageInfo(ctx, packageName);
        if(info != null){
            return info.applicationInfo.loadLabel(pm).toString();
        }
        return null;
    }

    /**
     * 获取App的Icon
     * @param ctx
     * @param packageName
     * @return
     */
    public Drawable getApplicationIcon(Context ctx, String packageName){
        if(TextUtils.isEmpty(packageName)){
            return null;
        }
        PackageManager pm = ctx.getPackageManager();
        PackageInfo info = getPackageInfo(ctx, packageName);
        if(info != null){
            return info.applicationInfo.loadIcon(pm);
        }
        return null;
    }

    /**
     * 根据Apk 文件，获取 PackageInfo
     * @param ctx
     * @param apkPath
     * @return
     */
    public PackageInfo getPackageArchiveInfo(Context ctx,String apkPath){
        try {
            PackageInfo info = ctx.getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
            return info;
        } catch (Throwable ignore){
            return null;
        }
    }

}
