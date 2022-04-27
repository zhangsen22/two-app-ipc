package com.watchdog.ipc;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.watchdog.ipc.entry.Message;
import com.watchdog.ipc.services.BuyAppleImpl;
import com.watchdog.ipc.services.IMessageImpl;

public class WatchDogDispatcher {
    private static final String TAG = "Dispatcher";

    private Handler handler = new Handler(Looper.getMainLooper());

    private RemoteCallbackList<MessagereceiveListener> messagereceiveListenerList = new RemoteCallbackList<>();

    public static WatchDogDispatcher sInstance;

    //private
    private WatchDogDispatcher() {

    }

    private static class SingletonInstance{
        private final static WatchDogDispatcher S = new WatchDogDispatcher();
    }
    // 3 返回对象
    public static WatchDogDispatcher getInstance() {
        return SingletonInstance.S;
    }

    public Handler getHandler() {
        return handler;
    }

    public RemoteCallbackList<MessagereceiveListener> getMessagereceiveListenerList() {
        return messagereceiveListenerList;
    }

    private IServiceManager serviceManager = new IServiceManager.Stub() {
        @Override
        public IBinder getService(String serviceName) throws RemoteException {
            if(IMessageService.class.getSimpleName().equals(serviceName)){
                return IMessageImpl.getInstance().asBinder();
            }else if(IBuyApple.class.getSimpleName().equals(serviceName)){
                return BuyAppleImpl.getInstance().asBinder();
            } else {
                return null;
            }
        }
    };

    public IServiceManager getServiceManager() {
        return serviceManager;
    }

    /**
     * 给其他进程发送消息
     * @param message
     */
    public void dispatcherMessage(Message message){
        int size = messagereceiveListenerList.beginBroadcast();
        for (int i = 0; i < size; i++) {
            try {
                messagereceiveListenerList.getBroadcastItem(i).onReceiveMessage(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        messagereceiveListenerList.finishBroadcast();
    }
}
