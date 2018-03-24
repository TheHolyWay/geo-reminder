package ru.holyway.georeminder.handler.message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.georeminder.entity.UserTask;
import ru.holyway.georeminder.service.PlaceTaskService;
import ru.holyway.georeminder.service.UserState;
import ru.holyway.georeminder.service.UserStateService;

@Component
public class PlaceMessageHandler implements MessageHandler {
    private final UserStateService userStateService;
    private final PlaceTaskService placeTaskService;

    public PlaceMessageHandler(UserStateService userStateService,
                               PlaceTaskService placeTaskService) {
        this.userStateService = userStateService;
        this.placeTaskService = placeTaskService;
    }

    @Override
    public boolean isNeedToHandle(Message message) {
        return UserState.ASK_PLACE.equals(userStateService.getCurrentUserState(message.getFrom().getId()))
                && message.getText() != null
                && !message.getText().startsWith("/");
    }

    @Override
    public void execute(Message message, AbsSender sender) throws TelegramApiException {
        final UserTask userTask = userStateService.getDraftUserTask(message.getFrom().getId());
        userTask.setTaskType(UserTask.TaskType.PLACE);
        if (message.getChat().isUserChat()) {
            userTask.setUserID(message.getFrom().getId());
        } else {
            userTask.setUserID(message.getChat().getId());
        }
        userTask.setChatID(message.getChatId());
        userTask.setTargetPlace(message.getText());
        userStateService.changeDraftTask(message.getFrom().getId(), userTask);
        placeTaskService.addRegionForTask(userTask.getId(), message.getText());

        sender.execute(new SendMessage().setText("\uD83D\uDCDD Опишите текст напоминания").setChatId(message.getChatId()));
        userStateService.changeUserState(message.getFrom().getId(), UserState.ASK_MESSAGE);
    }
}
