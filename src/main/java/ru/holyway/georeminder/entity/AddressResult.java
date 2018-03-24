package ru.holyway.georeminder.entity;

public class AddressResult {

    private String formattedAddress;
    private String name;
    private String rating;
    private Geometry geometry;

    public AddressResult(String formattedAddress, Geometry geometry, String name, String rating) {
        this.formattedAddress = formattedAddress;
        this.geometry = geometry;
        this.name = name;
        this.rating = rating;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
