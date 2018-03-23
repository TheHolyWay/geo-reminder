package ru.holyway.georeminder.entity;

public class Geometry {
    private AddressLocation location;

    public Geometry(AddressLocation location) {
        this.location = location;
    }

    public Geometry() {
    }

    public AddressLocation getLocation() {
        return location;
    }

    public void setLocation(AddressLocation location) {
        this.location = location;
    }
}
