package com.gof.ICNBack.DataSources.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@ConditionalOnProperty(name = "app.database.type", havingValue = "mongo")
public class MongoEmailCleanService {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final Logger logger = LoggerFactory.getLogger(MongoEmailCleanService.class);

    private static final long CODE_RETENTION_MINS = 10; // keep 10 min data

    @Scheduled(cron = "0 */10 * * * ?")
    public void scheduledCleanup() {
        logger.info("start email cleanup: " + LocalDateTime.now());
        try {
            int n = cleanupOldData();
            logger.info("finish clean database at {} cleaned {} records", LocalDateTime.now(), n);
        } catch (Exception e) {
            logger.error("validation code clean up failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int cleanupOldData() {
        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(CODE_RETENTION_MINS);
        Date cutoff = java.sql.Timestamp.valueOf(expiryTime);

        Query query = new Query();
        query.addCriteria(Criteria.where("createdDate").lt(cutoff));

        try {
            long countBefore = mongoTemplate.count(query, "Email");

            if (countBefore > 0) {
                mongoTemplate.remove(query, "Email");
                logger.debug("delete {} code (created before {})", countBefore, expiryTime);
                return (int) countBefore;
            }

        } catch (Exception e) {
            logger.error("error when clean validation code: " + e.getMessage());
            throw new RuntimeException("fail to clean validation codes", e);
        }

        return 0;
    }

    public int manualCodeCleanup() {
        return cleanupOldData();
    }
}
