package com.example.baseapp;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import java.util.ArrayList;
import java.util.List;


public  abstract class BaseApplication extends Application {
    private List<BaseApplication> applications=new ArrayList<>();

    public void init(Context application,String... s){
        for (String module: s){
            try {
                Class clazz = Class.forName(module);
                BaseApplication baseApp = (BaseApplication) clazz.newInstance();
                baseApp.onCreate(application);
                applications.add(baseApp);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }


    public abstract void onCreate(Context context);

    @Override
    public void onTerminate() {
        super.onTerminate();
        for (BaseApplication a:applications){
            a.onTerminate();
        }
        applications.clear();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        for (BaseApplication a:applications){
            a.onTrimMemory(level);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        for (BaseApplication a:applications){
            a.onLowMemory();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        for (BaseApplication a:applications){
            a.onConfigurationChanged(newConfig);
        }
    }
}
