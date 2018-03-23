package ru.holyway.georeminder.handler.message;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.georeminder.entity.UserTask;
import ru.holyway.georeminder.service.UserState;
import ru.holyway.georeminder.service.UserStateService;
import ru.holyway.georeminder.service.UserTaskService;

@Component
public class TaskTextMessageHandler implements MessageHandler {

    private final UserStateService userStateService;

    private final UserTaskService userTaskService;

    public TaskTextMessageHandler(UserStateService userStateService, UserTaskService userTaskService) {
        this.userStateService = userStateService;
        this.userTaskService = userTaskService;
    }

    @Override
    public boolean isNeedToHandle(Message message) {
        if (UserState.ASK_MESSAGE.equals(userStateService.getCurrentUserState(message.getFrom().getId()))) {
            final String mes = message.getText();
            return StringUtils.isNotEmpty(mes);
        }
        return false;
    }

    @Override
    public void execute(Message message, AbsSender sender) throws TelegramApiException {
        sender.execute(new SendMessage().setText("✅ Задача создана").setChatId(message.getChatId()));
        final String mes = message.getText();
        UserTask userTask = userStateService.getDraftUserTask(message.getFrom().getId());
        userTask.setMessage(mes);
        userTaskService.addTask(message.getFrom().getId(), message.getChatId(), userTask.getLocation(), userTask.getMessage());
        userStateService.changeUserState(message.getFrom().getId(), UserState.NO_STATE);
    }
}
