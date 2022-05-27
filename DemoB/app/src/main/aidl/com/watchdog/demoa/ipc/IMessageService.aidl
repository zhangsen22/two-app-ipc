// IMessageService.aidl
package com.watchdog.demoa.ipc;

import com.watchdog.demoa.ipc.entry.Message;

import com.watchdog.demoa.ipc.MessagereceiveListener;

import com.watchdog.demoa.ipc.IPCCallback;

//消息服务

interface IMessageService {

  void sendMessage(in Message message);

  void sendMessageWithCallback(in Message message,IPCCallback callback);

  void registMessageReceiveListener(MessagereceiveListener messagereceiveListener);

  void unRegistMessageReceiveListener(MessagereceiveListener messagereceiveListener);

}