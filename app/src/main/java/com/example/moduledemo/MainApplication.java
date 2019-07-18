package com.example.moduledemo;

import android.app.Application;

import com.example.baseapp.BaseApplication;

public class MainApplication extends BaseApplication {
    private static final String TAG = "MainApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        init(this);
    }

    //通过反射机制挂载模块
    @Override
    public void init(Application application) {
        //这个为主模块调用，其他模块不需要调用
        mount(application);
    }
}
