package com.example.baseadhesive.api.service;

import com.example.module_annotations.ServiceBelong;

@ServiceBelong(serviceName = "com.example.module_login.LoginService")
public class LoginServiceDefault implements ILoginService {
    @Override
    public String getUserJson() {
        return "a";
    }

    @Override
    public String getId() {
        return "a";
    }

    @Override
    public String getName() {
        return "a";
    }

    @Override
    public boolean isLogin() {
        return false;
    }
}
