package com.gof.ICNBack.Repositories;

import com.gof.ICNBack.DataSources.Entity.UserEntity;
import com.gof.ICNBack.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoUserRepository extends MongoRepository<UserEntity, String> {
    UserEntity findByEmailAndPassword(String email, String password);
    UserEntity findByEmail(String email);
    List<String> findOrganisationsByEmail(String email);

}
