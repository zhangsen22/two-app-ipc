package com.watchdog.ipc.services;

import android.os.Bundle;
import android.os.RemoteException;
import com.watchdog.ipc.IMessageService;
import com.watchdog.ipc.IPCCallback;
import com.watchdog.ipc.Logger;
import com.watchdog.ipc.MessagereceiveListener;
import com.watchdog.ipc.WatchDogDispatcher;
import com.watchdog.ipc.entry.Message;

public class IMessageImpl extends IMessageService.Stub {

    private static class SingletonInstance{
        private final static IMessageImpl S = new IMessageImpl();
    }
    // 3 返回对象
    public static IMessageImpl getInstance() {
        return IMessageImpl.SingletonInstance.S;
    }

    @Override
    public void sendMessage(Message message) throws RemoteException {
        Logger.d("sendMessage-->:  " + message.toString());
    }

    @Override
    public void sendMessageWithCallback(Message message, IPCCallback callback) throws RemoteException {
        /**
         * 测试客户端给服务端发送消息
         */
        Logger.d("sendMessageWithCallback-->:  " + message.toString());


        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }


        Bundle result = new Bundle();
        result.putInt("Result", 20);
        callback.onSuccess(result);
    }

    @Override
    public void registMessageReceiveListener(MessagereceiveListener messagereceiveListener) throws RemoteException {
        if(messagereceiveListener != null) {
            WatchDogDispatcher.getInstance().getMessagereceiveListenerList().register(messagereceiveListener);
        }
    }

    @Override
    public void unRegistMessageReceiveListener(MessagereceiveListener messagereceiveListener) throws RemoteException {
        if(messagereceiveListener != null) {
            WatchDogDispatcher.getInstance().getMessagereceiveListenerList().unregister(messagereceiveListener);
        }else {
            WatchDogDispatcher.getInstance().getMessagereceiveListenerList().kill();
        }
    }
}
