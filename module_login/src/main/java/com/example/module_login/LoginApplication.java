package com.example.module_login;

import android.app.Application;
import android.util.Log;

import com.example.baseadhesive.api.ActivityDirectional;
import com.example.baseapp.BaseApplication;
import com.example.baseadhesive.api.ServiceFactory;


public class LoginApplication extends BaseApplication {
    private static final String TAG = "LoginApplication";

    @Override
    public void init(Application application) {
        Log.d(TAG, "init: ");

        ServiceFactory.getInstance().addService("com.example.module_login.LoginService",new LoginService());
        ActivityDirectional.getInstance().addMap("com.example.module_login.ActivityConfig");
    }

}
