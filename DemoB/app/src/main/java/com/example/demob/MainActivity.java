package com.example.demob;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.watchdog.demoa.ipc.IWatchDogManager;
import com.watchdog.demoa.ipc.callback.BaseCallback;
import com.watchdog.demoa.ipc.IMessageService;
import com.watchdog.demoa.ipc.MessagereceiveListener;
import com.watchdog.demoa.ipc.entry.Message;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private Button btnSendMessage;
    private Button btnRegisterListener;
    private Button btnUnRegisterListener;
    private Button btn_send_message_with_callback;
    private Button btn_java_crash;
    private Button btn_anr_crash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnSendMessage = findViewById(R.id.btn_send_message);
        btnRegisterListener = findViewById(R.id.btn_register_listener);
        btnUnRegisterListener = findViewById(R.id.btn_unregister_listener);
        btn_send_message_with_callback = findViewById(R.id.btn_send_message_with_callback);
        btn_java_crash = findViewById(R.id.btn_java_crash);
        btn_anr_crash = findViewById(R.id.btn_anr_crash);


        btnSendMessage.setOnClickListener(this);
        btnRegisterListener.setOnClickListener(this);
        btnUnRegisterListener.setOnClickListener(this);
        btn_send_message_with_callback.setOnClickListener(this);
        btn_java_crash.setOnClickListener(this);
        btn_anr_crash.setOnClickListener(this);


        TextView text = findViewById(R.id.text);
        text.setText("versionCode: "+getVersionCode(this,"com.example.demob")+"  versionName: "+getVersionName(this,"com.example.demob"));

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
            case R.id.btn_send_message:
                Message message = new Message();
                message.setContent("message send from demoB");
                try {
                    IMessageService remoteService = IWatchDogManager.getInstance().getRemoteService(IMessageService.class);
                    if(remoteService != null){
                        remoteService.sendMessage(message);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_register_listener:
                try {
                    IMessageService remoteService = IWatchDogManager.getInstance().getRemoteService(IMessageService.class);
                    remoteService.registMessageReceiveListener(messagereceiveListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_unregister_listener:
                try {
                    IMessageService remoteService = IWatchDogManager.getInstance().getRemoteService(IMessageService.class);
                    remoteService.unRegistMessageReceiveListener(messagereceiveListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_send_message_with_callback:
                try {
                    IMessageService remoteService = IWatchDogManager.getInstance().getRemoteService(IMessageService.class);
                    if(remoteService != null){
                        Message message1 = new Message();
                        message1.setContent("??????");
                        remoteService.sendMessageWithCallback(message1, new BaseCallback() {
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
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_java_crash:
                int i = 1 / 0;
                break;
            case R.id.btn_anr_crash:
                SystemClock.sleep(100000);
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

    /**
     * ??????????????????PackageInfo
     * @param ctx
     * @param packageName
     * @return
     */
    public PackageInfo getPackageInfo(Context ctx, String packageName){
        if(TextUtils.isEmpty(packageName)){
            return null;
        }
        PackageManager pm = ctx.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return packageInfo;
        }catch (Throwable ignore){

        }
        return null;
    }

    /**
     * ???????????????
     * @param ctx
     * @param packageName
     * @return
     */
    public int getVersionCode(Context ctx,String packageName){
        PackageInfo packageInfo = getPackageInfo(ctx, packageName);
        if(packageInfo != null){
            return packageInfo.versionCode;
        }
        return -1;
    }

    /**
     * ???????????????
     * @param ctx
     * @param packageName
     * @return
     */
    public String getVersionName(Context ctx,String packageName){
        PackageInfo packageInfo = getPackageInfo(ctx, packageName);
        if(packageInfo != null){
            return packageInfo.versionName;
        }
        return null;
    }

}