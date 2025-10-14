package com.gof.ICNBack.Repositories;

import com.gof.ICNBack.DataSources.Entity.ItemEntity;
import com.gof.ICNBack.DataSources.Entity.OrganisationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MongoOrganisationRepository extends MongoRepository<OrganisationEntity, String> {

    @Query("{'Organizations.Organisation: Organisation ID' : ?0 }")
    List<OrganisationEntity> findByOrganisationId(String organisationId);
}
