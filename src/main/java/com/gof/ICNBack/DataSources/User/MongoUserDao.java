package com.gof.ICNBack.DataSources.User;


import com.gof.ICNBack.DataSources.Entity.UserEntity;
import com.gof.ICNBack.Entity.User;
import com.gof.ICNBack.Repositories.MongoUserRepository;
import com.gof.ICNBack.Service.GoogleMapsGeocodingService;
import com.gof.ICNBack.Utils.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("mongoUserDao")
public class MongoUserDao extends UserDao {
    @Autowired
    MongoUserRepository repo;

    private static final Logger logger = LoggerFactory.getLogger(GoogleMapsGeocodingService.class);

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public User getUserById(String id) {
        UserEntity user = repo.findById(id).orElse(null);
        return user == null ? null : user.toDomain();
    }

    @Override
    public User getUserByPair(String email, String password) {
        UserEntity user = repo.findByEmailAndPassword(email, password);
        logger.debug("Login event with email:{}, pass:{}", email, password);
        return user == null ? null: user.toDomain();
    }

    @Override
    public User getUserByEmail(String email) {
        UserEntity user = repo.findByEmail(email);
        return user == null ? null : user.toDomain();
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
        UserEntity user1 = repo.findById(user.getID()).orElse(null);
        if (user1 != null) {
            repo.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean create(UserEntity user) {
        if (repo.findByEmail(user.getEmail()) == null) {
            repo.save(user);
            return true;
        }
        return false;
    }
}
