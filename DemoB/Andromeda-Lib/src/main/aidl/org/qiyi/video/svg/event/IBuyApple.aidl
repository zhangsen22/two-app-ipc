// IBuyApple.aidl
package org.qiyi.video.svg.event;
import org.qiyi.video.svg.IPCCallback;

interface IBuyApple {
    int buyAppleInShop(int userId);
    void buyAppleOnNet(int userId,IPCCallback callback);
}
