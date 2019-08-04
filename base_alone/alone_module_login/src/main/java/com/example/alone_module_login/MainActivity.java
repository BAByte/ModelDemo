package com.example.alone_module_login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.module_login.LoginMainActivity;

//用来跳转登录模块的界面
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //直接跳转到指定界面进行调试
        Intent intent=new Intent(this,LoginMainActivity.class);
        startActivity(intent);
    }
}
