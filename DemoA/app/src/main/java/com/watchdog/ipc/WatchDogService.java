package com.watchdog.ipc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * 管理链接
 */
public class WatchDogService extends Service {
    private static final String TAG = "WatchDogService";

    public WatchDogService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        String ID = "com.sidebar.project";	//这里的id里面输入自己的项目的包的路径
//        String NAME = "LEFTBAR";
//        Intent intent = new Intent(WatchDogService.this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//        NotificationCompat.Builder notification; //创建服务对象
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(ID, NAME, manager.IMPORTANCE_HIGH);
//            channel.enableLights(true);
//            channel.setShowBadge(true);
//            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//            manager.createNotificationChannel(channel);
//            notification = new NotificationCompat.Builder(WatchDogService.this).setChannelId(ID);
//        } else {
//            notification = new NotificationCompat.Builder(WatchDogService.this);
//        }
//        notification.setContentIntent(pendingIntent).build();
//        Notification notification1 = notification.build();
//        startForeground(1,notification1);
//        //manager.notify(1,notification1);



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Context mContext = getApplicationContext();
            String CHANNEL_ID = "Background job";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            Notification notification =
                    new Notification.Builder(mContext, CHANNEL_ID)
                            .setContentTitle("Running background job")
                            .setContentText(mContext.getPackageName())
                            .setSmallIcon(R.drawable.notification_icon)
                            .build();
            startForeground(1, notification);
        }




        Log.i(TAG, "[WatchDogService] onCreate - Thread ID = " + Thread.currentThread().getId());
        WatchDogDispatcher.getInstance().onCreate(getApplicationContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "[WatchDogService] onBind - Thread ID = " + Thread.currentThread().getId());
        return WatchDogDispatcher.getInstance().getServiceManager().asBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "[WatchDogService] onUnbind - Thread ID = " + Thread.currentThread().getId());
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "[WatchDogService] onRebind - Thread ID = " + Thread.currentThread().getId());
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "[WatchDogService] onStartCommand - startId = " + startId + ", Thread ID = " + Thread.currentThread().getId());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "[WatchDogService] onDestroy - Thread ID = " + Thread.currentThread().getId());
        super.onDestroy();
        stopForeground(true);
    }

}
