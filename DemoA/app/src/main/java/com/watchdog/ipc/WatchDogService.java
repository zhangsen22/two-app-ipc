package com.watchdog.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.watchdog.ipc.entry.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 管理链接
 */
public class WatchDogService extends Service {
    private static final String TAG = "BinderSimple";
    //// aidl 接口专用容器
//    private RemoteCallbackList<IReceive> callbackList = new RemoteCallbackList<>();

    private Handler handler = new Handler(Looper.getMainLooper());

    private RemoteCallbackList<MessagereceiveListener> messagereceiveListenerList = new RemoteCallbackList<>();

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    private ScheduledFuture scheduledFuture;

    private boolean isConnected = false;

    public WatchDogService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "[RemoteService] onCreate");
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"[RemoteService] onBind");
        return serviceManager.asBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "[RemoteService] onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "[RemoteService] onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "[RemoteService] onDestroy");
        super.onDestroy();
    }


    private IConnectionService connectionService = new IConnectionService.Stub() {
        @Override
        public void connection() throws RemoteException {
            isConnected = true;
            try {
                //模拟连接阻塞
                Thread.sleep(5000);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WatchDogService.this,"connection",Toast.LENGTH_SHORT).show();
                    }
                });

                scheduledFuture = scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        int size = messagereceiveListenerList.beginBroadcast();
                        for (int i = 0; i < size; i++) {
                            Message message = new Message();
                            message.setContent("this is from WatchDogService");
                            try {
                                messagereceiveListenerList.getBroadcastItem(i).onReceiveMessage(message);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        messagereceiveListenerList.finishBroadcast();
                    }
                },5000,5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void disconnection() throws RemoteException {
            isConnected = false;
            scheduledFuture.cancel(true);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WatchDogService.this,"disconnection",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public boolean isConnection() throws RemoteException {
            return isConnected;
        }
    };

    private IMessageService messageService = new IMessageService.Stub() {
        @Override
        public void sendMessage(Message message) throws RemoteException {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WatchDogService.this,message.toString(),Toast.LENGTH_SHORT).show();
                }
            });

            if(isConnected){
                message.setSendSuccess(true);
            }else {
                message.setSendSuccess(false);
            }
        }

        @Override
        public void registMessageReceiveListener(MessagereceiveListener messagereceiveListener) throws RemoteException {
            if(messagereceiveListener != null) {
                messagereceiveListenerList.register(messagereceiveListener);
            }
        }

        @Override
        public void unRegistMessageReceiveListener(MessagereceiveListener messagereceiveListener) throws RemoteException {
            if(messagereceiveListener != null) {
                messagereceiveListenerList.unregister(messagereceiveListener);
            }
        }
    };

    private IServiceManager serviceManager = new IServiceManager.Stub() {
        @Override
        public IBinder getService(String serviceName) throws RemoteException {
            if(IConnectionService.class.getSimpleName().equals(serviceName)){
                return connectionService.asBinder();
            }else if(IMessageService.class.getSimpleName().equals(serviceName)){
                return messageService.asBinder();
            }else if(IBuyApple.class.getSimpleName().equals(serviceName)){
                return BuyAppleImpl.getInstance().asBinder();
            } else {
                return null;
            }
        }
    };

}
