package com.gof.ICNBack.Web;

import com.gof.ICNBack.Entity.User;
import com.gof.ICNBack.Entity.UserPayment;
import com.gof.ICNBack.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository repo;

    @GetMapping
    public User.UserFull UserLogin(
            @RequestParam(required = true) String email,
            @RequestParam(required = true) String password
    ) {
        return repo.loginUser(email, password).getFullUser();
    }

    @PutMapping
    public ResponseEntity<Void> updateUserInformation(
            @RequestBody User user
    ) {
        // TODO:update UserInformation
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/create")
    public ResponseEntity<Void> addUserAccount(
            @RequestBody User.InitialUser initialUser
    ) {
        // TODO: create UserAccount
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/payment")
    public ResponseEntity<Void> userPayment(
            @RequestBody UserPayment userPayment
    ) {
        // TODO: create User payment, update account state
        return ResponseEntity.status(201).build();
    }
}
