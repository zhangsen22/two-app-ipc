package com.watchdog.ipc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.btn_open_b);
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
    }
}