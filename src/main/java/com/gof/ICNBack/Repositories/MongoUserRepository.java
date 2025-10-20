package com.gof.ICNBack.Repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.gof.ICNBack.DataSources.Entity.UserEntity;

public interface MongoUserRepository extends MongoRepository<UserEntity, String> {

    Optional<UserEntity> findByEmailAndPassword(String email, String password);

    Optional<UserEntity> findByEmail(String email);

    UserEntity findFirstByEmail(String email);
}
