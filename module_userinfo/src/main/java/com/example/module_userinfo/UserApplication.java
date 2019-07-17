package com.example.module_userinfo;

import android.app.Application;
import android.util.Log;

import com.example.baseadhesive.api.ActivityDirectional;
import com.example.baseadhesive.api.ServiceFactory;
import com.example.baseapp.BaseApplication;

public class UserApplication extends BaseApplication {
    private static final String TAG = "UserApplication";
    @Override
    public void init(Application application) {
        ActivityDirectional.getInstance().addMap("com.example.module_userinfo.ActivityConfig");
        ServiceFactory.getInstance().addService("com.example.module_userinfo.UserInfoService",new UserInfoService());
        Log.d(TAG, "init: 用户信息模块");
    }
}
