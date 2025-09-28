package com.gof.ICNBack.DataSources.User;


import com.gof.ICNBack.DataSources.Entity.UserEntity;
import com.gof.ICNBack.Entity.User;
import com.gof.ICNBack.Repositories.MongoUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("mongoUserDao")
public class MongoUserDao extends UserDao {
    @Autowired
    MongoUserRepository repo;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public User getUserById(String id) {
        UserEntity user = repo.findById(id).orElse(null);
        return user == null ? null : user.toDomain();
    }

    @Override
    public User getUserByPair(String email, String password) {
        return repo.findByEmailAndPassword(email, password).toDomain();
    }

    @Override
    public List<String> getOrgIdByUser(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        query.fields().include("cards").exclude("_id");
        UserEntity user = mongoTemplate.findOne(query, UserEntity.class);
        return user != null ? user.getCards() : null;
    }

    @Override
    public boolean update(UserEntity user) {
        if (repo.findByEmailAndPassword(user.getEmail(), user.getPassword()) != null) {
            repo.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean create(UserEntity user) {
        if (repo.findByEmailAndPassword(user.getEmail(), user.getPassword()) == null) {
            repo.save(user);
            return true;
        }
        return false;
    }
}
