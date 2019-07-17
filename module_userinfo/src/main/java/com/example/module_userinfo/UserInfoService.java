package com.example.module_userinfo;

import com.example.baseadhesive.api.service.IUserInfo;

public class UserInfoService implements IUserInfo {
    @Override
    public String get() {
        return "UserInfo模块的服务";
    }
}
