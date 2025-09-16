package com.gof.ICNBack.Repositories;

import com.gof.ICNBack.DataSources.UserDao;
import com.gof.ICNBack.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class UserRepository {
    @Autowired
    UserDao userDao;

    public User loginUser(String email, String password) {
        //TODO: finish interactions with DAO
        return new User(0);
    }

    public int updateUser(User user) {
        return 0;
    }

    public boolean createUser(User.InitialUser initialUser) {
        return false;
    }
}
