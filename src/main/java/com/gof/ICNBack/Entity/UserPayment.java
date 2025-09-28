package com.gof.ICNBack.Entity;

/**
 * TODO: complete required features
 * */

public class UserPayment {
    private String email;
    private String password;
    private double amount;
    private String validationCode;

    public UserPayment() {
    }

    public UserPayment(String email, String password, double amount, String validationCode) {
        this.email = email;
        this.password = password;
        this.amount = amount;
        this.validationCode = validationCode;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Double getAmount() {
        return amount;
    }

    public String getValidationCode() {
        return validationCode;
    }
}
