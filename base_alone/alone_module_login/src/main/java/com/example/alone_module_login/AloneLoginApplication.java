package com.example.alone_module_login;
import android.content.Context;

import com.example.module_login.LoginApplication;

public class AloneLoginApplication extends LoginApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        //在单独编译时，可以先初始化全局编译时需要的服务

        //初始化登录模块的全局服务
        onCreate(getApplicationContext());
    }

    @Override
    public void onCreate(Context context) {
        super.onCreate(context);
    }
}
