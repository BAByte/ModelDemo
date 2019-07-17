package com.example.module_annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//保留策略（SOURCE,CLASS,RUNTIME）
@Retention(RetentionPolicy.CLASS)
//作用域
@Target(ElementType.TYPE)
public @interface ServiceBelong {
    //当前默认服务属于哪个服务
    String serviceName();
}
