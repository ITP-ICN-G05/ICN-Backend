package com.gof.ICNBack.Web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gof.ICNBack.Entity.User;
import com.gof.ICNBack.Entity.UserPayment;
import com.gof.ICNBack.Service.EmailService;
import com.gof.ICNBack.Service.OrganisationService;
import com.gof.ICNBack.Service.UserService;
import static com.gof.ICNBack.Web.Utils.Validator.isValidEmail;
import static com.gof.ICNBack.Web.Utils.Validator.isValidInitialUser;
import static com.gof.ICNBack.Web.Utils.Validator.isValidPassword;
import static com.gof.ICNBack.Web.Utils.Validator.isValidPayment;
import static com.gof.ICNBack.Web.Utils.Validator.isValidUserData;

/// @TODO: error handling
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService repo;

    @Autowired
    EmailService email;

    @Autowired
    OrganisationService orgRepo;

    @Autowired
    Environment env;

    @GetMapping
    public ResponseEntity<User.UserFull> UserLogin(
            @RequestParam(required = true) String email,
            @RequestParam(required = true) String password) {
        if (!isValidEmail(email) || !isValidPassword(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "invalid input")
                    .build();
        }
        User user = repo.loginUser(email, password);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header("X-Error", "invalid user account").build();
        }
        User.UserFull userF = user.getFullUser(orgRepo.getOrgCardsByIds(user.getCards()));
        return ResponseEntity.status(HttpStatus.OK).body(userF);
    }

    @PutMapping
    public ResponseEntity<Void> updateUserInformation(
            @RequestBody User user) {
        if (!isValidUserData(user)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "invalid input")
                    .build();
        }

        if (repo.updateUser(user)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header("X-Error", "item update failed").build();
    }

    @GetMapping("/getCode")
    public ResponseEntity<Void> validateEmail(
            @RequestParam(required = true) String email) {
        if (!isValidEmail(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "invalid input")
                    .build();
        }

        String code = this.email.generateValidationCode(email);
        Boolean result = false;
        if (code != null) {
            result = this.email.sendCode(code, email);
        }
        return result ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .header("X-Error", "something wrong with the server")
                        .build();
    }

    @PostMapping("/create")
    public ResponseEntity<Void> addUserAccount(
            @RequestBody User.InitialUser initialUser) {
        if (!isValidInitialUser(initialUser, env.getProperty("app.mail.code.length", Integer.class, 4))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "invalid input")
                    .build();
        }

        try {
            if (email.getValidationCode(initialUser.getEmail()).contains(initialUser.getCode())) {
                if (repo.createUser(initialUser.toUser().toEntity())) {
                    return ResponseEntity.status(HttpStatus.CREATED).build();
                }
                // TODO: handle errors in creating user, might related to database layer
            } else {
                return ResponseEntity.status(409).header("X-Error", "invalid validation code").build();
            }
        } catch (Exception e) {
            // TODO: using logger to handle exception
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("X-Error", "something wrong with the server").build();
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Void> resetPassword(
            @RequestParam(required = true) String email,
            @RequestParam(required = true) String code,
            @RequestParam(required = true) String newPassword) {
        if (!isValidEmail(email) || !isValidPassword(newPassword) || code == null || code.length() != 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "invalid input")
                    .build();
        }

        try {
            // Verify the validation code
            if (!this.email.getValidationCode(email).contains(code)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .header("X-Error", "invalid validation code")
                        .build();
            }

            // Find user by email and update password
            User user = repo.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .header("X-Error", "user not found")
                        .build();
            }

            user.setPassword(newPassword);
            if (repo.updateUser(user)) {
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .header("X-Error", "failed to update password")
                        .build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error", "something wrong with the server")
                    .build();
        }
    }

    @PostMapping("/payment")
    public ResponseEntity<Void> userPayment(
            @RequestBody UserPayment userPayment) {
        if (!isValidPayment(userPayment)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "invalid input")
                    .build();
        }
        // TODO: create User payment, update account state
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).header("X-Error", "Service developing").build();
    }
}
