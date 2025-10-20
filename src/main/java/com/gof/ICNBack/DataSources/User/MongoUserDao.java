package com.gof.ICNBack.DataSources.User;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.gof.ICNBack.DataSources.Entity.UserEntity;
import com.gof.ICNBack.Entity.User;
import com.gof.ICNBack.Repositories.MongoUserRepository;

@Repository("mongoUserDao")
public class MongoUserDao extends UserDao {
    private static final Logger logger = LoggerFactory.getLogger(MongoUserDao.class);

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
        UserEntity user = repo.findByEmailAndPassword(email, password).orElse(null);
        return user == null ? null : user.toDomain();
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            UserEntity user = repo.findByEmail(email).orElse(null);
            return user == null ? null : user.toDomain();
        } catch (IncorrectResultSizeDataAccessException e) {
            logger.warn("Found duplicate email records: {}, using fallback query method", email);
            // Use MongoTemplate's findOne as fallback solution
            Query query = new Query(Criteria.where("email").is(email));
            UserEntity user = mongoTemplate.findOne(query, UserEntity.class);
            return user == null ? null : user.toDomain();
        }
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
        try {
            // Find existing user by email (for profile updates, password is not required)
            UserEntity existingUser = repo.findByEmail(user.getEmail()).orElse(null);
            if (existingUser != null) {
                // Update only the provided fields, preserve existing password and other fields
                if (user.getName() != null && !user.getName().isEmpty()) {
                    existingUser.setName(user.getName());
                }
                if (user.getPhone() != null) {
                    existingUser.setPhone(user.getPhone());
                }
                if (user.getCompany() != null) {
                    existingUser.setCompany(user.getCompany());
                }
                if (user.getRole() != null) {
                    existingUser.setRole(user.getRole());
                }
                if (user.getAvatar() != null) {
                    existingUser.setAvatar(user.getAvatar());
                }
                if (user.getBookmarkedCompanies() != null) {
                    existingUser.setBookmarkedCompanies(user.getBookmarkedCompanies());
                }
                // Keep existing password unchanged for profile updates
                repo.save(existingUser);
                return true;
            }
            return false;
        } catch (IncorrectResultSizeDataAccessException e) {
            logger.error("Found duplicate email records when updating user info: {}, error: {}", user.getEmail(),
                    e.getMessage());
            // Use MongoTemplate's findOne as fallback solution
            Query query = new Query(Criteria.where("email").is(user.getEmail()));
            UserEntity existingUser = mongoTemplate.findOne(query, UserEntity.class);
            if (existingUser != null) {
                // Update only the provided fields, preserve existing password and other fields
                if (user.getName() != null && !user.getName().isEmpty()) {
                    existingUser.setName(user.getName());
                }
                if (user.getPhone() != null) {
                    existingUser.setPhone(user.getPhone());
                }
                if (user.getCompany() != null) {
                    existingUser.setCompany(user.getCompany());
                }
                if (user.getRole() != null) {
                    existingUser.setRole(user.getRole());
                }
                if (user.getAvatar() != null) {
                    existingUser.setAvatar(user.getAvatar());
                }
                if (user.getBookmarkedCompanies() != null) {
                    existingUser.setBookmarkedCompanies(user.getBookmarkedCompanies());
                }
                // Keep existing password unchanged for profile updates
                mongoTemplate.save(existingUser);
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean create(UserEntity user) {
        // Check if email already exists
        if (repo.findByEmail(user.getEmail()).isEmpty()) {
            repo.save(user);
            return true;
        }
        return false; // Email already exists, cannot register
    }
}
