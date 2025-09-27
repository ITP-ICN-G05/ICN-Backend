package com.gof.ICNBack.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@Service
public class GoogleMapsGeocodingService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleMapsGeocodingService.class);

    @Value("${app.google-map-geocode.geocoding-url}")
    private String geocodingUrl;

    @Value("${app.google-map-geocode.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public GoogleMapsGeocodingService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * return geocode from google geocoding service
     */
    public Optional<GeocodingResult> geocodeAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return Optional.empty();
        }

        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(geocodingUrl)
                    .queryParam("address", address)
                    .queryParam("key", apiKey)
                    .build()
                    .toUri();

            logger.info("Geocoding address: {}", address);
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode root = objectMapper.readTree(response.getBody());
                String status = root.path("status").asText();

                if ("OK".equals(status)) {
                    JsonNode firstResult = root.path("results").get(0);
                    JsonNode location = firstResult.path("geometry").path("location");

                    double lat = location.path("lat").asDouble();
                    double lng = location.path("lng").asDouble();
                    String formattedAddress = firstResult.path("formatted_address").asText();

                    GeocodingResult result = new GeocodingResult(lat, lng, formattedAddress);
                    logger.info("Geocoding successful: {} -> ({}, {})", address, lat, lng);

                    return Optional.of(result);
                } else {
                    logger.warn("Geocoding failed for address: {}. Status: {}", address, status);
                }
            } else {
                logger.error("Geocoding API request failed with status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error during geocoding for address: {}", address, e);
        }

        return Optional.empty();
    }


    public static class GeocodingResult {
        private final double latitude;
        private final double longitude;
        private final String formattedAddress;

        public GeocodingResult(double latitude, double longitude, String formattedAddress) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.formattedAddress = formattedAddress;
        }

        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getFormattedAddress() { return formattedAddress; }
    }
}
