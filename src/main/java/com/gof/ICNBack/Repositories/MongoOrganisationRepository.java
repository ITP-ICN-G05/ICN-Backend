package com.gof.ICNBack.Repositories;

import com.gof.ICNBack.DataSources.Entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoOrganisationRepository extends MongoRepository<UserEntity, String> {

}
