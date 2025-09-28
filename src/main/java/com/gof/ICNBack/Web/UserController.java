package com.gof.ICNBack.Web;

import com.gof.ICNBack.Entity.User;
import com.gof.ICNBack.Entity.UserPayment;
import com.gof.ICNBack.Service.EmailService;
import com.gof.ICNBack.Service.OrganisationService;
import com.gof.ICNBack.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.gof.ICNBack.Web.Utils.Validator.*;


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
            @RequestParam(required = true) String password
    ) {
        if (!isValidEmail(email) || !isValidPassword(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "invalid input")
                    .build();
        }
        User user = repo.loginUser(email, password);
        if (user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header("X-Error", "invalid user account").build();
        }
        User.UserFull userF = user.getFullUser(orgRepo.getOrgCardsByIds(user.getCards()));
        return ResponseEntity.status(HttpStatus.OK).body(userF);
    }

    @PutMapping
    public ResponseEntity<Void> updateUserInformation(
            @RequestBody User user
    ) {
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
    public ResponseEntity<User.UserFull> validateEmail(
            @RequestParam(required = true) String email
    ) {
        if (!isValidEmail(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "invalid input")
                    .build();
        }

        String code = this.email.generateValidationCode(email);
        return code == null ?
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .header("X-Error", "something wrong with the server")
                        .build()
                :ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .build();
    }

    @PostMapping("/create")
    public ResponseEntity<Void> addUserAccount(
            @RequestBody User.InitialUser initialUser
    ) {
        if (!isValidInitialUser(initialUser, env.getProperty("app.service.email.codeLength", Integer.class, 4))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "invalid input")
                    .build();
        }

        try {
            if (email.getValidationCode(initialUser.getEmail()).equals(initialUser.getCode())) {
                if (repo.createUser(initialUser.toUser().toEntity())){
                    return ResponseEntity.status(HttpStatus.CREATED).build();
                }
                //TODO: handle errors in creating user, might related to database layer
            } else {
                return ResponseEntity.status(409).header("X-Error", "invalid validation code").build();
            }
        }catch(Exception e){
            //TODO: using logger to handle exception
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("X-Error", "something wrong with the server").build();
    }

    @PostMapping("/payment")
    public ResponseEntity<Void> userPayment(
            @RequestBody UserPayment userPayment
    ) {
        if (!isValidPayment(userPayment)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "invalid input")
                    .build();
        }
        // TODO: create User payment, update account state
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).header("X-Error", "Service developing").build();
    }
}
