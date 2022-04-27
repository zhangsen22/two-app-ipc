// IBuyCherry.aidl
package org.qiyi.video.svg.event;
import org.qiyi.video.svg.IPCCallback;
// Declare any non-default types here with import statements

interface IBuyCherry {

    int buyCherryInShop(int userId);
    void buyCherryOnNet(int userId,IPCCallback callback);

}
