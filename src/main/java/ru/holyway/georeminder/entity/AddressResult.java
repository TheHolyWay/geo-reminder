package ru.holyway.georeminder.entity;

public class AddressResult {

    private String formatted_address;

    private Geometry geometry;

    public AddressResult(String formatted_address, Geometry geometry) {
        this.formatted_address = formatted_address;
        this.geometry = geometry;
    }

    public AddressResult() {
    }

    public String getFormattedaddress() {
        return formatted_address;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
