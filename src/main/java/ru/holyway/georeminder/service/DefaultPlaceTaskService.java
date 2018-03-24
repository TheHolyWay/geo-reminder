package ru.holyway.georeminder.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import ru.holyway.georeminder.data.UserTaskRepository;
import ru.holyway.georeminder.entity.AddressLocation;
import ru.holyway.georeminder.entity.PlaceRegion;
import ru.holyway.georeminder.entity.UserTask;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DefaultPlaceTaskService implements PlaceTaskService {

    private final Map<String, PlaceRegion> placesCache = new ConcurrentHashMap<>();

    private final RestTemplate restTemplate;
    private final String googleApiKey;

    public DefaultPlaceTaskService(final UserTaskRepository userTaskRepository,
                                   @Value("${credential.google.apikey}") final String googleApiKey,
                                   RestTemplate restTemplate) {
        this.googleApiKey = googleApiKey;
        this.restTemplate = restTemplate;

        List<UserTask> userTaskList = (List<UserTask>) userTaskRepository.findAll();

        // Fill places cache
        if (!CollectionUtils.isEmpty(userTaskList)) {
            for (UserTask task : userTaskList) {
                if (UserTask.TaskType.PLACE == task.getTaskType()) {
                    PlaceRegion regionForTask = new PlaceRegion(task.getTargetPlace(), task.getId(),
                            this.googleApiKey, this.restTemplate);
                    regionForTask.updatePlaceRegion(new AddressLocation(task.getLocation().getLatitude(),
                            task.getLocation().getLongitude()));
                    placesCache.put(task.getId(), regionForTask);
                }
            }
        }
    }

    @Override
    public void addRegionForTask(String taskId, String placeAlias) {
        PlaceRegion regionForTask = new PlaceRegion(placeAlias, taskId, this.googleApiKey, this.restTemplate);

        placesCache.put(taskId, regionForTask);
    }

    @Override
    public void addRegionForTask(String taskId, String placeAlias, AddressLocation location) {
        PlaceRegion regionForTask = new PlaceRegion(placeAlias, taskId, this.googleApiKey, this.restTemplate);
        regionForTask.updatePlaceRegion(location);

        placesCache.put(taskId, regionForTask);
    }

    @Override
    public PlaceRegion findPlaceByTask(String taskId) {
        return placesCache.get(taskId);
    }

    @Override
    public void removePlaceByTask(String taskId) {
        placesCache.remove(taskId);
    }
}
