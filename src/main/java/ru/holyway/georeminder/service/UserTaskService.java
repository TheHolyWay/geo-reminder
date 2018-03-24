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
    Set<UserTask> getUserTasks(final Integer userID);

    Set<UserTask> getSimpleUserTasks(final Integer userID);

    Set<UserTask> getPlaceUserTasks(final Integer userID);
}
