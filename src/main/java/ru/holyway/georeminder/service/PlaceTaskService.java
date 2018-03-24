package ru.holyway.georeminder.service;

import ru.holyway.georeminder.entity.AddressLocation;
import ru.holyway.georeminder.entity.PlaceRegion;

public interface PlaceTaskService {

    void addRegionForTask(String taskId, String placeAlias);

    void addRegionForTask(String taskId, String placeAlias, AddressLocation location);

    PlaceRegion findPlaceByTask(String taskId);

    void removePlaceByTask(String taskId);
}
