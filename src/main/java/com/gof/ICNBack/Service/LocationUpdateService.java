package com.gof.ICNBack.Service;

import com.gof.ICNBack.DataSources.Organisation.OrganisationDao;
import com.gof.ICNBack.Entity.Organisation;
import com.gof.ICNBack.Utils.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(LocationUpdateService.class);

    @Value("${app.google-map-geocode.batch-size:10}")
    private int batchSize = 10;

    @Value("${app.google-map-geocode.delay-between-requests:100}")
    private long delayBetweenRequests;

    private final GoogleMapsGeocodingService geocodingService;
    private final OrganisationDao organisationDao;

    @Autowired
    private Environment env;

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

        boolean debug = env.getProperty(Properties.GEOCODING_DEBUG, Boolean.class, false);

        int processedCount = 0;
        int successCount = 0;

        List<Organisation> organisations = organisationDao.getOrganisationsWithoutGeocode();

        logger.info("Found {} locations to geocode", organisations.size());

        for (Organisation org : organisations) {
            try {
                if (org.buildCoord() != null) {
                    if (debug) logger.info("skip org with geocoding {}", org.buildCoord());
                    continue;
                }
                processedCount++;

                // get geocode
                GoogleMapsGeocodingService.GeocodingResult result =
                        geocodingService.geocodeAddress(org.buildAddress())
                                .orElse(null);

                if (result != null) {
                    org.setCoord(new GeoJsonPoint(result.getLatitude(), result.getLongitude()));

                    successCount++;

                    if (debug) logger.info("Successfully updated location: {}", org.buildAddress());
                } else {
                    if (debug) logger.warn("Failed to geocode address: {}", org.buildAddress());
                }

                if (processedCount < organisations.size()) {
                    Thread.sleep(delayBetweenRequests);
                }

                if (processedCount % batchSize == 0) {
                    logger.info("Processed {}/{} locations", processedCount, organisations.size());
                }

            } catch (Exception e) {
                logger.error("Error processing location: {}", org.buildAddress(), e);
            }
        }

        organisationDao.updateGeocode(organisations);

        logger.info("Geocoding completed. Processed: {}, Success: {}, Failed: {}",
                processedCount, successCount, processedCount - successCount);
    }
}
