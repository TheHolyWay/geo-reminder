package ru.holyway.georeminder.service;

import org.telegram.telegrambots.api.objects.Location;
import ru.holyway.georeminder.entity.UserTask;

import java.util.Set;

/**
 *
 */
public interface UserTaskService {
    /**
     * @param userID
     * @param targetLocation
     * @param text
     */
    void addTask(final Integer userID, final Long chatID, final Location targetLocation, final String text);

    void updateTask(UserTask userTask);

    void removeUserTask(String taskID);

    /**
     * @param userID
     * @return
     */
    Set<UserTask> getUserTasks(final Integer userID);
}
