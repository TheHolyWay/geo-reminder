package ru.holyway.georeminder.entity;

import org.telegram.telegrambots.api.objects.Location;

public class AddressLocation extends Location {
    private float lat;

    private float lng;

    public AddressLocation(float lat, float lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public AddressLocation() {
    }

    @Override
    public Float getLongitude() {
        return lng;
    }

    @Override
    public Float getLatitude() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }
}
