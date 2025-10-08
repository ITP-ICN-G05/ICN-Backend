package com.gof.ICNBack.Repositories;

import com.gof.ICNBack.DataSources.Entity.EmailRecordEntity;
import com.gof.ICNBack.DataSources.Entity.ItemEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MongoEmailRepository extends MongoRepository<EmailRecordEntity, String> {
    List<EmailRecordEntity> findByEmail(String email);
}
