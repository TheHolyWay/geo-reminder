package ru.holyway.georeminder.handler.message;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.georeminder.entity.UserTask;
import ru.holyway.georeminder.service.UserState;
import ru.holyway.georeminder.service.UserStateService;

@Component
public class PlaceMessageHandler implements MessageHandler {
    private final UserStateService userStateService;

    public PlaceMessageHandler(UserStateService userStateService) {
        this.userStateService = userStateService;
    }

    @Override
    public boolean isNeedToHandle(Message message) {
        return UserState.ASK_PLACE.equals(userStateService.getCurrentUserState(message.getFrom().getId()))
                && message.getText() != null
                && !message.getText().startsWith("/");
    }

    @Override
    public void execute(Message message, AbsSender sender) throws TelegramApiException {
        final UserTask userTask = new UserTask();
        userTask.setTaskType(UserTask.TaskType.PLACE);
        userTask.setUserID(message.getFrom().getId());
        userStateService.changeDraftTask(message.getFrom().getId(), userTask);

        sender.execute(new SendMessage().setText("\uD83D\uDCDD Опишите текст напоминания").setChatId(message.getChatId()));
        userStateService.changeUserState(message.getFrom().getId(), UserState.ASK_MESSAGE);
    }
}