package com.watchdog.ipc;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 第一种：
 * Android 8.0之前的老系统版本，直接注册静态广播接受者即可实现；
 *
 * 第二种：
 * Android 8.0之后的版本，需要动态注册广播接收器，不能再清单文件中静态注册了；
 *
 *
 * 不同的操作发送不同的广播如下：
 *
 * 新安装:
 * 安装新APP发送的广播：android.intent.action.PACKAGE_ADDED
 *
 * 升级:
 * 覆盖安装APP发送的广播：
 * android.intent.action.PACKAGE_REMOVED //先卸载
 * android.intent.action.PACKAGE_ADDED //在安装
 * android.intent.action.PACKAGE_REPLACED //替换完成
 *
 * 卸载
 * 卸载APP发送的广播：android.intent.action.PACKAGE_REMOVED
 *
 */

public class PackageInstalledReceiver extends BroadcastReceiver {

    private static final String TAG = "PackageInstalledReceiver";
    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "APK动态广播！");
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getDataString();
            Log.e(TAG, packageName + "安装成功");
        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            Log.e(TAG, packageName + "替换成功");
        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            Log.e(TAG, packageName + "卸载成功");
        }
    }
}
