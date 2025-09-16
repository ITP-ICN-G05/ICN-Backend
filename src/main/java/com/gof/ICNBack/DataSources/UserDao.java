package com.gof.ICNBack.DataSources;

import com.gof.ICNBack.Entity.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


//TODO: build functions to query user table

@Component
@Transactional
public class UserDao {
    public User getUserById(String user) {
        return null;
    }
}
