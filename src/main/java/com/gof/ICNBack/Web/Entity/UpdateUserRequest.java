package com.gof.ICNBack.Web.Entity;

import com.gof.ICNBack.Entity.User;

import java.util.List;

public class UpdateUserRequest {
    private String id;
    private int premium;
    private String email;
    private String subscribeDueDate;
    private String name;
    private String password;
    private List<String> organisationIds;

    public UpdateUserRequest() {
    }

    public UpdateUserRequest(String id, int premium, String email, String subscribeDueDate, String name, String password, List<String> organisationIds) {
        this.id = id;
        this.premium = premium;
        this.email = email;
        this.subscribeDueDate = subscribeDueDate;
        this.name = name;
        this.password = password;
        this.organisationIds = organisationIds;
    }

    public User toUser(){
        return new User(
                id,
                premium,
                email,
                name,
                password,
                organisationIds,
                subscribeDueDate
        );
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPremium(int premium) {
        this.premium = premium;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSubscribeDueDate(String subscribeDueDate) {
        this.subscribeDueDate = subscribeDueDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setOrganisationIds(List<String> organisationIds) {
        this.organisationIds = organisationIds;
    }

    public String getId() {
        return id;
    }

    public int getPremium() {
        return premium;
    }

    public String getEmail() {
        return email;
    }

    public String getSubscribeDueDate() {
        return subscribeDueDate;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getOrganisationIds() {
        return organisationIds;
    }
}
