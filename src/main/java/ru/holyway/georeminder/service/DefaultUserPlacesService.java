package ru.holyway.georeminder.service;

import org.springframework.stereotype.Component;
import ru.holyway.georeminder.entity.UserLocation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DefaultUserPlacesService implements UserPlacesService {

    private Map<Long, Set<UserLocation>> userLocations = new ConcurrentHashMap<>();

    @Override
    public Set<UserLocation> getUserLocations(Long chatId) {
        return userLocations.computeIfAbsent(chatId, k -> new HashSet<>());
    }

    @Override
    public void updateUserLocation(Long chatID, UserLocation userLocation) {
        final Set<UserLocation> currentLocationsOfChat = userLocations.computeIfAbsent(chatID, k -> new HashSet<>());
        currentLocationsOfChat.add(userLocation);
    }
}
