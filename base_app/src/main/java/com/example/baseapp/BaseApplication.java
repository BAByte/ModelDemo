package com.example.baseapp;

import android.app.Application;

import com.example.baseadhesive.api.AppConfig;




public abstract class BaseApplication extends Application {

    public void mount(Application application){
        for (String module: AppConfig.mountModelApplication){
            try {
                Class clazz = Class.forName(module);
                BaseApplication baseApp = (BaseApplication) clazz.newInstance();
                baseApp.init(application);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }


    public abstract void init(Application application);
}
