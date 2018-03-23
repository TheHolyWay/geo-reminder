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

    private final Map<Integer, Set<UserTask>> userTasks = new ConcurrentHashMap<>();

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
    public void addTask(Integer userID, Long chatID, Location targetLocation, String text) {
        final Set<UserTask> tasks = userTasks.computeIfAbsent(userID, k -> new HashSet<>());
        final UserTask userTask = new UserTask(userID, text, targetLocation, chatID);
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
    public Set<UserTask> getUserTasks(Integer userID) {
        return userTasks.get(userID);
    }
}
