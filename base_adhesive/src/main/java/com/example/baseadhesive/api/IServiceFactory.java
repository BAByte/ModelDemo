package com.example.baseadhesive.api;

//当组件与组件之间需要进行数据交互时，通过该工厂类获取服务接口，进而进行数据交互
public interface IServiceFactory {

    //注册服务
    public void addService(String id,Object o);

    //获取服务
    public Object
    getService(String id);
}
