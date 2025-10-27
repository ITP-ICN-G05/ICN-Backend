package com.gof.ICNBack.DataSources.Email;

import com.gof.ICNBack.DataSources.Entity.EmailRecordEntity;
import com.gof.ICNBack.Repositories.MongoEmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Repository("mongoEmailDao")
public class MongoEmailDao extends EmailDao{

    @Autowired
    MongoEmailRepository repo;

    @Override
    public void createRecipe(String email, String code) {
        repo.save(new EmailRecordEntity(
                null,
                code,
                email,
                new Date()
        ));
    }

    @Override
    public List<String> getCodeByEmail(String email, LocalDateTime latestTime) {
        Date date = Date.from(latestTime.atZone(ZoneId.systemDefault()).toInstant());
        return repo.findByEmailAndCreatedDateAfter(email, date).stream().map(EmailRecordEntity::getCode).toList();
    }
}
