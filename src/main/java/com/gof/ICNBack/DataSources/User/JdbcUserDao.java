package com.gof.ICNBack.DataSources.User;

import com.gof.ICNBack.DataSources.Entity.UserEntity;
import com.gof.ICNBack.Entity.User;

import java.util.List;

public class JdbcUserDao extends UserDao {
    @Override
    public User getUserById(String user) {
        return null;
    }

    @Override
    public User getUserByPair(String email, String password) {
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        return null;
    }

    @Override
    public List<String> getOrgIdByUser(String email) {
        return null;
    }

    @Override
    public boolean update(UserEntity user) {
        return false;
    }

    @Override
    public boolean create(UserEntity user) {
        return false;
    }
}
