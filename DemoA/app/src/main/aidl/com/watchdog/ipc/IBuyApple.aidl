// IBuyApple.aidl
package com.watchdog.ipc;
import com.watchdog.ipc.IPCCallback;

interface IBuyApple {
    int buyAppleInShop(int userId);
    void buyAppleOnNet(int userId,IPCCallback callback);
}
