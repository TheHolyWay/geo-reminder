package ru.holyway.georeminder.entity;

import org.telegram.telegrambots.api.objects.Location;

import java.util.Objects;

public class UserLocation {

    private Integer userId;

    private String name;

    private Integer locationMessageID;

    private Location location;

    public UserLocation(Integer userId, String name, Integer locationMessageID, Location location) {
        this.userId = userId;
        this.name = name;
        this.locationMessageID = locationMessageID;
        this.location = location;
    }

    public Integer getUserId() {
        return userId;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLocation that = (UserLocation) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId);
    }

    public String getName() {
        return name;
    }

    public Integer getLocationMessageID() {
        return locationMessageID;
    }
}
