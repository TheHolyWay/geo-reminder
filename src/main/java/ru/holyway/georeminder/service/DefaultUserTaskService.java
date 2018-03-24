package ru.holyway.georeminder.service;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.api.objects.Location;
import ru.holyway.georeminder.data.UserTaskRepository;
import ru.holyway.georeminder.entity.UserTask;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DefaultUserTaskService implements UserTaskService {

    private final Map<Number, Set<UserTask>> userTasks = new ConcurrentHashMap<>();

    private final UserTaskRepository userTaskRepository;

    public DefaultUserTaskService(final UserTaskRepository userTaskRepository) {
        this.userTaskRepository = userTaskRepository;

        List<UserTask> userTaskList = (List<UserTask>) userTaskRepository.findAll();
        if (!CollectionUtils.isEmpty(userTaskList)) {
            for (UserTask userTask : userTaskList) {
                final Set<UserTask> tasks = userTasks.computeIfAbsent(userTask.getUserID(), k -> new HashSet<>());
                tasks.add(userTask);
            }
        }

    }

    @Override
    public void addTask(final UserTask userTask) {
        final Set<UserTask> tasks = userTasks.computeIfAbsent(userTask.getUserID(), k -> new HashSet<>());
        tasks.add(userTask);
        userTaskRepository.save(userTask);
    }

    @Override
    public void updateTask(final UserTask userTask) {
        final Set<UserTask> tasks = userTasks.computeIfAbsent(userTask.getUserID(), k -> new HashSet<>());
        tasks.add(userTask);
        userTaskRepository.save(userTask);
    }

    @Override
    public void removeUserTask(final String taskID) {
        final Collection<Set<UserTask>> tasks = userTasks.values();
        for (Set<UserTask> userTasks : tasks) {
            UserTask toFoundUserTask = new UserTask();
            toFoundUserTask.setId(taskID);
            if (userTasks.remove(toFoundUserTask)) {
                userTaskRepository.delete(taskID);
                return;
            }
        }
    }

    @Override
    public Set<UserTask> getUserTasks(Number userID) {
        return userTasks.get(userID);
    }

    @Override
    public Set<UserTask> getSimpleUserTasks(Number userID) {
        return getUserTasksByType(userID, UserTask.TaskType.SIMPLE);
    }

    @Override
    public Set<UserTask> getPlaceUserTasks(Number userID) {
        return getUserTasksByType(userID, UserTask.TaskType.PLACE);
    }

    @Override
    public Set<UserTask> getEventUserTasks(Number userID) {
        Set<UserTask> result = new HashSet<>();
        if (userTasks.get(userID) != null) {
            for (UserTask task : userTasks.get(userID)) {
                if (UserTask.EventType.EVENT == task.getEventType()) {
                    result.add(task);
                }
            }
        }
        return result;
    }

    private Set<UserTask> getUserTasksByType(Number userID, UserTask.TaskType type) {
        Set<UserTask> result = new HashSet<>();
        if (userTasks.get(userID) != null) {
            for (UserTask task : userTasks.get(userID)) {
                if (type == task.getTaskType() && !UserTask.EventType.EVENT.equals(task.getEventType())) {
                    result.add(task);
                }
            }
        }
        return result;
    }
}
