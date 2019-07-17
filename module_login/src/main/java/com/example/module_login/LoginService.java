package com.example.module_login;


import com.example.baseadhesive.api.service.ILoginService;



public class LoginService implements ILoginService {
    @Override
    public String getUserJson() {
        return UserModel.getInstance().getUser().toString();
    }

    @Override
    public String getId() {
        return UserModel.getInstance().getUser().getId();
    }

    @Override
    public String getName() {
        return UserModel.getInstance().getUser().getName();
    }

    @Override
    public boolean isLogin() {
        return UserModel.getInstance().getUser().isLogin();
    }
}
