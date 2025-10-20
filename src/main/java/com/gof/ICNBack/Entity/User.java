package com.gof.ICNBack.Entity;

import java.util.List;

import com.gof.ICNBack.DataSources.Entity.UserEntity;

public class User {

    private String id;
    private int VIP;
    private String email;
    private String name;
    private String password;
    private String phone;
    private String company;
    private String role;
    private String avatar;
    private List<String> cards;
    private List<String> bookmarkedCompanies;
    private String dueDate;
    private String createdAt;

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

    public User() {
    }

    public User(String id, int vip, String email, String name, String password, String phone, String company,
            String role, String avatar, List<String> cards, List<String> bookmarkedCompanies, String dueDate,
            String createdAt) {
        this.id = id;
        VIP = vip;
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.company = company;
        this.role = role;
        this.avatar = avatar;
        this.cards = cards;
        this.bookmarkedCompanies = bookmarkedCompanies;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
    }

    public int getVIP() {
        return VIP;
    }

    public UserFull getFullUser(List<Organisation.OrganisationCard> cards) {
        String endDate = this.getVIP() <= 0 ? "N/A" : dueDate;
        return new UserFull(id, cards, name, email, phone, company, role, avatar, VIP, endDate, createdAt,
                bookmarkedCompanies);
    }

    public InitialUser getInitialUser(String code) {
        return new InitialUser(email, name, password, phone, code);
    }

    public String getEmail() {
        return email;
    }

    public static class UserFull {
        public final String id;
        public final String name;
        public final String email;
        public final String phone;
        public final String company;
        public final String role;
        public final String avatar;
        public final List<Organisation.OrganisationCard> cards;
        public final int VIP;
        public final String endDate;
        public final String createdAt;
        public final String token;
        public final String refreshToken;
        public final List<String> bookmarkedCompanies;

        public UserFull(String id, List<Organisation.OrganisationCard> cards, String name, String email, String phone,
                String company, String role, String avatar, int vip, String endDate, String createdAt,
                List<String> bookmarkedCompanies) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.company = company;
            this.role = role;
            this.avatar = avatar;
            this.cards = cards;
            VIP = vip;
            this.endDate = endDate;
            this.createdAt = createdAt;
            this.token = null;
            this.refreshToken = null;
            this.bookmarkedCompanies = bookmarkedCompanies;
        }

        public UserFull(String id, List<Organisation.OrganisationCard> cards, String name, String email, String phone,
                String company, String role, String avatar, int vip, String endDate, String createdAt,
                String token, String refreshToken) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.company = company;
            this.role = role;
            this.avatar = avatar;
            this.cards = cards;
            VIP = vip;
            this.endDate = endDate;
            this.createdAt = createdAt;
            this.token = token;
            this.refreshToken = refreshToken;
            this.bookmarkedCompanies = null;
        }
    }

    public static class InitialUser {
        private String email;
        private String name;
        private String password;
        private String phone;
        private String code;

        public InitialUser() {
        }

        public InitialUser(String email, String name, String password, String phone, String code) {
            this.email = email;
            this.name = name;
            this.password = password;
            this.phone = phone;
            this.code = code;
        }

        public User toUser() {
            return new User(
                    null, 0, email, name, password, phone, "", "User", null, List.of(), List.of(), null, null);
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

        public String getPhone() {
            return phone;
        }
    }

    public UserEntity toEntity() {
        return new UserEntity(id, VIP, email, name, password, phone, company, role, avatar, cards, bookmarkedCompanies);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVIP(int VIP) {
        this.VIP = VIP;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getBookmarkedCompanies() {
        return bookmarkedCompanies;
    }

    public void setBookmarkedCompanies(List<String> bookmarkedCompanies) {
        this.bookmarkedCompanies = bookmarkedCompanies;
    }
}
