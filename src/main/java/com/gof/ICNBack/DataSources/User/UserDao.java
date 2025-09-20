package com.gof.ICNBack.DataSources.User;

import com.gof.ICNBack.DataSources.Entity.UserEntity;
import com.gof.ICNBack.Entity.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


//TODO: build functions to query user table

public abstract class UserDao {
    public abstract User getUserById(String user);

    public abstract User getUserByPair(String email, String password);

    public abstract List<String> getOrgIdByUser(String email);

    public abstract boolean update(UserEntity user);

    public abstract boolean create(UserEntity user);
}
