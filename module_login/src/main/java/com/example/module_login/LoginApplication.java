package com.example.module_login;

import android.app.Application;
import android.util.Log;

import com.example.baseapp.BaseApplication;
import com.example.baseadhesive.api.ServiceFactory;

import java.io.IOException;


public class LoginApplication extends BaseApplication {
    private static final String TAG = "LoginApplication";
    
    @Override
    public void onCreate(Application application) {
        Log.d(TAG, "init: 爱爱啊");
        try {
            ServiceFactory.getInstance().addService("LoginService",new LoginService());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ActivityConfig.initActivtyConfig();
    }

}
