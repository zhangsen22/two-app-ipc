// IMessageService.aidl
package com.watchdog.ipc;

import com.watchdog.ipc.entry.Message;

import com.watchdog.ipc.MessagereceiveListener;


//消息服务

interface IMessageService {

  void sendMessage(in Message message);

  void registMessageReceiveListener(MessagereceiveListener messagereceiveListener);

  void unRegistMessageReceiveListener(MessagereceiveListener messagereceiveListener);

}