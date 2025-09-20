package com.gof.ICNBack.Web;

import com.gof.ICNBack.Entity.User;
import com.gof.ICNBack.Entity.UserPayment;
import com.gof.ICNBack.Service.OrganisationService;
import com.gof.ICNBack.Service.UserService;
import com.gof.ICNBack.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<User.UserFull> UserLogin(
            @RequestParam(required = true) String email,
            @RequestParam(required = true) String password
    ) {
        User user = repo.loginUser(email, password);
        if (user == null){
            return ResponseEntity.status(409).header("invalid user account").build();
        }
        User.UserFull userF = user.getFullUser(orgRepo.getOrgCardsByIds(user.cards));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userF);
    }

    @PutMapping
    public ResponseEntity<Void> updateUserInformation(
            @RequestBody User user
    ) {
        if (repo.updateUser(user)) {
            return ResponseEntity.status(201).build();
        }
        return ResponseEntity.status(409).header("item update failed").build();
    }

    @GetMapping("/getCode")
    public ResponseEntity<User.UserFull> validateEmail(
            @RequestParam(required = true) String email
    ) {
        String code = this.email.generateValidationCode(email);
        return code == null ?
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .header("something wrong with the server")
                        .build()
                :ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .build();
    }

    @PostMapping("/create")
    public ResponseEntity<Void> addUserAccount(
            @RequestBody User.InitialUser initialUser
    ) {
        try {
            if (email.getValidationCode(initialUser.email).equals(initialUser.code)) {
                if (repo.createUser(initialUser)){
                    return ResponseEntity.status(HttpStatus.CREATED).build();
                }
                //TODO: handle errors in creating user, might related to database layer
            } else {
                return ResponseEntity.status(409).header("invalid validation code").build();
            }
        }catch(Exception e){
            //TODO: using logger to handle exception
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("something wrong with the server").build();
    }

    @PostMapping("/payment")
    public ResponseEntity<Void> userPayment(
            @RequestBody UserPayment userPayment
    ) {
        // TODO: create User payment, update account state
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}
