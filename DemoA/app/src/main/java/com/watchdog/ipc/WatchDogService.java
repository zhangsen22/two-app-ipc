package com.watchdog.ipc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

/**
 * 管理链接
 */
public class WatchDogService extends Service {
    private String CHANNEL_ID = "Background job";

    public WatchDogService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, getNotification());
        Logger.i("[WatchDogService] onCreate - Thread ID = " + Thread.currentThread().getId());
        WatchDogDispatcher.getInstance().onCreate(getApplicationContext());
    }

    private Notification getNotification() {
        Context mContext = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder;
        //创建NotificationChannel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);
            // 必须创建notifychannel, 不然会抛异常Bad notification for startForeground: java.lang.RuntimeException: invalid channel for service
            notificationManager.createNotificationChannel(channel);
            //设置Notification的ChannelID,否则不能正常显示
            builder = new Notification.Builder(mContext, CHANNEL_ID);
        }else {
            builder = new Notification.Builder(mContext);
        }

        Notification notification = builder.setContentTitle("WatchDogService is Running background")
                .setContentText(mContext.getPackageName())
                .setSmallIcon(R.drawable.notification_icon)
                .build();
        return notification;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.i("[WatchDogService] onBind - Thread ID = " + Thread.currentThread().getId());
        return WatchDogDispatcher.getInstance().getServiceManager().asBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.i("[WatchDogService] onUnbind - Thread ID = " + Thread.currentThread().getId());
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Logger.i("[WatchDogService] onRebind - Thread ID = " + Thread.currentThread().getId());
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.i("[WatchDogService] onStartCommand - startId = " + startId + ", Thread ID = " + Thread.currentThread().getId());
        /**
         * 表示Service运行的进程被Android系统强制杀掉之后，Android系统会将该Service依然设置为started状态（即运行状态），
         * 但是不再保存onStartCommand方法传入的intent对象，然后Android系统会尝试再次重新创建该Service，并执行onStartCommand回调方法，
         * 但是onStartCommand回调方法的Intent参数为null，也就是onStartCommand方法虽然会执行但是获取不到intent信息。
         * 如果你的Service可以在任意时刻运行或结束都没什么问题，而且不需要intent信息，那么就可以在onStartCommand方法中返回START_STICKY
         */
//        getPackageManager().setComponentEnabledSetting(new ComponentName(getPackageName(), WatchDogService.class.getName()),
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        LaunchAppManager.getInstance().startJobSchedulerService(getApplicationContext());
        return START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Logger.i("[WatchDogService] onDestroy - Thread ID = " + Thread.currentThread().getId());
        super.onDestroy();
        stopForeground(true);
    }
}
