package com.gof.ICNBack.Repositories;

import com.gof.ICNBack.DataSources.Entity.UserEntity;
import com.gof.ICNBack.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MongoUserRepository extends MongoRepository<UserEntity, String> {

    UserEntity findByEmailAndPassword(String email, String password);
    UserEntity findByEmail(String email);
}
