package com.gof.ICNBack.Web.Utils;

import com.gof.ICNBack.Entity.User;
import com.gof.ICNBack.Entity.UserPayment;
import com.gof.ICNBack.Web.Entity.CreateUserRequest;
import com.gof.ICNBack.Web.Entity.UpdateUserRequest;

import java.util.regex.Pattern;

public class Validator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{64}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s]{1,50}$");

    public static boolean isValidEmail(String email) {
        return !(email == null || !EMAIL_PATTERN.matcher(email).matches() || email.length() > 100);
    }

    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isValidUserId(String uid){
        return uid != null && NAME_PATTERN.matcher(uid).matches();
    }

    public static boolean isValidUserData(UpdateUserRequest user) {
        if (user == null) return false;
        if (!isValidUserId(user.getId())) return false;
        if (user.getName() == null || !NAME_PATTERN.matcher(user.getName()).matches()) return false;
        if (!isValidPassword(user.getPassword())) return false;
        return isValidEmail(user.getEmail());
    }

    public static boolean isValidInitialUser(CreateUserRequest createUserRequest, int codeLen) {
        if (createUserRequest == null) return false;
        if (createUserRequest.getName() == null || !NAME_PATTERN.matcher(createUserRequest.getName()).matches()) return false;
        if (!isValidEmail(createUserRequest.getEmail())) return false;
        if (!isValidPassword(createUserRequest.toUser().getPassword())) return false;

        return createUserRequest.getCode() != null && createUserRequest.getCode().length() == codeLen;
    }

    public static boolean isValidPayment(UserPayment payment) {
        if (payment == null) return false;
        if (!isValidEmail(payment.getEmail())) return false;
        if (!isValidPassword(payment.getPassword())) return false;
        // TODO: add payment validation
        return true;
    }

    public static String sanitizeInput(String input) {
        if (input == null) return null;
        return input.replaceAll("[<>\"']", "").trim();
    }
}
