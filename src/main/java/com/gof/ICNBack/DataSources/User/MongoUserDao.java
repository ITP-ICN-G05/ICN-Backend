package com.gof.ICNBack.DataSources.User;


import com.gof.ICNBack.DataSources.Entity.UserEntity;
import com.gof.ICNBack.Entity.User;
import com.gof.ICNBack.Repositories.MongoUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("mongoUserDao")
public class MongoUserDao extends UserDao {
    @Autowired
    MongoUserRepository repo;

    @Override
    public User getUserById(String id) {
        return repo.findById(id).orElse(null).toDomain();
    }

    @Override
    public User getUserByPair(String email, String password) {
        return repo.findByEmailAndPassword(email, password).toDomain();
    }

    @Override
    public List<String> getOrgIdByUser(String email) {
        return repo.findCardsByEmail(email);
    }

    @Override
    public boolean update(UserEntity user) {
        if (repo.findByEmailAndPassword(user.email, user.password) != null) {
            repo.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean create(UserEntity user) {
        if (repo.findByEmailAndPassword(user.email, user.password) == null) {
            repo.save(user);
            return true;
        }
        return false;
    }
}
