package com.example.baseadhesive.api.service;


import com.example.module_annotations.ServiceBelong;

@ServiceBelong(serviceName = "com.example.module_userinfo.UserInfoService")
public class UserInfoServieDefault implements IUserInfo {
    @Override
    public String get() {
        return "userInfo的默认服务";
    }
}
