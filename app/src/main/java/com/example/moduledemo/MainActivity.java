package com.example.moduledemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.baseadhesive.api.ActivityDirectional;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ActivityDirectional.getInstance().toActivity(this,"LoginMainActivity");

//        ILoginService loginService= (ILoginService) ServiceFactory.getInstance()
//                .getService("com.example.module_login.LoginService");
//
//        Log.d(TAG, "onCreate: "+loginService.getUserJson());
    }
}

