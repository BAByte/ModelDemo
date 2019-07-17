package com.example.module_login.module;

//用户信息
public class User {
    private String id;
    private String name;
    private boolean isLogin;


    public User(){}
    public User(String id, String name, boolean isLogin) {
        this.id = id;
        this.name = name;
        this.isLogin = isLogin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", isLogin=" + isLogin +
                '}';
    }
}
