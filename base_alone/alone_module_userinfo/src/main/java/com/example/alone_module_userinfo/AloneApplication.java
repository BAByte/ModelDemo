package com.example.alone_module_userinfo;

import android.content.Context;

import com.example.module_userinfo.UserApplication;

public class AloneApplication  extends UserApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        onCreate(getApplicationContext());
    }

    @Override
    public void onCreate(Context context) {
        super.onCreate(context);
    }
}
