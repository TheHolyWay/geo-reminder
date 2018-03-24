package ru.holyway.georeminder.service;

import org.telegram.telegrambots.api.objects.Location;
import ru.holyway.georeminder.entity.UserTask;

import java.util.Set;

/**
 *
 */
public interface UserTaskService {
    /**
     *
     */
    void addTask(final UserTask userTask);

    void updateTask(UserTask userTask);

    void removeUserTask(String taskID);

    /**
     * @param userID
     * @return
     */
    Set<UserTask> getUserTasks(final Number userID);

    Set<UserTask> getSimpleUserTasks(final Number userID);

    Set<UserTask> getPlaceUserTasks(final Number userID);
}
