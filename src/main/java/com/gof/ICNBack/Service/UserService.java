package com.gof.ICNBack.Service;

import com.gof.ICNBack.DataSources.Entity.UserEntity;
import com.gof.ICNBack.DataSources.User.UserDao;
import com.gof.ICNBack.Entity.User;
import com.gof.ICNBack.Web.Entity.UpdateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserService {
    @Autowired
    UserDao userDao;

    public User loginUser(String email, String password) {
        return userDao.getUserByPair(email, password);
    }

    public boolean updateUser(UpdateUserRequest request) {
        return userDao.update(request.toUser().toEntity());
    }

    public boolean createUser(UserEntity user) {
        return userDao.create(user);
    }
}
