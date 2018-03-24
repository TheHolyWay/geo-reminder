package ru.holyway.georeminder.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

public class PlaceRegion {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceRegion.class);

    private static int SEARCH_RADIUS = 500;
    private static String SEARCH_URL_BASE = "https://maps.googleapis.com/maps/api/place/textsearch/json?";

    private final RestTemplate restTemplate;
    private final String googleApiKey;

    /**
     * User input for search, e. g. 'Магнит' or 'Аптека'
     */
    private String placeAlias;
    private String taskId;
    private AddressLocation regionCenterLocation;
    private List<AddressResult> placesInRegion;

    public PlaceRegion(String placeAlias, String taskId,
                       final String googleApiKey,
                       final RestTemplate restTemplate) {
        this.placeAlias = placeAlias.replaceAll("\\s", "+");
        this.taskId = taskId;
        this.googleApiKey = googleApiKey;
        this.restTemplate = restTemplate;

        this.regionCenterLocation = null;
        this.placesInRegion = Collections.emptyList();

        LOGGER.info("Create place with alias: {} for task: {}.", this.placeAlias, this.taskId);
    }

    public void updatePlaceRegion(AddressLocation changedLocation) {
        if (regionCenterLocation == null) {
            this.placesInRegion = findPlacesInRegion(changedLocation);
            this.regionCenterLocation = changedLocation;
        } else {
            // TODO: Recalculate places if it needed
        }
    }

    private List<AddressResult> findPlacesInRegion(AddressLocation location) {
        final StringBuilder requestBuilder = new StringBuilder(SEARCH_URL_BASE);
        requestBuilder.append("location=").append(location.getLocationAsString());
        requestBuilder.append("&radius=").append(SEARCH_RADIUS);
        requestBuilder.append("&query=").append(URLEncoder.encode(this.placeAlias));
        requestBuilder.append("&key=").append(this.googleApiKey);
        requestBuilder.append("&language=ru");

        ResponseEntity<AddressResponse> adressResponse =
                restTemplate.getForEntity(URI.create(requestBuilder.toString()), AddressResponse.class);
        AddressResponse response = adressResponse.getBody();
        return response.getAddressResults();
    }
}
