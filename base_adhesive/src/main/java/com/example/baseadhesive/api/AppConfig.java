package com.example.baseadhesive.api;



//当需要挂上哪个模块时就定义在数组里面
public class AppConfig {
    private static final String TAG = "AppConfig";


    private static final  String MODEL_LOGIN ="com.example.module_login.LoginApplication";

    //选择要挂载的模块
    public static String[] mountModelApplication={MODEL_LOGIN};

}
