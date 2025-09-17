package com.gof.ICNBack.DataSources;

import com.gof.ICNBack.Entity.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


//TODO: build functions to query user table

@Component
@Transactional
public class UserDao {
    public User getUserById(String user) {
        return null;
    }

    public User getUserByPair(String email, String password) {
        return null;
    }

    public List<String> getOrgIdByUser(String email) {
        return null;
    }

    public boolean update(User user) {
        return false;
    }

    public boolean create(User user) {
        return false;
    }
}
