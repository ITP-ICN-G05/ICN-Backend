package com.gof.ICNBack.DataSources.Entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.gof.ICNBack.Entity.User;

@Document(collection = "User")
public class UserEntity {
    @Id
    private String _id;
    private int VIP;
    private String email;
    private String name;
    private String password;
    private String phone;
    private String company;
    private String role;
    private String avatar;
    private List<String> cards;
    private Date endDate;
    private Date createdAt;

    public UserEntity() {
    }

    public UserEntity(String id, int vip, String email, String name, String password,
            String phone, String company, String role, String avatar,
            List<String> cards) {
        _id = id;
        VIP = vip;
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.company = company;
        this.role = role;
        this.avatar = avatar;
        this.cards = cards;
        this.createdAt = new Date();
    }

    public User toDomain() {
        return new User(
                getID(),
                getVIP(),
                getEmail(),
                getName(),
                getPassword(),
                getPhone(),
                getCompany(),
                getRole(),
                getAvatar(),
                getCards(),
                this.endDate == null ? null : getEndDate().toString(),
                this.createdAt == null ? null : getCreatedAt().toString());
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
