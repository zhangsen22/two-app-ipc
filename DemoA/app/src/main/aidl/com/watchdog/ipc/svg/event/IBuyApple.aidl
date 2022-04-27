// IBuyApple.aidl
package com.watchdog.ipc.svg.event;
import com.watchdog.ipc.svg.IPCCallback;

interface IBuyApple {
    int buyAppleInShop(int userId);
    void buyAppleOnNet(int userId,IPCCallback callback);
}
