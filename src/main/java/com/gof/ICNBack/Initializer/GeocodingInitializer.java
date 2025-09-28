package com.gof.ICNBack.Initializer;

import com.gof.ICNBack.Service.LocationUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class GeocodingInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(GeocodingInitializer.class);

    private final LocationUpdateService locationUpdateService;

    @Value("${app.google-map-geocode.enable:0}")
    private int enabled;

    public GeocodingInitializer(LocationUpdateService locationUpdateService) {
        this.locationUpdateService = locationUpdateService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (enabled == 1){
            logger.info("Starting MongoDB geocoding initialization...");

            try {
                locationUpdateService.updateLocationsWithGeocoding();
                logger.info("MongoDB geocoding initialization completed successfully");
            } catch (Exception e) {
                logger.error("MongoDB geocoding initialization failed", e);
            }
        }else{
            logger.info("MongoDB geocoding initialization disabled!");
        }
    }
}