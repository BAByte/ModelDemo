package com.example.module_login;


import com.example.module_login.module.User;

//只是用来处理User单例，不要管写的怎么样
public class UserModel {
    private UserModel(){}

    public static UserModelImp getInstance(){
        return UserModelImp.userHandlerImp;
    }

    static class UserModelImp{
        static UserModelImp userHandlerImp=new UserModelImp();
        private User user;
        private UserModelImp(){
            //这里不处理加载数据的了，懒得写，直接new
            user=new User();
        }

        //懒得写get和set方法，这里为了方便直接给User类
        public User getUser(){
            return user;
        }

        public void  release(){
            user=new User();
        }
    }
}
