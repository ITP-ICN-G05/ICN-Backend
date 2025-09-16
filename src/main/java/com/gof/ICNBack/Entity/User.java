package com.gof.ICNBack.Entity;

public class User {

    private final int VIP;

    public User(int vip) {
        VIP = vip;
    }

    public int getVIP() {
        return VIP;
    }

    public UserFull getFullUser(){
        return new UserFull();
    }
    public static class UserFull {

    }

    public static class InitialUser{
        public final String email;
        public final String name;
        public final String password;

        public final String code;

        public InitialUser(String email, String name, String password, String code) {
            this.email = email;
            this.name = name;
            this.password = password;
            this.code = code;
        }
    }
}
