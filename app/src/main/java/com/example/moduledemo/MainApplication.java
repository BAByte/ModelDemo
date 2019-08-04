package com.example.moduledemo;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.example.baseapp.BaseApplication;

public class MainApplication extends BaseApplication {
    //声明已有模块
    private static final  String MODEL_LOGIN ="com.example.module_login.LoginApplication";
    private static final  String MODEL_USERINFO ="com.example.module_userinfo.UserApplication";

    private static final String TAG = "MainApplication";

    @Override
    public void onCreate(Context context) {
        init(context,MODEL_LOGIN,MODEL_USERINFO);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        onCreate(getApplicationContext());
    }
}
