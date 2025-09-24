package com.gof.ICNBack.Service;

import com.gof.ICNBack.DataSources.Organisation.OrganisationDao;
import com.gof.ICNBack.Entity.Organisation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

//TODO: refactor to meet data type
@Service
public class LocationUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(LocationUpdateService.class);

    @Value("${app.google-map-geocode.batch-size:10}")
    private int batchSize;

    @Value("${app.google-map-geocode.delay-between-requests:100}")
    private long delayBetweenRequests;

    private final GoogleMapsGeocodingService geocodingService;
    private final OrganisationDao organisationDao;

    @Autowired
    public LocationUpdateService(GoogleMapsGeocodingService geocodingService, OrganisationDao organisationDao) {
        this.geocodingService = geocodingService;
        this.organisationDao = organisationDao;
    }

    /**
     * update geocode for every company who haven't got one
     */
    public void updateLocationsWithGeocoding() {
        logger.info("Starting geocoding process...");

        int processedCount = 0;
        int successCount = 0;

        List<Organisation> organisations = organisationDao.getOrganisationsWithoutGeocode();

        logger.info("Found {} locations to geocode", organisations.size());

        for (Organisation org : organisations) {
            try {
                processedCount++;

                // get geocode
                GoogleMapsGeocodingService.GeocodingResult result =
                        geocodingService.geocodeAddress(org.getAddress())
                                .orElse(null);

                if (result != null) {
                    org.setCoord(new GeoJsonPoint(result.getLatitude(), result.getLongitude()));

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

        organisationDao.updateGeocode(organisations);

        logger.info("Geocoding completed. Processed: {}, Success: {}, Failed: {}",
                processedCount, successCount, processedCount - successCount);
    }
}
