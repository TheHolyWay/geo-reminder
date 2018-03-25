package ru.holyway.georeminder.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import utils.MathUtils;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlaceRegion {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceRegion.class);

    private static int SEARCH_RADIUS = 2000;
    private static int SEARCH_EPSILON = 100;
    private static String SEARCH_URL_BASE = "https://maps.googleapis.com/maps/api/place/textsearch/json?";

    private final RestTemplate restTemplate;
    private final String googleApiKey;

    /**
     * User input for search, e. g. 'Магнит' or 'Аптека'
     */
    private String placeAlias;
    private String taskId;
    private AddressLocation regionCenterLocation;
    private AddressLocation previousLocation;
    private List<AddressResult> placesInRegion;

    public PlaceRegion(String placeAlias, String taskId,
                       final String googleApiKey,
                       final RestTemplate restTemplate) {
        this.placeAlias = placeAlias.replaceAll("\\s", "+");
        this.taskId = taskId;
        this.googleApiKey = googleApiKey;
        this.restTemplate = restTemplate;

        this.regionCenterLocation = null;
        this.previousLocation = null;
        this.placesInRegion = Collections.emptyList();

        LOGGER.info("Create place with alias: {} for task: {}.", this.placeAlias, this.taskId);
    }

    public void updatePlaceRegion(AddressLocation changedLocation) {
        if (regionCenterLocation == null) {
            this.placesInRegion = findPlacesInRegion(changedLocation);
            this.regionCenterLocation = changedLocation;
        } else if (needRecalculate(changedLocation)) {
//            float newLat = (changedLocation.getLatitude() - this.regionCenterLocation.getLatitude()) +
//                    changedLocation.getLatitude();
//            float newLng = (changedLocation.getLongitude() - this.regionCenterLocation.getLongitude()) +
//                    changedLocation.getLongitude();
//            AddressLocation newCenter = new AddressLocation(newLat, newLng);
            AddressLocation newCenter = changedLocation;


            LOGGER.info("OldCenter: {}", this.regionCenterLocation.toString());
            LOGGER.info("NewCenter: {}", newCenter.toString());

            this.placesInRegion = findPlacesInRegion(newCenter);
            this.regionCenterLocation = newCenter;
        }

        this.previousLocation = changedLocation;
    }

    private boolean needRecalculate(AddressLocation changedLocation) {
        LOGGER.info("Start 'needRecalculate' method for task: {}.", this.taskId);
        double centerToChangedDistance = MathUtils.sphericalDistance(regionCenterLocation.getLongitude(),
                regionCenterLocation.getLatitude(), changedLocation.getLongitude(), changedLocation.getLatitude());

        if (centerToChangedDistance - SEARCH_RADIUS > 10) {
            return true;
        }

        if (centerToChangedDistance > (double) (SEARCH_RADIUS - SEARCH_EPSILON)) {
            if (this.previousLocation != null) {
                double centerToPrevDistance = MathUtils.sphericalDistance(regionCenterLocation.getLongitude(),
                        regionCenterLocation.getLatitude(), this.previousLocation.getLongitude(),
                        this.previousLocation.getLatitude());
                if (SEARCH_RADIUS - SEARCH_EPSILON <= centerToPrevDistance
                        && centerToPrevDistance <= SEARCH_RADIUS) {
                    if (MathUtils.sphericalDistance(this.previousLocation.getLongitude(), this.previousLocation.getLatitude(),
                            changedLocation.getLongitude(), changedLocation.getLatitude()) > SEARCH_RADIUS / 2) {
                        return true;
                    }
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

        return false;
    }

    private List<AddressResult> findPlacesInRegion(AddressLocation location) {
        String request = SEARCH_URL_BASE + "location=" + location.getLocationAsString() +
                "&radius=" + SEARCH_RADIUS +
                "&query=" + URLEncoder.encode(this.placeAlias) +
                "&key=" + this.googleApiKey +
                "&language=ru";

        ResponseEntity<AddressResponse> addressResponse =
                restTemplate.getForEntity(URI.create(request), AddressResponse.class);

        LOGGER.info("GoTo GOOGLE with query '{}'", request);

        AddressResponse response = addressResponse.getBody();
        List<AddressResult> result = response.getAddressResults();

        result.sort((o1, o2) -> {
            if (o1 == null) {
                if (o2 == null) {
                    return -1;
                } else {
                    return 1;
                }
            } else if (o2 == null) {
                return -1;
            }

            if (o1.getRating() == null) {
                if (o2.getRating() == null) {
                    return -1;
                } else {
                    return 1;
                }
            } else if (o2.getRating() == null) {
                return -1;
            }

            Double o1Rate = Double.valueOf(o1.getRating());
            Double o2Rate = Double.valueOf(o2.getRating());
            return o1Rate > o2Rate ? -1 : 1;
        });

        return result;
    }

    public AddressLocation getRegionCenterLocation() {
        return regionCenterLocation;
    }

    public List<AddressResult> getPlacesInRegion() {
        return placesInRegion;
    }
}
