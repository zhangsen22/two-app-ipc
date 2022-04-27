package com.example.demob;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.demob.callback.BaseCallback;
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
    private Button btn_buy_apple;

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
        btn_buy_apple = findViewById(R.id.btn_buy_apple);

        bottonConnect.setOnClickListener(this);
        bottonDisConnect.setOnClickListener(this);
        bottonIsConnect.setOnClickListener(this);
        btnSendMessage.setOnClickListener(this);
        btnRegisterListener.setOnClickListener(this);
        btnUnRegisterListener.setOnClickListener(this);
        btn_buy_apple.setOnClickListener(this);
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

                break;
            case R.id.btn_disconnect:

                break;
            case R.id.btn_isconnect:

                break;
            case R.id.btn_send_message:
                Message message = new Message();
                message.setContent("message send from demoB");
                try {
                    IWatchDogManager.getInstance().getMessageServiceProxy().sendMessage(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_register_listener:
                try {
                    IWatchDogManager.getInstance().getMessageServiceProxy().registMessageReceiveListener(messagereceiveListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_unregister_listener:
                try {
                    IWatchDogManager.getInstance().getMessageServiceProxy().unRegistMessageReceiveListener(messagereceiveListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_buy_apple:
                try {
                    IWatchDogManager.getInstance().getBuyAppleProxy().buyAppleOnNet(10, new BaseCallback() {
                        @Override
                        public void onSucceed(Bundle result) {
                            int appleNum = result.getInt("Result", 0);
                            Toast.makeText(MainActivity.this,
                                    "got remote service with callback in other process(:banana),appleNum:" + appleNum, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailed(String reason) {
                            Toast.makeText(MainActivity.this, "got remote service failed with callback!", Toast.LENGTH_SHORT).show();
                        }
                    });
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