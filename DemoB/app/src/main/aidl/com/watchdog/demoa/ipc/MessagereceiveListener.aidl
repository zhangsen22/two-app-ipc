// MessagereceiveListener.aidl
package com.watchdog.demoa.ipc;

import com.watchdog.demoa.ipc.entry.Message;

interface MessagereceiveListener {

    void onReceiveMessage(in Message message);

}