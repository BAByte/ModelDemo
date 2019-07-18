package com.example.module_userinfo;

import android.app.Application;


import com.example.baseadhesive.api.ServiceFactory;
import com.example.baseapp.BaseApplication;

import java.io.IOException;

public class UserApplication extends BaseApplication {
    private static final String TAG = "UserApplication";
    @Override
    public void init(Application application)  {
        ActivityConfig.initActivtyConfig();
        try {
            ServiceFactory.getInstance().addService("UserInfoService",new UserInfoService());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
