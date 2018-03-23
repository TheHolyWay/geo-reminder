package ru.holyway.georeminder.service;

import ru.holyway.georeminder.entity.UserTask;

/**
 *
 */
public interface UserStateService {

    /**
     * @param userID
     * @return
     */
    UserState getCurrentUserState(final Integer userID);

    /**
     * @param userID
     * @return
     */
    UserTask getDraftUserTask(final Integer userID);

    /**
     * @param userID
     * @param userState
     */
    void changeUserState(final Integer userID, final UserState userState);

    /**
     * @param userID
     * @param userTask
     */
    void changeDraftTask(final Integer userID, final UserTask userTask);
}
