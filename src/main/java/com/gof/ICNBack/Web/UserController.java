package com.gof.ICNBack.Web;

import com.gof.ICNBack.Entity.User;
import com.gof.ICNBack.Entity.UserPayment;
import com.gof.ICNBack.Repositories.UserRepository;
import com.gof.ICNBack.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository repo;

    @Autowired
    EmailService email;

    @GetMapping
    public ResponseEntity<User.UserFull> UserLogin(
            @RequestParam(required = true) String email,
            @RequestParam(required = true) String password
    ) {
        User.UserFull user = repo.loginUser(email, password).getFullUser();
        if (user == null){
            return ResponseEntity.status(409).header("invalid user account").build();
        }
        return ResponseEntity.status(201).body(user);
    }

    @PutMapping
    public ResponseEntity<Void> updateUserInformation(
            @RequestBody User user
    ) {
        if (repo.updateUser(user) == 1) {
            return ResponseEntity.status(201).build();
        }
        return ResponseEntity.status(409).header("item update failed").build();
    }

    @PostMapping("/create")
    public ResponseEntity<Void> addUserAccount(
            @RequestBody User.InitialUser initialUser
    ) {
        try {
            if (email.getValidationCode(initialUser.email).equals(initialUser.code)) {
                if (repo.createUser(initialUser)){
                    return ResponseEntity.status(201).build();
                }
                //TODO: handle errors in creating user, might related to database layer
            } else {
                return ResponseEntity.status(409).header("invalid validation code").build();
            }
        }catch(Exception e){
            //TODO: using logger to handle exception
        }
        return ResponseEntity.status(400).header("something wrong with the server").build();
    }

    @PostMapping("/payment")
    public ResponseEntity<Void> userPayment(
            @RequestBody UserPayment userPayment
    ) {
        // TODO: create User payment, update account state
        return ResponseEntity.status(201).build();
    }
}
