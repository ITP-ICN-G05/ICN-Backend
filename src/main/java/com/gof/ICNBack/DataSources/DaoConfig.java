package com.gof.ICNBack.DataSources;

import com.gof.ICNBack.DataSources.Organisation.JdbcOrgDao;
import com.gof.ICNBack.DataSources.Organisation.MongoOrgDao;
import com.gof.ICNBack.DataSources.Organisation.OrganisationDao;
import com.gof.ICNBack.DataSources.User.JdbcUserDao;
import com.gof.ICNBack.DataSources.User.MongoUserDao;
import com.gof.ICNBack.DataSources.User.UserDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class DaoConfig {

    @Bean
    @Primary
    public UserDao userDao(@Value("${app.database.type:mongo}") String databaseType) {

        switch (databaseType.toLowerCase()) {
            case "mongo":
                return new MongoUserDao();
            case "jdbc":
            default:
                return new JdbcUserDao();
        }
    }

    @Bean
    @Primary
    public OrganisationDao organisationDao(@Value("${app.database.type:mongo}") String databaseType) {

        switch (databaseType.toLowerCase()) {
            case "mongo":
                return new MongoOrgDao();
            case "jdbc":
            default:
                return new JdbcOrgDao();
        }
    }
}
