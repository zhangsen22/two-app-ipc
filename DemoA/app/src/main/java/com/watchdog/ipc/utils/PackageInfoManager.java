package com.watchdog.ipc.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;

import java.util.List;

public class PackageInfoManager {

    /**
     * 先说A拉起B可实现的几种方法
     * 方法一: 包名，特定Activity名拉起
     * B应用需要在manifest文件对应Activity添加 : android:exported="true"
     */
    public void openApp(Context context, String packageName, String activityName){
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
    public static boolean checkPackInfo(Context context, String packname) {
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

    public static boolean openPackage(Context context, String packageName) {
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

    public static Intent getAppOpenIntentByPackageName(Context context, String packageName){
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
     * 判断是否在主进程,这个方法判断进程名或者pid都可以,如果进程名一样那pid肯定也一样
     *
     * @return true:当前进程是主进程 false:当前进程不是主进程
     */
    public boolean isUIProcess(Context ctx) {
        ActivityManager am = ((ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = ctx.getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }



    /**
     * 判断service是否已经运行
     * 必须判断uid,因为可能有重名的Service,所以要找自己程序的Service,不同进程只要是同一个程序就是同一个uid,个人理解android系统中一个程序就是一个用户
     * 用pid替换uid进行判断强烈不建议,因为如果是远程Service的话,主进程的pid和远程Service的pid不是一个值,在主进程调用该方法会导致Service即使已经运行也会认为没有运行
     * 如果Service和主进程是一个进程的话,用pid不会出错,但是这种方法强烈不建议,如果你后来把Service改成了远程Service,这时候判断就出错了
     *
     * @param className Service的全名,例如PushService.class.getName()
     * @return true:Service已运行 false:Service未运行
     */
    public boolean isServiceExisted(Context ctx,String className) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = am.getRunningServices(Integer.MAX_VALUE);
        int myUid = android.os.Process.myUid();
        for (ActivityManager.RunningServiceInfo runningServiceInfo : serviceList) {
            if (runningServiceInfo.uid == myUid && runningServiceInfo.service.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
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

    /**
     * 只能杀死别人，不能杀死自己，比较优雅一点哈~
     * @param ctx
     * @param packageName
     */
    public static void killProcess(Context ctx, String packageName){
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(packageName);  //应用的包名
    }

//    public static boolean isAppAlive() {
//        String pName = GlobalContext.getPackageName();
//        int uid = getPackageUid(GlobalContext.getAppContext(), GlobalContext.getPackageName());
//        if (uid > 0) {
//            boolean rstA = isAppRunning(GlobalContext.getAppContext(), pName);
//            boolean rstB = isProcessRunning(GlobalContext.getAppContext(), uid);
//            Log.i(TAG, "isAppAlive: " + (rstA || rstB) + " pName :" + pName + " rstA :" + rstA + " rstB: " + rstB);
//            if (rstA || rstB) {
//                //指定包名的程序正在运行中
//                return true;
//            } else {
//                //指定包名的程序未在运行中
//                return false;
//            }
//        } else {
//            //应用未安装
//            return false;
//        }
//    }

    /**
     * 方法描述：判断某一应用是否正在运行
     *
     * @param context     上下文
     * @param packageName 应用的包名
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (list.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }


    //获取已安装应用的 uid，-1 表示未安装此应用或程序异常
    public static int getPackageUid(Context context, String packageName) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            if (applicationInfo != null) {
                return applicationInfo.uid;
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    /**
     * 判断某一 uid 的程序是否有正在运行的进程，即是否存活
     *
     * @param context 上下文
     * @param uid     已安装应用的 uid
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isProcessRunning(Context context, int uid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(200);
        if (runningServiceInfos.size() > 0) {
            for (ActivityManager.RunningServiceInfo appProcess : runningServiceInfos) {
                if (uid == appProcess.uid) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断版本更新
     * @param localVersion 本地app 版本号 1.2.3
     * @param newVersion 最新版本号       1.2.4
     * @return true 需要更新 false 不用
     */
    public static boolean updateApp(String localVersion, String newVersion) {
        String[] localVersionArray = localVersion.split("\\.");
        String[] newVersionArray = newVersion.split("\\.");
        if (localVersionArray.length < newVersionArray.length) {
            int cha = newVersionArray.length - localVersionArray.length;
            for (int i = 0; i < cha; i++) {
                localVersion = localVersion + ".0";
            }
            localVersionArray = localVersion.split("\\.");
        }
        try {
            for (int i = 0; i < newVersionArray.length; i++) {
                int temp = Integer.parseInt(newVersionArray[i]);
                int compar = Integer.parseInt(localVersionArray[i]);
                if (temp > compar) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
