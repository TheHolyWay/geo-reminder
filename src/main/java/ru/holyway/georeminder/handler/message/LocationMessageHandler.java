package ru.holyway.georeminder.handler.message;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Location;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.georeminder.entity.UserTask;
import ru.holyway.georeminder.service.UserState;
import ru.holyway.georeminder.service.UserStateService;

@Component
public class LocationMessageHandler implements MessageHandler {

    private final UserStateService userStateService;

    public LocationMessageHandler(UserStateService userStateService) {
        this.userStateService = userStateService;
    }

    @Override
    public boolean isNeedToHandle(Message message) {
        if (UserState.ASK_LOCATION.equals(userStateService.getCurrentUserState(message.getFrom().getId()))) {
            final Location location = message.getLocation();
            return location != null;
        }
        return false;
    }

    @Override
    public void execute(Message message, AbsSender sender) throws TelegramApiException {
        final Location location = message.getLocation();
        sender.execute(new SendMessage().setText("\uD83D\uDCDD Опишите текст напоминания").setChatId(message.getChatId()));
        userStateService.changeUserState(message.getFrom().getId(), UserState.ASK_MESSAGE);
        final UserTask userTask = new UserTask();
        userTask.setLocation(location);
        userTask.setUserID(message.getFrom().getId());
        userStateService.changeDraftTask(message.getFrom().getId(), userTask);
    }
}
