package com.example.moduledemo;

import android.app.Application;
import android.util.Log;

import com.example.baseadhesive.api.AppConfig;
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
        for (String module: AppConfig.mountModelApplication){
            try {
                Class clazz = Class.forName(module);
                Log.d(TAG, "init: "+clazz);
                BaseApplication baseApp = (BaseApplication) clazz.newInstance();
                baseApp.init(this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }
}
