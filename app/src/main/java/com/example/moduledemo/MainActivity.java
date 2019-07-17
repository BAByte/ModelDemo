package com.example.moduledemo;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.baseadhesive.api.ActivityDirectional;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //假装这个是欢迎界面，等待两秒后跳到登录界面
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Class c = ActivityDirectional.getInstance().getClazz("LoginMainActivity");
                Intent intent = new Intent(MainActivity.this, c);
                startActivity(intent);
                finish();
            }
        },2000);

    }
}

