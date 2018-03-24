package ru.holyway.georeminder.service;

import ru.holyway.georeminder.entity.UserLocation;

import java.util.Set;

public interface UserPlacesService {

    Set<UserLocation> getUserLocations(final Long chatId);

    void updateUserLocation(Long chatID, UserLocation userLocation);
}
