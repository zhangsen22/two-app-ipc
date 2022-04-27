package com.watchdog.ipc.services;

import android.os.Bundle;
import android.os.RemoteException;

import com.watchdog.ipc.IBuyApple;
import com.watchdog.ipc.IPCCallback;
import com.watchdog.ipc.Logger;

public class BuyAppleImpl extends IBuyApple.Stub {

    //private
    private BuyAppleImpl() {

    }

    private static class SingletonInstance{
        private final static BuyAppleImpl S = new BuyAppleImpl();
    }
    // 3 返回对象
    public static BuyAppleImpl getInstance() {
        return BuyAppleImpl.SingletonInstance.S;
    }

    @Override
    public int buyAppleInShop(int userId) throws RemoteException {
        Logger.d("BuyAppleImpl-->buyAppleInShop,userId:" + userId);
        if (userId == 10) {
            return 20;
        } else if (userId == 20) {
            return 30;
        } else {
            return -1;
        }
    }

    @Override
    public void buyAppleOnNet(int userId, IPCCallback callback) throws RemoteException {
        Logger.d("BuyAppleImpl-->buyAppleOnNet,userId:" + userId);


        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }


        Bundle result = new Bundle();
        if (userId == 10) {
            result.putInt("Result", 20);
            callback.onSuccess(result);
        } else if (userId == 20) {
            result.putInt("Result", 30);
            callback.onSuccess(result);
        } else {
            callback.onFail("Sorry, u are not authorized!");
        }
    }

}
