package com.watchdog.ipc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.signwaymanager.SignwayManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private PackageInstalledReceiver packageInstalledReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        packageInstalledReceiver = new PackageInstalledReceiver();
        register();

        Button button = findViewById(R.id.btn_open_b);
        Button btn_install = findViewById(R.id.btn_install);
        Button btn_uninstall = findViewById(R.id.btn_uninstall);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LaunchAppManager.getInstance().checkPackInfo(getApplicationContext(),"com.example.demob")) {
                    LaunchAppManager.getInstance().openPackage(MainActivity.this,"com.example.demob");
                } else {
                    Toast.makeText(MainActivity.this, "没有安装" + "",Toast.LENGTH_LONG).show();
                    //TODO  下载操作
                }
            }
        });


        btn_install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"开始静默安装",Toast.LENGTH_LONG).show();
                SignwayManager.getInstance(getApplication()).silentInstallApk("/mnt/sdcard/test.apk");
            }
        });

        btn_uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"开始静默卸载",Toast.LENGTH_LONG).show();
                SignwayManager.getInstance(getApplication()).silentUninstallApk1("com.example.demob");
            }
        });

//        LaunchAppManager.getInstance().
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(packageInstalledReceiver);
    }

    private void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");
        registerReceiver(packageInstalledReceiver, intentFilter);

    }
}