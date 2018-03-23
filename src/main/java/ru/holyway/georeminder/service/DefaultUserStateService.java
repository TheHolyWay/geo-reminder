package ru.holyway.georeminder.service;

import org.springframework.stereotype.Component;
import ru.holyway.georeminder.entity.UserTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DefaultUserStateService implements UserStateService {

    private final Map<Integer, UserState> userStateMap = new ConcurrentHashMap<>();

    private final Map<Integer, UserTask> userTaskMap = new ConcurrentHashMap<>();

    @Override
    public UserState getCurrentUserState(Integer userID) {
        final UserState userState = userStateMap.get(userID);
        return userState != null ? userState : UserState.NO_STATE;
    }

    @Override
    public UserTask getDraftUserTask(Integer userID) {
        return userTaskMap.get(userID);
    }

    @Override
    public void changeUserState(Integer userID, UserState userState) {
        userStateMap.put(userID, userState);
    }

    @Override
    public void changeDraftTask(Integer userID, UserTask userTask) {
        userTaskMap.put(userID, userTask);
    }
}
