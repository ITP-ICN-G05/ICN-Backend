package com.gof.ICNBack.Entity;

public class User {


    public UserFull getFullUser(){
        return new UserFull();
    }
    public static class UserFull {

    }

    public static class InitialUser{
        public String email;
        public String name;
        public String password;
    }
}
