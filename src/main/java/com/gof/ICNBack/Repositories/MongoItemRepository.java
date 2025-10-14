package com.gof.ICNBack.Repositories;

import com.gof.ICNBack.DataSources.Entity.ItemEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MongoItemRepository extends MongoRepository<ItemEntity, String> {

    @Query("{'Organisation IDs' : ?0 }")
    List<ItemEntity> findByOrganisationId(String organisationId);

    ItemEntity findByDetailedItemId(String detailedItemId);
}
