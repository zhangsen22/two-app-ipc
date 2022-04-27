package com.example.demob;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.watchdog.ipc.IConnectionService;
import com.watchdog.ipc.IMessageService;
import com.watchdog.ipc.IServiceManager;
import com.watchdog.ipc.MessagereceiveListener;
import com.watchdog.ipc.entry.Message;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private Button bottonConnect;
    private Button bottonDisConnect;
    private Button bottonIsConnect;
    private Button btnSendMessage;
    private Button btnRegisterListener;
    private Button btnUnRegisterListener;

    private IConnectionService connectionServiceProxy;
    private IMessageService messageServiceProxy;
    private IServiceManager serviceManagerProxy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottonConnect = findViewById(R.id.btn_connect);
        bottonDisConnect = findViewById(R.id.btn_disconnect);
        bottonIsConnect = findViewById(R.id.btn_isconnect);
        btnSendMessage = findViewById(R.id.btn_send_message);
        btnRegisterListener = findViewById(R.id.btn_register_listener);
        btnUnRegisterListener = findViewById(R.id.btn_unregister_listener);

        bottonConnect.setOnClickListener(this);
        bottonDisConnect.setOnClickListener(this);
        bottonIsConnect.setOnClickListener(this);
        btnSendMessage.setOnClickListener(this);
        btnRegisterListener.setOnClickListener(this);
        btnUnRegisterListener.setOnClickListener(this);

        bindService();
    }

    private void bindService() {
        Intent mIntent = new Intent();
        mIntent.setAction("com.watchdog.ipc.WatchDogService");
        mIntent.setPackage("com.watchdog.ipc");
//        mIntent.setComponent(new ComponentName("com.watchdog.ipc", "com.watchdog.ipc.WatchDogService"));
        getApplicationContext().bindService(mIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                serviceManagerProxy = IServiceManager.Stub.asInterface(service);
                try {
                    connectionServiceProxy = IConnectionService.Stub.asInterface(serviceManagerProxy.getService(IConnectionService.class.getSimpleName()));
                    messageServiceProxy = IMessageService.Stub.asInterface(serviceManagerProxy.getService(IMessageService.class.getSimpleName()));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_connect:
                try {
                    connectionServiceProxy.connection();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_disconnect:
                try {
                    connectionServiceProxy.disconnection();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_isconnect:
                try {
                    boolean connection = connectionServiceProxy.isConnection();
                    Toast.makeText(this,String.valueOf(connection),Toast.LENGTH_SHORT).show();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_send_message:
                Message message = new Message();
                message.setContent("message send from demoB");
                try {
                    messageServiceProxy.sendMessage(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_register_listener:
                try {
                    messageServiceProxy.registMessageReceiveListener(messagereceiveListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_unregister_listener:
                try {
                    messageServiceProxy.unRegistMessageReceiveListener(messagereceiveListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private MessagereceiveListener messagereceiveListener = new MessagereceiveListener.Stub() {
        @Override
        public void onReceiveMessage(Message message) throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,message.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

}