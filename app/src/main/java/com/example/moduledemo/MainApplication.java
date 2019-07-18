package com.example.moduledemo;

import android.app.Application;
import android.content.res.Configuration;

import com.example.baseapp.BaseApplication;

public class MainApplication extends BaseApplication {
    //声明已有模块
    private static final  String MODEL_LOGIN ="com.example.module_login.LoginApplication";
    private static final  String MODEL_USERINFO ="com.example.module_userinfo.UserApplication";


    private static final String TAG = "MainApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        //这个方法为主模块调用，其他模块不需要调用，主要是为了选择要挂载哪些模块，以及传入Application
        init(this,MODEL_LOGIN);
    }
}
