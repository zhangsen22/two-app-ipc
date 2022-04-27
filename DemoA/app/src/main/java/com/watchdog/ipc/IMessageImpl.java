package com.watchdog.ipc;

import android.os.RemoteException;

import com.watchdog.ipc.entry.Message;

public class IMessageImpl extends IMessageService.Stub{

    private static class SingletonInstance{
        private final static IMessageImpl S = new IMessageImpl();
    }
    // 3 返回对象
    public static IMessageImpl getInstance() {
        return IMessageImpl.SingletonInstance.S;
    }

    @Override
    public void sendMessage(Message message) throws RemoteException {

    }

    @Override
    public void sendMessageWithCallback(Message message, IPCCallback callback) throws RemoteException {

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
        }
    }
}
