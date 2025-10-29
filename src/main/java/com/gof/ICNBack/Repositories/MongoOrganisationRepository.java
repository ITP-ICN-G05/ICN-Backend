package com.gof.ICNBack.Repositories;

import com.gof.ICNBack.DataSources.Entity.ItemEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MongoOrganisationRepository extends MongoRepository<ItemEntity, String> {

    @Query("{'Organizations.Organisation: Organisation ID' : ?0 }")
    List<ItemEntity> findByOrganisationId(String organisationId);
}