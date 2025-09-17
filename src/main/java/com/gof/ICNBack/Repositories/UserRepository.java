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
        return userDao.getUserByPair(email, password);
    }

    public boolean updateUser(User user) {
        return userDao.update(user);
    }

    public boolean createUser(User.InitialUser initialUser) {
        User user = initialUser.completeWithEmptyValues();
        return userDao.create(user);
    }
}
