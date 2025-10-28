package com.gof.ICNBack.Service;

import com.gof.ICNBack.DataSources.Email.EmailDao;
import com.gof.ICNBack.DataSources.Entity.UserEntity;
import com.gof.ICNBack.DataSources.User.UserDao;
import com.gof.ICNBack.Entity.User;
import com.gof.ICNBack.Web.Entity.UpdateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class UserService {
    @Autowired
    UserDao userDao;

    @Autowired
    EmailService service;

    public User loginUser(String email, String password) {
        return userDao.getUserByPair(email, password);
    }

    public boolean updateUser(UpdateUserRequest request) {
        User user = userDao.getUserById(request.getId());
        if (user == null) return false;
        fillInUpdatedInfo(request, user);
        return userDao.update(user.toEntity());
    }

    private void fillInUpdatedInfo(UpdateUserRequest request, User user){
        if (request.getCode() != null && service.getValidationCode(user.getEmail()).contains(request.getCode())){
            Optional.ofNullable(request.getEmail()).ifPresent(user::setEmail);
            Optional.ofNullable(request.getPassword()).ifPresent(user::setPassword);
            Optional.ofNullable(request.getName()).ifPresent(user::setName);
        }

        Optional.ofNullable(request.getOrganisationIds()).ifPresent(user::setOrganisationIds);
    }

    public boolean createUser(UserEntity user) {
        return userDao.create(user);
    }
}
