package com.example.baseadhesive.api.service;

import com.example.module_annotations.ServiceBelong;

@ServiceBelong(serviceName = "LoginService")
public class LoginServiceDefault implements ILoginService {
    @Override
    public void release() {

    }

    @Override
    public String getUserJson() {
        return "无用户";
    }

    @Override
    public String getId() {
        return "无id";
    }

    @Override
    public String getName() {
        return "无名字";
    }

    @Override
    public boolean isLogin() {
        return false;
    }
}
