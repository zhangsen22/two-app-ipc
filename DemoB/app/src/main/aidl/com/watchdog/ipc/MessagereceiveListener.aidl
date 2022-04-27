// MessagereceiveListener.aidl
package com.watchdog.ipc;

import com.watchdog.ipc.entry.Message;

interface MessagereceiveListener {

    void onReceiveMessage(in Message message);

}