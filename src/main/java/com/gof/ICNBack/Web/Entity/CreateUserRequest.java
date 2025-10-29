package com.gof.ICNBack.Web.Entity;

import com.gof.ICNBack.Entity.User;

import java.util.List;

public class CreateUserRequest {
    private String email;
    private String name;
    private String password;
    private String code;

    public CreateUserRequest() {
    }

    public CreateUserRequest(String email, String name, String password, String code) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.code = code;
    }

    public User toUser(){
        return new User(
                null, 0, email, name, password, List.of(), null
        );
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return this.email;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
