package com.gof.ICNBack.Service;

import com.gof.ICNBack.Entity.Organisation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(LocationUpdateService.class);

    @Value("${app.google-map-geocode.batch-size:10}")
    private int batchSize;

    @Value("${app.google-map-geocode.delay-between-requests:100}")
    private long delayBetweenRequests;

    private final MongoTemplate mongoTemplate;
    private final GoogleMapsGeocodingService geocodingService;

    @Autowired
    public LocationUpdateService(MongoTemplate mongoTemplate,
                                 GoogleMapsGeocodingService geocodingService) {
        this.mongoTemplate = mongoTemplate;
        this.geocodingService = geocodingService;
    }

    /**
     * update geocode for every company who haven't got one
     */
    public void updateLocationsWithGeocoding() {
        logger.info("Starting geocoding process...");

        int processedCount = 0;
        int successCount = 0;

        // 查找所有未地理编码的文档
        Query query = new Query(Criteria.where("Geocoded").ne(true));
        List<Organisation> organisations = mongoTemplate.find(query, Organisation.class);

        logger.info("Found {} locations to geocode", organisations.size());

        for (Organisation org : organisations) {
            try {
                processedCount++;

                // 调用Google Maps API获取经纬度
                GoogleMapsGeocodingService.GeocodingResult result =
                        geocodingService.geocodeAddress(org.getAddress())
                                .orElse(null);

                if (result != null) {
                    // 更新文档
                    Update update = new Update();
                    update.set("Longitude", result.getLongitude());
                    update.set("Latitude", result.getLatitude());
                    update.set("Geocoded", true);

                    Query updateQuery = new Query(Criteria.where("_id").is(org.get_id()));
                    mongoTemplate.updateFirst(updateQuery, update, Organisation.class);

                    successCount++;
                    logger.info("Successfully updated location: {}", org.getAddress());
                } else {
                    logger.warn("Failed to geocode address: {}", org.getAddress());
                }

                if (processedCount < organisations.size()) {
                    Thread.sleep(delayBetweenRequests);
                }

                if (processedCount % batchSize == 0) {
                    logger.info("Processed {}/{} locations", processedCount, organisations.size());
                }

            } catch (Exception e) {
                logger.error("Error processing location: {}", org.getAddress(), e);
            }
        }

        logger.info("Geocoding completed. Processed: {}, Success: {}, Failed: {}",
                processedCount, successCount, processedCount - successCount);
    }
}
