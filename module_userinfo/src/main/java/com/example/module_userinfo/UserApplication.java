package com.example.module_userinfo;


import android.content.Context;
import android.util.Log;


import com.example.baseadhesive.api.ServiceFactory;
import com.example.baseapp.BaseApplication;

import java.io.IOException;

public class UserApplication extends BaseApplication {
    private static final String TAG = "UserApplication";

    @Override
    public void onCreate(Context context) {
        Log.d(TAG, "onCreate: ");
        ActivityConfig.initActivtyConfig();
        try {
            ServiceFactory.getInstance().addService("UserInfoService",new UserInfoService());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
