package ru.holyway.georeminder.entity;

public class AddressResult {

    private String formattedAddress;

    private Geometry geometry;

    public AddressResult(String formattedAddress, Geometry geometry) {
        this.formattedAddress = formattedAddress;
        this.geometry = geometry;
    }

    public AddressResult() {
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
