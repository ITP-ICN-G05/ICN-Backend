package com.gof.ICNBack.DataSources;

import com.gof.ICNBack.DataSources.Email.EmailDao;
import com.gof.ICNBack.DataSources.Email.MongoEmailDao;
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
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class DataConfig {

    @Bean
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
    public OrganisationDao organisationDao(@Value("${app.database.type:mongo}") String databaseType) {

        switch (databaseType.toLowerCase()) {
            case "mongo":
                return new MongoOrgDao();
            case "jdbc":
            default:
                return new JdbcOrgDao();
        }
    }

    @Bean
    public EmailDao emailDao(@Value("${app.database.type:mongo}") String databaseType) {

        switch (databaseType.toLowerCase()) {
            case "mongo":
                return new MongoEmailDao();
            case "jdbc":
            default:
                return null;
        }
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDbFactory, MongoMappingContext context) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, context);

        // remove _class column
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return new MongoTemplate(mongoDbFactory, converter);
    }
}
