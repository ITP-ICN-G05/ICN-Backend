package com.gof.ICNBack.Entity;

import com.gof.ICNBack.DataSources.Entity.UserEntity;
import org.springframework.data.annotation.Id;

import java.util.List;

public class User {

    private String id;
    private int VIP;
    private String email;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getCards() {
        return cards;
    }

    private String name;
    private String password;
    private List<String> cards;

    public User(){}

    public User(String id, int vip, String email, String name, String password, List<String> cards) {
        this.id = id;
        VIP = vip;
        this.email = email;
        this.name = name;
        this.password = password;
        this.cards = cards;
    }

    public int getVIP() {
        return VIP;
    }

    public UserFull getFullUser(List<Organisation.OrganisationCard> cards){
        return new UserFull(cards, name, VIP);
    }

    public InitialUser getInitialUser(String code){
        return new InitialUser(email, name, password, code);
    }

    public String getEmail() {
        return email;
    }

    public static class UserFull {
        public final String name;
        public final List<Organisation.OrganisationCard> cards;

        public final int VIP;

        public UserFull(List<Organisation.OrganisationCard> cards, String name, int vip) {
            this.name = name;
            this.cards = cards;
            VIP = vip;
        }
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

        public User toUser(){
            return new User(
                    null, 0, email, name, password, List.of()
            );
        }


    }

    public UserEntity toEntity(){
        return new UserEntity(id, VIP, email, name, password, cards);
    }
}
