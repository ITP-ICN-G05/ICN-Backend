package com.gof.ICNBack.Entity;

import com.gof.ICNBack.DataSources.Entity.UserEntity;

import java.util.List;

public class User {

    private String id;
    private int premium;
    private String email;
    private String subscribeDueDate;
    private String name;
    private String password;
    private List<String> organisationIds;

    public String getId() {
        return id;
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



    public User(){}

    public User(String id, int vip, String email, String name, String password, List<String> organisationIds, String subscribeDueDate) {
        this.id = id;
        premium = vip;
        this.email = email;
        this.name = name;
        this.password = password;
        this.organisationIds = organisationIds;
        this.subscribeDueDate = subscribeDueDate;
    }

    public int getPremium() {
        return premium;
    }

    public UserFull getFullUser(List<Organisation.OrganisationCard> cards){
        String endDate = this.getPremium() <= 0 ? "N/A" : subscribeDueDate;
        return new UserFull(id, cards, name, premium, endDate);
    }

    public InitialUser getInitialUser(String code){
        return new InitialUser(email, name, password, code);
    }

    public String getEmail() {
        return email;
    }

    public static class UserFull {
        public final String id;
        public final String name;
        public final List<Organisation.OrganisationCard> organisationCards;
        public final int premium;
        public final String subscribeDueDate;

        public UserFull(String id, List<Organisation.OrganisationCard> organisationCards, String name, int vip, String subscribeDueDate) {
            this.id = id;
            this.name = name;
            this.organisationCards = organisationCards;
            premium = vip;
            this.subscribeDueDate = subscribeDueDate;
        }
    }

    public static class InitialUser{
        private String email;
        private String name;
        private String password;

        private String code;

        public InitialUser(){}

        public InitialUser(String email, String name, String password, String code) {
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

    public UserEntity toEntity(){
        return new UserEntity(id, premium, email, name, password, organisationIds);
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

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setOrganisationIds(List<String> organisationIds) {
        this.organisationIds = organisationIds;
    }
}
