package com.gof.ICNBack.DataSources.Entity;

import com.gof.ICNBack.Entity.User;
import org.springframework.data.annotation.Id;

import java.util.List;

public class UserEntity {
    @Id
    private String _id;

    private int VIP;

    public String email;
    public String name;
    public String password;
    public List<String> cards;

    public UserEntity(){}

    public UserEntity(String id, int vip, String email, String name, String password, List<String> cards) {
        _id = id;
        VIP = vip;
        this.email = email;
        this.name = name;
        this.password = password;
        this.cards = cards;
    }

    public User toDomain(){
        return new User(
                getID(),
                getVIP(),
                getEmail(),
                getName(),
                getPassword(),
                getCards()
        );
    }

    public String getID() {
        return _id;
    }

    public void setID(String ID) {
        this._id = ID;
    }

    public int getVIP() {
        return VIP;
    }

    public void setVIP(int VIP) {
        this.VIP = VIP;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getCards() {
        return cards;
    }

    public void setCards(List<String> cards) {
        this.cards = cards;
    }
}
